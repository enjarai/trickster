package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.render.CircleRenderer;
import io.wispforest.owo.braid.animation.Animation;
import io.wispforest.owo.braid.animation.AutomaticallyAnimatedWidget;
import io.wispforest.owo.braid.animation.DoubleLerp;
import io.wispforest.owo.braid.animation.Easing;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.Key;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Transform;
import io.wispforest.owo.braid.widgets.drag.DragArena;
import io.wispforest.owo.braid.widgets.drag.DragArenaElement;
import io.wispforest.owo.braid.widgets.stack.Stack;
import org.joml.Matrix4f;

import java.time.Duration;

import static dev.enjarai.trickster.screen.scribing.CircleWidget.RADIUS;

public class CircleSoupElement extends AutomaticallyAnimatedWidget {
    private final CircleRenderer renderer;
    private final CircleSoupWidget.State.CircleState circleState;
    private final Constraints soupConstraints;
    private final boolean mutable;
    private final boolean allowsEval;
    private final boolean animateIn;

    private final boolean positioned;

    CircleSoupElement(Duration duration, Easing easing, CircleRenderer renderer,
        CircleSoupWidget.State.CircleState circleState, Constraints soupConstraints,
        boolean mutable, boolean allowsEval, boolean animateIn) {
        this(duration, easing, renderer, circleState, soupConstraints, mutable, allowsEval, animateIn, false);
    }

    CircleSoupElement(Duration duration, Easing easing, CircleRenderer renderer,
        CircleSoupWidget.State.CircleState circleState, Constraints soupConstraints,
        boolean mutable, boolean allowsEval, boolean animateIn, boolean positioned) {
        super(duration, easing);
        this.renderer = renderer;
        this.circleState = circleState;
        this.soupConstraints = soupConstraints;
        this.mutable = mutable;
        this.allowsEval = allowsEval;
        this.animateIn = animateIn;
        this.positioned = positioned;
    }

    @Override
    public State createState() {
        return new State();
    }

    public static class State extends AutomaticallyAnimatedWidget.State<CircleSoupElement> {
        private DoubleLerp x;
        private DoubleLerp y;
        private AngleLerp angle;
        private DoubleLerp radius;
        private DoubleLerp centerOffset;

        @Override
        protected void updateLerps() {
            var c = widget().circleState;
            x = visitLerp(x, c.x + widget().soupConstraints.maxWidth() / 2, DoubleLerp::new);
            y = visitLerp(y, c.y + widget().soupConstraints.maxHeight() / 2, DoubleLerp::new);
            angle = visitLerp(angle, c.angle, AngleLerp::new);
            radius = visitLerp(radius, c.radius, DoubleLerp::new);
            centerOffset = visitLerp(centerOffset, c.centerOffset, DoubleLerp::new);
        }

        @Override
        public Widget build(BuildContext context) {
            var c = widget().circleState;
            var radius = this.radius.compute(animationValue());
            var angle = this.angle.compute(animationValue());
            var centerOffset = this.centerOffset.compute(animationValue());

            var x = 0.;
            var y = 0.;

            if (!widget().positioned) {
                x = this.x.compute(animationValue());
                y = this.y.compute(animationValue());
            }

            var drawX = x + (centerOffset * Math.cos(angle));
            var drawY = y + (centerOffset * Math.sin(angle));
            return new DragArenaElement(
                drawX, drawY,
                new Stack(
                    new Transform(
                        new Matrix4f()
                            .translate(-RADIUS, -RADIUS, 0)
                            .scale((float) (double) radius / RADIUS)
                            .translate(0, 0, 1f / (float) (double) radius),
                        new ScaleInOutWidget(
                            true,
                            widget().animateIn ? Animation.Target.START : Animation.Target.END,
                            (t) -> {},
                            new CircleWidget(
                                widget().renderer,
                                radius,
                                c.angle,
                                c.partView,
                                c::updatePattern,
                                widget().mutable,
                                widget().allowsEval ? c::triggerEval : null
                            )
                        )
                    ),
                    new DragArena(c.childCircles.stream().map(c2 -> new CircleSoupElement(
                        Duration.ofMillis(250),
                        Easing.OUT_EXPO,
                        widget().renderer,
                        c2, widget().soupConstraints,
                        widget().mutable,
                        widget().allowsEval,
                        widget().animateIn,
                        true
                    ).key(
                        Key.of(c2.partView.uuid.toString())
                    )).toList())
                )
            );
        }
    }
}
