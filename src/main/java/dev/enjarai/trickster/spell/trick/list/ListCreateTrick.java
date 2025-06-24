package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

/**
 * Continues to exist for backwards compatibility, users should instead use the ConstantRevision with the same pattern.
 */
public class ListCreateTrick extends Trick<ListCreateTrick> {
    public ListCreateTrick() {
        super(Pattern.of(6, 3, 0, 2, 5, 8), Signature.of(ListCreateTrick::create, RetType.ANY.listOfRet()));
    }

    public List<Fragment> create(SpellContext ctx) throws BlunderException {
        return List.of();
    }
}
