package dev.enjarai.trickster.spell.trick.misc;

import dev.enjarai.trickster.cca.ModWorldComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import net.minecraft.util.math.ChunkPos;

public class PinChunkTrick extends Trick<PinChunkTrick> {
    public PinChunkTrick() {
        super(Pattern.of(6, 5, 0, 7, 2, 3, 8, 1, 6), Signature.of(FragmentType.VECTOR, PinChunkTrick::run));
    }

    public Fragment run(SpellContext ctx, VectorFragment pos) throws BlunderException {
        var chunkPos = new ChunkPos(pos.toBlockPos());

        ctx.useMana(this, 4);
        ModWorldComponents.PINNED_CHUNKS.get(ctx.source().getWorld()).pinChunk(chunkPos);

        return pos;
    }
}
