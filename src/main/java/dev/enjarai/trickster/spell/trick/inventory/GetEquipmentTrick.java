package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.*;
import dev.enjarai.trickster.spell.fragment.slot.ItemTypeFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class GetEquipmentTrick extends Trick<GetEquipmentTrick> {
    public GetEquipmentTrick() {
        super(Pattern.of(1, 8, 7, 6, 1, 4, 3, 0, 4, 5, 2, 4), Signature.of(FragmentType.ENTITY, GetEquipmentTrick::run, FragmentType.ITEM_TYPE.listOfRet()));
    }

    public List<ItemTypeFragment> run(SpellContext ctx, EntityFragment target) throws BlunderException {
        var entity = target.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (entity instanceof LivingEntity livingEntity) {
            List<ItemTypeFragment> list = new ArrayList<>();
            livingEntity.getEquippedItems().forEach(itemStack -> list.add(new ItemTypeFragment(itemStack.getItem())));
            return list;
        }

        throw new EntityInvalidBlunder(this);
    }
}
