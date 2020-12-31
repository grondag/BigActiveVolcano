package grondag.pyroclasm.volcano;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import grondag.fermion.position.PackedChunkPos;
import grondag.fermion.simulator.SimulationTickable;
import grondag.fermion.simulator.Simulator;
import grondag.fermion.simulator.persistence.SimulationTopNode;
import grondag.fermion.varia.BlueNoise;
import grondag.fermion.varia.NBTDictionary;
import grondag.fermion.varia.Useful;
import grondag.pyroclasm.Configurator;
import grondag.pyroclasm.command.VolcanoCommandException;
import grondag.pyroclasm.fluidsim.LavaSimulator;

public class VolcanoManager extends SimulationTopNode implements SimulationTickable {
    public VolcanoManager() {
        super(NBT_VOLCANO_MANAGER);
    }

    public static final String NBT_VOLCANO_MANAGER = NBTDictionary.GLOBAL.claim("volcMgr");
    private static final String NBT_VOLCANO_NODES = NBTDictionary.GLOBAL.claim("volcNodes");
    private static final String NBT_VOLCANO_MANAGER_IS_CREATED = NBTDictionary.GLOBAL.claim("volcExists");
    private static final String NBT_VOLCANO_MANAGER_LAST_CYCLE_TICK = NBTDictionary.GLOBAL.claim("vmLastCycleTick");

    /**
     * Will be reliable initialized via
     * {@link VolcanoManager#afterCreated(Simulator)}
     */
    ServerWorld world;

    private final Long2ObjectMap<VolcanoNode> nodes = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<VolcanoNode>());

    final Long2ObjectMap<VolcanoNode> activeNodes = Long2ObjectMaps.synchronize(new Long2ObjectOpenHashMap<VolcanoNode>());

    private final boolean isDirty = true;

    private long lastCycleTick = 0;

    /**
     * Will be reliably initialized via
     * {@link VolcanoManager#afterCreated(Simulator)}
     */
    private BlueNoise noise;

    @Override
    public void afterCreated(Simulator sim) {
        world = sim.getWorld();
//        long start = System.nanoTime();
        noise = BlueNoise.create(512, 40, world.getSeed());
//        Pyroclasm.INSTANCE.info("Blue noise generation completed in %d nanoseconds", System.nanoTime() - start);
    }

    public int dimension() {
        return world.dimension.getType().getRawId();
    }

    public boolean isVolcanoChunk(Chunk chunk) {
        return isVolcanoChunk(chunk.getPos());
    }

    public boolean isVolcanoChunk(ChunkPos pos) {
        return isVolcanoChunk(pos.x, pos.z);
    }

    public boolean isVolcanoChunk(int chunkX, int chunkZ) {
        return noise.isSet(chunkX, chunkZ);
    }

    public boolean isVolcanoChunk(BlockPos pos) {
        return isVolcanoChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public @Nullable BlockPos nearestVolcanoPos(BlockPos pos) {
        final ChunkPos cp = nearestVolcanoChunk(pos);
        return cp == null ? null : blockPosFromChunkPos(cp);
    }

    private VolcanoNode getOrCreateNode(ChunkPos chunkPos) {
        return getOrCreateNode(PackedChunkPos.getPackedChunkPos(chunkPos));
    }

    private VolcanoNode getOrCreateNode(long packedChunkPos) {
        VolcanoNode node;

        node = nodes.computeIfAbsent(packedChunkPos, new Function<Long, VolcanoNode>() {
            @Override
            public VolcanoNode apply(@Nullable Long k) {
                VolcanoManager.this.makeDirty();
                return new VolcanoNode(VolcanoManager.this, PackedChunkPos.unpackChunkPos(packedChunkPos));
            }
        });

        return node;
    }

    private @Nullable ChunkPos nearestVolcanoChunk(BlockPos pos) {
        final int originX = pos.getX() >> 4;
        final int originZ = pos.getZ() >> 4;

        for (final Vec3i vec : Useful.DISTANCE_SORTED_CIRCULAR_OFFSETS) {
            final int chunkX = originX + vec.getX();
            final int chunkZ = originZ + vec.getZ();
            if (isVolcanoChunk(chunkX, chunkZ)) {
                return new ChunkPos(chunkX, chunkZ);
            }
        }
        return null;
    }

    /**
     * Returns x, z position of volcano if successful. Null otherwise.
     */
    public @Nullable BlockPos wakeNearest(BlockPos blockPos) throws VolcanoCommandException {
        final ChunkPos cp = nearestVolcanoChunk(blockPos);
        if (cp == null)
            return null;

        // see if loaded
        final VolcanoNode node = this.getOrCreateNode(cp);

        if (node.isActive())
            throw new VolcanoCommandException("commands.volcano.wake.already_awake");

        if (!node.isActive())
            node.activate();

        return node.isActive() ? blockPosFromChunkPos(cp) : null;
    }

    public @Nullable VolcanoNode nearestActiveVolcano(BlockPos pos) {
        if (activeNodes.isEmpty())
            return null;

        final int originX = pos.getX() >> 4;
        final int originZ = pos.getZ() >> 4;
        VolcanoNode result = null;

        final int bestDist = Integer.MAX_VALUE;

        for (final VolcanoNode node : activeNodes.values()) {
            final ChunkPos p = node.chunkPos();
            final int d = (int) Math.sqrt(Useful.squared(p.x - originX) + Useful.squared(p.z - originZ));
            if (d < bestDist) {
                result = node;
            }
        }
        return result;
    }

    public Map<BlockPos, VolcanoNode> nearbyVolcanos(BlockPos pos) {
        final int originX = pos.getX() >> 4;
        final int originZ = pos.getZ() >> 4;

        final ImmutableMap.Builder<BlockPos, VolcanoNode> builder = ImmutableMap.builder();

        for (final Vec3i vec : Useful.DISTANCE_SORTED_CIRCULAR_OFFSETS) {
            final int chunkX = originX + vec.getX();
            final int chunkZ = originZ + vec.getZ();
            if (isVolcanoChunk(chunkX, chunkZ)) {
                final VolcanoNode node = getOrCreateNode(PackedChunkPos.getPackedChunkPosFromChunkXZ(chunkX, chunkZ));
                builder.put(blockPosFromChunkPos(chunkX, chunkZ), node);
            }
        }
        return builder.build();
    }

    static BlockPos blockPosFromChunkPos(ChunkPos pos) {
        return blockPosFromChunkPos(pos.x, pos.z);
    }

    static BlockPos blockPosFromChunkPos(int chunkX, int chunkZ) {
        return new BlockPos((chunkX << 4) + 7, 0, (chunkZ << 4) + 7);
    }

    @Override
    public void doOnTick() {
        if (LavaSimulator.isSuspended)
            return;

        if (!activeNodes.isEmpty()) {
            for (final VolcanoNode node : activeNodes.values()) {
                node.doOnTick();
            }
        }
    }

    /**
     * Checks for activation if no volcanos are active, or updates the active
     * volcano if there is one.
     */
    @Override
    public void doOffTick() {
        if (LavaSimulator.isSuspended)
            return;

        final long tick = Simulator.currentTick();

        // Ipdate weights (inhabited time) for loaded chunks
        // can happen off tick because doesn't mutate chunk in any way
        // Interval is about 4x more often than the chunks are updated by world
        // because we aren't synchronized with that timer
        if ((tick & 0x7FF) == 0x7FF)
            nodes.values().forEach(n -> n.refreshWeightFromChunk());

        // check activation every ~ten seconds
        if ((tick & 0xFF) == 0xFF) {
            if (activeNodes.isEmpty())
                tryActivate();
            else
                tryDeactivate();
        }

        if (!activeNodes.isEmpty())
            for (final VolcanoNode active : activeNodes.values())
                active.doOffTick();
    }

    private void tryActivate() {
        final long t = Simulator.currentTick() - Configurator.VOLCANO.minDormantTicks - (lastCycleTick == 0 ? Configurator.VOLCANO.graceTicks : lastCycleTick);

        if (t <= 0)
            return;

        // Lightweight deterministic randomization that honors in-flight changes to
        // thresholds.
        // Uses top half of mixed long as a second sample to make results somewhat
        // normalized.
        final int span = Configurator.VOLCANO.maxDormantTicks - Configurator.VOLCANO.minDormantTicks;
        final long r = HashCommon.mix(world.getSeed() ^ lastCycleTick);
        final long x = (r % span) + ((r >>> 32) % span);
        if (t < x / 2)
            return;

        long totalWeight = 0;

        final Object2LongOpenHashMap<VolcanoNode> candidates = new Object2LongOpenHashMap<>();

        for (final VolcanoNode node : nodes.values()) {
            if (node == null)
                continue;

            final long w = node.getWeight();
            if (w == 0)
                continue;

            candidates.put(node, w);
            totalWeight += w;
        }

        if (!candidates.isEmpty()) {
            long targetWeight = (long) (ThreadLocalRandom.current().nextFloat() * totalWeight);

            for (final Object2LongMap.Entry<VolcanoNode> candidate : candidates.object2LongEntrySet()) {
                targetWeight -= candidate.getLongValue();
                if (targetWeight < 0) {
                    candidate.getKey().activate();
                    lastCycleTick = Simulator.currentTick();
                    makeDirty();
                    return;
                }
            }
        }
    }

    private void tryDeactivate() {
        final Iterator<VolcanoNode> it = activeNodes.values().iterator();

        while (it.hasNext()) {
            final VolcanoNode node = it.next();

            final long t = Simulator.currentTick() - node.lastActivationTick() - Configurator.VOLCANO.minActiveTicks;

            if (t <= 0)
                continue;

            // Lightweight deterministic randomization that honors in-flight changes to
            // thresholds.
            // Uses top half of mixed long as a second sample to make results somewhat
            // normalized.
            final int span = Configurator.VOLCANO.maxActiveTicks - Configurator.VOLCANO.minActiveTicks;
            final long r = HashCommon.mix(world.getSeed() ^ node.lastActivationTick());
            final long x = (r % span) + ((r >>> 32) % span);
            if (t < x / 2)
                continue;

            node.sleep(false);
            it.remove();
            lastCycleTick = Simulator.currentTick();
            makeDirty();
        }
    }

    @Override
    public void fromTag(CompoundTag nbt) {
        nodes.clear();
        activeNodes.clear();

        if (nbt != null) {
            lastCycleTick = nbt.getLong(NBT_VOLCANO_MANAGER_LAST_CYCLE_TICK);
            final ListTag nbtSubNodes = nbt.getList(NBT_VOLCANO_NODES, 10);
            if (!nbtSubNodes.isEmpty()) {
                for (int i = 0; i < nbtSubNodes.size(); ++i) {
                    final VolcanoNode node = new VolcanoNode(this, nbtSubNodes.getCompoundTag(i));
                    nodes.put(node.packedChunkPos(), node);
                    if (node.isActive())
                        activeNodes.put(node.packedChunkPos(), node);
                }
            }
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag nbt) {

        if(nbt == null) {
            nbt = new CompoundTag();
        }

        // always save *something* to prevent warning when there are no volcanos
        nbt.putBoolean(NBT_VOLCANO_MANAGER_IS_CREATED, true);
        nbt.putLong(NBT_VOLCANO_MANAGER_LAST_CYCLE_TICK, lastCycleTick);

        // Do start because any changes made after this point aren't guaranteed to be
        // saved
        this.makeDirty(false);

        final ListTag nbtSubNodes = new ListTag();

        if (!nodes.isEmpty()) {
            for (final VolcanoNode node : nodes.values()) {
                final CompoundTag nodeTag = new CompoundTag();
                node.readTag(nodeTag);
                nbtSubNodes.add(nodeTag);
            }
        }
        nbt.put(NBT_VOLCANO_NODES, nbtSubNodes);

        return nbt;
    }

    @Override
    public boolean isDirty() {
        return isDirty;
    }

    public void handleChunkLoad(World world, Chunk chunk) {
        if (world == this.world) {
            final ChunkPos cp = chunk.getPos();
            if (isVolcanoChunk(cp)) {
                final VolcanoNode node = this.getOrCreateNode(cp);
                node.onChunkLoad(chunk);
            }
        }
    }

    public void handleChunkUnload(World world, Chunk chunk) {
        if (world == this.world) {
            final ChunkPos cp = chunk.getPos();
            if (isVolcanoChunk(cp)) {
                final VolcanoNode node = this.getOrCreateNode(cp);
                node.onChunkUnload(chunk);
            }
        }
    }

    @Override
    public void makeDirty() {
        makeDirty(true);
    }
}
