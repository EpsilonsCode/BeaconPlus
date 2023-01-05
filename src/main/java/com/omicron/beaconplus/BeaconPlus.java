package com.omicron.beaconplus;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("beaconplus")
public class BeaconPlus
{
    public BeaconPlus()
    {
        Config.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC, "beacon-plus-config.toml");
    }
}
