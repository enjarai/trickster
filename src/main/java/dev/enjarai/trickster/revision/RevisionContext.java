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

    RevisionContext DUMMY = new RevisionContext() {
        @Override
        public void updateSpell(SpellPart sp) {}

        @Override
        public void updateSpellWithSpell(SpellPart drawingPart, SpellPart spell) {}

        @Override
        public void updateOffHandSpell(SpellPart sp) {}

        @Override
        public SpellPart getOtherHandSpell() {
            return new SpellPart();
        }

        @Override
        public void executeOffhand() {}

        @Override
        public HashMap<Pattern, SpellPart> getMacros() {
            return HashMap.empty();
        }
    };
}
