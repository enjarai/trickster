package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Map;
import java.util.Optional;

public class BatteryCreationTrick extends Trick<BatteryCreationTrick> {
    private final Map<Item, KnotItem> types = Map.of(
            Items.AMETHYST_SHARD, ModItems.AMETHYST_KNOT,
            Items.QUARTZ, ModItems.QUARTZ_KNOT,
            Items.EMERALD, ModItems.EMERALD_KNOT,
            Items.DIAMOND, ModItems.DIAMOND_KNOT,
            Items.ECHO_SHARD, ModItems.ECHO_KNOT,
            Items.NETHER_STAR, ModItems.ASTRAL_KNOT
    );

    public BatteryCreationTrick() {
        super(Pattern.of(6, 8, 5, 2, 1, 8, 7, 6, 1, 0, 3, 6), Signature.of(FragmentType.SLOT.optionalOf(),
                FragmentType.SLOT.optionalOf(), BatteryCreationTrick::run));
    }

    public Fragment run(SpellContext ctx, Optional<SlotFragment> slot1, Optional<SlotFragment> slot2)
            throws BlunderException {
        var sourceSlot = slot1
                .orElseGet(() -> {
                    var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
                    var inventory = player.getInventory();

                    for (int i = 0; i < inventory.size(); i++) {
                        var stack = inventory.getStack(i);

                        if (stack.isOf(Items.AMETHYST_SHARD)) {
                            return new SlotFragment(i, Optional.empty());
                        }
                    }

                    throw new MissingItemBlunder(this);
                });
        var glass = ctx.getStack(
                this,
                slot2,
                (stack) -> stack.isOf(Items.GLASS)
        ).orElseThrow(() -> new MissingItemBlunder(this));
        var sourceItem = sourceSlot.getItem(this, ctx);
        var type = types.get(sourceItem);

        if (type == null) {
            throw new ItemInvalidBlunder(this);
        }

        try {
            var input = sourceSlot.move(this, ctx, 1);

            try {
                ctx.useMana(this, type.getCreationCost());
                ctx.source().offerOrDropItem(type.createStack(ctx.source().getWorld()));

                if (type == ModItems.ECHO_KNOT) {
                    ctx.source().getPlayer().ifPresent(ModCriteria.CREATE_ECHO_KNOT::trigger);
                }

                return VoidFragment.INSTANCE;
            } catch (Exception e) {
                ctx.source().offerOrDropItem(input);
                throw e;
            }
        } catch (Exception e) {
            ctx.source().offerOrDropItem(glass);
            throw e;
        }
    }
}
