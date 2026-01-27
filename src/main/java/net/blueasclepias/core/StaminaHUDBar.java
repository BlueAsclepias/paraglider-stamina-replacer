package net.blueasclepias.core;

import com.mojang.blaze3d.systems.RenderSystem;
import net.blueasclepias.config.HudConfigCache;
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
            new ResourceLocation(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_background.png");

    private static final ResourceLocation FILL =
            new ResourceLocation(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_progress.png");


    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 10;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        // we don't need to check for screens, since much like the rest of the HUD, it remains in place.
        if(mc.player==null || mc.options.hideGui ||
                !ParagliderMod.instance().getPlayerStateMap().hasStaminaConsumption()) return;

        // Pull stamina from Paraglider
        Stamina s = Stamina.get(mc.player);
        int maxStamina = s.maxStamina();
        int stamina = Math.min(maxStamina, s.stamina());
        if(stamina>=maxStamina) {
            renderBar(guiGraphics, 1, screenWidth, screenHeight);
        }
        float ratio = 0;
        if(!s.isDepleted()){
            ratio = Mth.clamp((float) stamina / (float) maxStamina, 0f, 1f);
        }
        renderBar(guiGraphics, ratio, screenWidth, screenHeight);
    }

    private void renderBar(
            GuiGraphics g,
            float ratio,
            int screenWidth,
            int screenHeight
    ) {

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        int x = screenWidth / 2 - HudConfigCache.barX;
        int y = screenHeight - HudConfigCache.barY;

        // background
        g.blit(
                BG,
                x, y,
                0, 0,
                BAR_WIDTH, BAR_HEIGHT,
                BAR_WIDTH, BAR_HEIGHT
        );

        // filled portion
        int filled = (int)(BAR_WIDTH * ratio);
        if (filled > 0) {
            g.blit(
                    FILL,
                    x, y,
                    0, 0,
                    filled, BAR_HEIGHT,
                    BAR_WIDTH, BAR_HEIGHT
            );
        }
    }
}
