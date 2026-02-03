package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.spell.SpellView;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.revision.RevisionContext;
import dev.enjarai.trickster.spell.revision.Revisions;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import io.wispforest.owo.braid.animation.Easing;
import io.wispforest.owo.braid.core.Alignment;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.Key;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.focus.Focusable;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import io.wispforest.owo.braid.widgets.stack.Stack;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.time.Duration;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CircleSoupWidget extends StatefulWidget {
    public static final double FORK_THRESHOLD_FORWARDS = 1;
    public static final double DISCARD_THRESHOLD_FORWARDS = 0.1;
    public static final double FORK_THRESHOLD_BACKWARDS = 3200;
    public static final double DISCARD_THRESHOLD_BACKWARDS = 12800;
    public static final double ZOOM_SPEED = 0.1;

    public final CircleRenderer renderer = new CircleRenderer(true, true, 0);

    private final SpellView view;
    private final RevisionContext revisionContext;
    private final boolean mutable;
    private final boolean allowsEval;
    private final double x;
    private final double y;
    private final double radius;
    private final double angle;
    private final double centerOffset;
    private final DisposeCallback disposeCallback;

    public CircleSoupWidget(SpellView view, RevisionContext revisionContext, boolean mutable, boolean allowsEval) {
        this(view, revisionContext, mutable, allowsEval, 0, 0, 80, 0, 0, (v, x, y, r, a, o) -> {});
    }

    public CircleSoupWidget(SpellView view, RevisionContext revisionContext, boolean mutable, boolean allowsEval,
        double x, double y, double radius, double angle, double centerOffset,
        DisposeCallback disposeCallback) {
        this.view = view;
        this.revisionContext = revisionContext;
        this.mutable = mutable;
        this.allowsEval = allowsEval;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.angle = angle;
        this.centerOffset = centerOffset;
        this.disposeCallback = disposeCallback;
    }

    @Override
    public WidgetState<?> createState() {
        return new State();
    }

    public static class State extends WidgetState<CircleSoupWidget> {
        private double mouseX, mouseY, draggingStartX, draggingStartY;
        private int zoomering;
        private Constraints arenaConstraints;
        private final Map<SpellView, CircleState> circles = new IdentityHashMap<>();
        private SpellView rootView;
        private boolean initialBuild = true;

        @Override
        public void init() {
            rootView = widget().view.getUpperParent();
            circles.clear();
            addCircle(new CircleState(
                widget().x,
                widget().y,
                widget().radius,
                widget().angle,
                widget().centerOffset,
                widget().view,
                io.vavr.collection.List.ofAll(widget().view.getPath())
            ));
        }

        @Override
        public void dispose() {
            CircleState closestState = null;
            var closestDistance = Double.MAX_VALUE;

            for (var state : circles.values()) {
                var x = state.x;
                var y = state.y;
                var radius = state.radius - 80;
                var cubedDistance = x * x + y * y + radius * radius;
                if (cubedDistance < closestDistance) {
                    closestDistance = cubedDistance;
                    closestState = state;
                }
            }

            if (closestState != null) {
                widget().disposeCallback.dispose(
                    closestState.partView, closestState.x, closestState.y,
                    closestState.radius, closestState.angle, closestState.centerOffset
                );
            }
        }

        @Override
        public Widget build(BuildContext context) {
            return new Focusable(
                focusable -> focusable
                    .keyUpCallback(this::keyUp)
                    .keyDownCallback(this::keyDown)
                    .autoFocus(true),
                new SharedState<>(
                    CircleSoupState::new,
                    new Builder(context1 -> {
                        var state = SharedState.get(context1, CircleSoupState.class);
                        return new MouseArea(
                            area -> area
                                .moveCallback(this::mouseMove)
                                .scrollCallback(this::zoom)
                                .dragCallback(drag(state))
                                .dragStartCallback(this::startDragging)
                                .dragEndCallback(endDragging(state)),
                            new LayoutBuilder((context2, constraints) -> {
                                arenaConstraints = constraints;
                                return new Align(
                                    Alignment.TOP_LEFT,
                                    new Stack(circles.values().stream()
                                        .filter(c -> c.parentCircle == null)
                                        .map(c -> new CircleSoupElement(
                                            Duration.ofMillis(250),
                                            Easing.OUT_EXPO,
                                            widget().renderer,
                                            c, constraints,
                                            widget().mutable,
                                            widget().allowsEval,
                                            !initialBuild
                                        ).key(
                                            Key.of(c.partView.uuid.toString())
                                        ))
                                        .toList()
                                    )
                                );
                            })
                        );
                    })
                )
            );
        }

        private boolean keyUp(int keyCode, KeyModifiers modifiers) {
            return switch (keyCode) {
                case GLFW.GLFW_KEY_W -> {
                    zoomering--;
                    scheduleAnimationCallback(this::doZoomering);
                    yield true;
                }
                case GLFW.GLFW_KEY_S -> {
                    zoomering++;
                    scheduleAnimationCallback(this::doZoomering);
                    yield true;
                }
                default -> false;
            };
        }

        private boolean keyDown(int keyCode, KeyModifiers modifiers) {
            return switch (keyCode) {
                case GLFW.GLFW_KEY_W -> {
                    if (zoomering != 0) yield false;
                    zoomering++;
                    scheduleAnimationCallback(this::doZoomering);
                    yield true;
                }
                case GLFW.GLFW_KEY_S -> {
                    if (zoomering != 0) yield false;
                    zoomering--;
                    scheduleAnimationCallback(this::doZoomering);
                    yield true;
                }
                case GLFW.GLFW_KEY_ESCAPE -> {
                    // TODO this is an ask glisco kinda moment
                    yield true;
                }
                default -> false;
            };
        }

        private void doZoomering(Duration delta) {
            if (zoomering == 0) return;

            var ticksElapsed = delta.toNanos() / (double) Duration.ofMillis(50).toNanos();
            var amount = zoomering * Trickster.CONFIG.keyZoomSpeed() * ticksElapsed;
            zoom(0, amount);

            scheduleAnimationCallback(this::doZoomering);
        }

        private void startDragging(int button, KeyModifiers modifiers) {
            draggingStartX = mouseX;
            draggingStartY = mouseY;
        }

        private MouseArea.DragEndCallback endDragging(CircleSoupState state) {
            return () -> state.dragging = false;
        }

        private void mouseMove(double x, double y) {
            mouseX = x - arenaConstraints.maxWidth() / 2;
            mouseY = y - arenaConstraints.maxHeight() / 2;
        }

        private MouseArea.DragCallback drag(CircleSoupState state) {
            return (x, y, deltaX, deltaY) -> {
                setState(() -> List.copyOf(circles.values())
                    .forEach(c -> c.drag(deltaX, deltaY)));

                if (!state.dragging) {
                    var xD = mouseX - draggingStartX;
                    var yD = mouseY - draggingStartY;

                    if (xD * xD + yD * yD > 10 * 10) {
                        state.dragging = true;
                    }
                }
            };
        }

        private boolean zoom(double horizontal, double vertical) {
            setState(() -> List.copyOf(circles.values())
                .forEach(c -> c.zoom(mouseX, mouseY, vertical)));
            return true;
        }

        private void addCircle(CircleState circle) {
            if (!circles.containsValue(circle)) {
                setState(() -> {
                    circles.put(circle.partView, circle);
                    circle.initialize();
                });
            }
        }

        private void removeCircle(CircleState circle) {
            setState(() -> circles.remove(circle.partView));
        }

        class CircleState {
            @Nullable
            CircleState parentCircle = null;
            final List<CircleState> childCircles = new ArrayList<>();
            double x, y, radius, angle, centerOffset;
            final SpellView partView;
            final io.vavr.collection.List<Integer> path;

            private CircleState(double x, double y, double radius, double angle, double centerOffset, SpellView partView, io.vavr.collection.List<Integer> path) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.angle = angle;
                this.centerOffset = centerOffset;
                this.partView = partView;
                this.path = path;
                // When part view makes changes and we're visible, rebuild all children.
                partView.updateListener = () -> {
                    initialBuild = false;
                    List.copyOf(childCircles).forEach(CircleState::discardChildren);
                    this.initialize();
                };
            }

            public void initialize() {
                if (radius > FORK_THRESHOLD_FORWARDS) {
                    fork();
                }
                if (radius < FORK_THRESHOLD_BACKWARDS) {
                    backtrack();
                }
            }

            private void fork() {
                List.copyOf(childCircles).forEach(CircleState::discardChildren);
                childCircles.clear();

                int i = 0;
                for (var child : partView.children) {
                    var angle = partView.part.subAngle(i, this.angle);
                    var subX = x + (centerOffset * Math.cos(this.angle));
                    var subY = y + (centerOffset * Math.sin(this.angle));

                    var circle = new CircleState(
                        subX, subY, partView.part.subRadius(radius),
                        angle, radius, child, path.push(i)
                    );
                    circle.parentCircle = this;

                    childCircles.add(circle);
                    addCircle(circle);

                    i++;
                }

                if (partView.inner != null) {
                    var subX = x + (centerOffset * Math.cos(angle));
                    var subY = y + (centerOffset * Math.sin(angle));

                    var circle = new CircleState(
                        subX, subY, radius / 3,
                        angle, 0, partView.inner, path.push(-1)
                    );
                    circle.parentCircle = this;

                    childCircles.add(circle);
                    addCircle(circle);
                }
            }

            private void backtrack() {
                if (parentCircle != null || partView.parent == null) return;

                if (partView.isInner) {
                    var parentRadius = partView.parent.part.superRadius(radius);
                    var parentX = x - (parentRadius * Math.cos(angle));
                    var parentY = y - (parentRadius * Math.sin(angle));

                    var circle = new CircleState(
                        parentX, parentY, radius * 3,
                        angle, parentRadius, partView.parent, path.pop()
                    );

                    relinkChildren(circle);
                    addCircle(circle);
                } else if (!partView.parent.children.isEmpty()) {
                    var parentAngle = partView.parent.part.superAngle(partView.getOwnIndex(), angle);
                    var parentRadius = partView.parent.part.superRadius(radius);
                    var parentParentRadius = partView.parent.parent != null ? partView.parent.parent.part.superRadius(parentRadius) : 0;
                    var parentX = x - (parentParentRadius * Math.cos(parentAngle));
                    var parentY = y - (parentParentRadius * Math.sin(parentAngle));

                    var circle = new CircleState(
                        parentX, parentY, parentRadius,
                        parentAngle, parentParentRadius, partView.parent, path.pop()
                    );

                    relinkChildren(circle);
                    addCircle(circle);
                }
            }

            private void relinkChildren(CircleState parent) {
                for (var child : parent.partView.children) {
                    if (circles.containsKey(child)) {
                        var childState = circles.get(child);
                        parent.childCircles.add(childState);
                        childState.parentCircle = parent;
                    }
                }

                if (parent.partView.inner != null && circles.containsKey(parent.partView.inner)) {
                    var innerState = circles.get(parent.partView.inner);
                    parent.childCircles.add(innerState);
                    innerState.parentCircle = parent;
                }
            }

            public void zoom(double mouseX, double mouseY, double amount) {
                var newRadius = radius + amount * radius * ZOOM_SPEED;
                var newOffset = centerOffset + amount * centerOffset * ZOOM_SPEED;

                var newX = x + (x - mouseX) * amount * ZOOM_SPEED;
                var newY = y + (y - mouseY) * amount * ZOOM_SPEED;

                updatePosition(newX, newY, newRadius, newOffset);
            }

            public void drag(double deltaX, double deltaY) {
                updatePosition(x + deltaX, y + deltaY, radius, centerOffset);
            }

            public void updatePosition(double x, double y, double radius, double centerOffset) {
                this.x = x;
                this.y = y;
                var oldRadius = this.radius;
                this.radius = radius;
                this.centerOffset = centerOffset;
                if (radius > FORK_THRESHOLD_FORWARDS && oldRadius < FORK_THRESHOLD_FORWARDS) {
                    fork();
                }
                if (radius < FORK_THRESHOLD_BACKWARDS && oldRadius > FORK_THRESHOLD_BACKWARDS) {
                    backtrack();
                }
                if (radius < DISCARD_THRESHOLD_FORWARDS || radius > DISCARD_THRESHOLD_BACKWARDS) {
                    discard();
                }
            }

            public void updatePattern(Pattern pattern) {
                if (widget().revisionContext.getMacros().contains(pattern)) {
                    widget().revisionContext.delegateToServer(pattern, partView, partView::replace);
                    partView.loading = true;
                }

                var rev = Revisions.lookup(pattern);
                if (rev.isPresent()) {
                    rev.get().apply(widget().revisionContext, partView);
                } else {
                    partView.replaceGlyph(new PatternGlyph(pattern));
                }

                widget().revisionContext.updateSpell(rootView.part);
            }

            public void triggerEval() {
                widget().revisionContext.delegateToServer(Pattern.EMPTY, partView, partView::replace);
                partView.loading = true;
            }

            private void discard() {
                if (parentCircle == null && childCircles.isEmpty()) {
                    return;
                }

                if (parentCircle != null) {
                    parentCircle.dropChild(this);
                }

                for (var child : childCircles) {
                    child.parentCircle = null;
                }

                childCircles.clear();
                removeCircle(this);
            }

            private void discardChildren() {
                List.copyOf(childCircles).forEach(CircleState::discardChildren);
                discard();
            }

            private void dropChild(CircleState circle) {
                childCircles.remove(circle);
            }

            public String getKey() {
                var builder = new StringBuilder();
                for (var i : path) {
                    builder.append(i);
                    builder.append(",");
                }
                return builder.toString();
            }
        }
    }

    public interface DisposeCallback {
        void dispose(SpellView closestView, double x, double y, double radius, double angle, double centerOffset);
    }
}
