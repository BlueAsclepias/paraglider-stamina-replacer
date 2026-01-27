package net.blueasclepias.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.IntValue OFFSET_X;
    public static ForgeConfigSpec.IntValue OFFSET_Y;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("hud");

        OFFSET_X = builder
                .comment("Horizontal offset of the stamina bar")
                .defineInRange("offsetX", 90, -10000, 10000);

        OFFSET_Y = builder
                .comment("Vertical offset of the stamina bar")
                .defineInRange("offsetY", 59, -10000, 10000);

        builder.pop();

        SPEC = builder.build();
    }
}