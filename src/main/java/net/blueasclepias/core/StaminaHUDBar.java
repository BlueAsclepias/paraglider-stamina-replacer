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

public class StaminaHUDBar implements IGuiOverlay {

    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_background.png");

    private static final ResourceLocation FILL =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_progress.png");

    private static final ResourceLocation RED =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_red.png");

    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 10;
    private static final int PIXELS_PER_STEP = 4;

    private float staminaAlpha = 1.0f;
    private float timeAtFull = 0f;
    private static final float FADE_DELAY = 10.0f;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if(!ParagliderStaminaReplacer.shouldRender) return;
        Minecraft mc = Minecraft.getInstance();
        // we don't need to check for screens, since much like the rest of the HUD, it remains in place.
        if(mc.player==null || mc.options.hideGui ||
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

        if(isFull) {
            renderBar(gui, guiGraphics, 1, screenWidth, screenHeight, false, staminaAlpha);
        } else {
            float ratio = Mth.clamp((float) stamina / (float) maxStamina, 0f, 1f);
            renderBar(gui, guiGraphics, ratio, screenWidth, screenHeight, s.isDepleted(), staminaAlpha);
        }


    }

    private void renderBar(
            ForgeGui gui,
            GuiGraphics g,
            float ratio,
            int screenWidth,
            int screenHeight,
            boolean isDepleted,
            float staminaAlpha
    ) {
        if (staminaAlpha <= 0f) return;
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1f, 1f, 1f, staminaAlpha);

        int x = (int) (screenWidth / 2);
        int y;

        // offsets are always relative to the anchor
        switch(HudConfigCache.anchor){
            case HEALTH -> {
                int armorOffset = hasArmor()?0:BAR_HEIGHT;
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

        // background
        g.blit(
                BG,
                x, y,
                0, 0,
                BAR_WIDTH, BAR_HEIGHT,
                BAR_WIDTH, BAR_HEIGHT
        );

        int filled = (int)(BAR_WIDTH * ratio);
        if(HudConfigCache.fillType == FillType.VANILLA){
            int stepsFilled = filled/PIXELS_PER_STEP;
            filled = stepsFilled * PIXELS_PER_STEP;
        }

        if (filled > 0) {
            if(HudConfigCache.fillDirection == FillDirection.LEFT) {
                // Draw filled bar right → left
                g.blit(
                        isDepleted ? RED : FILL,
                        x, y,
                        0, 0,
                        filled, BAR_HEIGHT,
                        BAR_WIDTH, BAR_HEIGHT
                );
            } else {
                // Normal left → right
                g.blit(
                        isDepleted ? RED : FILL,
                        x + (BAR_WIDTH - filled), // shift drawing position
                        y,
                        BAR_WIDTH - filled, // shift texture start
                        0,
                        filled, BAR_HEIGHT,
                        BAR_WIDTH, BAR_HEIGHT
                );
            }
        }
        RenderSystem.disableBlend();
    }

    private static boolean hasArmor() {
        Minecraft mc = Minecraft.getInstance();
        return mc.player != null && mc.player.getArmorValue() > 0;
    }
}
