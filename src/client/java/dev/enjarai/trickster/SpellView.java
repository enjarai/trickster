package dev.enjarai.trickster;

import dev.enjarai.trickster.spell.SpellPart;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class SpellView {
    public final SpellPart part;
    @Nullable
    public SpellView parent, inner;
    public List<SpellView> children = new ArrayList<>();
    public boolean isInner = false;

    private SpellView(SpellPart part) {
        this.part = part;
    }

    public static SpellView index(SpellPart part) {
        var stack = new ArrayDeque<SpellView>();

        var mainView = new SpellView(part);
        stack.offer(mainView);

        while (!stack.isEmpty()) {
            var view = stack.poll();

            for (var childPart : view.part.subParts) {
                var childView = new SpellView(childPart);
                childView.parent = view;
                view.children.add(childView);
                stack.offer(childView);
            }

            if (view.part.glyph instanceof SpellPart innerPart) {
                var innerView = new SpellView(innerPart);
                innerView.parent = view;
                innerView.isInner = true;
                view.inner = innerView;
                stack.offer(innerView);
            }
        }

        return mainView;
    }

    public int getOwnIndex() {
        if (isInner || parent == null || parent.children.isEmpty()) {
            return -1;
        }

        return parent.children.indexOf(this);
    }
}
