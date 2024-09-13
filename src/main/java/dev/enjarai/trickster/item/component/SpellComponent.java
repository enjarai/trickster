package dev.enjarai.trickster.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SpellComponent(SpellPart spell, Optional<String> name, boolean immutable, boolean closed) {
    public static final Codec<SpellComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            CodecUtils.toCodec(SpellPart.ENDEC).fieldOf("spell").forGetter(SpellComponent::spell),
            Codec.STRING.optionalFieldOf("name").forGetter(SpellComponent::name),
            Codec.BOOL.optionalFieldOf("immutable", false).forGetter(SpellComponent::immutable),
            Codec.BOOL.optionalFieldOf("closed", false).forGetter(SpellComponent::closed)
    ).apply(instance, SpellComponent::new));

    public SpellComponent(SpellPart spell) {
        this(spell, Optional.empty(), false, false);
    }

    public SpellComponent(SpellPart spell, boolean immutable) {
        this(spell, Optional.empty(), immutable, false);
    }

    public SpellComponent withClosed(Optional<String> name) {
        return new SpellComponent(spell, name, immutable, true);
    }

    public static Optional<SpellPart> getSpellPart(ItemStack stack) {
        return getReferencedStack(stack)
                .filter(stack2 -> stack2.contains(ModComponents.SPELL))
                .map(stack2 -> stack2.get(ModComponents.SPELL))
                .filter(component -> !component.closed())
                .map(SpellComponent::spell);
    }

    public static boolean setSpellPart(ItemStack stack, SpellPart spell, Optional<String> name, boolean closed) {
        return modifyReferencedStack(stack, stack2 -> {
            if (stack2.contains(ModComponents.SPELL) && stack2.get(ModComponents.SPELL).immutable()) {
                return false;
            }

            stack2.set(ModComponents.SPELL, new SpellComponent(spell, name, false, closed));
            return true;
        });
    }

    public static Optional<ItemStack> getReferencedStack(ItemStack stack) {
        var stackOptional = Optional.of(stack);

        if (stack.isIn(ModItems.HOLDABLE_HAT) && stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)) {
            stackOptional = stack.get(DataComponentTypes.CONTAINER).stream()
                    .skip(stack.get(ModComponents.SELECTED_SLOT).slot())
                    .findFirst();
        }

        return stackOptional;
    }

    public static boolean modifyReferencedStack(ItemStack stack, Function<ItemStack, Boolean> modifier) {
        if (stack.isIn(ModItems.HOLDABLE_HAT) && stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)) {
            var stacks = stack.get(DataComponentTypes.CONTAINER).stream().collect(Collectors.toCollection(ArrayList::new));
            var index = stack.get(ModComponents.SELECTED_SLOT).slot();

            var stack2 = stacks.get(index);
            if (modifier.apply(stack2)) {
                stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stacks));
                return true;
            }
            return false;
        } else {
            return modifier.apply(stack);
        }
    }
}
