package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Optional;

public class PlayerSpellContext extends SpellContext {
    private final ServerPlayerEntity player;
    private final EquipmentSlot slot;

    public PlayerSpellContext(ServerPlayerEntity player, EquipmentSlot slot) {
        super();
        this.player = player;
        this.slot = slot;
    }

    @Override
    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.of(player);
    }

    @Override
    public Optional<ItemStack> getOtherHandSpellStack() {
        if (slot == EquipmentSlot.MAINHAND) {
            return Optional.ofNullable(player.getOffHandStack()).filter(s -> s.contains(ModComponents.SPELL));
        } else if (slot == EquipmentSlot.OFFHAND) {
            return Optional.ofNullable(player.getMainHandStack()).filter(s -> s.contains(ModComponents.SPELL));
        }

        return Optional
                .ofNullable(player.getMainHandStack())
                .filter(s -> s.contains(ModComponents.SPELL))
                .or(() -> Optional.ofNullable(player.getOffHandStack())
                        .filter(s -> s.contains(ModComponents.SPELL)));
    }
}
