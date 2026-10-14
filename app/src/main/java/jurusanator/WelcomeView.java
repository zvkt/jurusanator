package jurusanator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
/**
 * Runs the first-launch animation: advances time, plays the sound cues on the beat,
 * and handles taps (first tap skips ahead, tap on the button continues).
 */
final class WelcomeView extends View {
    interface Listener {
        void onFinish();
    }
    private final WelcomeScene scene = new WelcomeScene();
    private final Sfx sfx;
    private final Listener listener;
    private final boolean[] popped = new boolean[WelcomeScene.POP_TIMES.length];
    private boolean whooshed, slammed, dinged, finished;
    private ValueAnimator anim;
    private long startedAt;
    private float skip;
    WelcomeView(Context context, Sfx sfx, Listener listener) {
        super(context);
        this.sfx = sfx;
        this.listener = listener;
    }
    private float time() {
        return (SystemClock.uptimeMillis() - startedAt) / 1000f + skip;
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startedAt = SystemClock.uptimeMillis();
        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(1000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                beats(time());
                invalidate();
            }
        });
        anim.start();
    }
    @Override
    protected void onDetachedFromWindow() {
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
        super.onDetachedFromWindow();
    }
    private void beats(float t) {
        if (!whooshed && t > 0.05f) {
            whooshed = true;
            sfx.whoosh();
        }
        for (int i = 0; i < popped.length; i++) {
            if (!popped[i] && t >= WelcomeScene.POP_TIMES[i]) {
                popped[i] = true;
                sfx.pop(i);
            }
        }
        if (!slammed && t >= WelcomeScene.SLAM_TIME) {
            slammed = true;
            sfx.slam();
        }
        if (!dinged && t >= WelcomeScene.DING_TIME) {
            dinged = true;
            sfx.ding();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        scene.draw(canvas, getWidth(), getHeight(), time());
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float t = time();
            if (t < WelcomeScene.READY) {
                // skip ahead to the final frame, without replaying every sound
                skip += WelcomeScene.READY + 0.05f - t;
                java.util.Arrays.fill(popped, true);
                whooshed = true;
                slammed = true;
                dinged = true;
                sfx.ding();
                invalidate();
            } else if (!finished) {
                finished = true;
                sfx.tap();
                listener.onFinish();
            }
        }
        return true;
    }
}


