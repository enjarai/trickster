package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.util.Hamt;

public interface RevisionContext {
    void updateSpell(SpellPart sp);
    void updateSpellWithSpell(SpellPart drawingPart, SpellPart spell);
    void updateOtherHandSpell(SpellPart sp);
    SpellPart getOtherHandSpell();
    void executeOffhand();
    Hamt<Pattern, SpellPart> getMacros();
}
