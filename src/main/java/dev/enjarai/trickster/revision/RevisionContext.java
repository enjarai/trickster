package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.SpellPart;

import java.util.List;

public interface RevisionContext {
    void updateSpell(SpellPart sp);
    void updateSpellWithSpell(SpellPart drawingPart, SpellPart spell);
    void updateOtherHandSpell(SpellPart sp);
    SpellPart getOtherHandSpell();
    void executeOffhand();
}
