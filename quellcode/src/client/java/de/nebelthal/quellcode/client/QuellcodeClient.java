package de.nebelthal.quellcode.client;

import de.nebelthal.quellcode.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public final class QuellcodeClient implements ClientModInitializer {
    private static KeyBinding key(String name, int code) {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding("key.quellcode." + name,
            InputUtil.Type.KEYSYM, code, "category.quellcode"));
    }
    @Override public void onInitializeClient() {
        Quellcode.VEHICLES.values().forEach(type -> EntityRendererRegistry.register(type, VehicleRendererV2::new));
        KeyBinding lights = key("lights", GLFW.GLFW_KEY_B);
        KeyBinding siren = key("siren", GLFW.GLFW_KEY_N);
        KeyBinding ladder = key("ladder", GLFW.GLFW_KEY_L);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean lightPressed = false, sirenPressed = false, ladderPressed = false;
            while (lights.wasPressed()) lightPressed = true;
            while (siren.wasPressed()) sirenPressed = true;
            while (ladder.wasPressed()) ladderPressed = true;
            if (client.player != null && client.player.getVehicle() instanceof VehicleEntity && ClientPlayNetworking.canSend(ControlPayload.ID)) {
                boolean active = client.currentScreen == null;
                ClientPlayNetworking.send(new ControlPayload(
                    active && client.options.forwardKey.isPressed(), active && client.options.backKey.isPressed(),
                    active && client.options.leftKey.isPressed(), active && client.options.rightKey.isPressed(),
                    active && lightPressed, active && sirenPressed, active && ladderPressed));
            }
        });
    }
}
