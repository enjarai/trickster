package dev.enjarai.trickster.compat.transmog;

import com.hidoni.transmog.registry.ModDataComponents;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.slot.SlotFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.Optional;

public class GetTransmogTrick extends Trick<GetTransmogTrick> {
    public GetTransmogTrick() {
        super(Pattern.of(6, 3, 4, 5, 2, 4, 6, 0, 1, 2, 8, 7, 6), Signature.of(FragmentType.SLOT, GetTransmogTrick::get, FragmentType.ITEM_TYPE.optionalOfRet()));
    }

    public Optional<ItemTypeFragment> get(SpellContext ctx, SlotFragment slot) {
        var stack = slot.reference(this, ctx);
        var currentTransmog = stack.get(ModDataComponents.TRANSMOG_APPEARANCE_ITEM.get());

        if (currentTransmog == null) {
            return Optional.empty();
        }

        return Optional.of(new ItemTypeFragment(currentTransmog.itemStack().getItem()));
    }
}
