package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.spell.ManaPool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class ManaComponent extends ManaPool implements AutoSyncedComponent, CommonTickingComponent {
    private final LivingEntity entity;

    public ManaComponent(LivingEntity entity) {
        this.entity = entity;
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
    public void applySyncPacket(RegistryByteBuf buf) {
        mana = buf.readFloat();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeFloat(mana);
    }

    @Override
    public void tick() {
        maxMana = ManaPool.manaFromHealth(entity.getMaxHealth());
        stdIncrease();
    }

    @Override
    public void increase(float amount) {
        mana = Math.max(Math.min(mana + amount, maxMana), 0);
    }

    /**
     * Returns whether the entity is still alive.
     */
    @Override
    public boolean decrease(float amount) {
        float f = mana - amount;
        mana = Math.max(Math.min(mana - amount, maxMana), 0);

        if (f < 0) { //TODO: funny death messages
            entity.damage(new DamageSource(entity.getRegistryManager().get(DamageTypes.MAGIC.getRegistryRef()).entryOf(DamageTypes.MAGIC)),
                    ManaPool.healthFromMana(f * -1));

            return !entity.isDead();
        }

        return true;
    }
}
