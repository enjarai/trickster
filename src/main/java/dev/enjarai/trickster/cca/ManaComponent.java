package dev.enjarai.trickster.cca;

import dev.enjarai.trickster.ModAttachments;
import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.misc.ModDamageTypes;
import dev.enjarai.trickster.spell.mana.ManaPool;
import dev.enjarai.trickster.spell.mana.ManaPoolType;
import dev.enjarai.trickster.spell.mana.SimpleManaPool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

public class ManaComponent extends SimpleManaPool implements AutoSyncedComponent, CommonTickingComponent {
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
    public void applySyncPacket(RegistryByteBuf buf) {
        mana = buf.readFloat();
    }

    @Override
    public void writeSyncPacket(RegistryByteBuf buf, ServerPlayerEntity recipient) {
        buf.writeFloat(mana);
    }

    @Override
    public void tick() {
        if (manaDevoid)
            return;

        maxMana = ManaPool.manaFromHealth(entity.getMaxHealth());
        stdIncrease();
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
