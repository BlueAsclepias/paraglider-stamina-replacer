package net.blueasclepias.config;

public final class HudConfigCache {

    public static int barX;
    public static int barY;

    private HudConfigCache() {}

    public static void bake() {
        barX = ClientConfig.OFFSET_X.get();
        barY = ClientConfig.OFFSET_Y.get();
    }
}
