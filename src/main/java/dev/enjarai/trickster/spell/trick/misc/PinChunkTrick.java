package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.ModWorldCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public class PinChunkTrick extends Trick {
    public PinChunkTrick() {
        super(Pattern.of(6, 5, 0, 7, 2, 3, 8, 1, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var pos = expectInput(fragments, FragmentType.VECTOR, 0);
        var chunkPos = new ChunkPos(pos.toBlockPos());

        ctx.useMana(this, 32);
        ModWorldCumponents.PINNED_CHUNKS.get(ctx.source().getWorld()).pinChunk(chunkPos);

        return VoidFragment.INSTANCE;
    }
}
