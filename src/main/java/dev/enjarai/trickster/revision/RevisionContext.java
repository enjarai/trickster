package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.SpellPart;

public interface RevisionContext {
    void updateSpell(SpellPart sp);
    void updateOtherHandSpell(SpellPart sp);
    SpellPart getOtherHandSpell();
    void executeOffhand();
}
