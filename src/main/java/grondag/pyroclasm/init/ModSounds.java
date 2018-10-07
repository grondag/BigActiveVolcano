package grondag.pyroclasm.init;

import grondag.pyroclasm.Pyroclasm;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
@ObjectHolder(Pyroclasm.MODID)
@SuppressWarnings("null")
public class ModSounds
{
    public static final SoundEvent lava_bubble = null;
    
    @SubscribeEvent
    public static void registerSounds(RegistryEvent.Register<SoundEvent> event) 
    {
        IForgeRegistry<SoundEvent> soundReg = event.getRegistry();
        
        registerSound("lava_bubble", soundReg);
    }
    
    private static void registerSound(String soundName, IForgeRegistry<SoundEvent> soundReg)
    {
        ResourceLocation loc = new ResourceLocation(Pyroclasm.MODID, soundName);
        soundReg.register(new SoundEvent(loc).setRegistryName(loc));
    }
}