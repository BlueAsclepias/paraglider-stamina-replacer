package net.blueasclepias.core;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = ParagliderStaminaReplacer.MOD_ID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class OverlayInterceptor {
    private static final ResourceLocation STAMINA_WHEEL =
            new ResourceLocation("paraglider", "stamina_wheel");

    @SubscribeEvent
    public static void onOverlayPre(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay().id().equals(STAMINA_WHEEL)) {
            event.setCanceled(true);
        }
    }
}
