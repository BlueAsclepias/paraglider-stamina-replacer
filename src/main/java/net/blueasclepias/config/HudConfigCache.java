package net.blueasclepias.config;

import net.blueasclepias.enums.FillDirection;
import net.blueasclepias.enums.FillType;
import net.blueasclepias.enums.HudAnchor;

public final class HudConfigCache {

    public static int barX;
    public static int barY;
    public static HudAnchor anchor;
    public static FillDirection fillDirection;
    public static FillType fillType;

    private HudConfigCache() {}

    public static void bake() {
        barX = ClientConfig.OFFSET_X.get();
        barY = ClientConfig.OFFSET_Y.get();
        anchor = ClientConfig.ANCHOR.get();
        fillDirection = ClientConfig.FILL_DIRECTION.get();
        fillType = ClientConfig.FILL_TYPE.get();
    }
}
