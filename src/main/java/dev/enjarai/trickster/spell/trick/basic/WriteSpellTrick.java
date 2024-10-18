package dev.enjarai.trickster.spell.trick.basic;

import com.mojang.datafixers.util.Pair;

import dev.enjarai.trickster.item.component.MapComponent;
import dev.enjarai.trickster.item.component.ModComponents;
import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.MapFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.util.Hamt;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ImmutableItemBlunder;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
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
        var input = supposeInput(fragments, 0).map(Fragment::applyEphemeral);
        var map = input.flatMap(fragment -> supposeType(fragment, FragmentType.MAP));

        if (map.isPresent()) {
            return player.map(ServerPlayerEntity::getOffHandStack).map(stack -> {
                var macros = map.get();
                MapComponent.setMap(stack, expectMacroMap(macros.downcast()));
                return macros;
            }).orElseThrow(() -> new NoPlayerBlunder(this));
        } else {
            var spell = input.flatMap(s -> {
                if (supposeType(s, FragmentType.VOID).isPresent()) {
                    return Optional.empty();
                } else {
                    return Optional.of(expectType(s, SpellPart.class, 0));
                }
            });

            return player.map(serverPlayerEntity -> Pair.of(serverPlayerEntity, serverPlayerEntity.getOffHandStack())).map(pair -> {
                var serverPlayer = pair.getFirst();
                var stack = pair.getSecond();

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

                        var itemDefault = s.getItem().getDefaultStack();
                        var itemDefaultSpell = itemDefault.get(ModComponents.SPELL);
                        var itemDefaultMap = itemDefault.get(ModComponents.MAP);

                        if (itemDefaultSpell != null) {
                            s.set(ModComponents.SPELL, itemDefaultSpell);
                        } else {
                            s.remove(ModComponents.SPELL);
                        }

                        if (itemDefaultMap != null) {
                            s.set(ModComponents.MAP, itemDefaultMap);
                        } else {
                            s.remove(ModComponents.MAP);
                        }

                        return true;
                    })) {
                        throw new ImmutableItemBlunder(this);
                    }
                });

                return spell.<Fragment>map(n -> n).orElse(VoidFragment.INSTANCE);
            }).orElseThrow(() -> new NoPlayerBlunder(this));
        }
    }

    private Hamt<Pattern, SpellPart> expectMacroMap(Hamt<Fragment, Fragment> map) throws IncorrectFragmentBlunder {
        var macros = new HashMap<Pattern, SpellPart>();

        map.iterator().forEachRemaining(entry -> {
            if (entry.getKey() instanceof SpellPart spellKey && spellKey.glyph instanceof PatternGlyph pattern
                    && entry.getValue() instanceof SpellPart spell) {
                macros.put(pattern.pattern(), spell);
            } else {
                throw new IncorrectFragmentBlunder(this, 0,
                        FragmentType.MAP.getName()
                                .append("<")
                                .append(FragmentType.PATTERN.getName())
                                .append(", ")
                                .append(FragmentType.SPELL_PART.getName())
                                .append(">"),
                        new MapFragment(map));
            }
        });

        return Hamt.fromMap(macros);
    }
}
