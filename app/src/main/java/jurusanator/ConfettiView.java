package jurusanator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.view.View;
import java.util.Random;
/** A one-time burst of falling confetti pieces. Hides itself when done. Ignores touches. */
final class ConfettiView extends View {
    private static final int PIECES = 38;
    private static final long DURATION = 3200;
    private static final int[] COLORS = {Pal.YELLOW, Pal.PINK, Pal.SKY, Pal.LILAC, Pal.LIME_HI,
                Pal.ORANGE, Pal.MINT, Pal.CORAL};
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final float[] x0 = new float[PIECES], y0 = new float[PIECES], vy = new float[PIECES],
            sway = new float[PIECES], phase = new float[PIECES], size = new float[PIECES],
            spin = new float[PIECES];
    private final int[] kind = new int[PIECES];
    private final float density;
    private ValueAnimator anim;
    private long startedAt;
    private boolean seeded;
    ConfettiView(Context context) {
        super(context);
        density = context.getResources().getDisplayMetrics().density;
        setClickable(false);
        setFocusable(false);
        fill.setStyle(Paint.Style.FILL);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(2f * density);
        line.setColor(Pal.INK);
        line.setStrokeJoin(Paint.Join.ROUND);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0 || seeded) return;
        seeded = true;
        Random rnd = new Random();
        for (int i = 0; i < PIECES; i++) {
            x0[i] = rnd.nextFloat() * w;
            y0[i] = -rnd.nextFloat() * h * 0.45f - 20 * density;
            vy[i] = h * (0.30f + rnd.nextFloat() * 0.35f);
            sway[i] = (10 + rnd.nextInt(26)) * density;
            phase[i] = rnd.nextFloat() * 6.28f;
            size[i] = (8 + rnd.nextInt(8)) * density;
            spin[i] = (rnd.nextBoolean() ? 1 : -1) * (120 + rnd.nextInt(240));
            kind[i] = rnd.nextInt(3);
        }
        startedAt = SystemClock.uptimeMillis();
        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(DURATION);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
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
    @Override
    protected void onDraw(Canvas canvas) {
        if (!seeded) return;
        float t = (SystemClock.uptimeMillis() - startedAt) / 1000f;
        if (t * 1000f > DURATION + 100) {
            setVisibility(GONE);
            return;
        }
        for (int i = 0; i < PIECES; i++) {
            float x = x0[i] + sway[i] * (float) Math.sin(t * 2.2f + phase[i]);
            float y = y0[i] + vy[i] * t;
            if (y < -size[i] || y > getHeight() + size[i]) continue;
            canvas.save();
            canvas.translate(x, y);
            canvas.rotate(spin[i] * t);
            fill.setColor(COLORS[i % COLORS.length]);
            float s = size[i];
            if (kind[i] == 0) {
                canvas.drawRect(-s / 2f, -s / 2f, s / 2f, s / 2f, fill);
                canvas.drawRect(-s / 2f, -s / 2f, s / 2f, s / 2f, line);
            } else if (kind[i] == 1) {
                canvas.drawCircle(0, 0, s / 2f, fill);
                canvas.drawCircle(0, 0, s / 2f, line);
            } else {
                path.reset();
                path.moveTo(0, -s / 2f);
                path.lineTo(s / 2f, s / 2f);
                path.lineTo(-s / 2f, s / 2f);
                path.close();
                canvas.drawPath(path, fill);
                canvas.drawPath(path, line);
            }
            canvas.restore();
        }
    }
}


