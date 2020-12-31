package grondag.pyroclasm.world;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import grondag.fermion.position.PackedBlockPos;
import grondag.fermion.varia.Useful;
import grondag.pyroclasm.Configurator;
import grondag.pyroclasm.block.CoolingBasaltBlock;
import grondag.pyroclasm.init.ModBlocks;
import grondag.xm.terrain.TerrainBlockHelper;

public class LavaTerrainHelper {

    private static final int RADIUS = 5;
    private static final int MASK_WIDTH = RADIUS * 2 + 1;
    private static final int MASK_SIZE = MASK_WIDTH * MASK_WIDTH;

    /**
     * information about positions within the configured radius, order by distance
     * ascending
     */
    private static final VisibilityNode[] VISIBILITY_NODES;
    private static final float INCREMENT = 1F / (RADIUS * 2 - 1);

    private static class VisibilityNode {
        public final int bitIndex;
        public final long packedBlockPos;
        public final int xOffset;
        public final int zOffset;
        public final float distance;
        /** position offsets that must be open are true, all others are false */
        public final BitSet visibilityMask;

        private VisibilityNode(long packedBlockPos, BitSet visibilityMask) {
            this.packedBlockPos = packedBlockPos;
            xOffset = PackedBlockPos.getX(packedBlockPos);
            zOffset = PackedBlockPos.getZ(packedBlockPos);
            bitIndex = getBitIndex(xOffset, zOffset);
            distance = (float) Math.sqrt(xOffset * xOffset + zOffset * zOffset);
            this.visibilityMask = visibilityMask;
        }
    }

    static {
        final LongArrayList circlePositions = Useful.fill2dCircleInPlaneXZ(RADIUS);

        final long origin = PackedBlockPos.pack(0, 0, 0);

        final ArrayList<VisibilityNode> result = new ArrayList<>();

        // start at 1 to skip origin
        for (int i = 1; i < circlePositions.size(); i++) {
            final BitSet visibilityMask = new BitSet(MASK_SIZE);

            final LongArrayList linePositions = Useful.line2dInPlaneXZ(origin, circlePositions.get(i));
            for (int l = 0; l < linePositions.size(); l++) {
                visibilityMask.set(getBitIndex(linePositions.get(l)));
            }

            result.add(new VisibilityNode(circlePositions.get(i), visibilityMask));
        }

        result.sort(new Comparator<VisibilityNode>() {
            @Override
            public int compare(@Nullable VisibilityNode o1, @Nullable VisibilityNode o2) {
                return Float.compare(o1.distance, o2.distance);
            }
        });

        VISIBILITY_NODES = result.toArray(new VisibilityNode[0]);

    }

    private static final int getBitIndex(long packedBlockPos) {
        return getBitIndex(PackedBlockPos.getX(packedBlockPos), PackedBlockPos.getZ(packedBlockPos));
    }

    private static final int getBitIndex(int x, int z) {
        return (x + RADIUS) * MASK_WIDTH + z + RADIUS;
    }

    /**
     * Returns true if space already contains lava or could contain lava. IOW, like
     * canLavaDisplace except returns true if contains lava height block.
     */
    public static boolean isLavaSpace(BlockState state) {
        return state.getBlock() == ModBlocks.lava_dynamic_height || LavaTerrainHelper.canLavaDisplace(state);

    }

    /**
     * Returns true if space already contains lava or could contain lava or did
     * contain lava at some point. Used to compute the native (non-flow) smoothed
     * terrain surface irrespective of any solidified lava. IOW, like
     * canLavaDisplace except returns true if contains lava height block.
     */
    public static boolean isOpenTerrainSpace(BlockState state) {
        final Block block = state.getBlock();
        return block == ModBlocks.lava_dynamic_height || block == ModBlocks.basalt_cool_dynamic_height || block == ModBlocks.basalt_cool_static_height
                || block == ModBlocks.basalt_cut || (block instanceof CoolingBasaltBlock && TerrainBlockHelper.isFlowHeight(state))
                || LavaTerrainHelper.canLavaDisplace(state);
    }

    /**
     * Want to avoid the synchronization penalty of pooled block pos.
     */
    private static ThreadLocal<BlockPos.Mutable> baseFlowPos = ThreadLocal.withInitial(BlockPos.Mutable::new);

    /**
     * Ideal height of flowing lava retained on base (non-flow) terrain at the given
     * location. Returned as fraction of 1 block.
     */
    public static float computeIdealBaseFlowHeight(World world, long originPackedPos) {
        final float NOT_FOUND = -1;
        float nearestRiseDistance = NOT_FOUND;
        float nearestFallDistance = NOT_FOUND;

        /**
         * updated each time we find a visible space as we expand from origin. If we go
         * 1 or more without a new visible space, walled in and can stop checking
         */
        float maxVisibleDistance = 0;

        final BitSet blockedPositions = new BitSet(MASK_SIZE);

        final BlockPos.Mutable targetPos = baseFlowPos.get();

        for (final VisibilityNode node : VISIBILITY_NODES) {

            final boolean isVisible = !node.visibilityMask.intersects(blockedPositions);

            if (!isVisible) {
                if (node.distance - maxVisibleDistance > 1)
                    break;
            } else {
                maxVisibleDistance = node.distance;
            }

            PackedBlockPos.unpackTo(PackedBlockPos.add(originPackedPos, node.packedBlockPos), targetPos);
            final boolean isOpen = isOpenTerrainSpace(world.getBlockState(targetPos));

            if (!isOpen) {
                blockedPositions.set(node.bitIndex);

                if (nearestRiseDistance == NOT_FOUND && isVisible) {
                    nearestRiseDistance = node.distance;
                }
            } else {
                // space is open, check for nearest drop if not already checked and position is
                // visible from origin
                if (nearestFallDistance == NOT_FOUND && isVisible && isOpenTerrainSpace(world.getBlockState(targetPos.offset(Direction.DOWN)))) {
                    nearestFallDistance = node.distance;

                }
            }

            if (nearestRiseDistance != NOT_FOUND && nearestFallDistance != NOT_FOUND)
                break;
        }

        if (nearestFallDistance == NOT_FOUND) {
            if (nearestRiseDistance == NOT_FOUND) {
                return 1;
            } else {
                return Math.max(1, 1.5F - nearestRiseDistance * INCREMENT);
            }

        } else if (nearestRiseDistance == NOT_FOUND) {
            return Math.min(1, 0.5F + (nearestFallDistance - 1) * INCREMENT);
        }

        else {
            return 1.5F - (nearestRiseDistance / (nearestRiseDistance + nearestFallDistance - 1));
        }

        // RP-D -> R=1, D=2, P=1.5-1/(1+2-1)=1;
        // R-PD -> R=2, D=1, p=1.5-2/(2+1-1)=0.5;

        // up in range, no down = min(1, 1.5. - dist * .1)
        // down in range, no up = max(1, .5 + (dist - 1) * .1)
        // up and down in range = 1.5 - (up dist / (combined dist - 1))
    }

    private static final Object2IntOpenHashMap<Material> MATERIAL_MAP = new Object2IntOpenHashMap<>();
    private static final int TOAST = -1;
    private static final int SAFE = 1;
    private static final int UNKNOWN = 0;

    //TODO: add more materials
    static {
        MATERIAL_MAP.put(Material.AIR, TOAST);
        MATERIAL_MAP.put(Material.SOIL, SAFE);
        MATERIAL_MAP.put(Material.WOOD, TOAST);
        MATERIAL_MAP.put(Material.STONE, SAFE);
        MATERIAL_MAP.put(Material.METAL, SAFE);
        MATERIAL_MAP.put(Material.REPAIR_STATION, SAFE);
        MATERIAL_MAP.put(Material.WATER, TOAST);
        MATERIAL_MAP.put(Material.LAVA, SAFE);
        MATERIAL_MAP.put(Material.LEAVES, TOAST);
        MATERIAL_MAP.put(Material.PLANT, TOAST);
        MATERIAL_MAP.put(Material.REPLACEABLE_PLANT, TOAST);
        MATERIAL_MAP.put(Material.SPONGE, TOAST);
        MATERIAL_MAP.put(Material.WOOL, TOAST);
        MATERIAL_MAP.put(Material.FIRE, TOAST);
        MATERIAL_MAP.put(Material.AGGREGATE, SAFE);
        MATERIAL_MAP.put(Material.SUPPORTED, TOAST);
        MATERIAL_MAP.put(Material.CARPET, TOAST);
        MATERIAL_MAP.put(Material.GLASS, TOAST);
        MATERIAL_MAP.put(Material.REDSTONE_LAMP, TOAST);
        MATERIAL_MAP.put(Material.TNT, TOAST);
        MATERIAL_MAP.put(Material.ORGANIC_PRODUCT, TOAST);
        MATERIAL_MAP.put(Material.DENSE_ICE, TOAST);
        MATERIAL_MAP.put(Material.SNOW_BLOCK, TOAST);
        MATERIAL_MAP.put(Material.SNOW_LAYER, TOAST);
        MATERIAL_MAP.put(Material.CACTUS, TOAST);
        //TODO: clean these up, probably some missing/wrong
//        MATERIAL_MAP.put(Material.CLAY, SAFE);
        MATERIAL_MAP.put(Material.GOURD, TOAST);
        MATERIAL_MAP.put(Material.EGG, SAFE);
        MATERIAL_MAP.put(Material.PORTAL, SAFE);
        MATERIAL_MAP.put(Material.CAKE, TOAST);
        MATERIAL_MAP.put(Material.COBWEB, TOAST);
        MATERIAL_MAP.put(Material.PISTON, TOAST);
        MATERIAL_MAP.put(Material.BARRIER, SAFE);
        MATERIAL_MAP.put(Material.STRUCTURE_VOID, TOAST);
    }

    public static boolean canLavaDisplace(BlockState state) {
        final Block block = state.getBlock();

        if (TerrainBlockHelper.isFlowBlock(state))
            return !TerrainBlockHelper.isFlowHeight(state);

        boolean isSafe = true;
        final Material material = state.getMaterial();
        final int materialOutcome = MATERIAL_MAP.getInt(material);
        if (materialOutcome == TOAST
                // check atrributes of unknown materials not found in material map
                || (materialOutcome == UNKNOWN
                        && (material.isReplaceable() || !material.blocksMovement() || !material.isSolid() || material.isLiquid() || material.isBurnable())))
            isSafe = false;

        return isSafe
                // safe unless specifically identified in config
                ? Configurator.Volcano.blocksDestroyedByLava.containsKey(block)
                : !Configurator.Volcano.blocksSafeFromLava.containsKey(block);
    }

//    public static String[] generateDefaultDisplaceableList()
//    {
//        ArrayList<String> results = new ArrayList<String>();
//
//        Iterator<Block> blocks = Block.REGISTRY.iterator();
//        while(blocks.hasNext())
//        {
//            Block b = blocks.next();
//            Material m = b.getMaterial(b.getDefaultState());
//            ResourceLocation r = b.getRegistryName();
//            if(r != null && (m.isLiquid() || m.isReplaceable()))
//            {
//                results.add(r.toString());
//            }
//        }
//
//        return results.toArray(new String[results.size()]);
//
//    }
}
