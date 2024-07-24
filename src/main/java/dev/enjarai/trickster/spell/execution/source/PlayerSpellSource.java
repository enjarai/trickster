package dev.enjarai.trickster.spell.execution.source;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.CrowMind;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.mana.ManaLink;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.NotEnoughManaBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;

import java.util.*;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSpellSource extends SpellSource {
    private final ServerPlayerEntity player;
    private final EquipmentSlot slot = EquipmentSlot.MAINHAND;

    public PlayerSpellSource(ServerPlayerEntity player) {
        this(ModEntityCumponents.MANA.get(player), player);
    }

    public PlayerSpellSource(ManaPool manaPool, ServerPlayerEntity player) {
        super();
        this.player = player;
    }

    @Override
    public ManaPool getManaPool() {
        return player.getComponent(ModEntityCumponents.MANA);
    }

    @Override
    public void useMana(Trick source, float amount) throws BlunderException {
        try {
            super.useMana(source, amount);
        } catch (NotEnoughManaBlunder blunder) {
            ModCriteria.MANA_OVERFLUX.trigger(player);
            throw blunder;
        }

        ModCriteria.MANA_USED.trigger(player, amount);
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
        addManaLink(source, new ManaLink(getManaPool(), target, player.getHealth(), limit));
    }
}
