package dev.enjarai.trickster.spell;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.cca.ManaComponent;
import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.TrickBlunderException;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Optional;

@SuppressWarnings("UnstableApiUsage")
public class PlayerSpellContext extends SpellContext {
    private final ServerPlayerEntity player;
    private final ManaComponent manaPool;
    private final EquipmentSlot slot;

    public PlayerSpellContext(ServerPlayerEntity player, EquipmentSlot slot) {
        this.player = player;
        this.manaPool = ModEntityCumponents.MANA.get(player);
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

    @Override
    public void useMana(Trick source, float amount) throws BlunderException {
        if (!manaLinks.isEmpty()) {
            float totalAvailable = 0;
            float leftOver = 0;
            var toUnlink = new ArrayList<ManaLink>();

            for (var link : manaLinks) {
                totalAvailable += link.getAvailable();
            }

            for (var link : manaLinks) {
                float available = link.getAvailable();
                float ratio = available / totalAvailable;
                float ratioD = amount * ratio;
                float used = link.useMana(ratioD);

                if (used < ratioD) {
                    leftOver += ratioD - used;
                    toUnlink.add(link);
                }
            }

            manaLinks.removeAll(toUnlink);
            amount = leftOver;
        }

        if (!manaPool.decrease(amount)) {
            throw new EntityInvalidBlunder(source); //TODO: make proper blunder
        }
    }

    @Override
    public float getMana() {
        return manaPool.get();
    }

    @Override
    public float getMaxMana() {
        return manaPool.get();
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
}
