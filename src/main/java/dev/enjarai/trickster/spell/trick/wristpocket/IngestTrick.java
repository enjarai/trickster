package dev.enjarai.trickster.spell.trick.wristpocket;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.blunder.NoPlayerBlunder;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

import java.util.List;

public class IngestTrick extends Trick {
    public IngestTrick() {
        super(Pattern.of(7, 4, 3, 0, 4, 2, 5, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var component = ctx.source().getComponent(ModEntityComponents.WRIST_POCKET)
                .orElseThrow(() -> new IncompatibleSourceBlunder(this));
        var player = ctx.source().getPlayer().orElseThrow(() -> new NoPlayerBlunder(this));
        var stack = component.getPocketed();

        if (stack.isOf(Items.POTION) || stack.isOf(Items.HONEY_BOTTLE) || stack.isOf(Items.MILK_BUCKET) || stack.isIn(ConventionalItemTags.FOODS)) {
            var originalItem = player.getStackInHand(Hand.MAIN_HAND);
            player.setStackInHand(Hand.MAIN_HAND, stack);

            var newStack = stack.finishUsing(ctx.source().getWorld(), player);
            component.setPocketed(newStack);
            player.setStackInHand(Hand.MAIN_HAND, originalItem);
        }

        return VoidFragment.INSTANCE;
    }
}
