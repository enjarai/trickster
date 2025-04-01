package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import io.vavr.collection.HashMap;

public interface RevisionContext {
    void updateSpell(SpellPart sp);
    void updateSpellWithSpell(SpellPart drawingPart, SpellPart spell);
    void updateOffHandSpell(SpellPart sp);
    SpellPart getOtherHandSpell();
    void executeOffhand();
    HashMap<Pattern, SpellPart> getMacros();
}
