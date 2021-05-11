package com.mpcs.craftculator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

@Mod("craftculator")
public class CraftculatorMod extends AbstractGui {
    public static KeyBinding openCalcKeybind;

    public CraftculatorMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CalcConfig.spec);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        openCalcKeybind = new KeyBinding("key.craftculator.open.desc", GLFW.GLFW_KEY_H, "key.craftculator.category");
        ClientRegistry.registerKeyBinding(openCalcKeybind);
    }

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (openCalcKeybind.isDown()) {
            if (Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new CalcScreen(new StringTextComponent("calc")));
            }
        }
    }
}
