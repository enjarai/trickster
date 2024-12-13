package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.AddableFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.text.Text;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.ArrayList;
import java.util.List;

public class AddTrick extends DistortionTrick<AddTrick> {
    public AddTrick() {
        super(Pattern.of(7, 4, 0, 1, 2, 4), Signature.of(variadic(AddableFragment.class), AddTrick::run));
        overload(Signature.of(FragmentType.LIST, AddTrick::fromList));
    }

    public Fragment fromList(SpellContext ctx, ListFragment list) throws BlunderException {
        var args = new ArrayList<AddableFragment>();
        int i = 0;

        for (var v : list.fragments()) {
            if (v instanceof AddableFragment a) {
                args.add(a);
            } else {
                throw new IncorrectFragmentBlunder(this, i, Text.literal("Addable"), v); //TODO: this is unacceptable; we need a better error
            }

            i++;
        }

        return run(ctx, args);
    }

    public Fragment run(SpellContext ctx, List<AddableFragment> args) throws BlunderException {
        AddableFragment result = null;

        for (int i = 0; i < args.size(); i++) {
            var value = args.get(i);

            if (result == null) {
                result = value;
            } else {
                result = result.add(value);
            }
        }

        if (result == null) {
            throw new MissingInputsBlunder(this);
        }

        return result;
    }
}
