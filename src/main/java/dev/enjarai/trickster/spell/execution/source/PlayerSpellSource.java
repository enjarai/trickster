package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.CrowMindAttachment;
import dev.enjarai.trickster.spell.fragment.slot.StorageSource;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.mana.PlayerManaPool;
import net.minecraft.util.math.BlockPos;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.mana.MutableManaPool;
import dev.enjarai.trickster.spell.mana.generation.ManaHandler;
import dev.enjarai.trickster.spell.mana.generation.PlayerManaHandler;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import dev.enjarai.trickster.spell.mana.CachedInventoryManaPool;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.ComponentKey;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSpellSource implements SpellSource {
    private final ServerPlayerEntity player;
    private final SpellExecutionManager executionManager;
    private final CachedInventoryManaPool pool;
    private final EquipmentSlot slot = EquipmentSlot.MAINHAND;

    public PlayerSpellSource(ServerPlayerEntity player) {
        this(player, ModEntityComponents.CASTER.get(player).getExecutionManager());
    }

    public PlayerSpellSource(ServerPlayerEntity player, SpellExecutionManager executionManager) {
        this.player = player;
        this.executionManager = executionManager;
        this.pool = new PlayerManaPool(player);
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
    public Optional<Inventory> getInventory() {
        return Optional.of(player.getInventory());
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
                .or(
                        () -> Optional.ofNullable(player.getOffHandStack())
                                .filter(filter)
                );
    }

    @Override
    public Optional<SlotFragment> getOtherHandSlot() {
        if (slot == EquipmentSlot.MAINHAND) {
            return Optional.of(new SlotFragment(
                    new StorageSource.Slot(PlayerInventory.OFF_HAND_SLOT, StorageSource.Caster.INSTANCE),
                    VariantType.ITEM
            ));
        } else if (slot == EquipmentSlot.OFFHAND) {
            return Optional.of(new SlotFragment(
                    new StorageSource.Slot(player.getInventory().selectedSlot, StorageSource.Caster.INSTANCE),
                    VariantType.ITEM
            ));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpellExecutionManager> getExecutionManager() {
        return Optional.of(executionManager);
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

    @Override
    public MutableManaPool getManaPool() {
        return pool;
    }

    public static boolean isSpellStack(ItemStack stack) {
        return stack.contains(ModComponents.FRAGMENT) ||
                (stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT));
    }

    @Override
    public Vector3d getPos() {
        return new Vector3d(player.getX(), player.getY(), player.getZ());
    }

    @Override
    public BlockPos getBlockPos() {
        return player.getBlockPos();
    }

    @Override
    public Optional<Vector3d> getFacing() {
        return Optional.of(player.getRotationVector().toVector3d());
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
        player.setAttached(ModAttachments.CROW_MIND, new CrowMindAttachment(fragment));
    }

    @Override
    public ManaHandler getManaHandler() {
        return new PlayerManaHandler(player);
    }

    @Override
    public void offerOrDropItem(ItemStack stack) {
        player.getInventory().offerOrDrop(stack);
    }
}
