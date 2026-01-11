package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.blunder.InvalidStorageBlunder;
import dev.enjarai.trickster.spell.trick.Trick;

public interface VariantingFragment extends Fragment {
    VariantType<?> variantType();

    default void assertVariantType(Trick<?> trick, VariantType<?> variantType) {
        if (variantType != variantType()) {
            throw new InvalidStorageBlunder(trick); // TODO maybe more info here
        }
    }
}
