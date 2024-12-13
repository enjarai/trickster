package dev.enjarai.trickster.datagen;

import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.enjarai.trickster.item.component.ManaComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;

public record RandomManaLootFunction(float fractionalMinimum, float fractionalMaximum) implements LootFunction {
    public static final MapCodec<RandomManaLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("min").forGetter(RandomManaLootFunction::fractionalMinimum),
            Codec.FLOAT.fieldOf("max").forGetter(RandomManaLootFunction::fractionalMaximum)).apply(instance, RandomManaLootFunction::new));

    private static final Random random = new Random();

    @Override
    public LootFunctionType<RandomManaLootFunction> getType() {
        return ModLoot.RANDOM_MANA_FUNCTION_TYPE;
    }

    @Override
    public ItemStack apply(ItemStack stack, LootContext lootContext) {
        var pool = stack.get(ModComponents.MANA).pool().makeClone(lootContext.getWorld());

        pool.set(pool.getMax(lootContext.getWorld()) * (random.nextFloat() * (fractionalMaximum - fractionalMinimum) + fractionalMinimum),
                lootContext.getWorld());
        stack.set(ModComponents.MANA, new ManaComponent(pool));

        return stack;
    }
}
