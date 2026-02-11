package net.blueasclepias.config;

import net.blueasclepias.core.ParagliderStaminaReplacer;
import net.blueasclepias.enums.FillDirection;
import net.blueasclepias.enums.FillType;
import net.blueasclepias.enums.HudAnchor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class StaminaHudConfigScreen extends Screen {

    private final Screen parent;
    private static final ResourceLocation BG =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_background.png");

    private static final ResourceLocation FILL =
            ResourceLocation.fromNamespaceAndPath(ParagliderStaminaReplacer.MOD_ID, "textures/gui/hud/stamina_bar_progress.png");

    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 10;

    private boolean dragging = false;
    private int dragOffsetX;
    private int dragOffsetY;

    private int barX = HudConfigCache.barX;
    private int barY = HudConfigCache.barY;
    private HudAnchor anchor = HudConfigCache.anchor;
    private FillDirection fillDirection = HudConfigCache.fillDirection;
    private FillType fillType = HudConfigCache.fillType;

    public StaminaHudConfigScreen(Screen parent) {
        super(Component.literal("Stamina HUD Configuration"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // Done button
        this.addRenderableWidget(
                Button.builder(Component.literal("Done"), b -> onClose())
                        .bounds(this.width - 105, this.height - 30, 100, 20)
                        .build()
        );

        if(minecraft.level != null) {
            this.addRenderableWidget(
                    Button.builder(Component.literal("Left"), b -> {
                                fillDirection = FillDirection.LEFT;
                            })
                            .bounds(this.width - 105, this.height - 113, 50, 20)
                            .build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("Right"), b -> {
                                fillDirection = FillDirection.RIGHT;
                            })
                            .bounds(this.width - 55, this.height - 113, 50, 20)
                            .build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("Dynamic"), b -> {
                                fillType = FillType.DYNAMIC;
                            })
                            .bounds(this.width - 105, this.height - 83, 50, 20)
                            .build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("Vanilla"), b -> {
                                fillType = FillType.VANILLA;
                            })
                            .bounds(this.width - 55, this.height - 83, 50, 20)
                            .build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("Health"), b -> {
                                anchor = HudAnchor.HEALTH;
                                onClose();
                            })
                            .bounds(this.width - 105, this.height - 53, 50, 20)
                            .build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("Hunger"), b -> {
                                anchor = HudAnchor.HUNGER;
                                onClose();
                            })
                            .bounds(this.width - 55, this.height - 53, 50, 20)
                            .build()
            );
        }

        ParagliderStaminaReplacer.shouldRender = false;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        boolean inWorld = minecraft.level != null;
        if (!inWorld) {
            gfx.fill(0, 0, this.width, this.height, 0xAA000000); // semi-transparent black
            gfx.drawCenteredString(
                    this.font,
                    Component.literal("Join a world to position the stamina bar"),
                    this.width / 2,
                    this.height / 2 - 10,
                    0xFFFFFF
            );
        } else {
            this.renderBackground(gfx);

            gfx.drawCenteredString(
                    this.font,
                    Component.literal("Left-Click the Stamina Bar to drag it around the screen."),
                    this.width / 2,
                    this.height / 2 - 50,
                    0x00FF00
            );

            gfx.drawString(
                    this.font,
                    Component.literal("Fill Direction: " + fillDirection),
                    this.width - 104,
                    this.height - 122,
                    0x00FF00
            );

            gfx.drawString(
                    this.font,
                    Component.literal("Fill Type: " + fillType),
                    this.width - 104,
                    this.height - 92,
                    0x00FF00
            );

            gfx.drawString(
                    this.font,
                    Component.literal("Snap To " + anchor),
                    this.width - 104,
                    this.height - 62,
                    0x00FF00
            );

            // Background
            gfx.blit(BG, this.width / 2 - barX, this.height - barY, 0, 0,
                    BAR_WIDTH, BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);

            // Fill (example 70%)
            int filled = (int) (BAR_WIDTH * 0.7f);
            gfx.blit(FILL, this.width / 2 - barX, this.height - barY, 0, 0, filled,
                    BAR_HEIGHT, BAR_WIDTH, BAR_HEIGHT);
        }
        super.render(gfx, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 &&
                mouseX >= this.width / 2 - barX && mouseX <= this.width / 2 - barX + BAR_WIDTH &&
                mouseY >= this.height - barY && this.height - mouseY <= barY + BAR_HEIGHT) {

            dragging = true;
            dragOffsetX = this.width / 2 - (int) mouseX - barX;
            dragOffsetY = this.height - (int) mouseY - barY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging) {
            // if we drag, change snapping mode to free movement
            anchor = HudAnchor.FREE;
            barX = this.width / 2 - (int) mouseX - dragOffsetX;
            barY = this.height - (int) mouseY - dragOffsetY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        ParagliderStaminaReplacer.shouldRender = true;
        ClientConfig.OFFSET_X.set(barX);
        ClientConfig.OFFSET_Y.set(barY);
        ClientConfig.ANCHOR.set(anchor);
        ClientConfig.FILL_DIRECTION.set(fillDirection);
        ClientConfig.FILL_TYPE.set(fillType);
        this.minecraft.setScreen(parent);
    }
}

