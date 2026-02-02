package dev.enjarai.trickster.screen.scribing;

import io.wispforest.owo.braid.animation.Animation;
import io.wispforest.owo.braid.animation.Easing;
import io.wispforest.owo.braid.framework.BuildContext;
import io.wispforest.owo.braid.framework.proxy.WidgetState;
import io.wispforest.owo.braid.framework.widget.StatefulWidget;
import io.wispforest.owo.braid.framework.widget.Widget;
import io.wispforest.owo.braid.widgets.basic.Transform;
import org.joml.Matrix4f;

import java.time.Duration;

public class ScaleInOutWidget extends StatefulWidget {
    private final boolean show;
    private final Animation.Target startingPoint;
    private final Animation.FinishListener finishListener;
    private final Widget child;

    public ScaleInOutWidget(boolean show, Animation.Target startingPoint, Animation.FinishListener finishListener, Widget child) {
        this.show = show;
        this.startingPoint = startingPoint;
        this.finishListener = finishListener;
        this.child = child;
    }

    @Override
    public WidgetState<?> createState() {
        return new State();
    }

    public static class State extends WidgetState<ScaleInOutWidget> {
        private Animation animation;

        private void callback(double progress) {
            this.setState(() -> {});
        }

        private void finish(Animation.Target atTarget) {
            widget().finishListener.onFinished(atTarget);
        }

        @Override
        public void init() {
            animation = new Animation(
                Easing.OUT_EXPO,
                Duration.ofMillis(1000),
                this::scheduleAnimationCallback,
                this::callback,
                this::finish,
                widget().startingPoint
            );

            if (widget().show) {
                animation.towards(Animation.Target.END, false);
            }
        }

        @Override
        public Widget build(BuildContext context) {
            return new Transform(
                new Matrix4f()
                    .scale((float) animation.progress()),
                widget().child
            );
        }

        @Override
        public void didUpdateWidget(ScaleInOutWidget oldWidget) {
            animation.towards(
                widget().show ? Animation.Target.END : Animation.Target.START,
                false
            );
        }
    }
}
