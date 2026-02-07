package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

import java.util.function.Consumer;

public class ReplaceGlyphWithOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 2, 4, 1, 0, 4, 7);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        ctx.delegateToServer(this, view, view::replaceGlyph);
    }

    @Override
    public void applyServer(RevisionContext ctx, SpellView view, Consumer<SpellPart> callback) {
        var stack = ctx.getOtherHandStack();
        if (stack == null || stack.isEmpty()) {
            callback.accept(new SpellPart());
            return;
        }

        var fragment = FragmentComponent.getFragment(stack);
        fragment.filter(SpellPart.class::isInstance).ifPresentOrElse(f -> {
            callback.accept((SpellPart) f);
        }, () -> {
            callback.accept(new SpellPart());
        });
    }
}
