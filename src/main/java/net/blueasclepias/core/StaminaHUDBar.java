package net.blueasclepias.core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blueasclepias.config.HudConfigCache;
import net.blueasclepias.enums.FillDirection;
import net.blueasclepias.enums.FillType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import tictim.paraglider.ParagliderMod;
import tictim.paraglider.api.stamina.Stamina;
import tictim.paraglider.config.Cfg;

public class StaminaHUDBar implements IGuiOverlay {

    public static final int MAX_STEPS_PER_BAR = 20;
    public static final float MAX_BARS = 3f;
    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_background.png");
    private static final ResourceLocation FILL =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_progress.png");
    private static final ResourceLocation RED =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_red.png");
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 10;
    private static final int PIXELS_PER_STEP = 4;
    private static final float FADE_DELAY = 10.0f;
    private float staminaAlpha = 1.0f;
    private float timeAtFull = 0f;

    private static boolean hasArmor() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getArmorValue() > 0;
    }

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!ParagliderStaminaReplacer.shouldRender) return;
        Minecraft mc = Minecraft.getInstance();
        // we don't need to check for screens, since much like the rest of the HUD, it remains in place.
        if (mc.player == null || mc.options.hideGui ||
                !ParagliderMod.instance().getPlayerStateMap().hasStaminaConsumption()) return;

        if (mc.player != null && mc.player.isCreative()) {
            return;
        }
        // Pull stamina from Paraglider
        Stamina s = Stamina.get(mc.player);
        int maxStamina = s.maxStamina();
        int stamina = Math.min(maxStamina, s.stamina());

        boolean isFull = stamina >= maxStamina;
        float delta = Minecraft.getInstance().getDeltaFrameTime();
        float fadeSpeed = 0.1f * delta;

        if (isFull) {
            timeAtFull += delta;
        } else {
            timeAtFull = 0f;
        }

        if (HudConfigCache.hideWhenUnused && isFull && timeAtFull >= FADE_DELAY) {
            staminaAlpha = Math.max(0f, staminaAlpha - fadeSpeed);
        } else {
            staminaAlpha = Math.min(1f, staminaAlpha + fadeSpeed);
        }

        if (isFull) {
            renderBar(gui, guiGraphics, 1, screenWidth, screenHeight, false);
        } else {
            float ratio = Mth.clamp((float) stamina / (float) maxStamina, 0f, 1f);
            renderBar(gui, guiGraphics, ratio, screenWidth, screenHeight, s.isDepleted());
        }


    }

    private void renderBar(
            ForgeGui gui,
            GuiGraphics g,
            float ratio,
            int screenWidth,
            int screenHeight,
            boolean isDepleted
    ) {
        if (staminaAlpha <= 0f) return;
        if (HudConfigCache.displayExtraIcons)
            renderStackedBars(gui, g, screenWidth, screenHeight, isDepleted);
        else
            renderSingleBar(gui, g, ratio, MAX_STEPS_PER_BAR, screenWidth, screenHeight, isDepleted);
    }

    private void renderStackedBars(
            ForgeGui gui,
            GuiGraphics g,
            int screenWidth,
            int screenHeight,
            boolean isDepleted
    ) {
        Minecraft mc = Minecraft.getInstance();
        Stamina s = Stamina.get(mc.player);

        int playerMaxStamina = s.maxStamina();
        int configuredMaxStamina = Cfg.get().maxStamina();
        int totalSteps = Mth.ceil(playerMaxStamina * MAX_BARS * MAX_STEPS_PER_BAR / configuredMaxStamina);
        int remainingSteps = totalSteps;
        int filledSteps = Math.round(
                Math.min(playerMaxStamina, s.stamina()) * totalSteps
                        / (float) playerMaxStamina
        );
        int remainingFilled = filledSteps;

        float maxStaminaPerBar = configuredMaxStamina / MAX_BARS;
        int barsToRender = Mth.clamp(
                Mth.ceil(playerMaxStamina / maxStaminaPerBar),
                1,
                3
        );

        for (int bar = 0; bar < barsToRender; bar++) {
            int steps = Math.min(remainingSteps, MAX_STEPS_PER_BAR);
            float staminaPerBar = configuredMaxStamina / 3.0f;
            float staminaInBar = Mth.clamp(
                    s.stamina() - bar * staminaPerBar,
                    0,
                    staminaPerBar
            );
            float barRatio = staminaInBar / staminaPerBar;

            renderSingleBar(
                    gui,
                    g,
                    barRatio,
                    steps,
                    screenWidth,
                    screenHeight - bar, // TODO: TOO HIGH!
                    isDepleted
            );

            remainingSteps -= steps;
            remainingFilled -= Math.min(remainingFilled, steps);
        }
    }

    private void renderSingleBar(
            ForgeGui gui,
            GuiGraphics g,
            float ratio,
            int steps,
            int screenWidth,
            int screenHeight,
            boolean isDepleted
    ) {

        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, staminaAlpha);

        int x = screenWidth / 2;
        int y;

        int icons = steps * PIXELS_PER_STEP;

        // offsets are always relative to the anchor
        switch (HudConfigCache.anchor) {
            case HEALTH -> {
                int armorOffset = hasArmor() ? 0 : BAR_HEIGHT;
                x -= 91;
                y = screenHeight - gui.leftHeight + armorOffset;
                gui.leftHeight += BAR_HEIGHT + 2;
            }
            case HUNGER -> {
                x += 10;
                y = screenHeight - gui.rightHeight;
                gui.rightHeight += BAR_HEIGHT + 2;
            }
            default -> { // FREE is default
                x -= HudConfigCache.barX;
                y = screenHeight - HudConfigCache.barY;
            }
        }

        int filled = (int) (icons * ratio);
        if (HudConfigCache.fillType == FillType.VANILLA)
            filled = (filled / PIXELS_PER_STEP) * PIXELS_PER_STEP;

        if (HudConfigCache.fillDirection == FillDirection.LEFT) {
            // Draw filled bar left → right
            // background
            g.blit(
                    BG,
                    x, y,
                    0, 0,
                    icons, BAR_HEIGHT,
                    BAR_WIDTH, BAR_HEIGHT
            );

            if (filled > 0) {
                // progress
                g.blit(
                        isDepleted ? RED : FILL,
                        x, y,
                        0, 0,
                        filled, BAR_HEIGHT,
                        BAR_WIDTH, BAR_HEIGHT
                );
            }
        } else {
            // right → left
            int missingIcons = MAX_STEPS_PER_BAR * PIXELS_PER_STEP - icons;

            // background
            g.blit(
                    BG,
                    x + missingIcons,
                    y,
                    icons,
                    0,
                    icons,
                    BAR_HEIGHT,
                    BAR_WIDTH,
                    BAR_HEIGHT
            );

            if (filled > 0) {
                // progress
                int fillOffset = icons - filled;
                g.blit(
                        isDepleted ? RED : FILL,
                        x + missingIcons + fillOffset,
                        y,
                        icons + fillOffset, // shift texture start
                        0,
                        filled, BAR_HEIGHT,
                        BAR_WIDTH, BAR_HEIGHT
                );
            }
        }

        RenderSystem.disableBlend();
    }
}
