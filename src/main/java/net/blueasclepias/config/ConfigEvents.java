package net.blueasclepias.config;

import net.blueasclepias.core.ParagliderStaminaReplacer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(
        modid = ParagliderStaminaReplacer.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ConfigEvents {

    @SubscribeEvent
    public static void onConfigLoad(final ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == ClientConfig.SPEC) {
            HudConfigCache.bake();
        }
    }

    @SubscribeEvent
    public static void onConfigReload(final ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == ClientConfig.SPEC) {
            HudConfigCache.bake();
        }
    }
}

