package dev.enjarai.trickster;

import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.net.MladyPacket;
import dev.enjarai.trickster.net.ModNetworking;
import dev.enjarai.trickster.net.ScrollHatPacket;
import dev.enjarai.trickster.net.SpellEditPacket;
import dev.enjarai.trickster.pond.QuackingInGameHud;
import io.wispforest.accessories.api.slot.SlotReference;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.EquipmentSlot;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static final KeyBinding TAKE_HAT = new KeyBinding("key.trickster.take_hat", GLFW.GLFW_KEY_G, "key.categories.trickster");
    public static final KeyBinding MODIFY_SPELL = new KeyBinding("key.trickster.modify_spell", GLFW.GLFW_KEY_F6, "key.categories.trickster");

    public static void register() {
        KeyBindingHelper.registerKeyBinding(TAKE_HAT);
        KeyBindingHelper.registerKeyBinding(MODIFY_SPELL);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            var player = client.player;
            if (player != null && client.currentScreen == null) {
                if (TAKE_HAT.wasPressed()) {
                    var hat = SlotReference.of(player, "hat", 0);
                    var hatStack = hat.getStack();
                    if (((hatStack != null && hatStack.isIn(ModItems.HOLDABLE_HAT)) || player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModItems.HOLDABLE_HAT)) && player.getEquippedStack(EquipmentSlot.OFFHAND).isEmpty()) {
                        ModNetworking.CHANNEL.clientHandle().send(new MladyPacket(true));
                    } else if (((hatStack != null && hatStack.isEmpty()) || player.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) && player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
                        ModNetworking.CHANNEL.clientHandle().send(new MladyPacket(false));
                    }
                    // Consume remaining key presses
                    while (TAKE_HAT.wasPressed()) { }
                }

                if (MODIFY_SPELL.wasPressed()) {
                    // Avoid unnecessary packets
                    if (client.player.isCreative() && !client.player.getMainHandStack().isEmpty()) {
                        ModNetworking.CHANNEL.clientHandle().send(new SpellEditPacket());
                    }
                    // Consume remaining key presses
                    while (MODIFY_SPELL.wasPressed()) { }
                }
            }
        });
    }

    public static boolean interceptScroll(float amount) {
        var player = MinecraftClient.getInstance().player;
        if (player != null
                && (((Trickster.CONFIG.topHatInterceptScrolling() || player.isSneaking())
                        && player.getOffHandStack().contains(ModComponents.SELECTED_SLOT))
                    || (player.isSneaking()
                        && player.getMainHandStack().contains(ModComponents.SELECTED_SLOT)))
        ) {
            var delta = Trickster.CONFIG.invertTopHatScrolling() ? amount : -amount;
            var packet = new ScrollHatPacket(delta, true);
            packet.handleCommon(player);
            ModNetworking.CHANNEL.clientHandle().send(packet);
            ((QuackingInGameHud) MinecraftClient.getInstance().inGameHud).trickster$scrollTheHat((int) delta);
            return true;
        }
        return false;
    }
}
