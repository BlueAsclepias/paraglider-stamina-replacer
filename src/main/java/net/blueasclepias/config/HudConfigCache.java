package net.blueasclepias.config;

import net.blueasclepias.enums.HudAnchor;

public final class HudConfigCache {

    public static int barX;
    public static int barY;
    public static HudAnchor anchor;

    private HudConfigCache() {}

    public static void bake() {
        barX = ClientConfig.OFFSET_X.get();
        barY = ClientConfig.OFFSET_Y.get();
        anchor = ClientConfig.ANCHOR.get();
    }
}
