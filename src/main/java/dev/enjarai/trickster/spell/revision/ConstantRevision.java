package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;

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
    public void apply(RevisionContext ctx, SpellView view) {
        view.replaceGlyph(fragment);
    }
}
