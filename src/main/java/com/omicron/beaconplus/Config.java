package com.omicron.beaconplus;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.InMemoryCommentedFormat;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.LinkedHashMap;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    public static HashMap<Block, Double> BLOCK_VALUES = new HashMap<>();

    public static ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.DoubleValue TIER_1;

    public static ForgeConfigSpec.DoubleValue TIER_2;

    public static ForgeConfigSpec.DoubleValue TIER_3;

    public static ForgeConfigSpec.DoubleValue TIER_4;

    public static ForgeConfigSpec.BooleanValue SHOULD_WORT_AT_ALL_Y_LEVELS;

    public static ForgeConfigSpec.ConfigValue<CommentedConfig> BLOCK_VALUES_CONFIG;

    public static void init()
    {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        //builder.comment("Beacon settings");

        TIER_1 = builder.comment("Minimum range of a tier 1 beacon").defineInRange("Tier 1", 20.0D, 1.0D, 1000.0D);

        TIER_2 = builder.comment("Minimum range of a tier 2 beacon").defineInRange("Tier 2", 30.0D, 1.0D, 1000.0D);

        TIER_3 = builder.comment("Minimum range of a tier 3 beacon").defineInRange("Tier 3", 40.0D, 1.0D, 1000.0D);

        TIER_4 = builder.comment("Minimum range of a tier 4 beacon").defineInRange("Tier 4", 50.0D, 1.0D, 1000.0D);

        SHOULD_WORT_AT_ALL_Y_LEVELS = builder.comment("Should beacons work at all Y levels").define("All Y", false);
        /*
        CommentedConfig defaultConfigValue = CommentedConfig.of(LinkedHashMap::new, InMemoryCommentedFormat.withUniversalSupport());
        defaultConfigValue.add(Blocks.IRON_BLOCK.getRegistryName().toString(), 0.0);
        defaultConfigValue.add(Blocks.GOLD_BLOCK.getRegistryName().toString(), 0.0);
        defaultConfigValue.add(Blocks.DIAMOND_BLOCK.getRegistryName().toString(), 0.0);
        defaultConfigValue.add(Blocks.EMERALD_BLOCK.getRegistryName().toString(), 0.0);
        defaultConfigValue.add(Blocks.NETHERITE_BLOCK.getRegistryName().toString(), 0.0);
        BLOCK_VALUES_CONFIG = builder.comment("Additional beacon range bonus per block map, add entries in this format - \"namespace:path\" = value ")
                .define("Block bonus values", defaultConfigValue);
        */
        SPEC = builder.build();
    }

    @SubscribeEvent
    public static void onLoad(final ModConfigEvent.Loading configEvent)
    {
        //onLoadAndReload();
    }

    @SubscribeEvent
    public static void onReload(final ModConfigEvent.Reloading configEvent)
    {
        //onLoadAndReload();
    }

    private static void onLoadAndReload()
    {
        BLOCK_VALUES.clear();
        BLOCK_VALUES_CONFIG.get().valueMap().forEach((block_name, value) -> {
            if(!(block_name instanceof String))
                return;
            if(!(value instanceof Double))
                return;
            try {
                ResourceLocation key = new ResourceLocation(block_name);
                Block block = ForgeRegistries.BLOCKS.getValue(key);
                if(block != Blocks.AIR)
                BLOCK_VALUES.put(block, ((Double) value));
            } catch (ResourceLocationException e)
            {

            }
        });
    }
}
