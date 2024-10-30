package dev.enjarai.trickster.spell.trick.basic;

import com.mojang.datafixers.util.Pair;

import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ImmutableItemBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Optional;

public class WriteSpellTrick extends Trick {
    public WriteSpellTrick() {
        super(Pattern.of(1, 4, 7, 8, 5, 4, 3, 6, 7));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return activate(ctx, fragments, false);
    }

    public Fragment activate(SpellContext ctx, List<Fragment> fragments, boolean closed) throws BlunderException {
        var player = ctx.source().getPlayer();
        var input = supposeInput(fragments, 0).map(Fragment::applyEphemeral).flatMap(fragment -> {
            if (supposeType(fragment, FragmentType.VOID).isPresent()) {
                return Optional.empty();
            } else {
                return Optional.of(fragment);
            }
        });

        return player.map(serverPlayerEntity -> Pair.of(serverPlayerEntity, serverPlayerEntity.getOffHandStack())).map(pair -> {
            var serverPlayer = pair.getFirst();
            var stack = pair.getSecond();

            input.ifPresentOrElse(v -> {
                var stack2 = stack;

                if (stack2.isOf(Items.BOOK)) {
                    serverPlayer.equipStack(EquipmentSlot.OFFHAND, stack2.withItem(Items.ENCHANTED_BOOK));
                    stack2 = serverPlayer.getOffHandStack();
                } else if (stack2.isOf(Items.ENCHANTED_BOOK)
                        && (stack.get(DataComponentTypes.STORED_ENCHANTMENTS) instanceof ItemEnchantmentsComponent enchants)
                        && enchants.isEmpty()) {
                    serverPlayer.equipStack(EquipmentSlot.OFFHAND, stack2.withItem(Items.BOOK));
                    stack2 = serverPlayer.getOffHandStack();
                }

                if (!FragmentComponent.setValue(stack2, v, supposeInput(fragments, FragmentType.STRING, 1).flatMap(str -> Optional.of(str.value())), closed)) {
                    throw new ImmutableItemBlunder(this);
                }
            }, () -> {
                var stack2 = stack;

                if (stack2.isOf(Items.ENCHANTED_BOOK)
                        && (stack.get(DataComponentTypes.STORED_ENCHANTMENTS) instanceof ItemEnchantmentsComponent enchants)
                        && enchants.isEmpty()) {
                    serverPlayer.equipStack(EquipmentSlot.OFFHAND, stack2.withItem(Items.BOOK));
                    stack2 = serverPlayer.getOffHandStack();
                }

                if (!FragmentComponent.modifyReferencedStack(stack2, s -> {
                    var spellComponent = s.get(ModComponents.FRAGMENT);

                    if (spellComponent == null || spellComponent.immutable())
                        return false;

                    var itemDefault = s.getItem().getDefaultStack();
                    var itemDefaultSpell = itemDefault.get(ModComponents.FRAGMENT);
                    var itemDefaultMap = itemDefault.get(ModComponents.FRAGMENT);

                    if (itemDefaultSpell != null) {
                        s.set(ModComponents.FRAGMENT, itemDefaultSpell);
                    } else {
                        s.remove(ModComponents.FRAGMENT);
                    }

                    if (itemDefaultMap != null) {
                        s.set(ModComponents.FRAGMENT, itemDefaultMap);
                    } else {
                        s.remove(ModComponents.FRAGMENT);
                    }

                    return true;
                })) {
                    throw new ImmutableItemBlunder(this);
                }
            });

            return input.orElse(VoidFragment.INSTANCE);
        }).orElseThrow(() -> new NoPlayerBlunder(this));
    }
}
