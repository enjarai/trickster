package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSpellContext extends SpellContext {
    private final ServerPlayerEntity player;
    private final EquipmentSlot slot;

    public PlayerSpellContext(ServerPlayerEntity player, EquipmentSlot slot) {
        this(0, player, slot);
    }

    public PlayerSpellContext(int recursions, ServerPlayerEntity player, EquipmentSlot slot) {
        this(ModEntityCumponents.MANA.get(player), recursions, player, slot);
    }

    public PlayerSpellContext(ManaPool manaPool, ServerPlayerEntity player, EquipmentSlot slot) {
        this(manaPool, 0, player, slot);
    }

    public PlayerSpellContext(ManaPool manaPool, int recursions, ServerPlayerEntity player, EquipmentSlot slot) {
        super(manaPool, recursions);
        this.player = player;
        this.slot = slot;
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
    public Optional<ItemStack> getOtherHandSpellStack() {
        if (slot == EquipmentSlot.MAINHAND) {
            return Optional.ofNullable(player.getOffHandStack()).filter(this::isSpellStack);
        } else if (slot == EquipmentSlot.OFFHAND) {
            return Optional.ofNullable(player.getMainHandStack()).filter(this::isSpellStack);
        }

        return Optional
                .ofNullable(player.getMainHandStack())
                .filter(this::isSpellStack)
                .or(() -> Optional.ofNullable(player.getOffHandStack())
                        .filter(this::isSpellStack));
    }

    protected boolean isSpellStack(ItemStack stack) {
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

    @Override
    public void addManaLink(Trick source, LivingEntity target, float limit) {
        addManaLink(source, new ManaLink(manaPool, target, player.getHealth(), limit));
    }
}
