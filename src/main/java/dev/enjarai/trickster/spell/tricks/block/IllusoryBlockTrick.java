package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.block.ModBlocks;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlockOccupiedBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;

public class IllusoryBlockTrick extends Trick {
    public IllusoryBlockTrick() {
        super(Pattern.of(0, 2, 8, 6, 3, 0, 1, 2, 5, 8, 7, 6, 0));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var blockType = expectInput(fragments, FragmentType.BLOCK_TYPE, 1);
        var blockPos = pos.toBlockPos();

        expectCanBuild(ctx, blockPos);

        if (blockType.block().getDefaultState().isAir()) {
            throw new BlunderException() {
                @Override
                public MutableText createMessage() {
                    return Text.literal("Cannot make invisible shadow block.");
                }
            };
        }

        var state = ctx.getWorld().getBlockState(blockPos);

        if (!state.isAir()) {
            throw new BlockOccupiedBlunder(this);
        }

        ctx.getWorld().setBlockState(blockPos, ModBlocks.SHADOW.getDefaultState());
        var blockEntity = ctx.getWorld().getBlockEntity(blockPos, ModBlocks.SHADOW_ENTITY);

        if (blockEntity.isPresent()) {
            blockEntity.get().disguise(blockType.block());
            blockEntity.get().markDirty();
        }
        else {
            throw new BlunderException() {
                @Override
                public MutableText createMessage() {
                    return Text.literal("Shadow block was not placed.");
                }
            };
        }

        return VoidFragment.INSTANCE;
    }
}
