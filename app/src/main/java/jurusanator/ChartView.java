package jurusanator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
/** Tiny chart: either a line (mock-test scores) or bars (study minutes per week). */
final class ChartView extends View {
    private final Paint box = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint ink = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint grid = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();
    private final Path path = new Path();
    private final float border;
    private final float radius;
    private final int barColor;
    private float[] values = new float[0];
    private boolean bars;
    private float max = 100f;
    ChartView(Context context, boolean bars, int barColor, float borderPx, float radiusPx) {
        super(context);
        this.bars = bars;
        this.barColor = barColor;
        this.border = borderPx;
        this.radius = radiusPx;
        box.setStyle(Paint.Style.FILL);
        box.setColor(Pal.PAPER);
        ink.setStyle(Paint.Style.STROKE);
        ink.setStrokeWidth(borderPx);
        ink.setColor(Pal.INK);
        ink.setStrokeJoin(Paint.Join.ROUND);
        ink.setStrokeCap(Paint.Cap.ROUND);
        fill.setStyle(Paint.Style.FILL);
        grid.setStyle(Paint.Style.STROKE);
        grid.setStrokeWidth(Math.max(1f, borderPx / 3f));
        grid.setColor(0x33111111);
    }
    /** @param top the value that reaches the top edge (e.g. 100 for scores). */
    void setValues(float[] data, float top) {
        values = data == null ? new float[0] : data;
        max = Math.max(1f, top);
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        float w = getWidth();
        float h = getHeight();
        if (w <= 0 || h <= 0) return;
        rect.set(border / 2f, border / 2f, w - border / 2f, h - border / 2f);
        canvas.drawRoundRect(rect, radius, radius, box);
        float pad = border * 3f;
        float left = pad, right = w - pad, top = pad, bottom = h - pad;
        for (int i = 1; i < 4; i++) {
            float y = top + (bottom - top) * i / 4f;
            canvas.drawLine(left, y, right, y, grid);
        }
        int n = values.length;
        if (n > 0) {
            if (bars) drawBars(canvas, left, right, top, bottom, n);
            else drawLine(canvas, left, right, top, bottom, n);
        }
        canvas.drawRoundRect(rect, radius, radius, ink);
    }
    private void drawBars(Canvas c, float l, float r, float t, float b, int n) {
        float slot = (r - l) / n;
        float barW = slot * 0.62f;
        fill.setColor(barColor);
        for (int i = 0; i < n; i++) {
            float v = Math.max(0f, Math.min(1f, values[i] / max));
            float x = l + slot * i + (slot - barW) / 2f;
            float top = b - (b - t) * v;
            if (v <= 0f) top = b - border;
            rect.set(x, top, x + barW, b);
            c.drawRect(rect.left, rect.top, rect.right, rect.bottom, fill);
            c.drawRect(rect.left, rect.top, rect.right, rect.bottom, ink);
        }
    }
    private void drawLine(Canvas c, float l, float r, float t, float b, int n) {
        path.reset();
        float[] xs = new float[n];
        float[] ys = new float[n];
        for (int i = 0; i < n; i++) {
            xs[i] = n == 1 ? (l + r) / 2f : l + (r - l) * i / (n - 1f);
            ys[i] = b - (b - t) * Math.max(0f, Math.min(1f, values[i] / max));
            if (i == 0) path.moveTo(xs[i], ys[i]);
            else path.lineTo(xs[i], ys[i]);
        }
        if (n > 1) c.drawPath(path, ink);
        fill.setColor(Pal.YELLOW);
        for (int i = 0; i < n; i++) {
            c.drawCircle(xs[i], ys[i], border * 1.8f, fill);
            c.drawCircle(xs[i], ys[i], border * 1.8f, ink);
        }
    }
}


