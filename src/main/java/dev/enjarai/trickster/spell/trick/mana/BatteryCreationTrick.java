package dev.enjarai.trickster.spell.trick.mana;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.item.KnotItem;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BatteryCreationTrick extends Trick<BatteryCreationTrick> {
    private final Map<Item, KnotItem> types = new HashMap<>();

    public BatteryCreationTrick() {
        super(Pattern.of(6, 8, 5, 2, 1, 8, 7, 6, 1, 0, 3, 6), Signature.of(FragmentType.SLOT.optionalOfArg(),
                FragmentType.SLOT.optionalOfArg(), BatteryCreationTrick::run, FragmentType.VOID));
    }

    public VoidFragment run(SpellContext ctx, Optional<SlotFragment> slot1, Optional<SlotFragment> slot2) {
        var sourceSlot = slot1.orElseGet(() -> ctx.findSlotOnPlayer(this, Items.AMETHYST_SHARD));
        var glassSlot = slot2.orElseGet(() -> ctx.findSlotOnPlayer(this, Items.GLASS));

        var sourceStorage = sourceSlot.getStorage(this, ctx, VariantType.ITEM);
        var glassStorage = glassSlot.getStorage(this, ctx, VariantType.ITEM);

        var sourceItem = sourceStorage.getResource().getItem();
        var type = types.get(sourceItem);

        if (type == null) {
            throw new ItemInvalidBlunder(this);
        }

        try (var trans = Transaction.openOuter()) {
            var takenSource = sourceStorage.extract(sourceStorage.getResource(), 1, trans);
            var takenGlass = glassStorage.extract(ItemVariant.of(Items.GLASS), 1, trans);

            if (takenSource != 1 || takenGlass != 1) {
                throw new ItemInvalidBlunder(this);
            }

            ctx.useMana(this, type.getCreationCost());

            ctx.source().offerOrDropItem(type.createStack(ctx.source().getWorld()));
            ctx.source().getPlayer().ifPresent(player -> ModCriteria.CREATE_KNOT.trigger(player, type));

            trans.commit();
        }

        return VoidFragment.INSTANCE;
    }

    public void registerKnot(Item input, KnotItem output) {
        var old = types.put(input, output);
        if (old != null) {
            //TODO: this could be improved, translations aren't loaded for modded items
            Trickster.LOGGER.warn("Creation of \"{}\" from \"{}\" has been overridden by \"{}\"", old.getName().getString(), input.getName().getString(), output.getName().getString());
        }
    }
}
