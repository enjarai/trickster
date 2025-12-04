package dev.enjarai.trickster.spell.trick.inventory;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.slot.ContainerFragment;
import dev.enjarai.trickster.spell.fragment.slot.ResourceVariantFragment;
import dev.enjarai.trickster.spell.fragment.slot.VariantType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

public class CountResourcesTrick extends Trick<CountResourcesTrick> {
    public CountResourcesTrick() {
        super(
                Pattern.of(4, 3, 6, 8, 5, 4, 6, 0, 4, 8, 2, 4, 1),
                Signature.of(FragmentType.CONTAINER, ArgType.simple(ResourceVariantFragment.class), CountResourcesTrick::run, FragmentType.NUMBER)
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T> NumberFragment run(SpellContext ctx, ContainerFragment container, ResourceVariantFragment resource) {
        var variantType = (VariantType<T>) container.variantType();
        resource.assertVariantType(this, variantType);
        var res = (ResourceVariantFragment<T>) resource;

        long sum = 0;
        var storage = container.getStorage(this, ctx, variantType);
        for (var view : storage.nonEmptyViews()) {
            if (res.resourceMatches(this, ctx, view.getResource())) {
                sum += view.getAmount();
            }
        }

        return new NumberFragment(sum);
    }
}
