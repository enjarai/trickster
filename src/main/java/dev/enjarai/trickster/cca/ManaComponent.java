package dev.enjarai.trickster.cca;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.misc.ModDamageTypes;
import dev.enjarai.trickster.spell.ManaPool;
import dev.enjarai.trickster.spell.SimpleManaPool;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.Objects;
import java.util.UUID;

public class ManaComponent extends SimpleManaPool implements AutoSyncedComponent, CommonTickingComponent {
    private final LivingEntity entity;
    private final boolean manaDevoid;

    public static Codec<ManaComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf("entity_uuid").forGetter(manaComponent -> manaComponent.entity.getUuid()),
            World.CODEC.fieldOf("entity_world").forGetter(manaComponent -> manaComponent.entity.getWorld().getRegistryKey())
    ).apply(instance, (entityUuid, worldRegistryKey) -> ModEntityCumponents.MANA.get(Objects.requireNonNull(Objects.requireNonNull(Trickster.getCurrentServer().getWorld(worldRegistryKey)).getEntity(entityUuid)))));

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
    public Codec<? extends ManaPool> getCodec() {
        return CODEC;
    }

    /**
     * Returns whether the entity is still alive.
     */
    @Override
    public boolean decrease(float amount) {
        if (manaDevoid)
            return true;

        float f = mana - amount;
        super.decrease(amount);

        if (f < 0) {
            entity.damage(ModDamageTypes.of(entity.getWorld(), ModDamageTypes.MANA_OVERFLUX), ManaPool.healthFromMana(f * -1));
            return entity.isAlive();
        }

        return true;
    }
}
