package dev.enjarai.trickster.spell.trick.basic;

import com.ibm.icu.impl.Pair;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.ImmutableItemBlunder;
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
        var spell = supposeInput(fragments, 0).flatMap(s -> supposeType(s, FragmentType.SPELL_PART)).map(s -> {
            var n = s.deepClone();
            n.brutallyMurderEphemerals();
            return n;
        });

        return player.map(serverPlayerEntity -> Pair.of(serverPlayerEntity, serverPlayerEntity.getOffHandStack())).map(pair -> {
            var serverPlayer = pair.first;
            var stack = pair.second;

            spell.ifPresentOrElse(s -> {
                var stack2 = stack;

                if (stack2.isOf(Items.BOOK)
                        && !s.isEmpty()) {
                    serverPlayer.equipStack(EquipmentSlot.OFFHAND, stack2.withItem(Items.ENCHANTED_BOOK));
                    stack2 = serverPlayer.getOffHandStack();
                } else if (stack2.isOf(Items.ENCHANTED_BOOK)
                        && s.isEmpty()
                        && (stack.get(DataComponentTypes.STORED_ENCHANTMENTS) instanceof ItemEnchantmentsComponent enchants)
                        && enchants.isEmpty()) {
                    serverPlayer.equipStack(EquipmentSlot.OFFHAND, stack2.withItem(Items.BOOK));
                    stack2 = serverPlayer.getOffHandStack();
                }

                if (!SpellComponent.setSpellPart(stack2, s, supposeInput(fragments, FragmentType.STRING, 1).flatMap(str -> Optional.of(str.value())), closed)) {
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

                if (!SpellComponent.modifyReferencedStack(stack2, s -> {
                    var spellComponent = s.get(ModComponents.SPELL);

                    if (spellComponent == null || spellComponent.immutable())
                        return false;

                    s.remove(ModComponents.SPELL);
                    return true;
                })) {
                    throw new ImmutableItemBlunder(this);
                }
            });
            return BooleanFragment.TRUE;
        }).orElse(BooleanFragment.FALSE);
    }
}
