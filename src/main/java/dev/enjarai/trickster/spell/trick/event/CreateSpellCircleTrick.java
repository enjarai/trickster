package dev.enjarai.trickster.spell.trick.event;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.block.SpellCircleBlock;
import dev.enjarai.trickster.block.SpellCircleBlockEntity;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.InvalidEventBlunder;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEventBlunder;
import dev.enjarai.trickster.spell.world.SpellCircleEvent;

import java.util.List;

public class CreateSpellCircleTrick extends Trick {
    public CreateSpellCircleTrick() {
        super(Pattern.of(7, 6, 3, 0, 1, 2, 5, 8, 7, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var position = expectInput(fragments, FragmentType.VECTOR, 0);
        var facingVector = expectInput(fragments, FragmentType.VECTOR, 1);
        var eventPart = expectInput(fragments, FragmentType.SPELL_PART, 2);
        var executable = expectInput(fragments, FragmentType.SPELL_PART, 3);

        if (!(eventPart.glyph instanceof PatternGlyph)) {
            throw new InvalidEventBlunder(this);
        }

        var pattern = ((PatternGlyph) eventPart.glyph).pattern();
        var event = SpellCircleEvent.lookup(pattern);
        if (event == null) {
            throw new UnknownEventBlunder(this);
        }

        var blockPos = position.toBlockPos();
        var facing = facingVector.toDirection();
        expectCanBuild(ctx, blockPos);

        if (ctx.source().getWorld().getBlockState(blockPos).isAir()) {
            ctx.useMana(this, 496);

            var spell = executable.deepClone();
            spell.brutallyMurderEphemerals();

            ctx.source().getWorld().setBlockState(blockPos,
                    ModBlocks.SPELL_CIRCLE.getDefaultState().with(SpellCircleBlock.FACING, facing));
            var entity = (SpellCircleBlockEntity) ctx.source().getWorld().getBlockEntity(blockPos);

            entity.event = event;
            entity.spell = spell;
            entity.markDirty();

            return BooleanFragment.TRUE;
        } else {
            throw new BlockOccupiedBlunder(this, position);
        }
    }
}
