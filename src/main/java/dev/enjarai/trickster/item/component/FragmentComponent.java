package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import io.vavr.collection.HashMap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record FragmentComponent(Fragment value, Optional<String> name, boolean immutable, boolean closed) {

    private static final Endec<FragmentComponent> OLD_ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", comp -> {
                throw new IllegalStateException("Serializing as a spell is no longer supported");
            }),
            Endec.STRING.optionalOf().optionalFieldOf("name", FragmentComponent::name, Optional.empty()),
            Endec.BOOLEAN.optionalFieldOf("immutable", FragmentComponent::immutable, false),
            Endec.BOOLEAN.optionalFieldOf("closed", FragmentComponent::closed, false),
            FragmentComponent::new);
    private static final Endec<FragmentComponent> NEW_ENDEC = StructEndecBuilder.of(
            Fragment.ENDEC.fieldOf("value", FragmentComponent::value),
            EndecTomfoolery.safeOptionalOf(Endec.STRING).fieldOf("name", FragmentComponent::name),
            Endec.BOOLEAN.optionalFieldOf("immutable", FragmentComponent::immutable, false),
            Endec.BOOLEAN.optionalFieldOf("closed", FragmentComponent::closed, false),
            FragmentComponent::new);
    public static final Endec<FragmentComponent> ENDEC = EndecTomfoolery.withAlternative(NEW_ENDEC, OLD_ENDEC);

    public FragmentComponent(SpellPart spell) {
        this(spell, Optional.empty(), false, false);
    }

    public FragmentComponent(SpellPart spell, boolean immutable) {
        this(spell, Optional.empty(), immutable, false);
    }

    public FragmentComponent withClosed(Optional<String> name) {
        return new FragmentComponent(value, name, immutable, true);
    }

    public static Optional<SpellPart> getSpellPart(ItemStack stack) {
        return getFragment(stack)
                .flatMap(value -> {
                    if (value instanceof SpellPart spell)
                        return Optional.of(spell);

                    return Optional.empty();
                });
    }

    public static Optional<Fragment> getFragment(ItemStack stack) {
        return getReferencedStack(stack)
                .filter(stack2 -> stack2.contains(ModComponents.FRAGMENT))
                .map(stack2 -> stack2.get(ModComponents.FRAGMENT))
                .filter(component -> !component.closed())
                .map(FragmentComponent::value);
    }

    public static boolean setValue(ItemStack stack, Fragment value, Optional<String> name, boolean closed) {
        return modifyReferencedStack(stack, stack2 -> {
            if (stack2.contains(ModComponents.FRAGMENT) && stack2.get(ModComponents.FRAGMENT).immutable()) {
                return false;
            }

            stack2.set(ModComponents.FRAGMENT, new FragmentComponent(value, name, false, closed));
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

    @SuppressWarnings("unchecked")
    public static <T extends Fragment> Optional<T> getValue(ItemStack stack, Class<T> clazz) {
        return Optional.ofNullable(stack)
                .filter(stack2 -> stack2.contains(ModComponents.FRAGMENT))
                .map(stack2 -> stack2.get(ModComponents.FRAGMENT))
                .map(FragmentComponent::value)
                .flatMap(value -> {
                    if (value.getClass() == clazz)
                        return Optional.of((T) value);

                    return Optional.empty();
                });
    }

    public static Optional<HashMap<Pattern, SpellPart>> getMap(ItemStack stack) {
        return getValue(stack, MapFragment.class)
                .map(MapFragment::getMacroMap);
    }

    public static Optional<HashMap<Pattern, SpellPart>> getUserMergedMap(PlayerEntity user, String type) {
        var capability = user.accessoriesCapability();

        if (capability == null)
            return Optional.empty();

        var ringContainer = capability.getContainers().get(type);

        if (ringContainer == null)
            return Optional.empty();

        var result = HashMap.<Pattern, SpellPart>empty();

        for (var pair : ringContainer.getAccessories()) {
            result = result.merge(getMap(pair.getSecond()).orElse(HashMap.empty()));
        }

        return Optional.of(result);
    }

    public static HashMap<Pattern, SpellPart> getUserMergedMap(PlayerEntity user, String type, Supplier<HashMap<Pattern, SpellPart>> otherwise) {
        return getUserMergedMap(user, type).orElseGet(otherwise);
    }
}
