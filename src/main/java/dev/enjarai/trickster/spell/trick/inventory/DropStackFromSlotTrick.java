package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.ItemEntity;

import java.util.Optional;

public class DropStackFromSlotTrick extends Trick<DropStackFromSlotTrick> {
    public DropStackFromSlotTrick() {
        super(Pattern.of(1, 4, 7, 3, 4, 5, 7), Signature.of(FragmentType.SLOT, FragmentType.VECTOR, FragmentType.NUMBER.optionalOfArg(), DropStackFromSlotTrick::run, FragmentType.ENTITY));
    }

    public EntityFragment run(SpellContext ctx, SlotFragment slot, VectorFragment pos, Optional<NumberFragment> optionalAmount) {
        var vector = pos.vector();
        var amount = optionalAmount.orElse(new NumberFragment(1)).number();

        if (amount < 1) {
            throw new NumberTooSmallBlunder(this, 1);
        }

        var storage = slot.getStorage(this, ctx, VariantType.ITEM);
        var resource = storage.getResource();

        if (resource.isBlank()) {
            throw new ItemInvalidBlunder(this);
        }

        try (var trans = Transaction.openOuter()) {
            var extracted = storage.extract(resource, Math.min(Math.round(amount), resource.getItem().getMaxCount()), trans);
            if (extracted <= 0) {
                throw new ItemInvalidBlunder(this);
            }

            slot.incurCost(this, ctx, vector, extracted);

            var stack = resource.toStack((int) extracted);
            var world = ctx.source().getWorld();
            var entity = new ItemEntity(world, vector.x(), vector.y(), vector.z(), stack);
            entity.setPickupDelay(10);

            trans.commit();

            world.spawnEntity(entity);
            return new EntityFragment(entity.getUuid(), entity.getName());
        }
    }
}
