package grondag.volcano.init;

import grondag.exotic_matter.model.BlockColorMapProvider;
import grondag.exotic_matter.model.BlockSubstance;
import grondag.exotic_matter.model.Chroma;
import grondag.exotic_matter.model.Hue;
import grondag.exotic_matter.model.Luminance;
import grondag.volcano.Configurator;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ModSubstances
{
    public static BlockSubstance BASALT = BlockSubstance.create("basalt", Configurator.SUBSTANCES.basalt, Material.ROCK, SoundType.STONE, BlockColorMapProvider.INSTANCE.getColorMap(Hue.COBALT, Chroma.NEUTRAL, Luminance.MEDIUM_DARK).ordinal);
    
    //can't use lava as material here - confuses the lava fluid renderer
    public static BlockSubstance VOLCANIC_LAVA = BlockSubstance.create("volcanic_lava", Configurator.SUBSTANCES.volcanicLava, Material.ROCK, SoundType.STONE, 0);

}
