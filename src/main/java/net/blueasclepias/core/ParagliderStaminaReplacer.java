package net.blueasclepias.core;

import com.mojang.logging.LogUtils;
import net.blueasclepias.config.ClientConfig;
import net.blueasclepias.config.StaminaHudConfigScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ParagliderStaminaReplacer.MOD_ID)
public class ParagliderStaminaReplacer {
    public static final String MOD_ID = "paraglider_stamina_replacer";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean shouldRender = true;

    public ParagliderStaminaReplacer(FMLJavaModLoadingContext context) {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(
                ModConfig.Type.CLIENT,
                ClientConfig.SPEC
        );

        ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (minecraft, parent) -> new StaminaHudConfigScreen(parent)
                )
        );
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void registerOverlay(RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(),"stamina_bar", new StaminaHUDBar());
            LOGGER.info("StaminaHUDBar registered correctly");
        }
    }
}
