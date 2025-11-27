package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.InvalidStorageBlunder;
import dev.enjarai.trickster.spell.trick.Trick;
import org.joml.Vector3dc;

import java.util.Optional;

public interface StorageFragment extends Fragment {
    VariantType<?> variantType();

    default void assertVariantType(Trick<?> trick, VariantType<?> variantType) {
        if (variantType != variantType()) {
            throw new InvalidStorageBlunder(trick); // TODO maybe more info here
        }
    }

    Optional<Vector3dc> getSourcePos(Trick<?> trick, SpellContext ctx);

    Vector3dc getSourceOrCasterPos(Trick<?> trick, SpellContext ctx);

    default float getMoveCost(Trick<?> trickSource, SpellContext ctx, Vector3dc pos, long amount) throws BlunderException {
        return getSourcePos(trickSource, ctx)
                .map(sourcePos -> (float) (pos.distance(sourcePos) * amount * 0.5))
                .orElse(0f);
    }

    default void incurCost(Trick<?> trick, SpellContext ctx, Vector3dc pos, long amountMoved) {
        ctx.useMana(trick, getMoveCost(trick, ctx, pos, amountMoved));
    }
}
