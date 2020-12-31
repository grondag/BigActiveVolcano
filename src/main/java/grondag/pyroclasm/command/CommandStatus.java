package grondag.pyroclasm.command;

import java.util.Map;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.fermion.simulator.Simulator;
import grondag.pyroclasm.Pyroclasm;
import grondag.pyroclasm.volcano.VolcanoManager;
import grondag.pyroclasm.volcano.VolcanoNode;
import grondag.pyroclasm.volcano.VolcanoStage;

//TODO: redo w/ Brigadier
public class CommandStatus { //extends CommandBase {

//    @Override
//    public String getName() {
//        return "status";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 2;
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.volcano.status.usage";
//    }

    public void execute(MinecraftServer server, ServerPlayerEntity sender, String[] args) { //throws CommandException {
        try {
            final VolcanoManager vm = Simulator.instance().getNode(VolcanoManager.class);
            final World world = sender.getEntityWorld();
            if (vm.dimension() == world.dimension.getType().getRawId()) {
                final int myX = sender.getBlockPos().getX();
                final int myZ = sender.getBlockPos().getZ();
                sender.sendMessage(new TranslatableText("commands.volcano.status_header"), false);
                sender.sendMessage(new TranslatableText("========================================"), false);
                for (final Map.Entry<BlockPos, VolcanoNode> entry : vm.nearbyVolcanos(sender.getBlockPos()).entrySet()) {
                    final BlockPos pos = entry.getKey();
                    final int vX = pos.getX();
                    final int vZ = pos.getZ();
                    final int dX = vX - myX;
                    final int dZ = vZ - myZ;
                    final int dist = (int) Math.sqrt(dX * dX + dZ * dZ);
                    final VolcanoNode node = entry.getValue();
                    final String stage = node == null ? VolcanoStage.DORMANT.toString() : node.getStage().toString();
                    final long weight = node == null ? 0 : node.getWeight();
                    sender.sendMessage(new TranslatableText("commands.volcano.status", dist, vX, vZ, stage, weight), false);
                }
            } else {
                sender.sendMessage(new TranslatableText("commands.volcano.dimension_disabled"), false);
            }
        } catch (final Exception e) {
            Pyroclasm.LOG.error("Unexpected error", e);
        }
    }

}
