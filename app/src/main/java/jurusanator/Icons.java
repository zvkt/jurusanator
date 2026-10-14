package jurusanator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
/** Small canvas-drawn icons for the toggle buttons (no image files). */
final class Icons {
    private Icons() {}
    static final int NONE = 0, SOUND_ON = 1, SOUND_OFF = 2, SUN = 3, MOON = 4, BELL = 5;
    private static final Paint STROKE = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Paint FILL = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final Path PATH = new Path();
    private static final Path CUT = new Path();
    private static final RectF OVAL = new RectF();
    /** Draws icon {@code type} centred at (cx,cy) inside a square of size 2r. UI thread only. */
    static void draw(Canvas c, int type, float cx, float cy, float r, float stroke) {
        STROKE.setStyle(Paint.Style.STROKE);
        STROKE.setStrokeWidth(stroke);
        STROKE.setStrokeCap(Paint.Cap.ROUND);
        STROKE.setStrokeJoin(Paint.Join.ROUND);
        STROKE.setColor(Pal.INK);
        FILL.setStyle(Paint.Style.FILL);
        switch (type) {
            case SOUND_ON:
            case SOUND_OFF:
            speaker(c, cx, cy, r, type == SOUND_OFF);
            break;
            case SUN:
            sun(c, cx, cy, r);
            break;
            case MOON:
            moon(c, cx, cy, r);
            break;
            case BELL:
            bell(c, cx, cy, r);
            break;
            default:
            break;
        }
    }
    private static void speaker(Canvas c, float cx, float cy, float r, boolean off) {
        PATH.reset();
        PATH.moveTo(cx - 0.85f * r, cy - 0.35f * r);
        PATH.lineTo(cx - 0.4f * r, cy - 0.35f * r);
        PATH.lineTo(cx + 0.1f * r, cy - 0.8f * r);
        PATH.lineTo(cx + 0.1f * r, cy + 0.8f * r);
        PATH.lineTo(cx - 0.4f * r, cy + 0.35f * r);
        PATH.lineTo(cx - 0.85f * r, cy + 0.35f * r);
        PATH.close();
        FILL.setColor(off ? Pal.GRAY : Pal.LIME_HI);
        c.drawPath(PATH, FILL);
        c.drawPath(PATH, STROKE);
        if (off) {
            c.drawLine(cx + 0.4f * r, cy - 0.45f * r, cx + 0.95f * r, cy + 0.45f * r, STROKE);
            c.drawLine(cx + 0.95f * r, cy - 0.45f * r, cx + 0.4f * r, cy + 0.45f * r, STROKE);
        } else {
            OVAL.set(cx - 0.2f * r, cy - 0.5f * r, cx + 0.5f * r, cy + 0.5f * r);
            c.drawArc(OVAL, -50f, 100f, false, STROKE);
            OVAL.set(cx - 0.3f * r, cy - 0.95f * r, cx + 0.95f * r, cy + 0.95f * r);
            c.drawArc(OVAL, -45f, 90f, false, STROKE);
        }
    }
    private static void sun(Canvas c, float cx, float cy, float r) {
        for (int i = 0; i < 8; i++) {
            double a = i * Math.PI / 4;
            float cos = (float) Math.cos(a);
            float sin = (float) Math.sin(a);
            c.drawLine(cx + cos * 0.62f * r, cy + sin * 0.62f * r,
                    cx + cos * 0.95f * r, cy + sin * 0.95f * r, STROKE);
        }
        FILL.setColor(Pal.YELLOW);
        c.drawCircle(cx, cy, 0.42f * r, FILL);
        c.drawCircle(cx, cy, 0.42f * r, STROKE);
    }
    private static void moon(Canvas c, float cx, float cy, float r) {
        PATH.reset();
        PATH.addCircle(cx, cy, 0.8f * r, Path.Direction.CW);
        CUT.reset();
        CUT.addCircle(cx + 0.5f * r, cy - 0.3f * r, 0.7f * r, Path.Direction.CW);
        boolean ok;
        try {
            ok = PATH.op(CUT, Path.Op.DIFFERENCE);
        } catch (RuntimeException e) {
            ok = false;
        }
        if (!ok) {
            PATH.reset();
            PATH.addCircle(cx, cy, 0.8f * r, Path.Direction.CW);
        }
        FILL.setColor(Pal.YELLOW);
        c.drawPath(PATH, FILL);
        c.drawPath(PATH, STROKE);
    }
    private static void bell(Canvas c, float cx, float cy, float r) {
        PATH.reset();
        PATH.moveTo(cx - 0.75f * r, cy + 0.5f * r);
        PATH.cubicTo(cx - 0.55f * r, cy + 0.2f * r, cx - 0.6f * r, cy - 0.85f * r, cx, cy - 0.85f * r);
        PATH.cubicTo(cx + 0.6f * r, cy - 0.85f * r, cx + 0.55f * r, cy + 0.2f * r, cx + 0.75f * r, cy + 0.5f * r);
        PATH.close();
        FILL.setColor(Pal.YELLOW);
        c.drawPath(PATH, FILL);
        c.drawPath(PATH, STROKE);
        FILL.setColor(Pal.INK);
        c.drawCircle(cx, cy + 0.82f * r, 0.18f * r, FILL);
    }
}


