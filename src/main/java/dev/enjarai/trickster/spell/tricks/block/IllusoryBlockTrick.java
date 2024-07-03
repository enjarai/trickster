package dev.enjarai.trickster.spell.tricks.block;

import dev.enjarai.trickster.cca.ModChunkComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.chunk.EmptyChunk;

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

        if (blockType.block().getDefaultState().isAir()) {
            throw new BlunderException() {
                @Override
                public MutableText createMessage() {
                    return Text.literal("Cannot make invisible shadow block.");
                }
            };
        }

        var chunk = ctx.getWorld().getChunk(blockPos);

        if (!(chunk instanceof EmptyChunk)) {
            var map = ModChunkComponents.SHADOW_DISGUISE_MAP.get(chunk).value(); //TODO: getting cleared?
            map.putIfAbsent(blockPos, blockType.block());
            ModChunkComponents.SHADOW_DISGUISE_MAP.sync(chunk);
            chunk.setNeedsSaving(true);
        }

        return VoidFragment.INSTANCE;
    }
}
