package dev.enjarai.trickster.net;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SelectedSlotComponent;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.item.component.WrittenScrollMetaComponent;
import io.wispforest.owo.network.OwoNetChannel;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.Optional;

public class ModNetworking {
    public static final OwoNetChannel CHANNEL = OwoNetChannel.create(Trickster.id("main"));

    public static void register() {
        CHANNEL.registerServerbound(MladyPacket.class, (packet, access) -> {
            var player = access.player();
            var inventory = player.getInventory();

            if (packet.hold()) {
                if (player.getEquippedStack(EquipmentSlot.HEAD).isIn(ModItems.HOLDABLE_HAT) && player.getOffHandStack().isEmpty()) {
                    var headStack = inventory.getArmorStack(EquipmentSlot.HEAD.getEntitySlotId());
                    inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), ItemStack.EMPTY);
                    inventory.offHand.set(0, headStack);
                }
            } else {
                if (player.getEquippedStack(EquipmentSlot.HEAD).isEmpty() && player.getOffHandStack().isIn(ModItems.HOLDABLE_HAT)) {
                    var offHandStack = inventory.offHand.getFirst();
                    inventory.offHand.set(0, ItemStack.EMPTY);
                    inventory.armor.set(EquipmentSlot.HEAD.getEntitySlotId(), offHandStack);
                }
            }
        });

        CHANNEL.registerServerbound(ScrollInGamePacket.class, (packet, access) -> {
            var player = access.player();

            if (Math.abs(packet.amount()) >= 1f) {
                ItemStack stack = null;

                if (player.isSneaking() && player.getMainHandStack().contains(ModComponents.SELECTED_SLOT)) {
                    stack = player.getMainHandStack();
                } else if (player.getOffHandStack().contains(ModComponents.SELECTED_SLOT)) {
                    stack = player.getOffHandStack();
                }

                if (stack != null) {
                    var current = stack.get(ModComponents.SELECTED_SLOT);
                    var container = stack.get(DataComponentTypes.CONTAINER);

                    if (current != null && container != null) {
                        var newSlot = Math.round(current.slot() + packet.amount());
                        int maxSlot = (int) Math.min(current.maxSlot(), container.stream().count());

                        if (maxSlot > 0) {
                            while (newSlot < 0) {
                                newSlot += maxSlot;
                            }
                            while (newSlot >= maxSlot) {
                                newSlot -= maxSlot;
                            }
                        } else {
                            newSlot = 0;
                        }

                        stack.set(ModComponents.SELECTED_SLOT,
                                new SelectedSlotComponent(newSlot, current.maxSlot()));

                        var name = container.stream().skip(newSlot).findFirst().filter(s -> !s.isEmpty());
                        var message = Text.translatable("trickster.scroll_hat", newSlot);

                        if (name.isPresent()) {
                            message = message.append(" [").append(name.get().getName()).append("]");
                        }

                        player.sendMessage(message, true);
                    }
                }
            }
        });

        CHANNEL.registerServerbound(IsEditingScrollPacket.class, (packet, access) -> {
            var player = access.player();
            player.getComponent(ModEntityCumponents.IS_EDITING_SCROLL).setEditing(packet.isEditing());
        });

        CHANNEL.registerServerbound(SignScrollPacket.class, (packet, access) -> {
            var player = access.player();
            var stack = player.getStackInHand(packet.hand());

            if (stack.isOf(ModItems.SCROLL_AND_QUILL)) {
                var component = stack.get(ModComponents.SPELL);
                if (component == null) {
                    return;
                }

                var spell = component.spell();
                var newStack = ModItems.WRITTEN_SCROLL.getDefaultStack();

                newStack.set(ModComponents.SPELL, new SpellComponent(spell, true));
                newStack.set(ModComponents.WRITTEN_SCROLL_META, new WrittenScrollMetaComponent(
                        packet.name(), player.getName().getString(), 0
                ));
                newStack.setCount(stack.getCount());

                player.setStackInHand(packet.hand(), newStack);
                player.swingHand(packet.hand());
            }
        });

        CHANNEL.registerClientboundDeferred(RebuildChunkPacket.class);
    }
}
