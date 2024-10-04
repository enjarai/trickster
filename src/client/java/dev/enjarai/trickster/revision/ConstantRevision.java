package dev.enjarai.trickster.revision;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellPart;

public class ConstantRevision implements Revision {
    private final Pattern pattern;
    private final Fragment fragment;

    public ConstantRevision(Pattern pattern, Fragment fragment) {
        this.pattern = pattern;
        this.fragment = fragment;
    }

    @Override
    public Pattern pattern() {
        return pattern;
    }

    @Override
    public SpellPart apply(RevisionContext ctx, SpellPart root, SpellPart drawingPart) {
        drawingPart.glyph = fragment;
        return root;
    }
}
