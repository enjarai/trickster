package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.ImmutableItemBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import io.vavr.collection.HashMap;
import io.wispforest.accessories.endec.CodecUtils;
import io.wispforest.endec.Endec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.Contract;

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
            (value, immutable, closed) -> (new FragmentComponent(value, Optional.empty(), immutable, closed))
    );
    private static final Endec<FragmentComponent> NEW_ENDEC = StructEndecBuilder.of(
            Fragment.ENDEC.fieldOf("value", FragmentComponent::value),
            EndecTomfoolery.safeOptionalOf(CodecUtils.ofCodec(TextCodecs.STRINGIFIED_CODEC)).fieldOf("name", FragmentComponent::name),
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
     * @param stack item stack to write fragment too
     * @return returns updated item stack. or empty on fail
     */
    public static Optional<ItemStack> writeSpell(ItemStack stack, Fragment fragment) {
        return writeSpell(stack, fragment, false, Optional.empty(), Optional.empty());
    }

    public static Optional<ItemStack> writeSpell(ItemStack stack, Fragment fragment, boolean closed, Optional<ServerPlayerEntity> player, Optional<Text> name) {
        var ret = stack;

        if (fragment.type() != FragmentType.SPELL_PART) {
            fragment = new SpellPart(fragment);
        }

        if (!FragmentComponent.setValue(ret, fragment, name, closed)) {
            return Optional.empty();
        }

        if (stack.isOf(Items.BOOK)) {
            ret = stack.withItem(Items.ENCHANTED_BOOK);
        }

        player.ifPresent(ModCriteria.INSCRIBE_SPELL::trigger);
        return Optional.of(ret);
    }

    /**
     * @param stack item stack to clear of fragment component
     * @return returns correct item stack (i.e, if enchanted book is cleared, returns book)
     * on attempting to modify immutable stack, returns empty, so that tricks know to throw
     */

    public static Optional<ItemStack> clearSpell(ItemStack stack) {
        if (getReferencedStack(stack).isEmpty()) return Optional.of(stack);

        boolean successful = FragmentComponent.modifyReferencedStack(stack, (s) -> {
            var component = s.get(ModComponents.FRAGMENT);

            if (component == null) {
                return true;
            }

            if (component.immutable())
                return false;

            var itemDefault = s.getItem().getDefaultStack().get(ModComponents.FRAGMENT);

            if (itemDefault != null) {
                s.set(ModComponents.FRAGMENT, itemDefault);
            } else {
                s.remove(ModComponents.FRAGMENT);
            }

            return true;
        });

        if (!successful) return Optional.empty();

        if (stack.isOf(Items.ENCHANTED_BOOK)
                && (stack.get(DataComponentTypes.STORED_ENCHANTMENTS) instanceof ItemEnchantmentsComponent enchants)
                && enchants.isEmpty()) {
            return Optional.of(stack.withItem(Items.BOOK));
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
     * @param stack  stack to modify with a given function
     * @param modifier function to apply to the given stack. return true if modified
     * @return returns true if stack was modified
     */
    public static boolean modifyReferencedStack(ItemStack stack, Function<ItemStack, Boolean> modifier) {
        if (stack.isIn(ModItems.HOLDABLE_HAT) && stack.contains(DataComponentTypes.CONTAINER) && stack.contains(ModComponents.SELECTED_SLOT)) {
            var stacks = stack.get(DataComponentTypes.CONTAINER).stream().collect(Collectors.toCollection(ArrayList::new));
            var index = stack.get(ModComponents.SELECTED_SLOT).slot();

            var stack2 = stacks.get(index);
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
}
