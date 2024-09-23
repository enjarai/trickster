package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSpellSource extends SpellSource {
    private final ServerPlayerEntity player;
    private final EquipmentSlot slot = EquipmentSlot.MAINHAND;

    public PlayerSpellSource(ServerPlayerEntity player) {
        super();
        this.player = player;
    }

    @Override
    public ManaPool getManaPool() {
        return player.getComponent(ModEntityComponents.MANA);
    }

    @Override
    public Optional<ServerPlayerEntity> getPlayer() {
        return Optional.of(player);
    }

    @Override
    public Optional<Entity> getCaster() {
        return Optional.of(player);
    }

    @Override
    public Optional<ItemStack> getOtherHandStack(Predicate<ItemStack> filter) {
        if (slot == EquipmentSlot.MAINHAND) {
            return Optional.ofNullable(player.getOffHandStack()).filter(filter);
        } else if (slot == EquipmentSlot.OFFHAND) {
            return Optional.ofNullable(player.getMainHandStack()).filter(filter);
        }

        return Optional
                .ofNullable(player.getMainHandStack())
                .filter(filter)
                .or(() -> Optional.ofNullable(player.getOffHandStack())
                        .filter(filter));
    }

    @Override
    public Optional<SlotFragment> getOtherHandSlot() {
        if (slot == EquipmentSlot.MAINHAND) {
            return Optional.of(new SlotFragment(PlayerInventory.OFF_HAND_SLOT, Optional.empty()));
        } else if (slot == EquipmentSlot.OFFHAND) {
            return Optional.of(new SlotFragment(player.getInventory().selectedSlot, Optional.empty()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpellExecutionManager> getExecutionManager() {
        return Optional.of(ModEntityComponents.CASTER.get(player).getExecutionManager());
    }

    @Override
    public <T extends Component> Optional<T> getComponent(ComponentKey<T> key) {
        return key.maybeGet(player);
    }

    @Override
    public float getHealth() {
        return player.getHealth();
    }

    @Override
    public float getMaxHealth() {
        return player.getMaxHealth();
    }

    public static boolean isSpellStack(ItemStack stack) {
        return stack.contains(ModComponents.SPELL) ||
                (stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT));
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(player.getX(), player.getY(), player.getZ());
    }

    @Override
    public ServerWorld getWorld() {
        return player.getServerWorld();
    }

    @Override
    public Fragment getCrowMind() {
        var crow = player.getAttached(ModAttachments.CROW_MIND);
        if (crow == null) {
            return VoidFragment.INSTANCE;
        }
        return crow.fragment();
    }

    @Override
    public void setCrowMind(Fragment fragment) {
        player.setAttached(ModAttachments.CROW_MIND, new CrowMind(fragment));
    }
}
