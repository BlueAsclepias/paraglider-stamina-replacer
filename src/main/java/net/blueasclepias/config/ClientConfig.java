package net.blueasclepias.config;

import net.blueasclepias.enums.HudAnchor;
import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.EnumValue<HudAnchor> ANCHOR;
    public static ForgeConfigSpec.IntValue OFFSET_X;
    public static ForgeConfigSpec.IntValue OFFSET_Y;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("hud");

        ANCHOR = builder
                .comment("How the stamina bar is anchored to the HUD")
                .defineEnum("anchor", HudAnchor.ABOVE_HEALTH);

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