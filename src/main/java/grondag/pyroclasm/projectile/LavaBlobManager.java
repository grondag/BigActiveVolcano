package grondag.pyroclasm.projectile;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;

import grondag.fermion.position.PackedBlockPos;
import grondag.fermion.simulator.Simulator;
import grondag.fermion.varia.NBTDictionary;
import grondag.pyroclasm.Configurator;
import grondag.pyroclasm.Pyroclasm;
import grondag.pyroclasm.fluidsim.LavaSimulator;

public class LavaBlobManager {
    private final static String NBT_LAVA_PARTICLE_MANAGER = NBTDictionary.GLOBAL.claim("lavaBlobs");
    private final static int NBT_SAVE_DATA_WIDTH = 4;

    private final static int MIN_WAIT_TICKS = 4;
    /**
     * if drop cell doesn't have anough to form a particle by this time the lava is
     * deleted
     */
    private final static int MAX_WAIT_TICKS = 200;

    private final ConcurrentHashMap<Long, ParticleInfo> map = new ConcurrentHashMap<>(512);

    public void clear() {
        map.clear();
    }

    public int size() {
        return map.size();
    }

    public void addLavaForParticle(long packedBlockPos, int fluidAmount) {
        ParticleInfo particle = map.get(packedBlockPos);

        if (particle == null) {
            particle = new ParticleInfo(Simulator.currentTick(), packedBlockPos, fluidAmount);
            map.put(packedBlockPos, particle);
//            HardScience.log.info("ParticleManager added new particle @" + PackedBlockPos.unpack(particle.packedBlockPos).toString() + " with amount=" + particle.getFluidUnits());
        } else {
            particle.addFluid(fluidAmount);
//            HardScience.log.info("ParticleManager updated particle @" + PackedBlockPos.unpack(particle.packedBlockPos).toString() + " with amount=" + particle.getFluidUnits() + " added " + fluidAmount);
        }

    }

    /** returns a collection of eligible particles up the max count given */
    public Collection<ParticleInfo> pollEligible(LavaSimulator sim, int maxCount) {
        if (map.isEmpty())
            return ImmutableList.of();

        final int firstEligibleTick = Simulator.currentTick() - MIN_WAIT_TICKS;
        final int forceEligibleTick = Simulator.currentTick() - MAX_WAIT_TICKS;

        // wait until minimum size * minimum age, full size, or max age
        final List<ParticleInfo> candidates = map.values().parallelStream()
                .filter(p -> p.tickCreated <= forceEligibleTick || p.fluidUnits >= LavaSimulator.FLUID_UNITS_PER_BLOCK
                        || (p.tickCreated <= firstEligibleTick && p.fluidUnits >= LavaSimulator.FLUID_UNITS_PER_LEVEL))
                .sorted(new Comparator<ParticleInfo>() {

                    @Override
                    public int compare(@Nullable ParticleInfo o1, @Nullable ParticleInfo o2) {
                        return ComparisonChain.start().compare(o2.fluidUnits, o1.fluidUnits).compare(o1.tickCreated, o2.tickCreated).result();
                    }
                }).sequential().limit(maxCount).collect(Collectors.toList());

        candidates.stream().forEach(p -> map.remove(p.packedBlockPos));

        return candidates;
    }

    public static class ParticleInfo {
        public final int tickCreated;
        private int fluidUnits = 0;
        public final long packedBlockPos;

        private ParticleInfo(int tickCreated, long packedBlockPos, int fluidUnits) {
            this.tickCreated = tickCreated;
            this.packedBlockPos = packedBlockPos;
            this.fluidUnits = fluidUnits;
        }

        private void addFluid(int fluidUnitsIn) {
            fluidUnits += fluidUnitsIn;
        }

        public int getFluidUnits() {
            return fluidUnits;
        }

        public int x() {
            return PackedBlockPos.getX(packedBlockPos);
        }

        public int y() {
            return PackedBlockPos.getY(packedBlockPos);
        }

        public int z() {
            return PackedBlockPos.getZ(packedBlockPos);
        }

    }

    public void readFromNBT(CompoundTag nbt) {
        map.clear();

        final int[] saveData = nbt.getIntArray(NBT_LAVA_PARTICLE_MANAGER);

        // confirm correct size
        if (saveData.length % NBT_SAVE_DATA_WIDTH != 0) {
            Pyroclasm.LOG.warn("Invalid save data loading lava entity state buffer. Lava entities may have been lost.");
            return;
        }

        int i = 0;

        while (i < saveData.length) {
            final ParticleInfo p = new ParticleInfo(saveData[i++], (long) saveData[i++] << 32 | saveData[i++], saveData[i++]);

            // protect against duplicate position weirdness in save data
            if (!map.containsKey(p.packedBlockPos)) {
                map.put(p.packedBlockPos, p);
            }
        }

        Pyroclasm.LOG.info("Loaded " + map.size() + " lava entities.");
    }

    public void writeToNBT(CompoundTag nbt) {
        if (Configurator.DEBUG.enablePerformanceLogging)
            Pyroclasm.LOG.info("Saving " + map.size() + " lava entities.");

        final int[] saveData = new int[map.size() * NBT_SAVE_DATA_WIDTH];
        int i = 0;

        for (final ParticleInfo p : map.values()) {
            saveData[i++] = p.tickCreated;
            saveData[i++] = (int) (p.packedBlockPos & 0xFFFFFFFF);
            saveData[i++] = (int) ((p.packedBlockPos >> 32) & 0xFFFFFFFF);
            saveData[i++] = p.fluidUnits;
        }

        nbt.putIntArray(NBT_LAVA_PARTICLE_MANAGER, saveData);

    }
}
