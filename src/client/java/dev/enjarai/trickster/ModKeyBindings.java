package dev.enjarai.trickster;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.MladyPacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.ScrollInGamePacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyBinding TAKE_HAT = new KeyBinding("key.trickster.take_hat", GLFW.GLFW_KEY_G, "trickster");

    public static void register() {
        KeyBindingHelper.registerKeyBinding(TAKE_HAT);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var player = client.player;
            if (player != null && client.currentScreen == null) {
                if (TAKE_HAT.wasPressed()) {
                    if (player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModItems.HOLDABLE_HAT) && player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) {
                        ModNetworking.CHANNEL.clientHandle().send(new MladyPacket(true));
                    } else if (player.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
                        ModNetworking.CHANNEL.clientHandle().send(new MladyPacket(false));
                    }
                }
            }
        });
    }

    public static boolean interceptScroll(float amount) {
        var player = MinecraftClient.getInstance().player;
        if (player != null && (player.getOffHandStack().contains(ModComponents.SELECTED_SLOT)
                || (player.isSneaking() && player.getMainHandStack().contains(ModComponents.SELECTED_SLOT)))) {
            ModNetworking.CHANNEL.clientHandle().send(new ScrollInGamePacket(amount));
            return true;
        }
        return false;
    }
}
