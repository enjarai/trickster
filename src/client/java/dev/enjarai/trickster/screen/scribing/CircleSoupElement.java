package dev.enjarai.trickster.screen.scribing;

import dev.enjarai.trickster.render.CircleRenderer;
import io.wispforest.owo.braid.animation.AutomaticallyAnimatedWidget;
import io.wispforest.owo.braid.animation.DoubleLerp;
import io.wispforest.owo.braid.animation.Easing;
import io.wispforest.owo.braid.core.Constraints;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Transform;
import io.wispforest.owo.braid.widgets.drag.DragArenaElement;
import org.joml.Matrix4f;

import java.time.Duration;

import static dev.enjarai.trickster.screen.scribing.CircleWidget.RADIUS;

public class CircleSoupElement extends AutomaticallyAnimatedWidget {
    private final CircleRenderer renderer;
    private final CircleSoupWidget.State.CircleState circleState;
    private final Constraints soupConstraints;
    private final boolean mutable;
    private final boolean allowsEval;

    CircleSoupElement(Duration duration, Easing easing, CircleRenderer renderer, CircleSoupWidget.State.CircleState circleState, Constraints soupConstraints, boolean mutable, boolean allowsEval) {
        super(duration, easing);
        this.renderer = renderer;
        this.circleState = circleState;
        this.soupConstraints = soupConstraints;
        this.mutable = mutable;
        this.allowsEval = allowsEval;
    }

    @Override
    public State createState() {
        return new State();
    }

    public static class State extends AutomaticallyAnimatedWidget.State<CircleSoupElement> {
        private DoubleLerp x;
        private DoubleLerp y;
        private DoubleLerp radius;

        @Override
        protected void updateLerps() {
            var c = widget().circleState;
            x = visitLerp(x, c.x + widget().soupConstraints.maxWidth() / 2, DoubleLerp::new);
            y = visitLerp(y, c.y + widget().soupConstraints.maxHeight() / 2, DoubleLerp::new);
            radius = visitLerp(radius, c.radius, DoubleLerp::new);
        }

        @Override
        public Widget build(BuildContext context) {
            var c = widget().circleState;
            var radius = this.radius.compute(animationValue());
            return new DragArenaElement(
                x.compute(animationValue()), y.compute(animationValue()),
                new Transform(
                    new Matrix4f()
                        .translate(-RADIUS, -RADIUS, 0)
                        .scale((float) (double) radius / RADIUS)
                        .translate(0, 0, 1f / (float) (double) radius),
                    new CircleWidget(
                        widget().renderer,
                        radius,
                        c.startingAngle,
                        c.partView,
                        c::updatePattern,
                        widget().mutable,
                        widget().allowsEval ? c::triggerEval : null
                    )
                )
            );
        }
    }
}
