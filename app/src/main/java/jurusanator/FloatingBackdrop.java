package jurusanator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import java.util.Random;
/**
 * Screen background: soft green with a dot grid and a few neo-brutalist shapes that
 * drift slowly. Cheap to draw (a tiled shader + 9 shapes). Pauses when not visible.
 */
final class FloatingBackdrop extends View {
    private static final int[] COLORS = {Pal.YELLOW, Pal.PINK, Pal.SKY, Pal.LILAC, Pal.LIME_HI,
                Pal.ORANGE, Pal.MINT, Pal.CORAL, Pal.YELLOW};
    private static final int N = COLORS.length;
    private final Paint dots = new Paint();
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final float density;
    private final float[] bx = new float[N], by = new float[N], amp = new float[N],
            speed = new float[N], phase = new float[N], size = new float[N], spin = new float[N];
    private final int[] kind = new int[N];
    private ValueAnimator anim;
    FloatingBackdrop(Context context, long seed) {
        super(context);
        density = context.getResources().getDisplayMetrics().density;
        Random rnd = new Random(seed);
        for (int i = 0; i < N; i++) {
            bx[i] = 0.06f + rnd.nextFloat() * 0.88f;
            by[i] = 0.05f + rnd.nextFloat() * 0.9f;
            amp[i] = (14 + rnd.nextInt(16)) * density;
            speed[i] = 0.25f + rnd.nextFloat() * 0.4f;
            phase[i] = rnd.nextFloat() * 6.28f;
            size[i] = (16 + rnd.nextInt(14)) * density;
            spin[i] = (rnd.nextBoolean() ? 1 : -1) * (10 + rnd.nextInt(20));
            kind[i] = i % 5;
        }
        fill.setStyle(Paint.Style.FILL);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(2.5f * density);
        line.setStrokeJoin(Paint.Join.ROUND);
        line.setColor(Pal.INK);
        makeDotShader();
    }
    private void makeDotShader() {
        int tile = Math.max(8, Math.round(24 * density));
        Bitmap bmp = Bitmap.createBitmap(tile, tile, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Pal.dots());
        c.drawCircle(tile / 2f, tile / 2f, 1.6f * density, p);
        dots.setShader(new BitmapShader(bmp, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
    }
    private void start() {
        if (anim != null) return;
        anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(1000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                invalidate();
            }
        });
        anim.start();
    }
    private void stop() {
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
    }
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }
    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) start();
        else stop();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        canvas.drawColor(Pal.bg());
        canvas.drawRect(0, 0, w, h, dots);
        float t = SystemClock.uptimeMillis() / 1000f;
        for (int i = 0; i < N; i++) {
            float x = bx[i] * w + amp[i] * (float) Math.sin(t * speed[i] + phase[i]);
            float y = by[i] * h + amp[i] * (float) Math.cos(t * speed[i] * 0.8f + phase[i]);
            drawShape(canvas, i, x, y, t * spin[i]);
        }
    }
    private void drawShape(Canvas c, int i, float x, float y, float angle) {
        float s = size[i];
        float off = 3f * density;
        c.save();
        c.translate(x, y);
        c.rotate(angle);
        fill.setColor(Pal.INK);
        c.save();
        c.translate(off, off);
        shape(c, kind[i], s, fill, false);
        c.restore();
        fill.setColor(COLORS[i]);
        shape(c, kind[i], s, fill, true);
        c.restore();
    }
    private void shape(Canvas c, int kind, float s, Paint paint, boolean outline) {
        switch (kind) {
            case 0:
            c.drawCircle(0, 0, s / 2f, paint);
            if (outline) c.drawCircle(0, 0, s / 2f, line);
            break;
            case 1:
            c.drawRect(-s / 2f, -s / 2f, s / 2f, s / 2f, paint);
            if (outline) c.drawRect(-s / 2f, -s / 2f, s / 2f, s / 2f, line);
            break;
            case 2:
            path.reset();
            path.moveTo(0, -s / 2f);
            path.lineTo(s / 2f, s / 2f);
            path.lineTo(-s / 2f, s / 2f);
            path.close();
            c.drawPath(path, paint);
            if (outline) c.drawPath(path, line);
            break;
            case 3:
            float a = s / 6f;
            float b = s / 2f;
            path.reset();
            path.moveTo(-a, -b);
            path.lineTo(a, -b);
            path.lineTo(a, -a);
            path.lineTo(b, -a);
            path.lineTo(b, a);
            path.lineTo(a, a);
            path.lineTo(a, b);
            path.lineTo(-a, b);
            path.lineTo(-a, a);
            path.lineTo(-b, a);
            path.lineTo(-b, -a);
            path.lineTo(-a, -a);
            path.close();
            c.drawPath(path, paint);
            if (outline) c.drawPath(path, line);
            break;
            default:
            if (outline) {
                c.drawCircle(0, 0, s / 2.4f, line);
            } else {
                c.drawCircle(0, 0, s / 2f, paint);
            }
            break;
        }
    }
}


