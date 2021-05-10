package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("examplemod")
public class ExampleMod extends AbstractGui
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static KeyBinding openCalcKeybind;

    private static CalcScreen calcScreen;

    public ExampleMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        MinecraftForge.EVENT_BUS.register(this);
        calcScreen = new CalcScreen(new StringTextComponent("calc"));
    }

    @SubscribeEvent
    public void onKeyInputEvent(InputEvent.KeyInputEvent event) {
        if (openCalcKeybind.isDown() && event.getAction() == GLFW.GLFW_PRESS) {
            if (Minecraft.getInstance().screen == null) {
                Minecraft.getInstance().setScreen(new CalcScreen(new StringTextComponent("calc")));
            }
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {

        //calcScreen.render(event.getMatrixStack(), 0, 0, event.getPartialTicks());
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        openCalcKeybind = new KeyBinding("key.craftculator.open.desc", GLFW.GLFW_KEY_H, "key.craftculator.category");
        ClientRegistry.registerKeyBinding(openCalcKeybind);
    }



    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // TODO: info about being loaded on server
    }
}
