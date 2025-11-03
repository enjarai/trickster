package dev.enjarai.trickster.spell.revision;

import dev.enjarai.trickster.item.component.FragmentComponent;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.spell.Pattern;

import java.util.function.Consumer;

public class WriteBranchToOffHandRevision implements Revision {
    @Override
    public Pattern pattern() {
        return Pattern.of(1, 4, 7, 6, 4, 8, 7);
    }

    @Override
    public void apply(RevisionContext ctx, SpellView view) {
        ctx.delegateToServer(this, view, p -> {});
    }

    @Override
    public void applyServer(RevisionContext ctx, SpellView view, Consumer<SpellPart> callback) {
        var stack = ctx.getOtherHandStack();

        if (stack != null && !stack.isEmpty()) {
            FragmentComponent.write(stack, view.part);
        }

        callback.accept(new SpellPart());
    }
}
