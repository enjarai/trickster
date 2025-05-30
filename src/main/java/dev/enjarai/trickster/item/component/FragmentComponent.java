package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import io.vavr.Function1;
import io.vavr.collection.HashMap;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import io.wispforest.owo.serialization.CodecUtils;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record FragmentComponent(Fragment value, Optional<Text> name, boolean immutable, boolean closed) {

    private static final Endec<FragmentComponent> OLD_ENDEC = StructEndecBuilder.of(
            SpellPart.ENDEC.fieldOf("spell", comp -> {
                throw new IllegalStateException("Serializing as a spell is no longer supported");
            }),
            Endec.BOOLEAN.optionalFieldOf("immutable", FragmentComponent::immutable, false),
            Endec.BOOLEAN.optionalFieldOf("closed", FragmentComponent::closed, false),
            (value, immutable, closed) -> new FragmentComponent(value, Optional.empty(), immutable, closed)
    );
    private static final Endec<FragmentComponent> NEW_ENDEC = StructEndecBuilder.of(
            Fragment.COMPACT_ENDEC.fieldOf("value", FragmentComponent::value),
            EndecTomfoolery.safeOptionalOf(CodecUtils.toEndec(TextCodecs.STRINGIFIED_CODEC)).fieldOf("name", FragmentComponent::name),
            Endec.BOOLEAN.optionalFieldOf("immutable", FragmentComponent::immutable, false),
            Endec.BOOLEAN.optionalFieldOf("closed", FragmentComponent::closed, false),
            FragmentComponent::new
    );
    public static final Endec<FragmentComponent> ENDEC = EndecTomfoolery.withAlternative(NEW_ENDEC, OLD_ENDEC);

    public FragmentComponent(SpellPart spell) {
        this(spell, Optional.empty(), false, false);
    }

    public FragmentComponent(SpellPart spell, boolean immutable) {
        this(spell, Optional.empty(), immutable, false);
    }

    public FragmentComponent withClosed(Optional<Text> name) {
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

    /**
     * @param stack the item stack to write the given fragment.
     * @param fragment the fragment to write.
     * @return the new item stack, or empty if the item's fragment is immutable.
     */
    public static Optional<ItemStack> write(ItemStack stack, Fragment fragment) {
        return write(stack, fragment, false, Optional.empty(), Optional.empty());
    }

    private static java.util.HashMap<Item, Function1<ItemStack, ItemStack>> customWriteBehaviors = new java.util.HashMap<>();

    /**
     * Writes a fragment to an item stack.
     * @param stack the item stack to write the given fragment.
     * @param fragment the fragment to write.
     * @param closed whether the fragment is unreadable.
     * @param player the player that is inscribing to the item, if applicable.
     * @param name the name of the fragment to be displayed in the item's tooltip.
     * @return the new item stack, or empty if the item's fragment is immutable.
     */
    public static Optional<ItemStack> write(ItemStack stack, Fragment fragment, boolean closed, Optional<ServerPlayerEntity> player, Optional<Text> name) {
        fragment = fragment.applyEphemeral();

        if (!FragmentComponent.setValue(stack, fragment, name, closed)) {
            return Optional.empty();
        }

        var behavior = customWriteBehaviors.get(stack.getItem());
        if (behavior != null) {
            stack = behavior.apply(stack);
        }

        player.ifPresent(ModCriteria.INSCRIBE_SPELL::trigger);
        return Optional.of(stack);
    }

    private static java.util.HashMap<Item, Function1<ItemStack, ItemStack>> customResetBehaviors = new java.util.HashMap<>();

    /**
     * Resets an item stack to its default fragment value.
     * @param stack the item stack to clear.
     * @return the new item stack, or empty if the item's fragment is immutable.
     */
    public static Optional<ItemStack> reset(ItemStack stack) {
        if (getReferencedStack(stack).isEmpty()) {
            return Optional.of(stack);
        }

        boolean successful = FragmentComponent.modifyReferencedStack(stack, s -> {
            var component = s.get(ModComponents.FRAGMENT);

            if (component == null) {
                return true;
            }

            if (component.immutable()) {
                return false;
            }

            var itemDefault = s.getItem().getDefaultStack().get(ModComponents.FRAGMENT);

            if (itemDefault != null) {
                s.set(ModComponents.FRAGMENT, itemDefault);
            } else {
                s.remove(ModComponents.FRAGMENT);
            }

            return true;
        });

        if (!successful) {
            return Optional.empty();
        }

        var behavior = customResetBehaviors.get(stack.getItem());
        if (behavior != null) {
            stack = behavior.apply(stack);
        }

        return Optional.of(stack);
    }

    public static Optional<Fragment> getFragment(ItemStack stack) {
        return getReferencedStack(stack)
                .filter(stack2 -> stack2.contains(ModComponents.FRAGMENT))
                .map(stack2 -> stack2.get(ModComponents.FRAGMENT))
                .filter(component -> !component.closed())
                .map(FragmentComponent::value);
    }

    public static boolean setValue(ItemStack stack, Fragment value, Optional<Text> name, boolean closed) {
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

    /**
     * @param stack the item stack to modify with the given function.
     * @param modifier the function to apply to the given stack. Must return true if the stack was modified.
     * @return true if the stack was modified, false otherwise.
     */
    public static boolean modifyReferencedStack(ItemStack stack, Function<ItemStack, Boolean> modifier) {
        if (stack.isIn(ModItems.HOLDABLE_HAT) && stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)) {
            var stacks = stack.get(DataComponentTypes.CONTAINER).stream().collect(Collectors.toCollection(ArrayList::new));
            var stack2 = stacks.get(stack.get(ModComponents.SELECTED_SLOT).slot());
            boolean modified = modifier.apply(stack2);

            if (modified) {
                stack.set(DataComponentTypes.CONTAINER, ContainerComponent.fromStacks(stacks));
            }

            return modified;
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

    public static void registerWriteConversion(Item type, Function1<ItemStack, ItemStack> onWrite) {
        var old = customWriteBehaviors.put(type, onWrite);
        if (old != null) {
            //TODO: this could be improved, translations aren't loaded for modded items
            Trickster.LOGGER.warn("Fragment write conversion for \"{}\" has been overriden", type.getName().getString());
        }
    }

    public static void registerResetConversion(Item type, Function1<ItemStack, ItemStack> onReset) {
        var old = customResetBehaviors.put(type, onReset);
        if (old != null) {
            //TODO: this could be improved, translations aren't loaded for modded items
            Trickster.LOGGER.warn("Fragment reset conversion for \"{}\" has been overriden", type.getName().getString());
        }
    }
}
