package dev.enjarai.trickster.spell.tricks.event;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.InvalidEventBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEventBlunder;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;

import java.util.List;

public class CreateSpellCircleTrick extends Trick {
    public CreateSpellCircleTrick() {
        super(Pattern.of(7, 6, 3, 0, 1, 2, 5, 8, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var position = expectInput(fragments, FragmentType.VECTOR, 0);
        var eventPart = expectInput(fragments, FragmentType.SPELL_PART, 1);
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 2);

        if (!(eventPart.glyph instanceof PatternGlyph)) {
            throw new InvalidEventBlunder(this);
        }

        var pattern = ((PatternGlyph) eventPart.glyph).pattern();
        var event = SpellCircleEvent.lookup(pattern);
        if (event == null) {
            throw new UnknownEventBlunder(this);
        }

        var blockPos = position.toBlockPos();
        expectCanBuild(ctx, blockPos);

        if (ctx.getWorld().getBlockState(blockPos).isAir()) {
            ctx.useMana(this, 496);

            var spell = executable.deepClone();
            spell.brutallyMurderEphemerals();

            ctx.getWorld().setBlockState(blockPos, ModBlocks.SPELL_CIRCLE.getDefaultState());
            ctx.setWorldAffected();
            var entity = (SpellCircleBlockEntity) ctx.getWorld().getBlockEntity(blockPos);

            entity.event = event;
            entity.spell = spell;
            entity.markDirty();

            return BooleanFragment.TRUE;
        } else {
            throw new BlockOccupiedBlunder(this);
        }
    }
}
