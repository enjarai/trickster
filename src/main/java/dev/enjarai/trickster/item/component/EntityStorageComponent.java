package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;

import java.util.Optional;

public record EntityStorageComponent(Optional<NbtCompound> nbt) {
    public static final Codec<EntityStorageComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            NbtCompound.CODEC.optionalFieldOf("compound").forGetter(EntityStorageComponent::nbt)).apply(instance, EntityStorageComponent::new));
}

