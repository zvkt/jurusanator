package jurusanator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
/** A horizontal stat bar: thick border, flat colour, animated fill. */
final class BarView extends View {
    private final Paint track = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF box = new RectF();
    private final float border;
    private final float radius;
    private float progress;
    private ValueAnimator anim;
    BarView(Context context, int fillColor, float borderPx, float radiusPx) {
        super(context);
        border = borderPx;
        radius = radiusPx;
        track.setStyle(Paint.Style.FILL);
        track.setColor(Pal.PAPER);
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(fillColor);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(borderPx);
        line.setColor(Pal.INK);
    }
    void setProgress(float value) {
        progress = Math.max(0f, Math.min(1f, value));
        invalidate();
    }
    /** Grow from 0 to the target value. */
    void animateTo(float target, long delayMs) {
        final float to = Math.max(0f, Math.min(1f, target));
        cancel();
        progress = 0f;
        anim = ValueAnimator.ofFloat(0f, to);
        anim.setStartDelay(delayMs);
        anim.setDuration(700);
        anim.setInterpolator(new DecelerateInterpolator(1.6f));
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                progress = (Float) a.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();
    }
    private void cancel() {
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
    }
    @Override
    protected void onDetachedFromWindow() {
        cancel();
        super.onDetachedFromWindow();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;
        box.set(border / 2f, border / 2f, w - border / 2f, h - border / 2f);
        canvas.drawRoundRect(box, radius, radius, track);
        float fillRight = border + (w - 2 * border) * progress;
        if (progress > 0f) {
            canvas.save();
            canvas.clipRect(border, border, fillRight, h - border);
            canvas.drawRoundRect(box, radius, radius, fill);
            canvas.restore();
            canvas.drawLine(fillRight, border, fillRight, h - border, line);
        }
        canvas.drawRoundRect(box, radius, radius, line);
    }
}


