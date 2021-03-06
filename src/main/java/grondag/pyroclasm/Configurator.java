package grondag.pyroclasm;

import java.util.IdentityHashMap;

import grondag.pyroclasm.fluidsim.LavaCell;
import grondag.xm.relics.BlockHarvestTool;
import grondag.xm.relics.SubstanceConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

//@LangKey("pyroclasm.config.general")
//@Config(modid = Pyroclasm.MODID, type = Type.INSTANCE)
public class Configurator {
    public static void recalcDerived() {
        Volcano.recalcDerived();
    }

    public static void recalcBlocks() {
        Volcano.recalcBlocks();
    }

    ////////////////////////////////////////////////////
    // RENDER
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.render")
//    @Comment("Visual Appearance.")
    public static Render RENDER = new Render();

    public static class Render {
//        @LangKey("pyroclasm.config.large_texture_scale")
//        @Comment({"If true, basalt and lava textures span 32 blocks, resulting in large cracks.",
//            "If false, same textures are scale to span 16 blocks. Purely an aesthetic choice. ",
//            "HD texture pack is recommended when set to true. Client-side only."})
//        @RequiresMcRestart
        public boolean largeTextureScale = false;
    }

    ////////////////////////////////////////////////////
    // SUBSTANCES
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.substance")
//    @Comment("Volcano material properties.")
    public static Substances SUBSTANCES = new Substances();

    public static class Substances {
//        @LangKey("pyroclasm.config.basalt_material")
        public SubstanceConfig basalt = new SubstanceConfig(2, BlockHarvestTool.PICK, 1, 10, 1.0);

//        @LangKey("pyroclasm.config.lava_material")
        public SubstanceConfig volcanicLava = new SubstanceConfig(-1, BlockHarvestTool.SHOVEL, 3, 2000, 0.75).setBurning().withPathNodeType(PathNodeType.LAVA);

//        @LangKey("pyroclasm.config.lava_light_level")
//        @Comment({"Light level emitted by fully-molten volcanic lava.",
//            "Larger numbers can harm performance by triggering many lighting updates.",
//            "The default is a balance between performance and aestheics.",
//            "For best performance, choose zero.",
//            "Note: changes will not be visible until chunks containing lava are relit due to other events."})
//        @RangeInt(min = 0, max = 15)
        public int lavaLightLevel = 0;

//        @LangKey("pyroclasm.config.destroyed_blocks")
//        @Comment({"Blocks that will be destroyed on contact by volcanic lava.",
//            "Blocks should be listed in modname:blockname format.",
//            "At this time, metadata and NBT values cannot be specified.",
//            "Some of the modded blocks listed would be destroyed by default because of material.",
//            "I added them to avoid testing if they actually use the material one would expect."})
        public String[] blocksDestroyedByVolcanicLava = { "minecraft:sponge", "minecraft:stone_pressure_plate", "minecraft:ice", "minecraft:snow",
                "minecraft:cactus", "minecraft:pumpkin", "minecraft:lit_pumpkin", "minecraft:cake", "minecraft:stained_glass", "minecraft:glass_pane",
                "minecraft:melon_block", "minecraft:redstone_lamp", "minecraft:lit_redstone_lamp", "minecraft:light_weighted_pressure_plate",
                "minecraft:heavy_weighted_pressure_plate", "minecraft:stained_glass_pane", "minecraft:slime", "minecraft:hay_block", "minecraft:coal_block",
                "minecraft:packed_ice", "minecraft:frosted_ice", "actuallyadditions:block_atomic_reconstructor", "actuallyadditions:block_battery_box",
                "actuallyadditions:block_bio_reactor", "actuallyadditions:block_black_lotus", "actuallyadditions:block_breaker",
                "actuallyadditions:block_canola", "actuallyadditions:block_canola_oil", "actuallyadditions:block_canola_press",
                "actuallyadditions:block_coal_generator", "actuallyadditions:block_coffee", "actuallyadditions:block_coffee_machine",
                "actuallyadditions:block_colored_lamp", "actuallyadditions:block_colored_lamp_on", "actuallyadditions:block_compost",
                "actuallyadditions:block_crystal_oil", "actuallyadditions:block_directional_breaker", "actuallyadditions:block_display_stand",
                "actuallyadditions:block_dropper", "actuallyadditions:block_empowered_oil", "actuallyadditions:block_empowerer",
                "actuallyadditions:block_energizer", "actuallyadditions:block_enervator", "actuallyadditions:block_farmer", "actuallyadditions:block_feeder",
                "actuallyadditions:block_fermenting_barrel", "actuallyadditions:block_firework_box", "actuallyadditions:block_fishing_net",
                "actuallyadditions:block_flax", "actuallyadditions:block_fluid_collector", "actuallyadditions:block_fluid_placer",
                "actuallyadditions:block_furnace_double", "actuallyadditions:block_furnace_solar", "actuallyadditions:block_giant_chest",
                "actuallyadditions:block_giant_chest_large", "actuallyadditions:block_giant_chest_medium", "actuallyadditions:block_greenhouse_glass",
                "actuallyadditions:block_grinder", "actuallyadditions:block_grinder_double", "actuallyadditions:block_heat_collector",
                "actuallyadditions:block_inputter", "actuallyadditions:block_inputter_advanced", "actuallyadditions:block_item_repairer",
                "actuallyadditions:block_item_viewer", "actuallyadditions:block_item_viewer_hopping", "actuallyadditions:block_lamp_powerer",
                "actuallyadditions:block_laser_relay", "actuallyadditions:block_laser_relay_advanced", "actuallyadditions:block_laser_relay_extreme",
                "actuallyadditions:block_laser_relay_fluids", "actuallyadditions:block_laser_relay_item", "actuallyadditions:block_laser_relay_item_whitelist",
                "actuallyadditions:block_lava_factory_controller", "actuallyadditions:block_leaf_generator", "actuallyadditions:block_miner",
                "actuallyadditions:block_misc", "actuallyadditions:block_oil_generator", "actuallyadditions:block_phantom_booster",
                "actuallyadditions:block_phantom_breaker", "actuallyadditions:block_phantom_energyface", "actuallyadditions:block_phantom_liquiface",
                "actuallyadditions:block_phantom_placer", "actuallyadditions:block_phantom_redstoneface", "actuallyadditions:block_phantomface",
                "actuallyadditions:block_placer", "actuallyadditions:block_player_interface", "actuallyadditions:block_ranged_collector",
                "actuallyadditions:block_refined_canola_oil", "actuallyadditions:block_rice", "actuallyadditions:block_shock_suppressor",
                "actuallyadditions:block_smiley_cloud", "actuallyadditions:block_tiny_torch", "actuallyadditions:block_treasure_chest",
                "actuallyadditions:block_wild", "actuallyadditions:block_xp_solidifier", "advancedrocketry:advrocketmotor", "advancedrocketry:airlock_door",
                "advancedrocketry:alienleaves", "advancedrocketry:aliensapling", "advancedrocketry:alienwood", "advancedrocketry:altitudecontroller",
                "advancedrocketry:arcfurnace", "advancedrocketry:astrobed", "advancedrocketry:beacon", "advancedrocketry:biomescanner",
                "advancedrocketry:blocklens", "advancedrocketry:charcoallog", "advancedrocketry:chemicalreactor", "advancedrocketry:circlelight",
                "advancedrocketry:crystal", "advancedrocketry:crystallizer", "advancedrocketry:cuttingmachine", "advancedrocketry:datapipe",
                "advancedrocketry:deployablerocketbuilder", "advancedrocketry:drill", "advancedrocketry:electricmushroom", "advancedrocketry:electrolyser",
                "advancedrocketry:energypipe", "advancedrocketry:forcefieldprojector", "advancedrocketry:fuelingstation", "advancedrocketry:fueltank",
                "advancedrocketry:gravitycontroller", "advancedrocketry:gravitymachine", "advancedrocketry:guidancecomputer", "advancedrocketry:hydrogenfluid",
                "advancedrocketry:intake", "advancedrocketry:lathe", "advancedrocketry:lightsource", "advancedrocketry:liquidpipe",
                "advancedrocketry:liquidtank", "advancedrocketry:loader", "advancedrocketry:microwavereciever", "advancedrocketry:monitoringstation",
                "advancedrocketry:nitrogenfluid", "advancedrocketry:observatory", "advancedrocketry:orientationcontroller", "advancedrocketry:oxygencharger",
                "advancedrocketry:oxygendetection", "advancedrocketry:oxygenfluid", "advancedrocketry:oxygenscrubber", "advancedrocketry:oxygenvent",
                "advancedrocketry:pipesealer", "advancedrocketry:planetanalyser", "advancedrocketry:planetholoselector", "advancedrocketry:planetselector",
                "advancedrocketry:planks", "advancedrocketry:platepress", "advancedrocketry:precisionassemblingmachine", "advancedrocketry:quartzcrucible",
                "advancedrocketry:railgun", "advancedrocketry:rocketbuilder", "advancedrocketry:rocketfuel", "advancedrocketry:rocketmotor",
                "advancedrocketry:rollingmachine", "advancedrocketry:satellitebuilder", "advancedrocketry:satellitecontrolcenter", "advancedrocketry:sawblade",
                "advancedrocketry:seat", "advancedrocketry:solargenerator", "advancedrocketry:solarpanel", "advancedrocketry:spaceelevatorcontroller",
                "advancedrocketry:spacelaser", "advancedrocketry:stationbuilder", "advancedrocketry:stationmarker", "advancedrocketry:suitworkstation",
                "advancedrocketry:terraformer", "advancedrocketry:thermitetorch", "advancedrocketry:unlittorch", "advancedrocketry:warpcore",
                "advancedrocketry:warpmonitor", "advancedrocketry:wirelesstransciever", "ae2stuff:encoder", "ae2stuff:grower", "ae2stuff:inscriber",
                "ae2stuff:wireless", "appliedenergistics2:cable_bus", "appliedenergistics2:cell_workbench", "appliedenergistics2:charger",
                "appliedenergistics2:chest", "appliedenergistics2:condenser", "appliedenergistics2:controller", "appliedenergistics2:crafting_accelerator",
                "appliedenergistics2:crafting_monitor", "appliedenergistics2:crafting_storage_16k", "appliedenergistics2:crafting_storage_1k",
                "appliedenergistics2:crafting_storage_4k", "appliedenergistics2:crafting_storage_64k", "appliedenergistics2:crafting_unit",
                "appliedenergistics2:crank", "appliedenergistics2:creative_energy_cell", "appliedenergistics2:drive", "appliedenergistics2:energy_acceptor",
                "appliedenergistics2:grindstone", "appliedenergistics2:inscriber", "appliedenergistics2:interface", "appliedenergistics2:io_port",
                "appliedenergistics2:light_detector", "appliedenergistics2:matrix_frame", "appliedenergistics2:molecular_assembler",
                "appliedenergistics2:paint", "appliedenergistics2:quantum_link", "appliedenergistics2:quantum_ring", "appliedenergistics2:quartz_fixture",
                "appliedenergistics2:quartz_growth_accelerator", "appliedenergistics2:quartz_pillar_double_slab", "appliedenergistics2:quartz_pillar_slab",
                "appliedenergistics2:quartz_pillar_stairs", "appliedenergistics2:security_station", "appliedenergistics2:spatial_io_port",
                "appliedenergistics2:spatial_pylon", "appliedenergistics2:tiny_tnt", "appliedenergistics2:wireless_access_point", "bibliocraft:armorstand",
                "bibliocraft:bell", "bibliocraft:bookcase", "bibliocraft:bookcasecreative", "bibliocraft:case", "bibliocraft:clipboard", "bibliocraft:clock",
                "bibliocraft:cookiejar", "bibliocraft:desk", "bibliocraft:dinnerplate", "bibliocraft:discrack", "bibliocraft:fancysign",
                "bibliocraft:fancyworkbench", "bibliocraft:framedchest", "bibliocraft:furniturepaneler", "bibliocraft:label", "bibliocraft:lampgold",
                "bibliocraft:lampiron", "bibliocraft:lanterngold", "bibliocraft:lanterniron", "bibliocraft:mapframe", "bibliocraft:markerpole",
                "bibliocraft:paintingframeborderless", "bibliocraft:paintingframefancy", "bibliocraft:paintingframeflat", "bibliocraft:paintingframemiddle",
                "bibliocraft:paintingframesimple", "bibliocraft:paintingpress", "bibliocraft:potionshelf", "bibliocraft:printingpress", "bibliocraft:seat",
                "bibliocraft:shelf", "bibliocraft:table", "bibliocraft:toolrack", "bibliocraft:typesettingtable", "bibliocraft:typewriter", "botany:flower",
                "botany:machine", "botany:plant", "botany:stained", "careerbees:alveary_frame", "careerbees:flower_pedastal", "charset:logic_gate",
                "chisel:auto_chisel", "chisel:block_charcoal", "chisel:block_charcoal1", "chisel:block_charcoal2", "chisel:block_coal", "chisel:block_coal1",
                "chisel:block_coal2", "chisel:block_coal_coke", "chisel:block_coal_coke1", "chisel:block_coal_coke2", "chisel:blocklead",
                "chisel:bookshelf_acacia", "chisel:bookshelf_birch", "chisel:bookshelf_darkoak", "chisel:bookshelf_jungle", "chisel:bookshelf_oak",
                "chisel:bookshelf_spruce", "chisel:carpet_black", "chisel:carpet_blue", "chisel:carpet_brown", "chisel:carpet_cyan", "chisel:carpet_gray",
                "chisel:carpet_green", "chisel:carpet_lightblue", "chisel:carpet_lightgray", "chisel:carpet_lime", "chisel:carpet_magenta",
                "chisel:carpet_orange", "chisel:carpet_pink", "chisel:carpet_purple", "chisel:carpet_red", "chisel:carpet_white", "chisel:carpet_yellow",
                "chisel:cloud", "chisel:glass", "chisel:glassdyedblack", "chisel:glassdyedblue", "chisel:glassdyedbrown", "chisel:glassdyedcyan",
                "chisel:glassdyedgray", "chisel:glassdyedgreen", "chisel:glassdyedlightblue", "chisel:glassdyedlightgray", "chisel:glassdyedlime",
                "chisel:glassdyedmagenta", "chisel:glassdyedorange", "chisel:glassdyedpink", "chisel:glassdyedpurple", "chisel:glassdyedred",
                "chisel:glassdyedwhite", "chisel:glassdyedyellow", "chisel:glowstone", "chisel:glowstone1", "chisel:glowstone2", "chisel:ice", "chisel:ice1",
                "chisel:ice2", "chisel:icepillar", "chisel:lapis", "chisel:paper", "chisel:planks-acacia", "chisel:planks-birch", "chisel:planks-dark-oak",
                "chisel:planks-jungle", "chisel:planks-oak", "chisel:planks-spruce", "chisel:redstone", "chisel:redstone1", "chisel:wool_black",
                "chisel:wool_blue", "chisel:wool_brown", "chisel:wool_cyan", "chisel:wool_gray", "chisel:wool_green", "chisel:wool_lightblue",
                "chisel:wool_lightgray", "chisel:wool_lime", "chisel:wool_magenta", "chisel:wool_orange", "chisel:wool_pink", "chisel:wool_purple",
                "chisel:wool_red", "chisel:wool_white", "chisel:wool_yellow", "chiselsandbits:bittank", "chiselsandbits:chiseled_cloth",
                "chiselsandbits:chiseled_fluid", "chiselsandbits:chiseled_glass", "chiselsandbits:chiseled_ice", "chiselsandbits:chiseled_leaves",
                "chiselsandbits:chiseled_packedice", "chiselsandbits:chiseled_snow", "chiselsandbits:chiseled_wood", "cookingforblockheads:cabinet",
                "cookingforblockheads:cooking_table", "cookingforblockheads:corner", "cookingforblockheads:counter", "cookingforblockheads:cow_jar",
                "cookingforblockheads:cutting_board", "cookingforblockheads:fridge", "cookingforblockheads:fruit_basket", "cookingforblockheads:kitchen_floor",
                "cookingforblockheads:milk_jar", "cookingforblockheads:oven", "cookingforblockheads:sink", "cookingforblockheads:spice_rack",
                "cookingforblockheads:toaster", "cookingforblockheads:tool_rack", "darkutils:anti_slime", "darkutils:ender_hopper",
                "darkutils:ender_pearl_hopper", "darkutils:ender_tether", "darkutils:fake_tnt", "darkutils:filter", "darkutils:filter_inverted",
                "darkutils:grate", "darkutils:monolith", "darkutils:pearl_block", "darkutils:slime_dyed", "darkutils:sneaky", "darkutils:sneaky_bedrock",
                "darkutils:sneaky_button", "darkutils:sneaky_ghost", "darkutils:sneaky_lever", "darkutils:sneaky_obsidian", "darkutils:sneaky_plate",
                "darkutils:sneaky_torch", "darkutils:timer", "darkutils:trap_anchor", "darkutils:trap_move", "darkutils:trap_move_fast",
                "darkutils:trap_move_hyper", "darkutils:trap_tile", "darkutils:update_detector", "efab:autocrafting_monitor", "efab:base", "efab:boiler",
                "efab:crafter", "efab:gearbox", "efab:grid", "efab:monitor", "efab:pipes", "efab:power_optimizer", "efab:processor", "efab:rfcontrol",
                "efab:rfstorage", "efab:rfstorage_advanced", "efab:steamengine", "efab:storage", "efab:tank", "efab:tank2", "elulib:multiblock_slave_empty",
                "elulib:multiblock_slave_modular", "elulib:test", "enderstorage:ender_storage", "engineersdoors:door_treated",
                "engineersdoors:fencegate_aluminium", "engineersdoors:fencegate_treated", "engineersdoors:trapdoor_treated", "engineersworkshop:blocktable",
                "environmentaltech:laser_core", "environmentaltech:laser_lens", "environmentaltech:laser_lens_colored", "environmentaltech:laser_lens_crystal",
                "environmentaltech:modifier_accuracy", "environmentaltech:modifier_null", "environmentaltech:modifier_speed", "extrabees:alveary",
                "extrabees:ectoplasm", "extrabees:hive", "extracells:certustank", "extracells:ecbaseblock", "extracells:fluidcrafter", "extracells:fluidfiller",
                "extracells:hardmedrive", "extracells:vibrantchamberfluid", "extratrees:binnie.beer.ale", "extratrees:binnie.beer.corn",
                "extratrees:binnie.beer.lager", "extratrees:binnie.beer.rye", "extratrees:binnie.beer.stout", "extratrees:binnie.beer.wheat",
                "extratrees:binnie.brandy.apple", "extratrees:binnie.brandy.apricot", "extratrees:binnie.brandy.cherry", "extratrees:binnie.brandy.citrus",
                "extratrees:binnie.brandy.elderberry", "extratrees:binnie.brandy.fruit", "extratrees:binnie.brandy.grape", "extratrees:binnie.brandy.pear",
                "extratrees:binnie.brandy.plum", "extratrees:binnie.cider.apple", "extratrees:binnie.cider.peach", "extratrees:binnie.ciderpear",
                "extratrees:binnie.fermented.potatoes", "extratrees:binnie.juice", "extratrees:binnie.juice.apple", "extratrees:binnie.juice.apricot",
                "extratrees:binnie.juice.banana", "extratrees:binnie.juice.carrot", "extratrees:binnie.juice.cherry", "extratrees:binnie.juice.cranberry",
                "extratrees:binnie.juice.elderberry", "extratrees:binnie.juice.grapefruit", "extratrees:binnie.juice.lemon", "extratrees:binnie.juice.lime",
                "extratrees:binnie.juice.olive", "extratrees:binnie.juice.orange", "extratrees:binnie.juice.peach", "extratrees:binnie.juice.pear",
                "extratrees:binnie.juice.pineapple", "extratrees:binnie.juice.plum", "extratrees:binnie.juice.red.grape", "extratrees:binnie.juice.tomato",
                "extratrees:binnie.juice.white.grape", "extratrees:binnie.latex", "extratrees:binnie.liqueur.almond", "extratrees:binnie.liqueur.anise",
                "extratrees:binnie.liqueur.banana", "extratrees:binnie.liqueur.blackberry", "extratrees:binnie.liqueur.blackcurrant",
                "extratrees:binnie.liqueur.cherry", "extratrees:binnie.liqueur.chocolate", "extratrees:binnie.liqueur.cinnamon",
                "extratrees:binnie.liqueur.coffee", "extratrees:binnie.liqueur.hazelnut", "extratrees:binnie.liqueur.herbal", "extratrees:binnie.liqueur.lemon",
                "extratrees:binnie.liqueur.melon", "extratrees:binnie.liqueur.mint", "extratrees:binnie.liqueur.orange", "extratrees:binnie.liqueur.peach",
                "extratrees:binnie.liqueur.raspberry", "extratrees:binnie.liquor.apple", "extratrees:binnie.liquor.apricot", "extratrees:binnie.liquor.cherry",
                "extratrees:binnie.liquor.elderberry", "extratrees:binnie.liquor.fruit", "extratrees:binnie.liquor.pear", "extratrees:binnie.mash.corn",
                "extratrees:binnie.mash.grain", "extratrees:binnie.mash.rye", "extratrees:binnie.mash.wheat", "extratrees:binnie.resin",
                "extratrees:binnie.rum.dark", "extratrees:binnie.rum.white", "extratrees:binnie.sap", "extratrees:binnie.spirit.gin",
                "extratrees:binnie.spirit.neutral", "extratrees:binnie.spirit.sugarcane", "extratrees:binnie.tequila", "extratrees:binnie.turpentine",
                "extratrees:binnie.vodka", "extratrees:binnie.whiskey", "extratrees:binnie.whiskey.corn", "extratrees:binnie.whiskey.rye",
                "extratrees:binnie.whiskey.wheat", "extratrees:binnie.wine.agave", "extratrees:binnie.wine.apricot", "extratrees:binnie.wine.banana",
                "extratrees:binnie.wine.carrot", "extratrees:binnie.wine.cherry", "extratrees:binnie.wine.citrus", "extratrees:binnie.wine.cranberry",
                "extratrees:binnie.wine.elderberry", "extratrees:binnie.wine.fortified", "extratrees:binnie.wine.pineapple", "extratrees:binnie.wine.plum",
                "extratrees:binnie.wine.red", "extratrees:binnie.wine.sparkling", "extratrees:binnie.wine.tomato", "extratrees:binnie.wine.white",
                "extratrees:carpentry", "extratrees:carpentrypanel", "extratrees:doors.alder", "extratrees:doors.apple", "extratrees:doors.ash",
                "extratrees:doors.banana", "extratrees:doors.beech", "extratrees:doors.box", "extratrees:doors.brazilwood", "extratrees:doors.butternut",
                "extratrees:doors.cedar", "extratrees:doors.cypress", "extratrees:doors.elder", "extratrees:doors.elm", "extratrees:doors.eucalyptus",
                "extratrees:doors.fig", "extratrees:doors.fir", "extratrees:doors.gingko", "extratrees:doors.hawthorn", "extratrees:doors.hazel",
                "extratrees:doors.hemlock", "extratrees:doors.hickory", "extratrees:doors.holly", "extratrees:doors.hornbeam", "extratrees:doors.iroko",
                "extratrees:doors.locust", "extratrees:doors.logwood", "extratrees:doors.maclura", "extratrees:doors.olive", "extratrees:doors.pear",
                "extratrees:doors.pinkivory", "extratrees:doors.purpleheart", "extratrees:doors.rosewood", "extratrees:doors.rowan",
                "extratrees:doors.sweetgum", "extratrees:doors.syzgium", "extratrees:doors.whitebeam", "extratrees:doors.yew", "extratrees:fence.gates.alder",
                "extratrees:fence.gates.apple", "extratrees:fence.gates.ash", "extratrees:fence.gates.banana", "extratrees:fence.gates.beech",
                "extratrees:fence.gates.box", "extratrees:fence.gates.brazilwood", "extratrees:fence.gates.butternut", "extratrees:fence.gates.cedar",
                "extratrees:fence.gates.cypress", "extratrees:fence.gates.elder", "extratrees:fence.gates.elm", "extratrees:fence.gates.eucalyptus",
                "extratrees:fence.gates.fig", "extratrees:fence.gates.fir", "extratrees:fence.gates.fireproof.alder", "extratrees:fence.gates.fireproof.apple",
                "extratrees:fence.gates.fireproof.ash", "extratrees:fence.gates.fireproof.banana", "extratrees:fence.gates.fireproof.beech",
                "extratrees:fence.gates.fireproof.box", "extratrees:fence.gates.fireproof.brazilwood", "extratrees:fence.gates.fireproof.butternut",
                "extratrees:fence.gates.fireproof.cedar", "extratrees:fence.gates.fireproof.cypress", "extratrees:fence.gates.fireproof.elder",
                "extratrees:fence.gates.fireproof.elm", "extratrees:fence.gates.fireproof.eucalyptus", "extratrees:fence.gates.fireproof.fig",
                "extratrees:fence.gates.fireproof.fir", "extratrees:fence.gates.fireproof.gingko", "extratrees:fence.gates.fireproof.hawthorn",
                "extratrees:fence.gates.fireproof.hazel", "extratrees:fence.gates.fireproof.hemlock", "extratrees:fence.gates.fireproof.hickory",
                "extratrees:fence.gates.fireproof.holly", "extratrees:fence.gates.fireproof.hornbeam", "extratrees:fence.gates.fireproof.iroko",
                "extratrees:fence.gates.fireproof.locust", "extratrees:fence.gates.fireproof.logwood", "extratrees:fence.gates.fireproof.maclura",
                "extratrees:fence.gates.fireproof.olive", "extratrees:fence.gates.fireproof.pear", "extratrees:fence.gates.fireproof.pinkivory",
                "extratrees:fence.gates.fireproof.purpleheart", "extratrees:fence.gates.fireproof.rosewood", "extratrees:fence.gates.fireproof.rowan",
                "extratrees:fence.gates.fireproof.sweetgum", "extratrees:fence.gates.fireproof.syzgium", "extratrees:fence.gates.fireproof.whitebeam",
                "extratrees:fence.gates.fireproof.yew", "extratrees:fence.gates.gingko", "extratrees:fence.gates.hawthorn", "extratrees:fence.gates.hazel",
                "extratrees:fence.gates.hemlock", "extratrees:fence.gates.hickory", "extratrees:fence.gates.holly", "extratrees:fence.gates.hornbeam",
                "extratrees:fence.gates.iroko", "extratrees:fence.gates.locust", "extratrees:fence.gates.logwood", "extratrees:fence.gates.maclura",
                "extratrees:fence.gates.olive", "extratrees:fence.gates.pear", "extratrees:fence.gates.pinkivory", "extratrees:fence.gates.purpleheart",
                "extratrees:fence.gates.rosewood", "extratrees:fence.gates.rowan", "extratrees:fence.gates.sweetgum", "extratrees:fence.gates.syzgium",
                "extratrees:fence.gates.whitebeam", "extratrees:fence.gates.yew", "extratrees:fences.0", "extratrees:fences.1", "extratrees:fences.2",
                "extratrees:fences.fireproof.0", "extratrees:fences.fireproof.1", "extratrees:fences.fireproof.2", "extratrees:hops",
                "extratrees:leaves.decorative.0", "extratrees:leaves.decorative.1", "extratrees:leaves.decorative.2", "extratrees:leaves.decorative.3",
                "extratrees:leaves.decorative.4", "extratrees:leaves.decorative.5", "extratrees:leaves.decorative.6", "extratrees:leaves.default.0",
                "extratrees:leaves.default.1", "extratrees:leaves.default.10", "extratrees:leaves.default.11", "extratrees:leaves.default.12",
                "extratrees:leaves.default.13", "extratrees:leaves.default.14", "extratrees:leaves.default.15", "extratrees:leaves.default.16",
                "extratrees:leaves.default.17", "extratrees:leaves.default.18", "extratrees:leaves.default.19", "extratrees:leaves.default.2",
                "extratrees:leaves.default.20", "extratrees:leaves.default.21", "extratrees:leaves.default.22", "extratrees:leaves.default.23",
                "extratrees:leaves.default.24", "extratrees:leaves.default.3", "extratrees:leaves.default.4", "extratrees:leaves.default.5",
                "extratrees:leaves.default.6", "extratrees:leaves.default.7", "extratrees:leaves.default.8", "extratrees:leaves.default.9", "extratrees:logs.0",
                "extratrees:logs.1", "extratrees:logs.2", "extratrees:logs.3", "extratrees:logs.4", "extratrees:logs.5", "extratrees:logs.6",
                "extratrees:logs.7", "extratrees:logs.8", "extratrees:logs.9", "extratrees:logs.fireproof.0", "extratrees:logs.fireproof.1",
                "extratrees:logs.fireproof.2", "extratrees:logs.fireproof.3", "extratrees:logs.fireproof.4", "extratrees:logs.fireproof.5",
                "extratrees:logs.fireproof.6", "extratrees:logs.fireproof.7", "extratrees:logs.fireproof.8", "extratrees:logs.fireproof.9",
                "extratrees:machine", "extratrees:multifence", "extratrees:planks.0", "extratrees:planks.1", "extratrees:planks.2",
                "extratrees:planks.fireproof.0", "extratrees:planks.fireproof.1", "extratrees:planks.fireproof.2", "extratrees:shrub_log", "extratrees:slabs.0",
                "extratrees:slabs.1", "extratrees:slabs.2", "extratrees:slabs.3", "extratrees:slabs.4", "extratrees:slabs.double.0",
                "extratrees:slabs.double.1", "extratrees:slabs.double.2", "extratrees:slabs.double.3", "extratrees:slabs.double.4",
                "extratrees:slabs.fireproof.0", "extratrees:slabs.fireproof.1", "extratrees:slabs.fireproof.2", "extratrees:slabs.fireproof.3",
                "extratrees:slabs.fireproof.4", "extratrees:slabs.fireproof.double.0", "extratrees:slabs.fireproof.double.1",
                "extratrees:slabs.fireproof.double.2", "extratrees:slabs.fireproof.double.3", "extratrees:slabs.fireproof.double.4", "extratrees:stainedglass",
                "extratrees:stairs.alder", "extratrees:stairs.apple", "extratrees:stairs.ash", "extratrees:stairs.banana", "extratrees:stairs.beech",
                "extratrees:stairs.box", "extratrees:stairs.brazilwood", "extratrees:stairs.butternut", "extratrees:stairs.cedar", "extratrees:stairs.cypress",
                "extratrees:stairs.elder", "extratrees:stairs.elm", "extratrees:stairs.eucalyptus", "extratrees:stairs.fig", "extratrees:stairs.fir",
                "extratrees:stairs.fireproof.alder", "extratrees:stairs.fireproof.apple", "extratrees:stairs.fireproof.ash",
                "extratrees:stairs.fireproof.banana", "extratrees:stairs.fireproof.beech", "extratrees:stairs.fireproof.box",
                "extratrees:stairs.fireproof.brazilwood", "extratrees:stairs.fireproof.butternut", "extratrees:stairs.fireproof.cedar",
                "extratrees:stairs.fireproof.cypress", "extratrees:stairs.fireproof.elder", "extratrees:stairs.fireproof.elm",
                "extratrees:stairs.fireproof.eucalyptus", "extratrees:stairs.fireproof.fig", "extratrees:stairs.fireproof.fir",
                "extratrees:stairs.fireproof.gingko", "extratrees:stairs.fireproof.hawthorn", "extratrees:stairs.fireproof.hazel",
                "extratrees:stairs.fireproof.hemlock", "extratrees:stairs.fireproof.hickory", "extratrees:stairs.fireproof.holly",
                "extratrees:stairs.fireproof.hornbeam", "extratrees:stairs.fireproof.iroko", "extratrees:stairs.fireproof.locust",
                "extratrees:stairs.fireproof.logwood", "extratrees:stairs.fireproof.maclura", "extratrees:stairs.fireproof.olive",
                "extratrees:stairs.fireproof.pear", "extratrees:stairs.fireproof.pinkivory", "extratrees:stairs.fireproof.purpleheart",
                "extratrees:stairs.fireproof.rosewood", "extratrees:stairs.fireproof.rowan", "extratrees:stairs.fireproof.sweetgum",
                "extratrees:stairs.fireproof.syzgium", "extratrees:stairs.fireproof.whitebeam", "extratrees:stairs.fireproof.yew", "extratrees:stairs.gingko",
                "extratrees:stairs.hawthorn", "extratrees:stairs.hazel", "extratrees:stairs.hemlock", "extratrees:stairs.hickory", "extratrees:stairs.holly",
                "extratrees:stairs.hornbeam", "extratrees:stairs.iroko", "extratrees:stairs.locust", "extratrees:stairs.logwood", "extratrees:stairs.maclura",
                "extratrees:stairs.olive", "extratrees:stairs.pear", "extratrees:stairs.pinkivory", "extratrees:stairs.purpleheart",
                "extratrees:stairs.rosewood", "extratrees:stairs.rowan", "extratrees:stairs.sweetgum", "extratrees:stairs.syzgium",
                "extratrees:stairs.whitebeam", "extratrees:stairs.yew", "extrautils2:analogcrafter", "extrautils2:angelblock", "extrautils2:crafter",
                "extrautils2:decorativeglass", "extrautils2:decorativesolidwood", "extrautils2:drum", "extrautils2:indexer", "extrautils2:interactionproxy",
                "extrautils2:klein", "extrautils2:largishchest", "extrautils2:machine", "extrautils2:miner", "extrautils2:minichest",
                "extrautils2:passivegenerator", "extrautils2:pipe", "extrautils2:pipe_1", "extrautils2:pipe_2", "extrautils2:pipe_3", "extrautils2:playerchest",
                "extrautils2:powerbattery", "extrautils2:poweroverload", "extrautils2:powertransmitter", "extrautils2:rainbowgenerator",
                "extrautils2:redstoneclock", "extrautils2:redstonelantern", "extrautils2:resonator", "extrautils2:scanner", "extrautils2:screen",
                "extrautils2:simpledecorative", "extrautils2:soundmuffler", "extrautils2:spike_wood", "extrautils2:spotlight", "extrautils2:teleporter",
                "extrautils2:terraformer", "extrautils2:transferholder", "extrautils2:trashcan", "extrautils2:trashcanenergy", "extrautils2:trashcanfluid",
                "extrautils2:trashchest", "extrautils2:user", "fluxnetworks:fluxcontroller", "fluxnetworks:fluxplug", "fluxnetworks:fluxpoint",
                "fluxnetworks:fluxstorage", "fluxnetworks:gargantuanfluxstorage", "fluxnetworks:herculeanfluxstorage", "forestry:alveary.fan",
                "forestry:alveary.heater", "forestry:alveary.hygro", "forestry:alveary.plain", "forestry:alveary.sieve", "forestry:alveary.stabiliser",
                "forestry:alveary.swarmer", "forestry:analyzer", "forestry:apiary", "forestry:arboretum", "forestry:bee_chest", "forestry:bee_combs_0",
                "forestry:bee_combs_1", "forestry:bee_house", "forestry:beehives", "forestry:bog_earth", "forestry:bottler", "forestry:butterfly_chest",
                "forestry:candle", "forestry:carpenter", "forestry:centrifuge", "forestry:charcoal", "forestry:climatiser", "forestry:cocoon",
                "forestry:cocoon.solid", "forestry:database", "forestry:doors.acacia", "forestry:doors.balsa", "forestry:doors.baobab", "forestry:doors.cherry",
                "forestry:doors.chestnut", "forestry:doors.citrus", "forestry:doors.cocobolo", "forestry:doors.ebony", "forestry:doors.giganteum",
                "forestry:doors.greenheart", "forestry:doors.ipe", "forestry:doors.kapok", "forestry:doors.larch", "forestry:doors.lime",
                "forestry:doors.mahoe", "forestry:doors.mahogany", "forestry:doors.maple", "forestry:doors.padauk", "forestry:doors.palm",
                "forestry:doors.papaya", "forestry:doors.pine", "forestry:doors.plum", "forestry:doors.poplar", "forestry:doors.sequoia", "forestry:doors.teak",
                "forestry:doors.walnut", "forestry:doors.wenge", "forestry:doors.willow", "forestry:doors.zebrawood", "forestry:engine_biogas",
                "forestry:engine_clockwork", "forestry:engine_peat", "forestry:escritoire", "forestry:fabricator", "forestry:farm_crops", "forestry:farm_ender",
                "forestry:farm_gourd", "forestry:farm_mushroom", "forestry:farm_nether", "forestry:fence.gates.acacia", "forestry:fence.gates.balsa",
                "forestry:fence.gates.baobab", "forestry:fence.gates.cherry", "forestry:fence.gates.chestnut", "forestry:fence.gates.citrus",
                "forestry:fence.gates.cocobolo", "forestry:fence.gates.ebony", "forestry:fence.gates.fireproof.acacia", "forestry:fence.gates.fireproof.balsa",
                "forestry:fence.gates.fireproof.baobab", "forestry:fence.gates.fireproof.cherry", "forestry:fence.gates.fireproof.chestnut",
                "forestry:fence.gates.fireproof.citrus", "forestry:fence.gates.fireproof.cocobolo", "forestry:fence.gates.fireproof.ebony",
                "forestry:fence.gates.fireproof.giganteum", "forestry:fence.gates.fireproof.greenheart", "forestry:fence.gates.fireproof.ipe",
                "forestry:fence.gates.fireproof.kapok", "forestry:fence.gates.fireproof.larch", "forestry:fence.gates.fireproof.lime",
                "forestry:fence.gates.fireproof.mahoe", "forestry:fence.gates.fireproof.mahogany", "forestry:fence.gates.fireproof.maple",
                "forestry:fence.gates.fireproof.padauk", "forestry:fence.gates.fireproof.palm", "forestry:fence.gates.fireproof.papaya",
                "forestry:fence.gates.fireproof.pine", "forestry:fence.gates.fireproof.plum", "forestry:fence.gates.fireproof.poplar",
                "forestry:fence.gates.fireproof.sequoia", "forestry:fence.gates.fireproof.teak", "forestry:fence.gates.fireproof.walnut",
                "forestry:fence.gates.fireproof.wenge", "forestry:fence.gates.fireproof.willow", "forestry:fence.gates.fireproof.zebrawood",
                "forestry:fence.gates.giganteum", "forestry:fence.gates.greenheart", "forestry:fence.gates.ipe", "forestry:fence.gates.kapok",
                "forestry:fence.gates.larch", "forestry:fence.gates.lime", "forestry:fence.gates.mahoe", "forestry:fence.gates.mahogany",
                "forestry:fence.gates.maple", "forestry:fence.gates.padauk", "forestry:fence.gates.palm", "forestry:fence.gates.papaya",
                "forestry:fence.gates.pine", "forestry:fence.gates.plum", "forestry:fence.gates.poplar", "forestry:fence.gates.sequoia",
                "forestry:fence.gates.teak", "forestry:fence.gates.vanilla.fireproof.acacia", "forestry:fence.gates.vanilla.fireproof.birch",
                "forestry:fence.gates.vanilla.fireproof.dark_oak", "forestry:fence.gates.vanilla.fireproof.jungle",
                "forestry:fence.gates.vanilla.fireproof.oak", "forestry:fence.gates.vanilla.fireproof.spruce", "forestry:fence.gates.walnut",
                "forestry:fence.gates.wenge", "forestry:fence.gates.willow", "forestry:fence.gates.zebrawood", "forestry:fences.0", "forestry:fences.1",
                "forestry:fences.fireproof.0", "forestry:fences.fireproof.1", "forestry:fences.vanilla.fireproof.0", "forestry:fermenter", "forestry:ffarm",
                "forestry:fluid.bio.ethanol", "forestry:fluid.biomass", "forestry:fluid.for.honey", "forestry:fluid.glass", "forestry:fluid.ice",
                "forestry:fluid.juice", "forestry:fluid.milk", "forestry:fluid.seed.oil", "forestry:fluid.short.mead", "forestry:genetic_filter",
                "forestry:greenhouse", "forestry:greenhouse.window", "forestry:greenhouse.window_up", "forestry:humus", "forestry:leaves",
                "forestry:leaves.decorative.0", "forestry:leaves.decorative.1", "forestry:leaves.decorative.2", "forestry:leaves.default.0",
                "forestry:leaves.default.1", "forestry:leaves.default.2", "forestry:leaves.default.3", "forestry:leaves.default.4", "forestry:leaves.default.5",
                "forestry:leaves.default.6", "forestry:leaves.default.7", "forestry:leaves.default.8", "forestry:loam", "forestry:logs.0", "forestry:logs.1",
                "forestry:logs.2", "forestry:logs.3", "forestry:logs.4", "forestry:logs.5", "forestry:logs.6", "forestry:logs.7", "forestry:logs.fireproof.0",
                "forestry:logs.fireproof.1", "forestry:logs.fireproof.2", "forestry:logs.fireproof.3", "forestry:logs.fireproof.4", "forestry:logs.fireproof.5",
                "forestry:logs.fireproof.6", "forestry:logs.fireproof.7", "forestry:logs.vanilla.fireproof.0", "forestry:logs.vanilla.fireproof.1",
                "forestry:mailbox", "forestry:moistener", "forestry:mushroom", "forestry:peat_bog", "forestry:planks.0", "forestry:planks.1",
                "forestry:planks.fireproof.0", "forestry:planks.fireproof.1", "forestry:planks.vanilla.fireproof.0", "forestry:pods.banana",
                "forestry:pods.cocoa", "forestry:pods.coconut", "forestry:pods.dates", "forestry:pods.papaya", "forestry:pods.plantain",
                "forestry:pods.red_banana", "forestry:rainmaker", "forestry:raintank", "forestry:resource_storage", "forestry:resources", "forestry:sapling_ge",
                "forestry:slabs.0", "forestry:slabs.1", "forestry:slabs.2", "forestry:slabs.3", "forestry:slabs.double.0", "forestry:slabs.double.1",
                "forestry:slabs.double.2", "forestry:slabs.double.3", "forestry:slabs.double.fireproof.0", "forestry:slabs.double.fireproof.1",
                "forestry:slabs.double.fireproof.2", "forestry:slabs.double.fireproof.3", "forestry:slabs.fireproof.0", "forestry:slabs.fireproof.1",
                "forestry:slabs.fireproof.2", "forestry:slabs.fireproof.3", "forestry:slabs.vanilla.double.fireproof.0", "forestry:slabs.vanilla.fireproof.0",
                "forestry:squeezer", "forestry:stairs.acacia", "forestry:stairs.balsa", "forestry:stairs.baobab", "forestry:stairs.cherry",
                "forestry:stairs.chestnut", "forestry:stairs.citrus", "forestry:stairs.cocobolo", "forestry:stairs.ebony", "forestry:stairs.fireproof.acacia",
                "forestry:stairs.fireproof.balsa", "forestry:stairs.fireproof.baobab", "forestry:stairs.fireproof.cherry", "forestry:stairs.fireproof.chestnut",
                "forestry:stairs.fireproof.citrus", "forestry:stairs.fireproof.cocobolo", "forestry:stairs.fireproof.ebony",
                "forestry:stairs.fireproof.giganteum", "forestry:stairs.fireproof.greenheart", "forestry:stairs.fireproof.ipe",
                "forestry:stairs.fireproof.kapok", "forestry:stairs.fireproof.larch", "forestry:stairs.fireproof.lime", "forestry:stairs.fireproof.mahoe",
                "forestry:stairs.fireproof.mahogany", "forestry:stairs.fireproof.maple", "forestry:stairs.fireproof.padauk", "forestry:stairs.fireproof.palm",
                "forestry:stairs.fireproof.papaya", "forestry:stairs.fireproof.pine", "forestry:stairs.fireproof.plum", "forestry:stairs.fireproof.poplar",
                "forestry:stairs.fireproof.sequoia", "forestry:stairs.fireproof.teak", "forestry:stairs.fireproof.walnut", "forestry:stairs.fireproof.wenge",
                "forestry:stairs.fireproof.willow", "forestry:stairs.fireproof.zebrawood", "forestry:stairs.giganteum", "forestry:stairs.greenheart",
                "forestry:stairs.ipe", "forestry:stairs.kapok", "forestry:stairs.larch", "forestry:stairs.lime", "forestry:stairs.mahoe",
                "forestry:stairs.mahogany", "forestry:stairs.maple", "forestry:stairs.padauk", "forestry:stairs.palm", "forestry:stairs.papaya",
                "forestry:stairs.pine", "forestry:stairs.plum", "forestry:stairs.poplar", "forestry:stairs.sequoia", "forestry:stairs.teak",
                "forestry:stairs.vanilla.fireproof.acacia", "forestry:stairs.vanilla.fireproof.birch", "forestry:stairs.vanilla.fireproof.dark_oak",
                "forestry:stairs.vanilla.fireproof.jungle", "forestry:stairs.vanilla.fireproof.oak", "forestry:stairs.vanilla.fireproof.spruce",
                "forestry:stairs.walnut", "forestry:stairs.wenge", "forestry:stairs.willow", "forestry:stairs.zebrawood", "forestry:stamp_collector",
                "forestry:still", "forestry:stump", "forestry:trade_station", "forestry:tree_chest", "forestry:wood_pile", "forestry:wood_pile_decorative",
                "forestry:worktable", "forgemultipartcbe:multipart_block", "gendustry:extractor", "gendustry:imprinter", "gendustry:industrial_apiary",
                "gendustry:liquiddna", "gendustry:liquifier", "gendustry:mutagen", "gendustry:mutagen_producer", "gendustry:mutatron",
                "gendustry:mutatron_advanced", "gendustry:protein", "gendustry:replicator", "gendustry:sampler", "gendustry:transposer", "genetics:adv_machine",
                "genetics:binnie.bacteria", "genetics:binnie.bacteria.poly", "genetics:binnie.bacteria.vector", "genetics:binnie.dna.raw",
                "genetics:binnie.growth.medium", "genetics:lab_machine", "genetics:machine", "harvestcraft:almond_sapling", "harvestcraft:apiary",
                "harvestcraft:apple_sapling", "harvestcraft:apricot_sapling", "harvestcraft:aridgarden", "harvestcraft:avocado_sapling",
                "harvestcraft:banana_sapling", "harvestcraft:beehive", "harvestcraft:candledeco1", "harvestcraft:candledeco10", "harvestcraft:candledeco11",
                "harvestcraft:candledeco12", "harvestcraft:candledeco13", "harvestcraft:candledeco14", "harvestcraft:candledeco15", "harvestcraft:candledeco16",
                "harvestcraft:candledeco2", "harvestcraft:candledeco3", "harvestcraft:candledeco4", "harvestcraft:candledeco5", "harvestcraft:candledeco6",
                "harvestcraft:candledeco7", "harvestcraft:candledeco8", "harvestcraft:candledeco9", "harvestcraft:carrotcake", "harvestcraft:cashew_sapling",
                "harvestcraft:cheesecake", "harvestcraft:cherry_sapling", "harvestcraft:cherrycheesecake", "harvestcraft:chestnut_sapling",
                "harvestcraft:chocolatesprinklecake", "harvestcraft:cinnamon_sapling", "harvestcraft:coconut_sapling", "harvestcraft:date_sapling",
                "harvestcraft:dragonfruit_sapling", "harvestcraft:durian_sapling", "harvestcraft:fig_sapling", "harvestcraft:frostgarden",
                "harvestcraft:gooseberry_sapling", "harvestcraft:grapefruit_sapling", "harvestcraft:grinder", "harvestcraft:groundtrap",
                "harvestcraft:holidaycake", "harvestcraft:honey", "harvestcraft:honeycomb", "harvestcraft:lamingtoncake", "harvestcraft:lemon_sapling",
                "harvestcraft:lime_sapling", "harvestcraft:mango_sapling", "harvestcraft:maple_sapling", "harvestcraft:market", "harvestcraft:nutmeg_sapling",
                "harvestcraft:olive_sapling", "harvestcraft:orange_sapling", "harvestcraft:pamalmond", "harvestcraft:pamapple", "harvestcraft:pamapricot",
                "harvestcraft:pamartichokecrop", "harvestcraft:pamasparaguscrop", "harvestcraft:pamavocado", "harvestcraft:pambambooshootcrop",
                "harvestcraft:pambanana", "harvestcraft:pambarleycrop", "harvestcraft:pambeancrop", "harvestcraft:pambeetcrop",
                "harvestcraft:pambellpeppercrop", "harvestcraft:pamblackberrycrop", "harvestcraft:pamblueberrycrop", "harvestcraft:pambroccolicrop",
                "harvestcraft:pambrusselsproutcrop", "harvestcraft:pamcabbagecrop", "harvestcraft:pamcactusfruitcrop", "harvestcraft:pamcandleberrycrop",
                "harvestcraft:pamcantaloupecrop", "harvestcraft:pamcashew", "harvestcraft:pamcauliflowercrop", "harvestcraft:pamcelerycrop",
                "harvestcraft:pamcherry", "harvestcraft:pamchestnut", "harvestcraft:pamchilipeppercrop", "harvestcraft:pamcinnamon", "harvestcraft:pamcoconut",
                "harvestcraft:pamcoffeebeancrop", "harvestcraft:pamcorncrop", "harvestcraft:pamcottoncrop", "harvestcraft:pamcranberrycrop",
                "harvestcraft:pamcucumbercrop", "harvestcraft:pamcurryleafcrop", "harvestcraft:pamdate", "harvestcraft:pamdragonfruit",
                "harvestcraft:pamdurian", "harvestcraft:pameggplantcrop", "harvestcraft:pamfig", "harvestcraft:pamgarliccrop", "harvestcraft:pamgigapicklecrop",
                "harvestcraft:pamgingercrop", "harvestcraft:pamgooseberry", "harvestcraft:pamgrapecrop", "harvestcraft:pamgrapefruit",
                "harvestcraft:pamkalecrop", "harvestcraft:pamkiwicrop", "harvestcraft:pamleekcrop", "harvestcraft:pamlemon", "harvestcraft:pamlettucecrop",
                "harvestcraft:pamlime", "harvestcraft:pammango", "harvestcraft:pammaple", "harvestcraft:pammustardseedscrop", "harvestcraft:pamnutmeg",
                "harvestcraft:pamoatscrop", "harvestcraft:pamokracrop", "harvestcraft:pamolive", "harvestcraft:pamonioncrop", "harvestcraft:pamorange",
                "harvestcraft:pampapaya", "harvestcraft:pampaperbark", "harvestcraft:pamparsnipcrop", "harvestcraft:pampeach", "harvestcraft:pampeanutcrop",
                "harvestcraft:pampear", "harvestcraft:pampeascrop", "harvestcraft:pampecan", "harvestcraft:pampeppercorn", "harvestcraft:pampersimmon",
                "harvestcraft:pampineapplecrop", "harvestcraft:pampistachio", "harvestcraft:pamplum", "harvestcraft:pampomegranate",
                "harvestcraft:pamradishcrop", "harvestcraft:pamraspberrycrop", "harvestcraft:pamrhubarbcrop", "harvestcraft:pamricecrop",
                "harvestcraft:pamrutabagacrop", "harvestcraft:pamryecrop", "harvestcraft:pamscallioncrop", "harvestcraft:pamseaweedcrop",
                "harvestcraft:pamsesameseedscrop", "harvestcraft:pamsoybeancrop", "harvestcraft:pamspiceleafcrop", "harvestcraft:pamspiderweb",
                "harvestcraft:pamspinachcrop", "harvestcraft:pamstarfruit", "harvestcraft:pamstrawberrycrop", "harvestcraft:pamsweetpotatocrop",
                "harvestcraft:pamtealeafcrop", "harvestcraft:pamtomatocrop", "harvestcraft:pamturnipcrop", "harvestcraft:pamvanillabean",
                "harvestcraft:pamwalnut", "harvestcraft:pamwaterchestnutcrop", "harvestcraft:pamwhitemushroomcrop", "harvestcraft:pamwintersquashcrop",
                "harvestcraft:pamzucchinicrop", "harvestcraft:papaya_sapling", "harvestcraft:paperbark_sapling", "harvestcraft:pavlovacake",
                "harvestcraft:peach_sapling", "harvestcraft:pear_sapling", "harvestcraft:pecan_sapling", "harvestcraft:peppercorn_sapling",
                "harvestcraft:persimmon_sapling", "harvestcraft:pineappleupsidedowncake", "harvestcraft:pistachio_sapling", "harvestcraft:plum_sapling",
                "harvestcraft:pomegranate_sapling", "harvestcraft:pressedwax", "harvestcraft:presser", "harvestcraft:pumpkincheesecake",
                "harvestcraft:redvelvetcake", "harvestcraft:shadedgarden", "harvestcraft:shippingbin", "harvestcraft:soggygarden",
                "harvestcraft:spiderweb_sapling", "harvestcraft:starfruit_sapling", "harvestcraft:tropicalgarden", "harvestcraft:vanillabean_sapling",
                "harvestcraft:walnut_sapling", "harvestcraft:waterfilter", "harvestcraft:watertrap", "harvestcraft:waxcomb", "harvestcraft:well",
                "harvestcraft:windygarden", "immersiveengineering:aluminum_scaffolding_stairs0", "immersiveengineering:aluminum_scaffolding_stairs1",
                "immersiveengineering:aluminum_scaffolding_stairs2", "immersiveengineering:cloth_device", "immersiveengineering:connector",
                "immersiveengineering:conveyor", "immersiveengineering:fake_light", "immersiveengineering:fluidbiodiesel", "immersiveengineering:fluidcreosote",
                "immersiveengineering:fluidethanol", "immersiveengineering:fluidplantoil", "immersiveengineering:hemp",
                "immersiveengineering:metal_decoration0", "immersiveengineering:metal_decoration1", "immersiveengineering:metal_decoration1_slab",
                "immersiveengineering:metal_decoration2", "immersiveengineering:metal_device0", "immersiveengineering:metal_device1",
                "immersiveengineering:metal_ladder", "immersiveengineering:metal_multiblock", "immersiveengineering:sheetmetal",
                "immersiveengineering:sheetmetal_slab", "immersiveengineering:storage", "immersiveengineering:storage_slab",
                "immersiveengineering:treated_wood", "immersiveengineering:treated_wood_slab", "immersiveengineering:treated_wood_stairs0",
                "immersiveengineering:treated_wood_stairs1", "immersiveengineering:treated_wood_stairs2", "immersiveengineering:wooden_decoration",
                "immersiveengineering:wooden_device0", "immersiveengineering:wooden_device1", "immersivepetroleum:dummy", "immersivepetroleum:fluid_crude_oil",
                "immersivepetroleum:fluid_diesel", "immersivepetroleum:fluid_gasoline", "immersivepetroleum:fluid_lubricant", "immersivepetroleum:fluid_napalm",
                "immersivepetroleum:metal_device", "immersivepetroleum:metal_multiblock", "immersivepetroleum:stone_decoration",
                "industrialforegoing:animal_byproduct_recolector", "industrialforegoing:animal_growth_increaser",
                "industrialforegoing:animal_independence_selector", "industrialforegoing:animal_resource_harvester",
                "industrialforegoing:animal_stock_increaser", "industrialforegoing:biofuel", "industrialforegoing:biofuel_generator",
                "industrialforegoing:bioreactor", "industrialforegoing:black_hole_controller", "industrialforegoing:black_hole_label",
                "industrialforegoing:black_hole_tank", "industrialforegoing:black_hole_unit", "industrialforegoing:block_destroyer",
                "industrialforegoing:block_placer", "industrialforegoing:conveyor", "industrialforegoing:crop_enrich_material_injector",
                "industrialforegoing:crop_recolector", "industrialforegoing:crop_sower", "industrialforegoing:dye_mixer",
                "industrialforegoing:enchantment_aplicator", "industrialforegoing:enchantment_extractor", "industrialforegoing:enchantment_invoker",
                "industrialforegoing:enchantment_refiner", "industrialforegoing:energy_field_provider", "industrialforegoing:essence",
                "industrialforegoing:fluid_crafter", "industrialforegoing:fluid_pump", "industrialforegoing:fluiddictionary_converter",
                "industrialforegoing:froster", "industrialforegoing:hydrator", "industrialforegoing:if.pink_slime", "industrialforegoing:if.protein",
                "industrialforegoing:item_splitter", "industrialforegoing:laser_base", "industrialforegoing:laser_drill", "industrialforegoing:latex",
                "industrialforegoing:latex_processing_unit", "industrialforegoing:lava_fabricator", "industrialforegoing:material_stonework_factory",
                "industrialforegoing:meat", "industrialforegoing:milk", "industrialforegoing:mob_detector", "industrialforegoing:mob_duplicator",
                "industrialforegoing:mob_relocator", "industrialforegoing:mob_slaughter_factory", "industrialforegoing:ore_fermenter",
                "industrialforegoing:ore_processor", "industrialforegoing:ore_sieve", "industrialforegoing:ore_washer",
                "industrialforegoing:oredictionary_converter", "industrialforegoing:petrified_fuel_generator", "industrialforegoing:plant_interactor",
                "industrialforegoing:potion_enervator", "industrialforegoing:protein_generator", "industrialforegoing:protein_reactor",
                "industrialforegoing:resourceful_furnace", "industrialforegoing:sewage", "industrialforegoing:sewage_composter_solidifier",
                "industrialforegoing:sludge", "industrialforegoing:sludge_refiner", "industrialforegoing:spores_recreator",
                "industrialforegoing:tree_fluid_extractor", "industrialforegoing:villager_trade_exchanger", "industrialforegoing:water_condensator",
                "industrialforegoing:water_resources_collector", "industrialforegoing:wither_builder", "ironchest:iron_chest",
                "ironchest:iron_shulker_box_black", "ironchest:iron_shulker_box_blue", "ironchest:iron_shulker_box_brown", "ironchest:iron_shulker_box_cyan",
                "ironchest:iron_shulker_box_gray", "ironchest:iron_shulker_box_green", "ironchest:iron_shulker_box_light_blue",
                "ironchest:iron_shulker_box_lime", "ironchest:iron_shulker_box_magenta", "ironchest:iron_shulker_box_orange", "ironchest:iron_shulker_box_pink",
                "ironchest:iron_shulker_box_purple", "ironchest:iron_shulker_box_red", "ironchest:iron_shulker_box_silver", "ironchest:iron_shulker_box_white",
                "ironchest:iron_shulker_box_yellow", "libvulpes:advancedmotor", "libvulpes:advstructuremachine", "libvulpes:blockphantom",
                "libvulpes:coalgenerator", "libvulpes:coil0", "libvulpes:creativepowerbattery", "libvulpes:elitemotor", "libvulpes:enhancedmotor",
                "libvulpes:forgepowerinput", "libvulpes:forgepoweroutput", "libvulpes:hatch", "libvulpes:metal0", "libvulpes:motor", "libvulpes:ore0",
                "libvulpes:placeholder", "libvulpes:structuremachine", "magicbees:effectjar", "magicbees:enchanted_earth", "magicbees:hiveblock",
                "natura:amaranth_button", "natura:amaranth_fence", "natura:amaranth_fence_gate", "natura:amaranth_pressure_plate", "natura:amaranth_trap_door",
                "natura:barley_crop", "natura:blaze_hopper", "natura:blaze_rail", "natura:blaze_rail_activator", "natura:blaze_rail_detector",
                "natura:blaze_rail_golden", "natura:bloodwood_button", "natura:bloodwood_fence", "natura:bloodwood_fence_gate",
                "natura:bloodwood_pressure_plate", "natura:bloodwood_trap_door", "natura:bluebells_flower", "natura:clouds", "natura:colored_grass",
                "natura:colored_grass_slab", "natura:colored_grass_stairs_autumnal", "natura:colored_grass_stairs_bluegrass",
                "natura:colored_grass_stairs_topiary", "natura:cotton_crop", "natura:darkwood_button", "natura:darkwood_fence", "natura:darkwood_fence_gate",
                "natura:darkwood_pressure_plate", "natura:darkwood_trap_door", "natura:eucalyptus_button", "natura:eucalyptus_fence",
                "natura:eucalyptus_fence_gate", "natura:eucalyptus_pressure_plate", "natura:eucalyptus_trap_door", "natura:fusewood_button",
                "natura:fusewood_fence", "natura:fusewood_fence_gate", "natura:fusewood_pressure_plate", "natura:fusewood_trap_door", "natura:ghostwood_button",
                "natura:ghostwood_fence", "natura:ghostwood_fence_gate", "natura:ghostwood_pressure_plate", "natura:ghostwood_trap_door",
                "natura:hopseed_button", "natura:hopseed_fence", "natura:hopseed_fence_gate", "natura:hopseed_pressure_plate", "natura:hopseed_trap_door",
                "natura:lit_netherrack_furnace", "natura:maple_button", "natura:maple_fence", "natura:maple_fence_gate", "natura:maple_pressure_plate",
                "natura:maple_trap_door", "natura:overworld_berrybush_blackberry", "natura:overworld_berrybush_blueberry",
                "natura:overworld_berrybush_maloberry", "natura:overworld_berrybush_raspberry", "natura:overworld_bookshelves",
                "natura:overworld_door_eucalyptus", "natura:overworld_door_hopseed", "natura:overworld_door_maple", "natura:overworld_door_redwood",
                "natura:overworld_door_redwood_bark", "natura:overworld_door_sakura", "natura:overworld_door_silverbell", "natura:overworld_door_tiger",
                "natura:overworld_leaves", "natura:overworld_leaves2", "natura:overworld_logs", "natura:overworld_logs2", "natura:overworld_planks",
                "natura:overworld_sapling", "natura:overworld_sapling2", "natura:overworld_slab", "natura:overworld_slab2", "natura:overworld_stairs_amaranth",
                "natura:overworld_stairs_eucalyptus", "natura:overworld_stairs_hopseed", "natura:overworld_stairs_maple", "natura:overworld_stairs_redwood",
                "natura:overworld_stairs_sakura", "natura:overworld_stairs_silverbell", "natura:overworld_stairs_tiger", "natura:overworld_stairs_willow",
                "natura:overworld_workbenches", "natura:redwood_button", "natura:redwood_fence", "natura:redwood_fence_gate", "natura:redwood_leaves",
                "natura:redwood_logs", "natura:redwood_pressure_plate", "natura:redwood_sapling", "natura:redwood_trap_door", "natura:respawn_obelisk",
                "natura:saguaro", "natura:saguaro_baby", "natura:saguaro_fruit", "natura:sakura_button", "natura:sakura_fence", "natura:sakura_fence_gate",
                "natura:sakura_pressure_plate", "natura:sakura_trap_door", "natura:silverbell_button", "natura:silverbell_fence",
                "natura:silverbell_fence_gate", "natura:silverbell_pressure_plate", "natura:silverbell_trap_door", "natura:tiger_button", "natura:tiger_fence",
                "natura:tiger_fence_gate", "natura:tiger_pressure_plate", "natura:tiger_trap_door", "natura:willow_button", "natura:willow_fence",
                "natura:willow_fence_gate", "natura:willow_pressure_plate", "natura:willow_trap_door", "openblocks:auto_anvil",
                "openblocks:auto_enchantment_table", "openblocks:beartrap", "openblocks:big_button", "openblocks:big_button_wood", "openblocks:block_breaker",
                "openblocks:block_placer", "openblocks:builder_guide", "openblocks:cannon", "openblocks:canvas", "openblocks:canvas_glass",
                "openblocks:donation_station", "openblocks:drawing_table", "openblocks:elevator", "openblocks:elevator_rotating", "openblocks:fan",
                "openblocks:flag", "openblocks:golden_egg", "openblocks:guide", "openblocks:heal", "openblocks:imaginary", "openblocks:item_dropper",
                "openblocks:ladder", "openblocks:paint_can", "openblocks:paint_mixer", "openblocks:path", "openblocks:projector", "openblocks:rope_ladder",
                "openblocks:scaffolding", "openblocks:sky", "openblocks:sponge", "openblocks:sprinkler", "openblocks:tank", "openblocks:target",
                "openblocks:trophy", "openblocks:vacuum_hopper", "openblocks:village_highlighter", "openblocks:xp_bottler", "openblocks:xp_drain",
                "openblocks:xp_shower", "projecte:collector_mk1", "projecte:collector_mk2", "projecte:collector_mk3", "projecte:condenser_mk1",
                "projecte:condenser_mk2", "projecte:fuel_block", "projecte:interdiction_torch", "projecte:nova_cataclysm", "projecte:nova_catalyst",
                "projecte:relay_mk1", "projecte:relay_mk2", "projecte:relay_mk3", "projecte:transmutation_table", "quark:acacia_button",
                "quark:acacia_pressure_plate", "quark:acacia_trapdoor", "quark:andesite_speleothem", "quark:bark", "quark:bark_acacia_slab",
                "quark:bark_acacia_slab_double", "quark:bark_acacia_stairs", "quark:bark_acacia_wall", "quark:bark_birch_slab", "quark:bark_birch_slab_double",
                "quark:bark_birch_stairs", "quark:bark_birch_wall", "quark:bark_dark_oak_slab", "quark:bark_dark_oak_slab_double", "quark:bark_dark_oak_stairs",
                "quark:bark_dark_oak_wall", "quark:bark_jungle_slab", "quark:bark_jungle_slab_double", "quark:bark_jungle_stairs", "quark:bark_jungle_wall",
                "quark:bark_oak_slab", "quark:bark_oak_slab_double", "quark:bark_oak_stairs", "quark:bark_oak_wall", "quark:bark_spruce_slab",
                "quark:bark_spruce_slab_double", "quark:bark_spruce_stairs", "quark:bark_spruce_wall", "quark:birch_button", "quark:birch_pressure_plate",
                "quark:birch_trapdoor", "quark:candle", "quark:carved_wood", "quark:chute", "quark:colored_flowerpot_black", "quark:colored_flowerpot_blue",
                "quark:colored_flowerpot_brown", "quark:colored_flowerpot_cyan", "quark:colored_flowerpot_gray", "quark:colored_flowerpot_green",
                "quark:colored_flowerpot_light_blue", "quark:colored_flowerpot_lime", "quark:colored_flowerpot_magenta", "quark:colored_flowerpot_orange",
                "quark:colored_flowerpot_pink", "quark:colored_flowerpot_purple", "quark:colored_flowerpot_red", "quark:colored_flowerpot_silver",
                "quark:colored_flowerpot_white", "quark:colored_flowerpot_yellow", "quark:custom_bookshelf", "quark:custom_chest", "quark:custom_chest_trap",
                "quark:dark_oak_button", "quark:dark_oak_pressure_plate", "quark:dark_oak_trapdoor", "quark:dark_prismarine_wall", "quark:diorite_speleothem",
                "quark:duskbound_lantern", "quark:ender_watcher", "quark:framed_glass", "quark:framed_glass_pane", "quark:glowstone_dust_block",
                "quark:gold_button", "quark:granite_speleothem", "quark:gunpowder_block", "quark:iron_button", "quark:iron_ladder", "quark:iron_plate",
                "quark:iron_plate_slab", "quark:iron_rod", "quark:jungle_button", "quark:jungle_pressure_plate", "quark:jungle_trapdoor", "quark:leaf_carpet",
                "quark:lit_lamp", "quark:midori_block", "quark:midori_block_slab", "quark:midori_block_slab_double", "quark:midori_block_stairs",
                "quark:midori_block_wall", "quark:midori_pillar", "quark:obsidian_pressure_plate", "quark:paper_lantern", "quark:paper_wall",
                "quark:paper_wall_big", "quark:quilted_wool", "quark:rain_detector", "quark:redstone_randomizer", "quark:reed_block", "quark:reed_block_slab",
                "quark:reed_block_slab_double", "quark:reed_block_stairs", "quark:reed_block_wall", "quark:sandy_bricks", "quark:sandy_bricks_slab",
                "quark:sandy_bricks_slab_double", "quark:sandy_bricks_stairs", "quark:sandy_bricks_wall", "quark:snow_bricks", "quark:snow_bricks_slab",
                "quark:snow_bricks_slab_double", "quark:snow_bricks_stairs", "quark:snow_bricks_wall", "quark:spruce_button", "quark:spruce_pressure_plate",
                "quark:spruce_trapdoor", "quark:stained_planks", "quark:stained_planks_black_slab", "quark:stained_planks_black_slab_double",
                "quark:stained_planks_black_stairs", "quark:stained_planks_blue_slab", "quark:stained_planks_blue_slab_double",
                "quark:stained_planks_blue_stairs", "quark:stained_planks_brown_slab", "quark:stained_planks_brown_slab_double",
                "quark:stained_planks_brown_stairs", "quark:stained_planks_cyan_slab", "quark:stained_planks_cyan_slab_double",
                "quark:stained_planks_cyan_stairs", "quark:stained_planks_gray_slab", "quark:stained_planks_gray_slab_double",
                "quark:stained_planks_gray_stairs", "quark:stained_planks_green_slab", "quark:stained_planks_green_slab_double",
                "quark:stained_planks_green_stairs", "quark:stained_planks_light_blue_slab", "quark:stained_planks_light_blue_slab_double",
                "quark:stained_planks_light_blue_stairs", "quark:stained_planks_lime_slab", "quark:stained_planks_lime_slab_double",
                "quark:stained_planks_lime_stairs", "quark:stained_planks_magenta_slab", "quark:stained_planks_magenta_slab_double",
                "quark:stained_planks_magenta_stairs", "quark:stained_planks_orange_slab", "quark:stained_planks_orange_slab_double",
                "quark:stained_planks_orange_stairs", "quark:stained_planks_pink_slab", "quark:stained_planks_pink_slab_double",
                "quark:stained_planks_pink_stairs", "quark:stained_planks_purple_slab", "quark:stained_planks_purple_slab_double",
                "quark:stained_planks_purple_stairs", "quark:stained_planks_red_slab", "quark:stained_planks_red_slab_double",
                "quark:stained_planks_red_stairs", "quark:stained_planks_silver_slab", "quark:stained_planks_silver_slab_double",
                "quark:stained_planks_silver_stairs", "quark:stained_planks_white_slab", "quark:stained_planks_white_slab_double",
                "quark:stained_planks_white_stairs", "quark:stained_planks_yellow_slab", "quark:stained_planks_yellow_slab_double",
                "quark:stained_planks_yellow_stairs", "quark:stone_speleothem", "quark:sugar_block", "quark:thatch", "quark:thatch_slab",
                "quark:thatch_slab_double", "quark:thatch_stairs", "quark:vertical_planks", "quark:vertical_stained_planks", "rangedpumps:pump",
                "redstonearsenal:storage", "refinedstorage:cable", "refinedstorage:constructor", "refinedstorage:controller", "refinedstorage:crafter",
                "refinedstorage:crafter_manager", "refinedstorage:crafting_monitor", "refinedstorage:destructor", "refinedstorage:detector",
                "refinedstorage:disk_drive", "refinedstorage:disk_manipulator", "refinedstorage:exporter", "refinedstorage:external_storage",
                "refinedstorage:fluid_interface", "refinedstorage:fluid_storage", "refinedstorage:grid", "refinedstorage:importer", "refinedstorage:interface",
                "refinedstorage:machine_casing", "refinedstorage:network_receiver", "refinedstorage:network_transmitter", "refinedstorage:portable_grid",
                "refinedstorage:quartz_enriched_iron_block", "refinedstorage:reader", "refinedstorage:relay", "refinedstorage:security_manager",
                "refinedstorage:storage", "refinedstorage:storage_monitor", "refinedstorage:wireless_transmitter", "refinedstorage:writer",
                "rftools:analog_block", "rftools:block_protector", "rftools:booster", "rftools:builder", "rftools:coalgenerator", "rftools:composer",
                "rftools:counter_block", "rftools:crafter1", "rftools:crafter2", "rftools:crafter3", "rftools:destination_analyzer", "rftools:dialing_device",
                "rftools:digit_block", "rftools:elevator", "rftools:ender_monitor", "rftools:endergenic", "rftools:environmental_controller",
                "rftools:invchecker_block", "rftools:item_filter", "rftools:level_emitter", "rftools:liquid_monitor", "rftools:locator", "rftools:logic_block",
                "rftools:machine_base", "rftools:machine_frame", "rftools:machine_infuser", "rftools:matter_beamer", "rftools:matter_booster",
                "rftools:matter_receiver", "rftools:matter_transmitter", "rftools:modular_storage", "rftools:pearl_injector", "rftools:powercell",
                "rftools:powercell_advanced", "rftools:powercell_creative", "rftools:powercell_simple", "rftools:projector", "rftools:redstone_receiver_block",
                "rftools:redstone_transmitter_block", "rftools:relay", "rftools:remote_scanner", "rftools:remote_storage", "rftools:rf_monitor",
                "rftools:scanner", "rftools:screen", "rftools:screen_controller", "rftools:screen_hitblock", "rftools:security_manager", "rftools:sensor_block",
                "rftools:sequencer_block", "rftools:shield_block1", "rftools:shield_block2", "rftools:shield_block3", "rftools:shield_block4",
                "rftools:shield_template_block", "rftools:simple_dialer", "rftools:space_chamber", "rftools:space_chamber_controller", "rftools:spawner",
                "rftools:storage_scanner", "rftools:storage_terminal", "rftools:support_block", "rftools:timer_block", "rftools:wire_block",
                "rftoolscontrol:craftingstation", "rftoolscontrol:node", "rftoolscontrol:processor", "rftoolscontrol:programmer", "rftoolscontrol:tank",
                "rftoolscontrol:workbench", "rftoolspower:cell1", "rftoolspower:cell2", "rftoolspower:cell3", "rftoolspower:information_screen",
                "stevescarts:blockactivator", "stevescarts:blockadvdetector", "stevescarts:blockcargomanager", "stevescarts:blockcartassembler",
                "stevescarts:blockdetector", "stevescarts:blockdistributor", "stevescarts:blockjunction", "stevescarts:blockliquidmanager",
                "stevescarts:blockmetalstorage", "stevescarts:upgrade", "storagedrawers:basicdrawers", "storagedrawers:compdrawers",
                "storagedrawers:controller", "storagedrawers:controllerslave", "storagedrawers:customdrawers", "storagedrawers:customtrim",
                "storagedrawers:framingtable", "storagedrawers:keybutton", "storagedrawers:trim", "storagedrawersextra:extra_drawers",
                "storagedrawersextra:extra_trim_0", "storagedrawersextra:extra_trim_1", "storagedrawersextra:extra_trim_2", "storagedrawersextra:extra_trim_3",
                "tconstruct:blood", "tconstruct:blueslime", "tconstruct:channel", "tconstruct:clear_glass", "tconstruct:clear_stained_glass",
                "tconstruct:firewood", "tconstruct:firewood_slab", "tconstruct:firewood_stairs", "tconstruct:glow", "tconstruct:lavawood_stairs",
                "tconstruct:milk", "tconstruct:mudbrick_stairs", "tconstruct:punji", "tconstruct:rack", "tconstruct:slime", "tconstruct:slime_channel",
                "tconstruct:slime_congealed", "tconstruct:slime_leaves", "tconstruct:slime_sapling", "tconstruct:slime_vine_blue",
                "tconstruct:slime_vine_blue_end", "tconstruct:slime_vine_blue_mid", "tconstruct:slime_vine_purple", "tconstruct:slime_vine_purple_end",
                "tconstruct:slime_vine_purple_mid", "tconstruct:stone_ladder", "tconstruct:stone_torch", "tconstruct:toolforge", "tconstruct:tooltables",
                "tconstruct:wood_rail", "tconstruct:wood_rail_trapdoor", "techreborn:adjustable_su", "techreborn:alarm", "techreborn:alloy_smelter",
                "techreborn:assembly_machine", "techreborn:auto_crafting_table", "techreborn:cable", "techreborn:charge_o_mat", "techreborn:chemical_reactor",
                "techreborn:chunk_loader", "techreborn:compressor", "techreborn:computer_cube", "techreborn:diesel_generator", "techreborn:digital_chest",
                "techreborn:distillation_tower", "techreborn:dragon_egg_syphon", "techreborn:electric_furnace", "techreborn:extractor",
                "techreborn:fluid_replicator", "techreborn:fusion_coil", "techreborn:fusion_control_computer", "techreborn:gas_turbine", "techreborn:grinder",
                "techreborn:high_voltage_su", "techreborn:hv_transformer", "techreborn:implosion_compressor", "techreborn:industrial_centrifuge",
                "techreborn:industrial_electrolyzer", "techreborn:industrial_grinder", "techreborn:industrial_sawmill", "techreborn:interdimensional_su",
                "techreborn:iron_alloy_furnace", "techreborn:iron_furnace", "techreborn:lamp_incandescent", "techreborn:lamp_led", "techreborn:lapotronic_su",
                "techreborn:lightning_rod", "techreborn:low_voltage_su", "techreborn:lsu_storage", "techreborn:lv_transformer", "techreborn:machine_casing",
                "techreborn:machine_frame", "techreborn:magic_energy_absorber", "techreborn:magic_energy_converter", "techreborn:matter_fabricator",
                "techreborn:medium_voltage_su", "techreborn:mv_transformer", "techreborn:nuke", "techreborn:ore", "techreborn:ore2",
                "techreborn:plasma_generator", "techreborn:player_detector", "techreborn:quantum_chest", "techreborn:quantum_tank", "techreborn:recycler",
                "techreborn:refined_iron_fence", "techreborn:rolling_machine", "techreborn:rubber_leaves", "techreborn:rubber_log",
                "techreborn:rubber_plank_double_slab", "techreborn:rubber_plank_slab", "techreborn:rubber_plank_stair", "techreborn:rubber_planks",
                "techreborn:rubber_sapling", "techreborn:scrapboxinator", "techreborn:semi_fluid_generator", "techreborn:solar_panel",
                "techreborn:solid_fuel_generator", "techreborn:storage", "techreborn:storage2", "techreborn:techreborn_techreborn.carbonfiber",
                "techreborn:techreborn_techreborn.chlorite", "techreborn:techreborn_techreborn.compressedair", "techreborn:techreborn_techreborn.deuterium",
                "techreborn:techreborn_techreborn.diesel", "techreborn:techreborn_techreborn.electrolyzedwater", "techreborn:techreborn_techreborn.glyceryl",
                "techreborn:techreborn_techreborn.helium", "techreborn:techreborn_techreborn.helium3", "techreborn:techreborn_techreborn.heliumplasma",
                "techreborn:techreborn_techreborn.hydrogen", "techreborn:techreborn_techreborn.lithium", "techreborn:techreborn_techreborn.mercury",
                "techreborn:techreborn_techreborn.methane", "techreborn:techreborn_techreborn.nitrocarbon", "techreborn:techreborn_techreborn.nitrocoalfuel",
                "techreborn:techreborn_techreborn.nitrodiesel", "techreborn:techreborn_techreborn.nitrofuel", "techreborn:techreborn_techreborn.nitrogen",
                "techreborn:techreborn_techreborn.nitrogendioxide", "techreborn:techreborn_techreborn.oil", "techreborn:techreborn_techreborn.sodiumpersulfate",
                "techreborn:techreborn_techreborn.sodiumsulfide", "techreborn:techreborn_techreborn.sulfuricacid", "techreborn:techreborn_techreborn.tritium",
                "techreborn:techreborn_techreborn.wolframium", "techreborn:thermal_generator", "techreborn:vacuum_freezer", "techreborn:water_mill",
                "techreborn:wind_mill", "thermaldynamics:duct_0", "thermaldynamics:duct_16", "thermaldynamics:duct_32", "thermaldynamics:duct_48",
                "thermaldynamics:duct_64", "thermalexpansion:apparatus", "thermalexpansion:cache", "thermalexpansion:cell", "thermalexpansion:device",
                "thermalexpansion:dynamo", "thermalexpansion:machine", "thermalexpansion:strongbox", "thermalexpansion:tank",
                "thermalfoundation:fluid_aerotheum", "thermalfoundation:fluid_crude_oil", "thermalfoundation:fluid_cryotheum", "thermalfoundation:fluid_ender",
                "thermalfoundation:fluid_glowstone", "thermalfoundation:fluid_mana", "thermalfoundation:fluid_petrotheum", "thermalfoundation:fluid_pyrotheum",
                "thermalfoundation:fluid_redstone", "thermalfoundation:glass", "thermalfoundation:storage", "thermalfoundation:storage_alloy",
                "thermalfoundation:storage_resource", "torchmaster:dread_lamp", "torchmaster:feral_flare_lantern", "torchmaster:invisible_light",
                "torchmaster:mega_torch", "torchmaster:terrain_lighter", "translocators:craftinggrid", "traverse:brown_autumnal_leaves",
                "traverse:brown_autumnal_sapling", "traverse:fir_door", "traverse:fir_double_slab", "traverse:fir_fence", "traverse:fir_fence_gate",
                "traverse:fir_leaves", "traverse:fir_log", "traverse:fir_planks", "traverse:fir_sapling", "traverse:fir_slab", "traverse:fir_stairs",
                "traverse:orange_autumnal_leaves", "traverse:orange_autumnal_sapling", "traverse:red_autumnal_leaves", "traverse:red_autumnal_sapling",
                "traverse:yellow_autumnal_leaves", "traverse:yellow_autumnal_sapling", "xnet:advanced_connector", "xnet:antenna", "xnet:antenna_base",
                "xnet:antenna_dish", "xnet:connector", "xnet:controller", "xnet:facade", "xnet:netcable", "xnet:redstone_proxy", "xnet:redstone_proxy_upd",
                "xnet:router", "xnet:wireless_router", "yabba:antibarrel", "yabba:compound_item_barrel", "yabba:decorative_block", "yabba:item_barrel",
                "yabba:item_barrel_connector" };

//        @LangKey("pyroclasm.config.safe_blocks")
//        @Comment({"Blocks that will stop the flow of volcanic lava.",
//            "Blocks should be listed in modname:blockname format.",
//            "At this time, metadata and NBT values cannot be specified."})
        public String[] blocksSafeFromVolcanicLava = { "minecraft:end_gateway", "minecraft:portal", "minecraft:end_portal" };
    }

    ////////////////////////////////////////////////////
    // PERFORMANCE
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.performance")
//    @Comment("Performance Tuning")
    public static Performance PERFORMANCE = new Performance();

    public static class Performance {
//        @LangKey("pyroclasm.config.max_chunk_updates_per_tick")
//        @Comment({"Maximum number of chunk updates applied to world each tick.",
//        "The actual number of chunk render updates can be higher due to effects on neighboring chunks.",
//        "Higher numbers provide more realism but can negatively affect performance.",
//        "Server-side only"})
//        @RangeInt(min = 1, max = 10)
        public int maxChunkUpdatesPerTick = 1;

//        @LangKey("pyroclasm.config.cooldown_target_load_factor")
//        @Comment({"Fraction of alloted CPU usage must be drop below this before volcano in cooldown mode starts to flow again.",
//            "Server-side only"})
//        @RangeDouble(min = 0.3, max = 0.7)
        public float cooldownTargetLoadFactor = 0.5F;

//        @LangKey("pyroclasm.config.cooldown_wait_ticks")
//        @Comment({"After volcano in cooldown reaches the target load factor, it waits this many ticks before flowing again.",
//            "Server-side only"})
//        @RangeInt(min = 0, max = 6000)
        public int cooldownWaitTicks = 200;

//        @LangKey("pyroclasm.config.total_tick_budget")
//        @Comment({"Percentage of elapsed time that can be devoted to volcano fluid simulation overall.",
//            "This includes both single-threaded on-tick time and multi-threaded processing that occurs between server ticks.",
//            "Larger numbers will enable larger lava flows but will consume more CPU used for other tasks on the machine where it runs.",
//            "If you are seeing lag or log spam that the server can't keep up, reduce this mumber or disable volcanos.",
//            "Server-side only"})
//        @RangeInt(min = 5, max = 60)
        public int totalProcessingBudget = 10;

//        @LangKey("pyroclasm.config.on_tick_budget")
//        @Comment({"Percentage of each server tick (1/20 of a second) that can be devoted to volcano fluid simulation.",
//            "This is the single-threaded part of the simulation that interacts with the game world.",
//            "Larger numbers will enable larger lava flows but casue simulation to compete with other in-game objects that tick.",
//            "If you are seeing log spam that the server can't keep up, reduce this mumber or disable volcanos.",
//            "Server-side only"})
//        @RangeInt(min = 2, max = 30)
        public int onTickProcessingBudget = 5;

//        @LangKey("pyroclasm.config.max_lava_chunks")
//        @Comment({"Maximum number of chunks containing lava to be targeted by lava simulator.",
//            "Lava flow will be throttled when in excess of this number.",
//            "While it can affect CPU consumption, CPU usage is controlled more directly via tick budgets.",
//            "Small numbers will mean smaller lava flows, lower memory consumption, and shorter/smaller world saves.",
//            "Server-side only"})
//        @RangeInt(min = 50, max = 400)
        public int chunkBudget = 100;

//        @LangKey("pyroclasm.config.max_cooling_blocks")
//        @Comment({"Maximum number of cooling basalt blocks to be tracked by lava simulator.",
//            "Lava flow will be throttled when in excess of this number.",
//            "While it can affect CPU consumption, CPU usage is controlled more directly via tick budgets.",
//            "Smaller numbers will cause more blocks to cool before lava starts flowing again and may also ",
//            "cause smaller lava flows, lower memory consumption, and shorter/smaller world saves.",
//            "Server-side only"})
//        @RangeInt(min = 10000, max = 20000000)
        public int coolingBlockBudget = 20000;

//        @LangKey("pyroclasm.config.concurrency_threshold")
//        @Comment({"When cell counts are above this limit, lava simulator will use multiple threads for cell processing.",
//            "Defaults should generally be OK, but allows ajustment to tune performance for your hardware.",
//            "Server-side only"})
//        @RangeInt(min = 1000, max = 1000000)
        public int concurrencyThreshold = 2000;

//        @LangKey("pyroclasm.config.perf_sample_secs")
//        @Comment({"Number of seconds in each volcano fluid simulation performance sample.",
//            "Larger numbers reduce log spam when performance logging is enabled.",
//            "Smaller numbers (recommended) make fluid simulation performance throttling more responsive.",
//            "Server-side only"})
//        @RangeInt(min = 10, max = 120)
        public int performanceSampleInterval = 20;

//        @LangKey("pyroclasm.config.max_tree_ops_per_tick")
//        @Comment({"Maximum number of tree-cutting operations per tick. ",
//            "Higher values offer faster & more dramtic destruction of trees due to lava flows ",
//            "but also mean potentially more tick lag. Value represents a budget ",
//            "and each operation has a cost - value doesn't equate directly to block updates. ",
//            "Server-side only"})
//        @RangeInt(min = 50, max = 2000)
        public int maxTreeOperationsPerTick = 200;

//        @LangKey("pyroclasm.config.terrain_setup_off_thread")
//        @Comment({"When true, chunk rebuilds will always happen outside the main client thread. ",
//        "Normally, Minecraft will rebuild nearby chunks on the main thread, but because volcanos ",
//        "generate so many block updates, so frequently, it can cause low frame rates. ",
//        "This settting is identical to the Forge setting of the same name, and even uses the Forge ",
//        "implementation internally to avoid creating a redundant hook, except it is enabled by default.",
//        "*** Strongly recommended you leave this on. ***",
//        "Client-side only."})
        public boolean alwaysSetupTerrainOffThread = true;

    }

    ////////////////////////////////////////////////////
    // DEBUG
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.debug")
//    @Comment("Debug and Testing Features")
    public static Debug DEBUG = new Debug();

    public static class Debug {

//        @LangKey("pyroclasm.config.disable_performance_throttling")
//        @Comment({"Disable simulation performance auto-throttling. *** EXTREME CAUTION ***",
//            "Allows volcana/lava to crush your server. Useful for performance profiling.",
//        "Sever-side only."})
        public boolean disablePerformanceThrottle = false;

//        @LangKey("pyroclasm.config.cell_debug_render")
//        @Comment({"Enable render of lava cell bounding boxes for debug purposes.",
//        "Client-side only."})
        public boolean enableLavaCellDebugRender = false;

//        @LangKey("pyroclasm.config.chunk_debug_render")
//        @Comment({"Enable render of lava cell chunk bounding boxes for debug purposes.",
//        "Client-side only."})
        public boolean enableLavaChunkDebugRender = false;

//        @LangKey("pyroclasm.config.enable_perf_log")
//        @RequiresMcRestart
//        @Comment({"If true, volcano simulation will periodically output performance statistics to log.",
//            "Does cause minor additional overhead and log spam so should generally only be enabled for testing.",
//            "Turning this off does NOT disable the minimal performance counting needed to detect simulation overload."})
        public boolean enablePerformanceLogging = false;

//        @LangKey("pyroclasm.config.enable_flow_tracking")
//        @RequiresMcRestart
//        @Comment({"If true, volcano simulation will track and output the amount of fluid that flows across cell connections.",
//            "Can cause additional overhead and log spam so should generally only be enabled for testing.",
//            "Turning this off does NOT disable the minimal performance counting needed to detect simulation overload."})
        public boolean enableFlowTracking = false;

//        @LangKey("pyroclasm.config.enable_cell_stats")
//        @Comment({"If true, volcano simulation will output cell debug information each performance interval.",
//            "Will cause significant log spam so should only be enabled for testing."})
        public boolean outputLavaCellDebugSummaries = false;

//        @LangKey("pyroclasm.config.enable_chunk_trace")
//        @Comment({"If true, volcano simulation will output cell chunk creation, load and unload.",
//            "Will cause significant log spam so should only be enabled for debug and testing."})
        public boolean enableLavaCellChunkTrace = false;

//        @LangKey("pyroclasm.config.enable_volcano_trace")
//        @Comment({"If true, will log state changes in volcano state machine. ",
//            "Only works if running Java with assertions enabled using -ea JVM parameter.",
//            "Will cause significant log spam so should only be enabled for debug and testing."})
        public boolean traceVolcaneStateMachine = false;

//        @LangKey("pyroclasm.config.enable_probe_output")
//        @Comment({"If true and The One Probe is installed, will display lava cell debug info in probe display."})
        public boolean enableLavaBlockProbeOutput = false;

//        @LangKey("pyroclasm.config.enable_test_items")
//        @RequiresMcRestart
//        @Comment({"If true, item(s) used for testing in the dev environment will be registered. These items have no recipes."})
        public boolean enableTestItems = false;

//        @LangKey("pyroclasm.config.enable_bomb_trace")
//        @Comment({"If true, will output debug information for lava bombs.",
//            "Will cause significant log spam so should only be enabled for debug and testing."})
        public boolean enableLavaBombTrace = false;

//        @LangKey("pyroclasm.config.enable_blocklist_trace")
//        @Comment({"If true, will output debug information for lava safe/destroyed block lists.",
//            "Will cause significant log spam so should only be enabled for debug and testing."})
        public boolean enable_blocklist_trace = false;

    }

    ////////////////////////////////////////////////////
    // VOLCANO
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.lava")
//    @Comment("Lava and Basalt Configuration")
    public static Lava LAVA = new Lava();

    public static class Lava {
//        @LangKey("pyroclasm.config.lava_cooling_ticks")
//        @Comment({"Number of ticks lava should go without a significant flow before it becomes basalt.",
//            "Should be larger than minDormantTicks"})
//        @RangeInt(min = 200, max = 200000)
        public int lavaCoolingTicks = 200;

//        @LangKey("pyroclasm.config.basalt_cooling_ticks")
//        @Comment({"Minimum number of ticks needed for basalt to cool from one stage to another."})
//        @RangeInt(min = 1, max = 20000)
        public int basaltCoolingTicks = 40;

//        @LangKey("pyroclasm.config.min_cooling_propagation_ticks")
//        @Comment({"Once lava starts cooling, the minimum number of ticks (inclusive) an interior block has to wait after",
//            "a neighbor successfully cools before it can also cool."})
//        @RangeInt(min = 1, max = 2000)
        public int lavaCoolingPropagationMin = 20;

//        @LangKey("pyroclasm.config.max_cooling_propagation_ticks")
//        @Comment({"Once lava starts cooling, the maximum number of ticks (exclusive) an interior block has to wait after",
//            "a neighbor successfully cools before it can also cool."})
//        @RangeInt(min = 2, max = 2001)
        public int lavaCoolingPropagationMax = 40;

//        @LangKey("pyroclasm.config.lava_keepalive_flow_threshold")
//        @Comment({"The minimum lava fluid units that must flow through a cell (in or out) during a single tick to delay cooling.",
//            "1000 units is a single level of lava, and 12000 units are an entire block of lava.",
//            "Small numbers will allow lava to fully level before it cools but it may take a longer time.",
//            "Larger number will result in faster cooling but the resulting terrain may be less even."})
//        @RangeInt(min = 2, max = 1000)
        public int lavaKeepaliveFlowThreshold = 40;

//        @LangKey("pyroclasm.config.lava_flow_reversal_threshold")
//        @Comment({"The minimum difference in cell lava surface units required for the direction of flow between two cell to reverse direction.",
//            "1000 units is a single level of lava, and 12000 units are an entire block of lava.",
//            "Larger numbers will help prevent oscillations in pooled lava so that cooling can begin.",
//            "Smaller numbers will allow lava to fully equalize levels before lava hardens but may take longer."})
//        @RangeInt(min = 2, max = 1000)
        public int lavaFlowReversalThreshold = 250;

//        @LangKey("pyroclasm.config.lava_pressure_factor")
//        @RequiresMcRestart
//        @Comment({"Excess lava in a cell above it's normal volume is multiplied by this. ",
//            "factor to compute an effective fluid surface for determining flow. ",
//            "Larger numbers mean the fluid is less compressible which is more realistic. ",
//            "Smaller numbers result in faster flow through long tunnels and faster convergence ",
//            "to equilibirum pressure surface in complex shapes with narrow junctions."})
//        @RangeInt(min = 1, max = 20)
        public int pressureFactor = 5;

//        @LangKey("pyroclasm.config.lava_fire_chance")
//        @Comment({"Controls chance for lava and lava bombs to set fire to nearby blocks.",
//            "A value of zero disables this feature.",  
//            "A value of 100 gives a normal (low) chance for wood to catch fire.",
//            "Highly flammable blocks like leaves will almost always catch fire at a setting of 100 or higher.",
//            "Values less than 100 give reduced chances for ignitiion.",
//            "At 6000, wood will always ignite.  At 30000, any flammable block will always ignite."})
//        @RangeInt(min = 0, max = 30000)
        public int lavaFireChance = 3000;

//        @LangKey("pyroclasm.config.lava_bomb_fire_chance")
//        @Comment({"Controls chance for lava bombs to set fire to passing blocks.",
//            "A value of zero disables this feature.",  
//            "A value of 100 gives a normal (low) chance for wood to catch fire.",
//            "Highly flammable blocks like leaves will almost always catch fire at a setting of 100 or higher.",
//            "Values less than 100 give reduced chances for ignitiion.",
//            "At 6000, wood will always ignite.  At 30000, any flammable block will always ignite."})
//        @RangeInt(min = 0, max = 30000)
        public int lavaBombFireChance = 20;
    }

    ////////////////////////////////////////////////////
    // VOLCANO
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.sound")
//    @Comment("Settings for Volcano & Lava Sounds")
    public static Sound SOUND = new Sound();

    public static class Sound {
//        @LangKey("pyroclasm.config.launch_volume")
//        @Comment({"Loudness (range) of lava bombs when they launch. Zero disables.",
//            "This is a client-side setting."})
//        @RangeDouble(min = 0.0, max = 64.0)
        public double launchVolume = 8.0;

//        @LangKey("pyroclasm.config.rumble_volume")
//        @Comment({"Loudness (range) of volcano rumble. Zero disables.",
//            "Currently this is a SERVER-side setting. That's stupid but I didn't have time to do the extra work for client side. ", 
//            "For now, you can turn down Ambient sounds on client."})
//        @RangeDouble(min = 0.0, max = 64.0)
        public double rumbleVolume = 16.0;
    }

    ////////////////////////////////////////////////////
    // VOLCANO
    ////////////////////////////////////////////////////
//    @LangKey("pyroclasm.config.volcano")
//    @Comment("Settings for Volcano Feature")
    public static Volcano VOLCANO = new Volcano();

    public static class Volcano {
//        @LangKey("pyroclasm.config.lava_blocks_per_second")
//        @Comment("Volume of lava ejected from bore, in full blocks per second.  Does not include proectile lava volume.")
//        @RangeInt(min = 1, max = 64)
        public int lavaBlocksPerSecond = 16;

//        @LangKey("pyroclasm.config.max_y_level")
//        @Comment("Y-axis build limit at which Volcano becomes permanently dormant.")
//        @RangeInt(min = 128, max = 255)
//        @RequiresMcRestart
        public int maxYLevel = 147;

//        @LangKey("pyroclasm.config.mound_blocks_per_tick")
//        @Comment("Number of blocks per tick that can be cleared by volcano mounding. Values above 1 are mostly useful for testing.")
//        @RangeInt(min = 1, max = 64)
        public int moundBlocksPerTick = 1;

//        @LangKey("pyroclasm.config.mound_radius")
//        @Comment("Radius of one standard deviation, in blocks, for volcano mound building.")
//        @RangeInt(min = 14, max = 28)
        public int moundRadius = 20;

//        @LangKey("pyroclasm.config.grace_ticks")
//        @Comment({"Number of ticks before volcano activation clock starts in a new world.",
//            "There are 24000 ticks in one minecraft day."})
//        @RangeInt(min = 0, max = 480000000)
        public int graceTicks = 24000;

//        @LangKey("pyroclasm.config.min_dormant_ticks")
//        @Comment("Minimum number of ticks volcanos remain dormant between activations.")
//        @RangeInt(min = 20, max = 240000000)
        public int minDormantTicks = 24000;

//        @LangKey("pyroclasm.config.max_dormant_ticks")
//        @Comment({"Maximum number of ticks a volcanos remain dormant between activations.",
//            "Should be larger than minDormantTicks."})
//        @RangeInt(min = 40, max = 480000000)
        public int maxDormantTicks = 60000;

//        @LangKey("pyroclasm.config.min_active_ticks")
//        @Comment("Minimum number of ticks a volcano remains active.")
//        @RangeInt(min = 20, max = 240000000)
        public int minActiveTicks = 60000;

//        @LangKey("pyroclasm.config.max_active_ticks")
//        @Comment({"Maximum number of ticks a volcano remain active.",
//            "Should be larger than minActiveTicks."})
//        @RangeInt(min = 40, max = 480000000)
        public int maxActiveTicks = 120000;

//        @LangKey("pyroclasm.config.max_lava_projectiles")
//        @Comment({"Maximum number of flying/falling volcanic lava entities that may be in the world simultaneously.",
//            "Higher numbers may provide more responsive flowing and better realism but can create lag."})
//        @RangeInt(min = 0, max = 200)
        public int maxLavaEntities = 4;

//        @LangKey("pyroclasm.config.ops_per_tick")
//        @Comment({"Maximum number of operations each tick for clearing the volcano bore, spouting lava, etc.",
//            "Higher numbers make volcanos more dynamic.  Try smaller numbers if volcano may be causing lag."})
//        @RangeInt(min = 16, max = 512)
        public int operationsPerTick = 64;

        /** Contains block objects configured to be destroyed by lava */
        public static final IdentityHashMap<Block, Block> blocksDestroyedByLava = new IdentityHashMap<Block, Block>();

        /** Contains block objects configured to be unharmed by lava */
        public static final IdentityHashMap<Block, Block> blocksSafeFromLava = new IdentityHashMap<Block, Block>();

        /** Number of milliseconds in a volcano fluid simulation performance sample */
        public static int performanceSampleIntervalMillis;

        /** Number of nanoseconds in a volcano fluid simulation performance sample */
        public static long peformanceSampleIntervalNanos;

        /** Number of nanoseconds budgeted each interval for on-tick processing */
        public static long performanceBudgetOnTickNanos;

        /** Number of nanoseconds budgeted each interval for off-tick processing */
        public static long performanceBudgetTotalNanos;

        /**
         * Like {@link #lavaKeepaliveFlowThreshold} but for cells under pressure. Flows
         * in cells under pressure count for more than cells in free flow. Will always
         * be >= 1.
         */
        public static int lavaCoolingPressuredKeepaliveThreshold;

        private static void recalcDerived() {
            lavaCoolingPressuredKeepaliveThreshold = Math.max(1, LAVA.lavaKeepaliveFlowThreshold / LavaCell.PRESSURE_FACTOR);

            performanceSampleIntervalMillis = PERFORMANCE.performanceSampleInterval * 1000;
            peformanceSampleIntervalNanos = (long) performanceSampleIntervalMillis * 1000000;
            performanceBudgetOnTickNanos = peformanceSampleIntervalNanos * PERFORMANCE.onTickProcessingBudget / 100;
            performanceBudgetTotalNanos = peformanceSampleIntervalNanos * PERFORMANCE.totalProcessingBudget / 100;
            if (DEBUG.disablePerformanceThrottle) {
                performanceBudgetOnTickNanos = Long.MAX_VALUE;
                performanceBudgetTotalNanos = Long.MAX_VALUE;
            }

            if (VOLCANO.maxDormantTicks <= VOLCANO.minDormantTicks) {
                VOLCANO.maxDormantTicks = VOLCANO.minDormantTicks + 20;
            }
        }

        private static void recalcBlocks() {
            Registry<Block> reg = Registry.BLOCK;

            blocksDestroyedByLava.clear();
            for (String s : SUBSTANCES.blocksDestroyedByVolcanicLava) {
                Identifier rl = new Identifier(s);
                if (reg.containsId(rl)) {
                    Block b = reg.get(rl);
                    blocksDestroyedByLava.put(b, b);
                    if (DEBUG.enable_blocklist_trace)
                        Pyroclasm.LOG.info("Configured " + s + " to be DESTROYED by volcanic lava.");

                } else if (DEBUG.enable_blocklist_trace) {
                    Pyroclasm.LOG.info("Did not find block " + s + " configured to be destroyed by volcanic lava. Confirm block name is correct.");
                }
            }

            blocksSafeFromLava.clear();
            for (String s : SUBSTANCES.blocksSafeFromVolcanicLava) {
                Identifier rl = new Identifier(s);
                if (reg.containsId(rl)) {
                    Block b = reg.get(rl);
                    blocksSafeFromLava.put(b, b);

                    if (DEBUG.enable_blocklist_trace)
                        Pyroclasm.LOG.info("Configured " + s + " to be SAFE from volcanic lava.");
                } else if (DEBUG.enable_blocklist_trace) {
                    Pyroclasm.LOG.info("Did not find block " + s + " configured to be safe from volcanic lava. Confirm block name is correct.");
                }
            }
        }
    }
}
