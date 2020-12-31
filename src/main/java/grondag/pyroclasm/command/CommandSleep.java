package grondag.pyroclasm.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import grondag.fermion.simulator.Simulator;
import grondag.pyroclasm.Pyroclasm;
import grondag.pyroclasm.volcano.VolcanoManager;
import grondag.pyroclasm.volcano.VolcanoNode;

//TODO: redo w/ Brigadier
public class CommandSleep { //extends CommandBase {

//    @Override
//    public String getName() {
//        return "sleep";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 2;
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "commands.volcano.sleep.usage";
//    }

    public void execute(MinecraftServer server, ServerPlayerEntity sender, String[] args) { //throws CommandException {
        try {
            final VolcanoManager vm = Simulator.instance().getNode(VolcanoManager.class);
            final World world = sender.getEntityWorld();
            if (vm.dimension() == world.dimension.getType().getRawId()) {
                final VolcanoNode node = vm.nearestActiveVolcano(sender.getBlockPos());

                if (node == null) {
                    sender.sendMessage(new TranslatableText("commands.volcano.sleep.no_active_found"), true);
                } else {
                    node.sleep(true);
                    if (node.isActive()) {
                        sender.sendMessage(new TranslatableText("commands.volcano.sleep.fail"), true);
                    } else {
                        final BlockPos pos = node.blockPos();
                        sender.sendMessage(new TranslatableText("commands.volcano.sleep.success", pos.getX(), pos.getZ()), true);
                    }
                }
            }
        } catch (final Exception e) {
            Pyroclasm.LOG.error("Unhandled error putting volcano to sleep", e);
        }
    }

}
