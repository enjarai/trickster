package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.revision.RevisionContext;
import dev.enjarai.trickster.revision.Revisions;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.owo.braid.animation.Easing;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.core.KeyModifiers;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.Key;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.*;
import io.wispforest.owo.braid.widgets.drag.DragArena;
import io.wispforest.owo.braid.widgets.focus.Focusable;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
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
    public static final double FORK_THRESHOLD_BACKWARDS = 100;
    public static final double DISCARD_THRESHOLD_BACKWARDS = 300;
    public static final double ZOOM_SPEED = 0.1;

    //    private SpellPart spellPart;
    //
    //    public CircleSoupWidget(SpellPart spellPart) {
    //        this.spellPart = spellPart;
    //    }

    @Override
    public WidgetState<?> createState() {
        return new State();
    }

    public static class State extends WidgetState<CircleSoupWidget> {
        private RevisionContext revisionContext;
        private double mouseX, mouseY, draggingStartX, draggingStartY;
        private int zoomering;
        private Constraints arenaConstraints;
        private final Map<SpellView, CircleState> circles = new IdentityHashMap<>();

        @Override
        public void init() {
            circles.clear();
            addCircle(new CircleState(
                -320, 0,
                80,
                0,
                SpellView.index((SpellPart) Fragment.fromBase64(
                    "YxEpKcpMzi4uSS2yKi5IzcmJL0gsKsEqKIgQLEgsAVJ5G32YWFkZmRkYGECYgZERUwkDAxNIiomBSsocoMrY8CnjFGBnIMoYDCsFEGrzSnOTUosYEICJgQlZKZFhBNbJWIfPORs8mFjxOtfFgYsse+GepL7RTIyc+Mw15GhqITZUSYgpHpLdCgpbRiLTHh4HOxiQ4WByQhWvS50awLEFDAhdfMpA2RKf/KIGvH61/0CsXx0QikgOFaLigVCCJcoQqmYYTogU7iiChAmRCRqfSg4GFqJDn9h8RgRBTqKVxudOpiaGhcSnRixWKAhQKUQXMTB04gks+weogYXbRkYF0lxEYg4B5m+8IUq3+oJIraDCnkDJhai4ia5sCQeZACMDHsOILsaUG5w68JjjoESkOUzoLicpATiBA4jUREVk4JBcBzigxQROc4C2cOB1hwO2QMHjS1YSrCM2aQPzAzPtmqlIgVeWmlySX8QMC7oDExjQAaHa7QD2WIEavGeCkW4DUG4/NG3bzI/pTiAumhywF31Qg/dfXssoguRMewSfxDYAsTE9GpO0iUn70ZgcpjG5fzQmh2hMopeuVItJDIJRFJ9xhDqGyC0YKg2uEKuSwl4hlpBAGp8YHCkZSxcvhIjwFnJhwCuPbAiUAAC2SONd1RMAAA==")
                )
            ));
            addCircle(new CircleState(
                0, 0,
                80,
                0,
                SpellView.index((SpellPart) Fragment.fromBase64(
                    "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")
                )
            ));
            revisionContext = RevisionContext.DUMMY;
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
                                return new DragArena(circles.values().stream().map(c -> new CircleSoupElement(
                                    Duration.ofMillis(0),
                                    Easing.OUT_BOUNCE,
                                    c, constraints
                                ).key(
                                    Key.of(String.valueOf(c.hashCode()))
                                )).toList());
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
            private CircleState parentCircle = null;
            private final List<CircleState> childCircles = new ArrayList<>();
            double x, y, radius, startingAngle;
            final SpellView partView;

            private CircleState(double x, double y, double radius, double startingAngle, SpellView partView) {
                this.x = x;
                this.y = y;
                this.radius = radius;
                this.startingAngle = startingAngle;
                this.partView = partView;
                // When part view makes changes and we're visible, rebuild all children.
                partView.rebuildListener = () -> {
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
                    var angle = partView.part.subAngle(i, startingAngle);
                    var subX = x + (radius * Math.cos(angle));
                    var subY = y + (radius * Math.sin(angle));

                    var circle = new CircleState(
                        subX, subY, partView.part.subRadius(radius),
                        angle, child
                    );
                    circle.parentCircle = this;

                    childCircles.add(circle);
                    addCircle(circle);

                    i++;
                }

                if (partView.inner != null) {
                    var circle = new CircleState(
                        x, y, radius / 3,
                        startingAngle, partView.inner
                    );
                    circle.parentCircle = this;

                    childCircles.add(circle);
                    addCircle(circle);
                }
            }

            private void backtrack() {
                if (parentCircle != null || partView.parent == null) return;

                if (partView.isInner) {
                    var circle = new CircleState(
                        x, y, radius * 3,
                        startingAngle, partView.parent
                    );

                    relinkChildren(circle);
                    addCircle(circle);
                } else if (!partView.parent.children.isEmpty()) {
                    var parentAngle = partView.parent.part.superAngle(partView.getOwnIndex(), startingAngle);
                    var parentRadius = partView.parent.part.superRadius(radius);
                    var parentX = x - (parentRadius * Math.cos(startingAngle));
                    var parentY = y - (parentRadius * Math.sin(startingAngle));

                    var circle = new CircleState(
                        parentX, parentY, parentRadius,
                        parentAngle, partView.parent
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

                var newX = x + (x - mouseX) * amount * ZOOM_SPEED;
                var newY = y + (y - mouseY) * amount * ZOOM_SPEED;

                updatePosition(newX, newY, newRadius);
            }

            public void drag(double deltaX, double deltaY) {
                updatePosition(x + deltaX, y + deltaY, radius);
            }

            public void updatePosition(double x, double y, double radius) {
                this.x = x;
                this.y = y;
                var oldRadius = this.radius;
                this.radius = radius;
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
                var rev = Revisions.lookup(pattern);

                //                if (compiled.equals(Revisions.EXECUTE_OFF_HAND.pattern())) {
                //                    toBeReplaced = drawingPart; //TODO: allow handling this in a more generic way?
                //                    Revisions.EXECUTE_OFF_HAND.apply(revisionContext, spellPart, drawingPart);
                //                } else

                if (rev.isPresent()) {
                    rev.get().apply(revisionContext, partView);
                    //                } else if (revisionContext.getMacros().get(compiled).isDefined()) {
                    //                    toBeReplaced = drawingPart;
                    //                    revisionContext.updateSpellWithSpell(drawingPart, revisionContext.getMacros().get(compiled).get());
                } else {
                    partView.replaceGlyph(new PatternGlyph(pattern));
                }
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
        }
    }
}
