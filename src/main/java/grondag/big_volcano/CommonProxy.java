package grondag.big_volcano;

import javax.annotation.Nullable;

import grondag.acuity.api.IRenderPipeline;
import grondag.big_volcano.init.ModBlocks;
import grondag.big_volcano.init.ModEntities;
import grondag.big_volcano.init.ModRecipes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy 
{
	public void preInit(FMLPreInitializationEvent event) 
	{
		BigActiveVolcano.setLog(event.getModLog());
		Configurator.recalcDerived();
		ModEntities.preInit(event);
	}

	public void init(FMLInitializationEvent event) 
	{
		ModRecipes.init(event);
        ModBlocks.init(event);
	}

    public void postInit(FMLPostInitializationEvent event)
    {
        
    }
    
    public void spawnLavaBlobParticle(World worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float radius)
    {
        //client only
    }

    @Nullable
    public IRenderPipeline lavaPipeline()
    {
        //populated client only
        return null;
    }
}
