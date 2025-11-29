package dev.enjarai.trickster.spell.trick.color;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.ItemInvalidBlunder;
import dev.enjarai.trickster.spell.fragment.ColorFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ItemTypeFragment;
import dev.enjarai.trickster.spell.fragment.ZalgoFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.ColorHelper;

import java.util.function.Function;

public class ItemToColorTrick extends Trick<ItemToColorTrick> {
    Function<DyeColor, Integer> acc;

    public ItemToColorTrick(Pattern pattern, Function<DyeColor, Integer> acc) {
        super(pattern,
                Signature.of(FragmentType.ITEM_TYPE, ItemToColorTrick::toColor, FragmentType.COLOR));
        overload(Signature.of(FragmentType.ZALGO, ItemToColorTrick::randomColor, FragmentType.COLOR));
        this.acc = acc;
    }

    public ColorFragment toColor(SpellContext ctx, ItemTypeFragment item) {
        if (item.item() instanceof DyeItem dye) {
            return new ColorFragment(ColorHelper.Argb.withAlpha(255, acc.apply(dye.getColor())));
        } else if (item.item() == Items.GLASS) {
            return new ColorFragment(ColorHelper.Argb.getArgb(0, 255, 255, 255));
        } else if (item.item() == Items.TINTED_GLASS) {
            return new ColorFragment(ColorHelper.Argb.getArgb(0, 0, 0, 0));
        } else {
            throw new ItemInvalidBlunder(this);
        }
    }

    public ColorFragment randomColor(SpellContext ctx, ZalgoFragment z) {
        return new ColorFragment(ColorHelper.Argb.withAlpha(255, ZalgoFragment.RANDOM.nextInt()));
    }
}
