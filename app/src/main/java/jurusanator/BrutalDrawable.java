package jurusanator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
/**
 * The neo-brutalist box: flat colour, thick black border, hard offset shadow (no blur).
 * The shadow lives in the bottom/right strip of the bounds, so views using it need that much
 * extra padding. When "flat" the shadow is hidden (the press-down look).
 */
final class BrutalDrawable extends Drawable {
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shade = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF outer = new RectF();
    private final RectF back = new RectF();
    private final RectF edge = new RectF();
    private final float radius;
    private final float border;
    private final float shadow;
    private boolean flat;
    BrutalDrawable(int fillColor, float radius, float border, float shadow, int shadowColor) {
        this.radius = radius;
        this.border = border;
        this.shadow = shadow;
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(fillColor);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeWidth(border);
        line.setColor(Pal.INK);
        shade.setStyle(Paint.Style.FILL);
        shade.setColor(shadowColor);
    }
    void setFill(int color) {
        fill.setColor(color);
        invalidateSelf();
    }
    void setFlat(boolean value) {
        if (flat != value) {
            flat = value;
            invalidateSelf();
        }
    }
    @Override
    public void draw(Canvas canvas) {
        Rect b = getBounds();
        outer.set(b.left, b.top, b.right - shadow, b.bottom - shadow);
        if (!flat && shadow > 0f) {
            back.set(outer);
            back.offset(shadow, shadow);
            canvas.drawRoundRect(back, radius, radius, shade);
        }
        canvas.drawRoundRect(outer, radius, radius, fill);
        edge.set(outer);
        edge.inset(border / 2f, border / 2f);
        float inner = Math.max(0f, radius - border / 2f);
        canvas.drawRoundRect(edge, inner, inner, line);
    }
    @Override
    public void setAlpha(int alpha) {
        fill.setAlpha(alpha);
        line.setAlpha(alpha);
        shade.setAlpha(alpha);
    }
    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        // not used
    }
    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}


