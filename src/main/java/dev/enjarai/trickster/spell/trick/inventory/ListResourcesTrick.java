package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.ResourceVariantFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.ArrayList;
import java.util.List;

public class ListResourcesTrick extends Trick<ListResourcesTrick> {
    public ListResourcesTrick() {
        super(
                Pattern.of(4, 3, 6, 8, 5, 4, 6, 0, 2, 8, 4, 1),
                Signature.of(FragmentType.CONTAINER, ListResourcesTrick::run, RetType.simple(ResourceVariantFragment.class).listOfRet())
        );
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private <T> List<ResourceVariantFragment> run(SpellContext ctx, ContainerFragment container) {
        var result = new ArrayList<ResourceVariantFragment>();

        var variantType = (VariantType<T>) container.variantType();
        var storage = container.getStorage(this, ctx, variantType);
        for (var view : storage.nonEmptyViews()) {
            var fragment = variantType.fragmentFromResource(view.getResource());
            if (!result.contains(fragment)) {
                result.add(fragment);
            }
        }

        return result;
    }
}
