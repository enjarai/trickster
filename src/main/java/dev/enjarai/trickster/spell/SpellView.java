package dev.enjarai.trickster.spell;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpellView {
    public final SpellPart part;

    @Nullable
    public SpellView parent, inner;
    public List<SpellView> children = new ArrayList<>();
    public boolean isInner = false;
    public boolean beingReplaced = false;
    public boolean loading = false;

    public UUID uuid = UUID.randomUUID();

    public Runnable updateListener;

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

    public List<Integer> getPath() {
        var path = new ArrayList<Integer>();

        var current = this;
        while (current.parent != null) {
            if (current.isInner) {
                path.addFirst(-1);
            } else {
                path.addFirst(current.getOwnIndex());
            }
            current = current.parent;
        }

        return path;
    }

    @Nullable
    public SpellView traverseTo(List<Integer> path) {
        var current = this;

        for (var i : path) {
            if (current == null) {
                return null;
            }

            if (i < 0) {
                current = current.inner;
            } else {
                if (i >= current.children.size()) {
                    return null;
                }

                current = current.children.get(i);
            }
        }

        return current;
    }

    public SpellView getUpperParent() {
        var current = this;

        while (current.parent != null) {
            current = current.parent;
        }

        return current;
    }

    public void comeOutAsTrans() {
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

    public void addChild(int position, SpellPart child) {
        var childView = index(child);
        childView.parent = this;
        this.part.subParts.add(position, child);
        this.children.add(position, childView);
        triggerRebuild();
    }

    /**
     * Given view must not already be parented
     */
    public void addChild(int position, SpellView child) {
        child.parent = this;
        this.part.subParts.add(position, child.part);
        this.children.add(position, child);
        triggerRebuild();
    }

    public SpellView removeChild(int position) {
        this.part.subParts.remove(position);
        var oldChild = this.children.remove(position);
        oldChild.parent = null;
        triggerRebuild();
        return oldChild;
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

    /**
     * Given view must not already be parented
     */
    public SpellView replaceInner(SpellView inner) {
        SpellView oldInner = null;
        if (this.inner != null) {
            oldInner = this.inner;
            oldInner.comeOutAsTrans();
        }

        inner.parent = this;
        inner.isInner = true;
        this.part.glyph = inner.part;
        this.inner = inner;
        triggerRebuild();

        return oldInner;
    }

    public void replace(SpellPart newPart) {
        replaceGlyph(newPart.glyph);
        replaceChildren(newPart.subParts);
    }

    /**
     * Only use if replacement isn't to be used anywhere else
     */
    public void replace(SpellView replacement) {
        if (parent != null) {
            if (isInner) {
                parent.inner = replacement;
                parent.part.glyph = replacement.part;
            } else {
                var i = parent.children.indexOf(this);
                parent.children.set(i, replacement);
                parent.part.subParts.set(i, replacement.part);
            }
            parent.triggerRebuild();
            parent = null;
            isInner = false;
        }
    }

    public void triggerRebuild() {
        if (updateListener != null) {
            updateListener.run();
        }
    }
}
