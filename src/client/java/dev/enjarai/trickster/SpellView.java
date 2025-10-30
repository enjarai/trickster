package dev.enjarai.trickster;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PatternGlyph;
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

    public Runnable rebuildListener;

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

    public void delete() {
        if (parent != null) {
            if (isInner) {
                parent.inner = null;
                parent.part.glyph = new PatternGlyph();
            } else {
                parent.children.remove(this);
                parent.part.subParts.remove(this.part);
            }
            parent.triggerRebuild();
            parent = null;
            isInner = false;
        }
    }

    public void replaceChildren(List<SpellPart> children) {
        part.subParts = new ArrayList<>(children);
        for (var child : this.children) {
            child.parent = null;
        }
        this.children.clear();
        for (var childPart : children) {
            var childView = index(childPart);
            childView.parent = this;
            this.children.add(childView);
        }
        triggerRebuild();
    }

    public void replaceGlyph(Fragment glyph) {
        var oldGlyph = part.glyph;
        part.glyph = glyph;
        if (glyph instanceof SpellPart glyphPart) {
            var innerView = index(glyphPart);
            innerView.parent = this;
            innerView.isInner = true;
            inner = innerView;
        } else if (inner != null) {
            inner.parent = null;
            inner.isInner = false;
            inner = null;
        }
        if (oldGlyph instanceof SpellPart || glyph instanceof SpellPart) {
            triggerRebuild();
        }
    }

    public void replace(SpellPart newPart) {
        replaceGlyph(newPart.glyph);
        replaceChildren(newPart.subParts);
    }

    public void triggerRebuild() {
        if (rebuildListener != null) {
            rebuildListener.run();
        }
    }
}
