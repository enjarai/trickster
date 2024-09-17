package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;

public record MementoChargeComponent(float maxMana, int usesLeft) {
    public static final Codec<MementoChargeComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("max_mana").forGetter(MementoChargeComponent::maxMana),
            Codec.INT.fieldOf("uses_left").forGetter(MementoChargeComponent::usesLeft)
    ).apply(builder, MementoChargeComponent::new));

    public ItemStack use(ItemStack stack) {
        stack.set(ModComponents.MEMENTO_CHARGE, new MementoChargeComponent(maxMana, usesLeft - 1));
        return stack;
    }
}
