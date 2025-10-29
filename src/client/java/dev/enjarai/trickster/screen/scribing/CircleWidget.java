package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.ModSounds;
import dev.enjarai.trickster.SpellView;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.render.CircleRenderer;
import dev.enjarai.trickster.spell.Pattern;
import io.wispforest.owo.braid.core.BraidDrawContext;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.instance.WidgetTransform;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.CustomDraw;
import io.wispforest.owo.braid.widgets.basic.MouseArea;
import io.wispforest.owo.braid.widgets.basic.Sized;
import io.wispforest.owo.braid.widgets.sharedstate.SharedState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static dev.enjarai.trickster.render.CircleRenderer.getPatternDotPosition;
import static dev.enjarai.trickster.render.CircleRenderer.isInsideHitbox;
import static dev.enjarai.trickster.render.SpellCircleRenderer.PART_PIXEL_RADIUS;
import static dev.enjarai.trickster.render.SpellCircleRenderer.PATTERN_TO_PART_RATIO;

public class CircleWidget extends StatefulWidget {
    public static final Byte MIDDLE_DOT = 4;
    public static final Byte DOT_COUNT = 9;

    public static final Byte[] RING_ORDER = {
        (byte) 0, (byte) 1, (byte) 2, (byte) 5, (byte) 8, (byte) 7, (byte) 6, (byte) 3
    };
    public static final Byte[] RING_INDICES = {
        (byte) 0, (byte) 1, (byte) 2, (byte) 7, (byte) 0, (byte) 3, (byte) 6, (byte) 5, (byte) 4
    };

    private static final CircleRenderer renderer = new CircleRenderer(true, true);

    private final double radius, startingAngle;
    private final SpellView partView;
    private final Consumer<Pattern> updatePattern;

    public CircleWidget(double radius, double startingAngle, SpellView partView, Consumer<Pattern> updatePattern) {
        this.radius = radius;
        this.startingAngle = startingAngle;
        this.partView = partView;
        this.updatePattern = updatePattern;
    }

    @Override
    public WidgetState<?> createState() {
        return new State();
    }

    public static class State extends WidgetState<CircleWidget> {
        private double mouseX, mouseY;
        @Nullable
        private List<Byte> drawingPattern;

        private void draw(BraidDrawContext ctx, WidgetTransform transform) {
            renderer.setMousePosition(mouseX, mouseY);
            renderer.renderCircle(
                ctx.getMatrices(), widget().partView.part,
                16, 16, 16, widget().startingAngle, 0,
                (float) Math.clamp(1 / (widget().radius / ctx.getScaledWindowHeight() * 3), 0.0, 0.8),
                new Vec3d(-1, 0, 0), drawingPattern
            );
            CircleRenderer.VERTEX_CONSUMERS.draw();
        }

        @Override
        public Widget build(BuildContext context) {
            return new MouseArea(
                area -> {
                    area.moveCallback(this::mouseMove);
                    area.releaseCallback((x, y, button, modifiers) -> mouseClick(x, y, button, SharedState.get(context, CircleSoupState.class)));
                },
                new Sized(
                    32, 32,
                    new CustomDraw(this::draw)
                )
            );
        }

        public boolean mouseClick(double x, double y, int button, CircleSoupState state) {
            if (state.dragging) {
                return false;
            }

            if (drawingPattern != null) {
                finishDrawing(true, state);
                return true;
            }

            if (state.drawing) {
                return false;
            }

            var dot = getHoveredDot(x, y);
            if (dot != null && button == 0) {
                startDrawing(state).add(dot);
                return true;
            }

            return false;
        }

        public void mouseMove(double x, double y) {
            mouseX = x;
            mouseY = y;

            if (drawingPattern == null) {
                return;
            }

            var dot = getHoveredDot(x, y);
            if (dot == null || drawingPattern == null || dot.equals(drawingPattern.getLast())) {
                return;
            }

            var moves = possibleMoves(drawingPattern);
            if (moves.get(dot) != null) {
                boolean removing = drawingPattern.size() > moves.get(dot).size();
                drawingPattern = moves.get(dot);

                MinecraftClient.getInstance().getSoundManager().play(
                    PositionedSoundInstance.master(ModSounds.DRAW, ModSounds.randomPitch(removing ? 0.6f : 1.0f, 0.2f))
                );
            }
        }

        public void finishDrawing(boolean apply, CircleSoupState state) {
            if (drawingPattern == null) {
                return;
            }

            state.drawing = false;

            if (apply) {
                if (drawingPattern.size() >= 2) {
                    widget().updatePattern.accept(Pattern.from(drawingPattern));
                } else {
                    widget().updatePattern.accept(Pattern.EMPTY);
                }
            }

            MinecraftClient.getInstance().getSoundManager().play(
                PositionedSoundInstance.master(ModSounds.COMPLETE, drawingPattern.size() > 1 ? 1f : 0.6f)
            );

            drawingPattern = null;
        }

        private List<Byte> startDrawing(CircleSoupState state) {
            drawingPattern = new ArrayList<>();
            state.drawing = true;
            return drawingPattern;
        }

        private static boolean areAdjacent(byte a, byte b) {
            if (a == MIDDLE_DOT || b == MIDDLE_DOT) {
                return false;
            } else {
                var i = RING_INDICES[a];
                return b == RING_ORDER[(i + 1) % 8] ||
                    b == RING_ORDER[i == 0 ? 7 : (i - 1) % 8];
            }
        }

        private boolean hasLine(byte a, byte b) {
            for (int i = 0; i < drawingPattern.size(); i++) {
                if (i < drawingPattern.size() - 1) {
                    var prev = drawingPattern.get(i);
                    var next = drawingPattern.get(i + 1);
                    if ((prev == a && next == b) || (prev == b && next == a)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private Byte getHoveredDot(double mouseX, double mouseY) {
            for (byte i = 0; i < DOT_COUNT; i++) {
                // if we are checking the closest neighboring dots on the ring
                // translate the dots outward a bit by enlarging the radius
                // this will make it easier to connect lines to the next neighbors on the ring
                var patternRadius = 16 / PATTERN_TO_PART_RATIO;
                var pixelSize = patternRadius / PART_PIXEL_RADIUS;

                if (drawingPattern != null && !drawingPattern.isEmpty()) {
                    var last = drawingPattern.getLast();
                    if (areAdjacent(last, i)) {
                        patternRadius += pixelSize * Trickster.CONFIG.adjacentPixelCollisionOffset() * 3;
                    }
                }

                var pos = getPatternDotPosition(16, 16, i, (float) patternRadius);

                if (isInsideHitbox(pos, (float) pixelSize, mouseX, mouseY)) {
                    return i;
                }
            }
            return null;
        }

        // generates all possible moves from the current drawingPattern
        // assigns the target dot with the resulting next drawingPattern
        private HashMap<Byte, List<Byte>> possibleMoves(List<Byte> drawingPattern) {
            var moves = new HashMap<Byte, List<Byte>>();
            if (drawingPattern.isEmpty()) {
                for (byte i = 0; i < DOT_COUNT; i++) {
                    var move = new ArrayList<Byte>();
                    move.add(i);
                    moves.put(i, move);
                }
            } else {
                var last = drawingPattern.getLast();
                for (byte i = 0; i < DOT_COUNT; i++) {
                    if (i == last) {
                        continue;
                    }

                    if (!hasLine(i, last)) {
                        var move = new ArrayList<>(drawingPattern);
                        // resolve the middle dot if we are going across
                        if (i == 8 - last) {
                            if (hasLine(i, MIDDLE_DOT) || hasLine(last, MIDDLE_DOT)) {
                                // we are already connected to the middle dot
                                // going across is impossible
                                continue;
                            } else {
                                move.add(MIDDLE_DOT);
                            }
                        }
                        move.add(i);
                        moves.put(i, move);
                    } else if (drawingPattern.size() >= 2 && drawingPattern.get(drawingPattern.size() - 2) == i) {
                        var move = new ArrayList<>(drawingPattern);
                        move.removeLast();
                        moves.put(i, move);
                    }
                }
            }
            return moves;
        }
    }
}
