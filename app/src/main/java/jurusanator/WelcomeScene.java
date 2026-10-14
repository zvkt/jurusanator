package jurusanator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Typeface;
/**
 * The first-launch vector animation, drawn purely with canvas shapes:
 * blocks slide in, a graduation cap is DRAWN stroke by stroke, 8 major stickers pop around it,
 * the title letters drop in with a bounce, then the tagline and start button appear.
 * draw() is a pure function of the time t (seconds), so it is easy to preview and test.
 */
final class WelcomeScene {
    /** From this time on the "tap to start" button is shown and a tap continues. */
    static final float READY = 3.6f;
    static final float[] POP_TIMES = {1.50f, 1.61f, 1.72f, 1.83f, 1.94f, 2.05f, 2.16f, 2.27f};
    static final float SLAM_TIME = 3.0f;
    static final float DING_TIME = 3.45f;
    private static final String TITLE = "jurusanator";
    private static final int[] RING_ART = {Art.LAPTOP, Art.FLASK, Art.PALETTE, Art.SCALES,
                Art.HEART, Art.ROCKET, Art.BULB, Art.GLOBE};
    private static final int[] RING_BG = {Pal.PINK, Pal.SKY, Pal.YELLOW, Pal.LILAC,
                Pal.MINT, Pal.ORANGE, Pal.PINK, Pal.YELLOW};
    private final Art art = new Art();
    private final Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint line = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint title = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mono = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path capTop = new Path();
    private final Path capBase = new Path();
    private final Path capTassel = new Path();
    private final Path seg = new Path();
    private final PathMeasure measure = new PathMeasure();
    private final RectF rect = new RectF();
    private final float[] letterX = new float[TITLE.length() + 1];
    private float titleSize;
    WelcomeScene() {
        fill.setStyle(Paint.Style.FILL);
        line.setStyle(Paint.Style.STROKE);
        line.setStrokeJoin(Paint.Join.ROUND);
        line.setStrokeCap(Paint.Cap.ROUND);
        line.setColor(Pal.INK);
        title.setTypeface(Typeface.create("sans-serif-black",Typeface.NORMAL));
        title.setTextAlign(Paint.Align.LEFT);
        mono.setTypeface(Typeface.create("monospace",Typeface.BOLD));
        mono.setTextAlign(Paint.Align.CENTER);
        // graduation cap, relative to its centre (0,0), in design units
        capTop.moveTo(-21, -6);
        capTop.lineTo(0, -17);
        capTop.lineTo(21, -6);
        capTop.lineTo(0, 5);
        capTop.close();
        capBase.moveTo(-11, -1);
        capBase.lineTo(-11, 8);
        capBase.cubicTo(-11, 14, 11, 14, 11, 8);
        capBase.lineTo(11, -1);
        capTassel.moveTo(21, -6);
        capTassel.lineTo(21, 7);
        title.setTextSize(100f);
        float w100 = title.measureText(TITLE);
        titleSize = w100 <= 0f ? 14f : 100f * 84f / w100;
        title.setTextSize(titleSize);
        float total = title.measureText(TITLE);
        for (int i = 0; i <= TITLE.length(); i++) {
            letterX[i] = 50f - total / 2f + title.measureText(TITLE.substring(0, i));
        }
    }
    // --------------------------------------------------------------------easing
    private static float clamp(float v) {
        return v < 0f ? 0f : (v > 1f ? 1f : v);
    }
    private static float easeOutBack(float x) {
        float c1 = 1.70158f;
        float c3 = c1 + 1f;
        float y = x - 1f;
        return 1f + c3 * y * y * y + c1 * y * y;
    }
    private static float bounce(float x) {
        float n = 7.5625f;
        float d = 2.75f;
        if (x < 1f / d) return n * x * x;
        if (x < 2f / d) { x -= 1.5f / d; return n * x * x + 0.75f; }
        if (x < 2.5f / d) { x -= 2.25f / d; return n * x * x + 0.9375f; }
        x -= 2.625f / d;
        return n * x * x + 0.984375f;
    }
    // ---------------------------------------------------------------------- draw
    void draw(Canvas c, float w, float h, float t) {
        if (w <= 0f || h <= 0f) return;
        float u = w / 100f;
        float hu = h / u;
        c.drawColor(Pal.LIME);
        c.save();
        c.scale(u, u);
        float cx = 50f;
        float cy = hu * 0.33f;
        float titleY = cy + 62f;
        drawBlocks(c, hu, t);
        drawStage(c, cx, cy, t);
        drawCap(c, cx, cy, t);
        drawRing(c, cx, cy, t);
        drawTitle(c, titleY, t);
        drawTagline(c, titleY + 16f, t);
        drawButton(c, Math.max(titleY + 36f, hu * 0.84f), t);
        drawSkipHint(c, hu, t);
        c.restore();
    }
    private void box(Canvas c, float l, float t, float r, float b, float rad, int color, float shadow) {
        rect.set(l, t, r, b);
        if (shadow > 0f) {
            rect.offset(shadow, shadow);
            fill.setColor(Pal.INK);
            c.drawRoundRect(rect, rad, rad, fill);
            rect.offset(-shadow, -shadow);
        }
        fill.setColor(color);
        c.drawRoundRect(rect, rad, rad, fill);
        line.setStrokeWidth(1.0f);
        c.drawRoundRect(rect, rad, rad, line);
    }
    private void drawBlocks(Canvas c, float hu, float t) {
        float b = easeOutBack(clamp(t / 0.55f));
        c.save();
        c.translate(-70f * (1f - b), 0f);
        c.rotate(-8f, 0f, 0f);
        box(c, -14f, -8f, 46f, 20f, 3f, Pal.FOREST, 2.4f);
        c.restore();
        float b2 = easeOutBack(clamp((t - 0.1f) / 0.55f));
        float y2 = hu - 6f + 70f * (1f - b2);
        fill.setColor(Pal.INK);
        c.drawCircle(94.4f, y2 + 2.4f, 30f, fill);
        fill.setColor(Pal.PINK);
        c.drawCircle(92f, y2, 30f, fill);
        line.setStrokeWidth(1.0f);
        c.drawCircle(92f, y2, 30f, line);
        float b3 = easeOutBack(clamp((t - 0.3f) / 0.5f));
        if (b3 > 0.02f) {
            c.save();
            c.translate(90f, hu * 0.1f);
            c.rotate(18f + 40f * (1f - b3));
            c.scale(b3, b3);
            box(c, -7f, -7f, 7f, 7f, 1.5f, Pal.YELLOW, 1.6f);
            c.restore();
        }
        float b4 = easeOutBack(clamp((t - 0.45f) / 0.5f));
        if (b4 > 0.02f) {
            c.save();
            c.translate(8f, hu * 0.84f);
            c.scale(b4, b4);
            fill.setColor(Pal.INK);
            c.drawCircle(1.6f, 1.6f, 7f, fill);
            fill.setColor(Pal.LILAC);
            c.drawCircle(0f, 0f, 7f, fill);
            line.setStrokeWidth(1.0f);
            c.drawCircle(0f, 0f, 7f, line);
            c.restore();
        }
    }
    private void drawStage(Canvas c, float cx, float cy, float t) {
        float s = easeOutBack(clamp((t - 0.35f) / 0.45f));
        if (s <= 0.02f) return;
        float r = 25f * s;
        fill.setColor(Pal.INK);
        c.drawCircle(cx + 3f, cy + 3f, r, fill);
        fill.setColor(Pal.PAPER);
        c.drawCircle(cx, cy, r, fill);
        line.setStrokeWidth(1.2f);
        c.drawCircle(cx, cy, r, line);
    }
    private void drawCap(Canvas c, float cx, float cy, float t) {
        if (t < 0.7f) return;
        float bob = t > 1.6f ? (float) Math.sin(t * 2.2f) * 0.9f : 0f;
        c.save();
        c.translate(cx, cy + bob);
        part(c, capBase, Pal.MINT, true, 0.95f, 1.30f, t, false);
        part(c, capTop, Pal.FOREST, true, 0.70f, 1.15f, t, true);
        part(c, capTassel, 0, false, 1.20f, 1.50f, t, false);
        float knob = easeOutBack(clamp((t - 1.45f) / 0.3f));
        if (knob > 0.02f) {
            fill.setColor(Pal.YELLOW);
            c.drawCircle(21f, 9.6f, 2.6f * knob, fill);
            line.setStrokeWidth(1.0f);
            c.drawCircle(21f, 9.6f, 2.6f * knob, line);
        }
        c.restore();
    }
    /** Draws one cap part: the outline is revealed progressively, the fill fades in at the end. */
    private void part(Canvas c, Path p, int color, boolean filled, float t0, float t1, float t, boolean closed) {
        float prog = clamp((t - t0) / (t1 - t0));
        if (prog <= 0f) return;
        if (filled && prog > 0.6f) {
            float a = (prog - 0.6f) / 0.4f;
            fill.setColor(color);
            fill.setAlpha(Math.round(255 * a));
            c.drawPath(p, fill);
            fill.setAlpha(255);
        }
        measure.setPath(p, closed);
        float len = measure.getLength();
        if (len <= 0f) return;
        line.setStrokeWidth(1.4f);
        if (prog >= 0.999f) {
            c.drawPath(p, line);
            return;
        }
        seg.reset();
        measure.getSegment(0f, len * prog, seg, true);
        c.drawPath(seg, line);
    }
    private void drawRing(Canvas c, float cx, float cy, float t) {
        float orbit = Math.max(0f, t - 2.6f) * 0.28f;
        for (int i = 0; i < RING_ART.length; i++) {
            float p = clamp((t - POP_TIMES[i]) / 0.4f);
            float s = easeOutBack(p);
            if (s <= 0.02f) continue;
            double a = -Math.PI / 2 + i * Math.PI / 4 + orbit;
            float bob = t > 2.7f ? (float) Math.sin(t * 1.7f + i) * 1.1f : 0f;
            float x = cx + 40f * (float) Math.cos(a);
            float y = cy + 34f * (float) Math.sin(a) + bob;
            art.sticker(c, RING_ART[i], RING_BG[i], x, y, 19f * s);
        }
    }
    private void drawTitle(Canvas c, float baseY, float t) {
        title.setTextSize(titleSize);
        for (int i = 0; i < TITLE.length(); i++) {
            float p = clamp((t - (2.35f + i * 0.055f)) / 0.5f);
            if (p <= 0f) continue;
            float drop = -(1f - bounce(p)) * 46f;
            String ch = TITLE.substring(i, i + 1);
            float x = letterX[i];
            float y = baseY + drop;
            title.setStyle(Paint.Style.FILL);
            title.setColor(Pal.INK);
            c.drawText(ch, x + 1.4f, y + 1.4f, title);
            title.setStyle(Paint.Style.STROKE);
            title.setStrokeWidth(2.4f);
            title.setStrokeJoin(Paint.Join.ROUND);
            title.setColor(Pal.INK);
            c.drawText(ch, x, y, title);
            title.setStyle(Paint.Style.FILL);
            title.setColor(Pal.PAPER);
            c.drawText(ch, x, y, title);
        }
    }
    private void drawTagline(Canvas c, float y, float t) {
        float p = clamp((t - 3.1f) / 0.4f);
        if (p <= 0f) return;
        String text = L.t("FIND YOUR DIRECTION", "TEMUKAN ARAHMU");
        mono.setTextSize(4.2f);
        mono.setLetterSpacing(0.06f);
        float tw = mono.measureText(text);
        float slide = (1f - easeOutBack(p)) * 40f;
        c.save();
        c.translate(50f + slide, y);
        c.rotate(-2f);
        box(c, -tw / 2f - 3.5f, -4.2f, tw / 2f + 3.5f, 4.2f, 1.2f, Pal.YELLOW, 1.6f);
        mono.setColor(Pal.INK);
        mono.setStyle(Paint.Style.FILL);
        c.drawText(text, 0f, 1.5f, mono);
        c.restore();
    }
    private void drawButton(Canvas c, float y, float t) {
        float p = easeOutBack(clamp((t - 3.4f) / 0.35f));
        if (p <= 0.02f) return;
        float pulse = t > 3.8f ? 1f + 0.025f * (float) Math.sin((t - 3.8f) * 5f) : 1f;
        String text = L.t("TAP TO START", "KETUK UNTUK MULAI");
        mono.setTextSize(4.6f);
        mono.setLetterSpacing(0.05f);
        float tw = mono.measureText(text);
        float half = Math.max(31f, tw / 2f + 6f);
        c.save();
        c.translate(50f, y);
        c.scale(p * pulse, p * pulse);
        box(c, -half, -6.8f, half, 6.8f, 2.2f, Pal.FOREST, 2.4f);
        mono.setColor(Pal.LIME);
        c.drawText(text, 0f, 1.7f, mono);
        c.restore();
    }
    private void drawSkipHint(Canvas c, float hu, float t) {
        if (t >= READY || t < 0.4f) return;
        mono.setTextSize(3.2f);
        mono.setLetterSpacing(0.05f);
        mono.setColor(Pal.FOREST);
        c.drawText(L.t("TAP TO SKIP", "KETUK UNTUK LEWATI"), 50f, hu - 6f, mono);
    }
}


