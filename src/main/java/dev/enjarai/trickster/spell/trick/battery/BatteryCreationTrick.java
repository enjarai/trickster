package dev.enjarai.trickster.spell.trick.battery;

import dev.enjarai.trickster.item.ManaCrystalItem;
import dev.enjarai.trickster.item.ModItems;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingItemBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BatteryCreationTrick extends Trick {
    private final Map<Item, ManaCrystalItem> types = Map.of(
            Items.AMETHYST_SHARD, ModItems.AMETHYST_MANA_CRYSTAL,
            Items.EMERALD, ModItems.EMERALD_MANA_CRYSTAL,
            Items.DIAMOND, ModItems.DIAMOND_MANA_CRYSTAL,
            Items.ECHO_SHARD, ModItems.ECHO_MANA_CRYSTAL
    );

    public BatteryCreationTrick() {
        super(Pattern.of(6, 8, 5, 2, 1, 8, 7, 6, 1, 0, 3, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var sourceSlot = supposeInput(fragments, FragmentType.SLOT, 0)
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
        var sourceItem = sourceSlot.getItem(this, ctx);

        var type = types.get(sourceItem);
        ctx.useMana(this, type.getCreationCost());

        sourceSlot.move(this, ctx, 1);
        ctx.source().offerOrDropItem(type.createStack());

        return VoidFragment.INSTANCE;
    }
}
