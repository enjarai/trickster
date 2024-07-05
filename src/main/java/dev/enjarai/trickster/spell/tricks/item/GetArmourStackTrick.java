package dev.enjarai.trickster.spell.tricks.item;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemStackFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.NoSuchSlotBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class GetArmourStackTrick extends Trick {
    public GetArmourStackTrick() {
        super(Pattern.of(0, 1, 2, 4, 3, 6, 7, 8, 5, 4, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var entity = expectInput(fragments, FragmentType.ENTITY, 0).getEntity(ctx);
        var slot = expectInput(fragments, FragmentType.NUMBER, 1).number();

        if (entity.isPresent()) {
            var entity2 = entity.get();

            if (entity2 instanceof LivingEntity living) {
                var items = living.getAllArmorItems();
                int index = 0;

                for (var item : items) {
                    if (index == slot) {
                        return new ItemStackFragment(item);
                    }

                    index++;
                }

                throw new NoSuchSlotBlunder(this);
            }

            throw new EntityInvalidBlunder(this);
        }

        throw new UnknownEntityBlunder(this);
    }
}
