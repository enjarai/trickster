package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.effects.ModEffects;
import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.misc.ModDamageTypes;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.ManaPoolType;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import io.wispforest.accessories.api.slot.SlotReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class ManaComponent extends SimpleManaPool implements CommonTickingComponent {
    private final LivingEntity entity;
    private final boolean manaDevoid;

    public ManaComponent(LivingEntity entity) {
        super(0); // Max mana gets updated later
        this.entity = entity;
        this.manaDevoid = entity.getType().isIn(ModEntities.MANA_DEVOID);
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        mana = tag.getFloat("mana");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putFloat("mana", mana);
    }

    @Override
    public void tick() {
        if (manaDevoid)
            return;

        if (entity.age % 80 == 0 && entity instanceof ServerPlayerEntity player) {
            processManaDeficiency(player);
        }

        maxMana = ManaPool.manaFromHealth(entity.getMaxHealth());
        stdIncrease();
    }

    private void processManaDeficiency(ServerPlayerEntity player) {
        var inventory = player.getInventory();
        var effect = 0;

        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);
            var component = stack.get(ModComponents.ENTITY_STORAGE);
            if (component != null && component.nbt().isPresent()) {
                effect++;
            }
        }

        // Handle the hat slot separately.
        // Can we modify this to generically work with all accessories slots?
        var hat = SlotReference.of(player, "hat", 0);
        var component = hat.getStack().get(ModComponents.ENTITY_STORAGE);
        if (component != null && component.nbt().isPresent()) {
            effect++;
        }

        if (effect > 0) {
            player.addStatusEffect(new StatusEffectInstance(
                    ModEffects.MANA_DEFICIENCY,
                    320, effect - 1,
                    false, false, true
            ));
        }
    }

    @Override
    public void stdIncrease() {
        var multiplier = 1;

        var hyperflux = entity.getStatusEffect(ModEffects.MANA_BOOST);
        if (hyperflux != null) {
            multiplier *= hyperflux.getAmplifier() + 2;
        }

        var hypoflux = entity.getStatusEffect(ModEffects.MANA_DEFICIENCY);
        if (hypoflux != null) {
            multiplier /= hypoflux.getAmplifier() + 2;
        }

        stdIncrease(multiplier);
    }

    @Override
    public ManaPoolType<?> type() {
        return null;
    }

    /**
     * Returns whether the entity is still alive and hasn't triggered a totem.
     */
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean decrease(float amount) {
        if (manaDevoid)
            return true;

        float f = mana - amount;
        super.decrease(amount);

        if (f < 0) {
            entity.damage(ModDamageTypes.of(entity.getWorld(), ModDamageTypes.MANA_OVERFLUX), ManaPool.healthFromMana(f * -1));
            return entity.isAlive() && !((entity.getAttached(ModAttachments.WHY_IS_THERE_NO_WAY_TO_DETECT_THIS) instanceof Boolean b) && Boolean.TRUE.equals(b));
        }

        return true;
    }
}
