package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;
import io.vavr.collection.HashMap;

public interface RevisionContext {
    void updateSpell(SpellPart sp);

    HashMap<Pattern, SpellPart> getMacros();

    RevisionContext DUMMY = new RevisionContext() {
        @Override
        public void updateSpell(SpellPart sp) {}

        @Override
        public HashMap<Pattern, SpellPart> getMacros() {
            return HashMap.empty();
        }
    };
}
