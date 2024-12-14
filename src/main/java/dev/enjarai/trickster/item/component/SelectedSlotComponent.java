package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SelectedSlotComponent(int slot, int maxSlot) {
    public static final Codec<SelectedSlotComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("slot").forGetter(SelectedSlotComponent::slot),
            Codec.INT.fieldOf("max_slot").forGetter(SelectedSlotComponent::maxSlot)).apply(instance, SelectedSlotComponent::new));
}
