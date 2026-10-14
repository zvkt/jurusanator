package jurusanator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
/**
 * Vector illustrations: one hand-built drawing per major (67 in total).
 * Everything is drawn with plain Canvas shapes in a 100 x 100 box, neo-brutalist style:
 * flat colours, thick black outlines. No image files are used. */
final class Art {
    static final int LAPTOP = 0, CHIP = 1, DATABASE = 2, NETWORK = 3, CHART = 4, BELL_CURVE = 5,
            PI = 6, ATOM = 7, FLASK = 8, CELL = 9, DNA = 10, BOLT = 11, GEAR = 12, HELMET = 13,
            FACTORY = 14, TANK = 15, WINDMILL = 16, BLUEPRINT = 17, CITY = 18, MEDKIT = 19,
            HEART = 20, PILL = 21, SHIELD = 22, APPLE = 23, TOOTH = 24, BRAIN = 25, BOTTLE = 26,
            DUMBBELL = 27, SCALES = 28, BALLOT = 29, GLOBE = 30, PEOPLE = 31, MASK = 32,
            SPEECH = 33, TEMPLE = 34, ORGCHART = 35, CALC = 36, COINS = 37, PHONE = 38,
            ROCKET = 39, BRIEFCASE = 40, TRUCK = 41, SHAPES = 42, BULB = 43, CHAIR = 44,
            PLAY = 45, CLAPPER = 46, PALETTE = 47, SHIRT = 48, MUSIC = 49, BOARD = 50,
            LETTER = 51, LEAF = 52, MAGNET = 53, PENCIL = 54, BLOCKS = 55, QUILL = 56,
            BOOK = 57, NEWS = 58, WHEAT = 59, SPROUT = 60, BARN = 61, TREE = 62, BOAT = 63,
            FISH = 64, PIN = 65, BELL = 66;
    static final int COUNT = 67;
    private static final float SW = 4.4f;     // outline width (in 100-unit box)
    private static final float THIN = 2.4f;
    private final Paint fp = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint tp = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rf = new RectF();
    private final Path path = new Path();
    private int hole = Pal.PAPER;     // colour used for "holes" (palette thumb hole)
    Art() {
        fp.setStyle(Paint.Style.FILL);
        sp.setStyle(Paint.Style.STROKE);
        sp.setStrokeJoin(Paint.Join.ROUND);
        sp.setStrokeCap(Paint.Cap.ROUND);
        sp.setColor(Pal.INK);
        sp.setStrokeWidth(SW);
        tp.setStyle(Paint.Style.FILL);
        tp.setTypeface(Typeface.create("sans-serif-black",Typeface.NORMAL));
        tp.setTextAlign(Paint.Align.CENTER);
    }
    // ------------------------------------------------------------------sticker
    /** A round coloured sticker with a hard shadow and the illustration on top. */
    void sticker(Canvas c, int id, int bg, float cx, float cy, float size) {
        float s = size / 100f;
        c.save();
        c.translate(cx - size / 2f, cy - size / 2f);
        c.scale(s, s);
        fp.setColor(Pal.INK);
        c.drawCircle(53f, 53f, 44f, fp);
        fp.setColor(bg);
        c.drawCircle(47f, 47f, 44f, fp);
        sp.setStrokeWidth(SW);
        c.drawCircle(47f, 47f, 44f - SW / 2f, sp);
        hole = bg;
        c.save();
        c.translate(47f, 47f);
        c.scale(0.7f, 0.7f);
        c.translate(-50f, -50f);
        draw(c, id);
        c.restore();
        c.restore();
    }
    /** Draw illustration {@code id} into the (0,0)-(100,100) box of the current canvas. */
    void draw(Canvas c, int id) {
        sp.setStrokeWidth(SW);
        sp.setColor(Pal.INK);
        switch (id) {
            case LAPTOP: laptop(c); break;
            case CHIP: chip(c); break;
            case DATABASE: database(c); break;
            case NETWORK: network(c); break;
            case CHART: chart(c); break;
            case BELL_CURVE: bellCurve(c); break;
            case PI: pi(c); break;
            case ATOM: atom(c); break;
            case FLASK: flask(c); break;
            case CELL: cell(c); break;
            case DNA: dna(c); break;
            case BOLT: bolt(c); break;
            case GEAR: gear(c); break;
            case HELMET: helmet(c); break;
            case FACTORY: factory(c); break;
            case TANK: tank(c); break;
            case WINDMILL: windmill(c); break;
            case BLUEPRINT: blueprint(c); break;
            case CITY: city(c); break;
            case MEDKIT: medkit(c); break;
            case HEART: heart(c); break;
            case PILL: pill(c); break;
            case SHIELD: shield(c); break;
            case APPLE: apple(c); break;
            case TOOTH: tooth(c); break;
            case BRAIN: brain(c); break;
            case BOTTLE: bottle(c); break;
            case DUMBBELL: dumbbell(c); break;
            case SCALES: scales(c); break;
            case BALLOT: ballot(c); break;
            case GLOBE: globe(c); break;
            case PEOPLE: people(c); break;
            case MASK: mask(c); break;
            case SPEECH: speech(c); break;
            case TEMPLE: temple(c); break;
            case ORGCHART: orgChart(c); break;
            case CALC: calc(c); break;
            case COINS: coins(c); break;
            case PHONE: phone(c); break;
            case ROCKET: rocket(c); break;
            case BRIEFCASE: briefcase(c); break;
            case TRUCK: truck(c); break;
            case SHAPES: shapes(c); break;
            case BULB: bulb(c); break;
            case CHAIR: chair(c); break;
            case PLAY: play(c); break;
            case CLAPPER: clapper(c); break;
            case PALETTE: palette(c); break;
            case SHIRT: shirt(c); break;
            case MUSIC: music(c); break;
            case BOARD: board(c); break;
            case LETTER: letter(c); break;
            case LEAF: leaf(c); break;
            case MAGNET: magnet(c); break;
            case PENCIL: pencil(c); break;
            case BLOCKS: blocks(c); break;
            case QUILL: quill(c); break;
            case BOOK: book(c); break;
            case NEWS: news(c); break;
            case WHEAT: wheat(c); break;
            case SPROUT: sprout(c); break;
            case BARN: barn(c); break;
            case TREE: tree(c); break;
            case BOAT: boat(c); break;
            case FISH: fish(c); break;
            case PIN: pin(c); break;
            case BELL: bell(c); break;
            default: shapes(c); break;
        }
    }
    // ------------------------------------------------------------------helpers
    private void rect(Canvas c, float l, float t, float r, float b, float rad, int fill) {
        rf.set(l, t, r, b);
        fp.setColor(fill);
        c.drawRoundRect(rf, rad, rad, fp);
        c.drawRoundRect(rf, rad, rad, sp);
    }
    private void oval(Canvas c, float l, float t, float r, float b, int fill) {
        rf.set(l, t, r, b);
        fp.setColor(fill);
        c.drawOval(rf, fp);
        c.drawOval(rf, sp);
    }
    private void circle(Canvas c, float x, float y, float r, int fill) {
        fp.setColor(fill);
        c.drawCircle(x, y, r, fp);
        c.drawCircle(x, y, r, sp);
    }
    private void dot(Canvas c, float x, float y, float r, int color) {
        fp.setColor(color);
        c.drawCircle(x, y, r, fp);
    }
    private void shape(Canvas c, int fill) {
        fp.setColor(fill);
        c.drawPath(path, fp);
        c.drawPath(path, sp);
    }
    private void poly(Canvas c, int fill, float... p) {
        path.reset();
        path.moveTo(p[0], p[1]);
        for (int i = 2; i + 1 < p.length; i += 2) path.lineTo(p[i], p[i + 1]);
        path.close();
        shape(c, fill);
    }
    private void line(Canvas c, float x1, float y1, float x2, float y2) {
        c.drawLine(x1, y1, x2, y2, sp);
    }
    private void thin(Canvas c, float x1, float y1, float x2, float y2) {
        sp.setStrokeWidth(THIN);
        c.drawLine(x1, y1, x2, y2, sp);
        sp.setStrokeWidth(SW);
    }
    /** Stroke the shared path thinly (details such as veins). */
    private void thinPath(Canvas c) {
        sp.setStrokeWidth(THIN);
        c.drawPath(path, sp);
        sp.setStrokeWidth(SW);
    }
    /** Thick coloured stroke with a black outline (wires, wave, DNA...). */
    private void fat(Canvas c, int color, float w) {
        sp.setColor(Pal.INK);
        sp.setStrokeWidth(w + SW * 1.6f);
        c.drawPath(path, sp);
        sp.setColor(color);
        sp.setStrokeWidth(w);
        c.drawPath(path, sp);
        sp.setColor(Pal.INK);
        sp.setStrokeWidth(SW);
    }
    private void text(Canvas c, String s, float cx, float cy, float size, int color) {
        tp.setTextSize(size);
        tp.setColor(color);
        float baseline = cy - (tp.ascent() + tp.descent()) / 2f;
        c.drawText(s, cx, baseline, tp);
    }
    private void star(Canvas c, float cx, float cy, float big, float small, int fill) {
        path.reset();
        for (int i = 0; i < 10; i++) {
            double a = -Math.PI / 2 + i * Math.PI / 5;
            float r = (i % 2 == 0) ? big : small;
            float x = cx + (float) Math.cos(a) * r;
            float y = cy + (float) Math.sin(a) * r;
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.close();
        shape(c, fill);
    }
    /** Four-point sparkle. */
    private void sparkle(Canvas c, float cx, float cy, float r, int fill) {
        float n = r * 0.28f;
        poly(c, fill, cx, cy - r, cx + n, cy - n, cx + r, cy, cx + n, cy + n,
                cx, cy + r, cx - n, cy + n, cx - r, cy, cx - n, cy - n);
    }
    /** Cylinder: side body with a curved bottom, plus an elliptical top. */
    private void cyl(Canvas c, float l, float top, float r, float body, float ry, int side, int cap) {
        float y0 = top + ry;
        float y1 = y0 + body;
        path.reset();
        path.moveTo(l, y0);
        path.lineTo(l, y1);
        path.cubicTo(l, y1 + ry * 1.4f, r, y1 + ry * 1.4f, r, y1);
        path.lineTo(r, y0);
        path.close();
        shape(c, side);
        oval(c, l, top, r, top + 2 * ry, cap);
    }
    /** Speech bubble with a tail on the bottom-left. */
    private void bubble(Canvas c, float l, float t, float r, float b, float tailX, int fill) {
        float k = 9f;
        path.reset();
        path.moveTo(l + k, t);
        path.lineTo(r - k, t);
        path.quadTo(r, t, r, t + k);
        path.lineTo(r, b - k);
        path.quadTo(r, b, r - k, b);
        path.lineTo(tailX + 14f, b);
        path.lineTo(tailX, b + 13f);
        path.lineTo(tailX, b);
        path.lineTo(l + k, b);
        path.quadTo(l, b, l, b - k);
        path.lineTo(l, t + k);
        path.quadTo(l, t, l + k, t);
        path.close();
        shape(c, fill);
    }
    // ------------------------------------------------------------illustrations
    private void laptop(Canvas c) {
        poly(c, Pal.GRAY, 6, 62, 94, 62, 88, 77, 12, 77);
        rect(c, 16, 16, 84, 62, 4, Pal.FOREST);
        text(c, "</>", 50, 39, 23, Pal.LIME);
        thin(c, 41, 70, 59, 70);
    }
    private void chip(Canvas c) {
        for (int i = 0; i < 3; i++) {
            float p = 36 + i * 14;
            line(c, p, 12, p, 28);
            line(c, p, 72, p, 88);
            line(c, 12, p, 28, p);
            line(c, 72, p, 88, p);
        }
        rect(c, 26, 26, 74, 74, 6, Pal.FOREST);
        rect(c, 38, 38, 62, 62, 3, Pal.LIME);
        dot(c, 50, 50, 4, Pal.INK);
    }
    private void database(Canvas c) {
        int cap = 0xFFD2F0FC;
        cyl(c, 20, 56, 80, 14, 9, Pal.SKY, cap);
        dot(c, 31, 78, 2.6f, Pal.INK);
        cyl(c, 20, 37, 80, 14, 9, Pal.SKY, cap);
        dot(c, 31, 59, 2.6f, Pal.INK);
        cyl(c, 20, 18, 80, 14, 9, Pal.SKY, cap);
        dot(c, 31, 40, 2.6f, Pal.INK);
    }
    private void network(Canvas c) {
        float[][] n = {{20, 24}, {80, 24}, {20, 76}, {80, 76}};
        int[] col = {Pal.YELLOW, Pal.PINK, Pal.SKY, Pal.ORANGE};
        for (float[] p : n) line(c, 50, 50, p[0], p[1]);
        for (int i = 0; i < 4; i++) circle(c, n[i][0], n[i][1], 9, col[i]);
        circle(c, 50, 50, 13, Pal.LIME);
        dot(c, 50, 50, 4, Pal.INK);
    }
    private void chart(Canvas c) {
        rect(c, 10, 14, 90, 86, 6, Pal.PAPER);
        rect(c, 20, 54, 34, 76, 1, Pal.SKY);
        rect(c, 41, 42, 55, 76, 1, Pal.LIME_HI);
        rect(c, 62, 30, 76, 76, 1, Pal.PINK);
        path.reset();
        path.moveTo(20, 44);
        path.lineTo(42, 32);
        path.lineTo(58, 38);
        path.lineTo(80, 20);
        fat(c, Pal.CORAL, 3f);
    }
    private void bellCurve(Canvas c) {
        rect(c, 10, 14, 90, 86, 6, Pal.PAPER);
        path.reset();
        path.moveTo(18, 74);
        path.cubicTo(34, 74, 40, 28, 50, 28);
        path.cubicTo(60, 28, 66, 74, 82, 74);
        path.close();
        shape(c, Pal.LIME_HI);
        thin(c, 50, 28, 50, 74);
        thin(c, 34, 52, 34, 74);
        thin(c, 66, 52, 66, 74);
    }
    private void pi(Canvas c) {
        poly(c, Pal.YELLOW, 14, 84, 14, 18, 84, 84);
        text(c, "\u03C0", 38, 62, 38, Pal.INK);
        circle(c, 76, 24, 9, Pal.PINK);
        text(c, "+", 76, 24, 14, Pal.INK);
    }
    private void atom(Canvas c) {
        int[] col = {Pal.YELLOW, Pal.PINK, Pal.LIME_HI};
        for (int k = 0; k < 3; k++) {
            c.save();
            c.rotate(k * 60f, 50, 50);
            rf.set(10, 35, 90, 65);
            c.drawOval(rf, sp);
            circle(c, 90, 50, 5.5f, col[k]);
            c.restore();
        }
        circle(c, 50, 50, 10, Pal.CORAL);
    }
    private void flask(Canvas c) {
        path.reset();
        path.moveTo(41, 12);
        path.lineTo(41, 38);
        path.lineTo(16, 82);
        path.lineTo(84, 82);
        path.lineTo(59, 38);
        path.lineTo(59, 12);
        path.close();
        shape(c, Pal.PAPER);
        poly(c, Pal.LIME_HI, 28.5f, 60, 71.5f, 60, 84, 82, 16, 82);
        line(c, 36, 12, 64, 12);
        circle(c, 45, 72, 4.5f, Pal.PAPER);
        circle(c, 59, 68, 3.2f, Pal.PAPER);
        circle(c, 52, 44, 3.4f, Pal.LIME);
    }
    private void cell(Canvas c) {
        oval(c, 10, 18, 90, 82, Pal.LIME);
        circle(c, 42, 46, 15, Pal.PINK);
        dot(c, 40, 44, 5, Pal.INK);
        oval(c, 62, 56, 80, 68, Pal.ORANGE);
        oval(c, 62, 28, 78, 38, Pal.SKY);
        dot(c, 30, 70, 3, Pal.INK);
    }
    private void dna(Canvas c) {
        float[] ax = new float[25];
        float[] bx = new float[25];
        float[] y = new float[25];
        for (int i = 0; i <= 24; i++) {
            float t = i / 24f;
            double a = t * Math.PI * 3;
            y[i] = 8 + 84 * t;
            ax[i] = 50 + 24 * (float) Math.sin(a);
            bx[i] = 50 - 24 * (float) Math.sin(a);
        }
        for (int i = 2; i <= 22; i += 2) thin(c, ax[i], y[i], bx[i], y[i]);
        path.reset();
        path.moveTo(ax[0], y[0]);
        for (int i = 1; i <= 24; i++) path.lineTo(ax[i], y[i]);
        fat(c, Pal.PINK, 4.5f);
        path.reset();
        path.moveTo(bx[0], y[0]);
        for (int i = 1; i <= 24; i++) path.lineTo(bx[i], y[i]);
        fat(c, Pal.SKY, 4.5f);
    }
    private void bolt(Canvas c) {
        poly(c, Pal.YELLOW, 58, 6, 22, 56, 46, 56, 38, 94, 78, 38, 54, 38);
        sparkle(c, 80, 20, 9, Pal.LIME_HI);
        sparkle(c, 20, 78, 7, Pal.SKY);
    }
    private void gear(Canvas c) {
        for (int k = 0; k < 8; k++) {
            c.save();
            c.rotate(k * 45f, 50, 50);
            rect(c, 42, 10, 58, 30, 3, Pal.ORANGE);
            c.restore();
        }
        circle(c, 50, 50, 30, Pal.ORANGE);
        circle(c, 50, 50, 11, Pal.PAPER);
    }
    private void helmet(Canvas c) {
        path.reset();
        path.moveTo(14, 62);
        path.cubicTo(14, 18, 86, 18, 86, 62);
        path.close();
        shape(c, Pal.YELLOW);
        rect(c, 43, 31, 57, 62, 2, Pal.ORANGE);
        rect(c, 6, 60, 94, 74, 5, Pal.YELLOW);
        thin(c, 24, 46, 30, 36);
    }
    private void factory(Canvas c) {
        rect(c, 62, 18, 74, 56, 0, Pal.CORAL);
        rect(c, 78, 30, 88, 56, 0, Pal.CORAL);
        circle(c, 66, 12, 6, Pal.PAPER);
        circle(c, 76, 6, 4, Pal.PAPER);
        poly(c, Pal.MINT, 10, 54, 10, 38, 30, 54);
        poly(c, Pal.MINT, 30, 54, 30, 38, 50, 54);
        rect(c, 10, 52, 90, 88, 0, Pal.SKY);
        rect(c, 18, 62, 32, 74, 0, Pal.YELLOW);
        rect(c, 40, 62, 54, 74, 0, Pal.YELLOW);
        rect(c, 62, 62, 76, 74, 0, Pal.YELLOW);
        rect(c, 42, 78, 58, 88, 0, Pal.PAPER);
    }
    private void tank(Canvas c) {
        path.reset();
        path.moveTo(70, 52);
        path.lineTo(90, 52);
        path.lineTo(90, 84);
        fat(c, Pal.GRAY, 4f);
        cyl(c, 26, 14, 70, 56, 9, Pal.MINT, Pal.LIME);
        circle(c, 48, 50, 9, Pal.PAPER);
        line(c, 48, 50, 53, 45);
        line(c, 32, 84, 32, 92);
        line(c, 64, 84, 64, 92);
        circle(c, 90, 52, 5, Pal.CORAL);
    }
    private void windmill(Canvas c) {
        poly(c, Pal.PAPER, 45, 50, 55, 50, 60, 90, 40, 90);
        for (int k = 0; k < 3; k++) {
            c.save();
            c.rotate(k * 120f, 50, 44);
            poly(c, Pal.MINT, 46, 40, 54, 40, 52, 4, 48, 4);
            c.restore();
        }
        circle(c, 50, 44, 6, Pal.LIME_HI);
        line(c, 14, 90, 86, 90);
    }
    private void blueprint(Canvas c) {
        rect(c, 10, 14, 90, 86, 5, Pal.SKY);
        for (int i = 1; i < 4; i++) {
            thin(c, 10 + i * 20, 14, 10 + i * 20, 86);
        }
        thin(c, 10, 38, 90, 38);
        thin(c, 10, 62, 90, 62);
        poly(c, Pal.PAPER, 28, 70, 28, 48, 50, 30, 72, 48, 72, 70);
        rect(c, 44, 54, 56, 70, 1, Pal.CORAL);
        rect(c, 33, 52, 41, 60, 0, Pal.SKY);
        rect(c, 59, 52, 67, 60, 0, Pal.SKY);
    }
    private void city(Canvas c) {
        rect(c, 10, 42, 36, 86, 0, Pal.SKY);
        rect(c, 36, 20, 64, 86, 0, Pal.LIME_HI);
        rect(c, 64, 48, 90, 86, 0, Pal.PINK);
        for (int r = 0; r < 3; r++) {
            for (int k = 0; k < 2; k++) {
                rect(c, 42 + k * 11, 28 + r * 14, 49 + k * 11, 36 + r * 14, 0, Pal.PAPER);
            }
        }
        for (int r = 0; r < 2; r++) {
            rect(c, 16, 50 + r * 14, 22, 58 + r * 14, 0, Pal.PAPER);
            rect(c, 25, 50 + r * 14, 31, 58 + r * 14, 0, Pal.PAPER);
            rect(c, 70, 56 + r * 14, 76, 64 + r * 14, 0, Pal.PAPER);
            rect(c, 79, 56 + r * 14, 85, 64 + r * 14, 0, Pal.PAPER);
        }
    }
    private void medkit(Canvas c) {
        rect(c, 33, 14, 67, 36, 7, Pal.GRAY);
        rect(c, 10, 28, 90, 86, 8, Pal.PAPER);
        poly(c, Pal.CORAL, 42, 40, 58, 40, 58, 51, 70, 51, 70, 65, 58, 65, 58, 76,
                42, 76, 42, 65, 30, 65, 30, 51, 42, 51);
    }
    private void heart(Canvas c) {
        path.reset();
        path.moveTo(50, 88);
        path.cubicTo(6, 58, 12, 20, 36, 20);
        path.cubicTo(45, 20, 50, 27, 50, 34);
        path.cubicTo(50, 27, 55, 20, 64, 20);
        path.cubicTo(88, 20, 94, 58, 50, 88);
        path.close();
        shape(c, Pal.PINK);
        path.reset();
        path.moveTo(16, 52);
        path.lineTo(36, 52);
        path.lineTo(43, 36);
        path.lineTo(54, 68);
        path.lineTo(60, 52);
        path.lineTo(84, 52);
        fat(c, Pal.PAPER, 3f);
    }
    private void pill(Canvas c) {
        c.save();
        c.rotate(-38f, 50, 44);
        rf.set(10, 30, 90, 58);
        fp.setColor(Pal.PAPER);
        c.drawRoundRect(rf, 14, 14, fp);
        c.save();
        c.clipRect(10, 20, 50, 70);
        fp.setColor(Pal.CORAL);
        c.drawRoundRect(rf, 14, 14, fp);
        c.restore();
        c.drawRoundRect(rf, 14, 14, sp);
        line(c, 50, 30, 50, 58);
        c.restore();
        circle(c, 70, 78, 11, Pal.YELLOW);
        thin(c, 62, 78, 78, 78);
    }
    private void shield(Canvas c) {
        path.reset();
        path.moveTo(50, 8);
        path.lineTo(86, 20);
        path.lineTo(86, 48);
        path.cubicTo(86, 70, 68, 84, 50, 92);
        path.cubicTo(32, 84, 14, 70, 14, 48);
        path.lineTo(14, 20);
        path.close();
        shape(c, Pal.MINT);
        poly(c, Pal.CORAL, 43, 30, 57, 30, 57, 43, 70, 43, 70, 57, 57, 57, 57, 70,
                43, 70, 43, 57, 30, 57, 30, 43, 43, 43);
    }
    private void apple(Canvas c) {
        path.reset();
        path.moveTo(50, 30);
        path.cubicTo(40, 20, 12, 24, 12, 54);
        path.cubicTo(12, 78, 32, 94, 44, 89);
        path.cubicTo(48, 87.5f, 52, 87.5f, 56, 89);
        path.cubicTo(68, 94, 88, 78, 88, 54);
        path.cubicTo(88, 24, 60, 20, 50, 30);
        path.close();
        shape(c, Pal.LIME_HI);
        line(c, 50, 30, 54, 12);
        poly(c, Pal.GREEN, 56, 22, 78, 10, 74, 30);
        dot(c, 30, 52, 4.5f, Pal.PAPER);
    }
    private void tooth(Canvas c) {
        path.reset();
        path.moveTo(30, 14);
        path.cubicTo(40, 10, 46, 22, 50, 22);
        path.cubicTo(54, 22, 60, 10, 70, 14);
        path.cubicTo(88, 20, 86, 44, 77, 56);
        path.cubicTo(73, 64, 73, 80, 67, 89);
        path.cubicTo(62, 95, 57, 85, 55, 70);
        path.cubicTo(53, 62, 47, 62, 45, 70);
        path.cubicTo(43, 85, 38, 95, 33, 89);
        path.cubicTo(27, 80, 27, 64, 23, 56);
        path.cubicTo(14, 44, 12, 20, 30, 14);
        path.close();
        shape(c, Pal.PAPER);
        sparkle(c, 80, 18, 11, Pal.YELLOW);
        path.reset();
        path.moveTo(28, 30);
        path.quadTo(24, 42, 31, 50);
        thinPath(c);
    }
    private void brain(Canvas c) {
        path.reset();
        path.moveTo(50, 22);
        path.cubicTo(40, 8, 16, 14, 18, 34);
        path.cubicTo(6, 40, 8, 62, 20, 66);
        path.cubicTo(18, 82, 40, 86, 50, 78);
        path.cubicTo(60, 86, 82, 82, 80, 66);
        path.cubicTo(92, 62, 94, 40, 82, 34);
        path.cubicTo(84, 14, 60, 8, 50, 22);
        path.close();
        shape(c, Pal.PINK);
        thin(c, 50, 24, 50, 76);
        path.reset();
        path.moveTo(26, 46);
        path.quadTo(36, 36, 40, 50);
        path.moveTo(74, 46);
        path.quadTo(64, 36, 60, 50);
        path.moveTo(30, 64);
        path.quadTo(38, 56, 43, 66);
        path.moveTo(70, 64);
        path.quadTo(62, 56, 57, 66);
        thinPath(c);
    }
    private void bottle(Canvas c) {
        path.reset();
        path.moveTo(42, 26);
        path.cubicTo(42, 6, 58, 6, 58, 26);
        path.close();
        shape(c, Pal.ORANGE);
        rect(c, 33, 26, 67, 38, 3, Pal.LIME_HI);
        rect(c, 34, 38, 66, 88, 8, Pal.PAPER);
        c.save();
        c.clipRect(30, 56, 70, 92);
        rf.set(34, 38, 66, 88);
        fp.setColor(Pal.SKY);
        c.drawRoundRect(rf, 8, 8, fp);
        c.restore();
        rf.set(34, 38, 66, 88);
        c.drawRoundRect(rf, 8, 8, sp);
        thin(c, 34, 48, 44, 48);
        thin(c, 34, 62, 44, 62);
        thin(c, 34, 76, 44, 76);
    }
    private void dumbbell(Canvas c) {
        rect(c, 22, 44, 78, 56, 2, Pal.GRAY);
        rect(c, 22, 28, 34, 72, 3, Pal.CORAL);
        rect(c, 10, 36, 22, 64, 3, Pal.ORANGE);
        rect(c, 66, 28, 78, 72, 3, Pal.CORAL);
        rect(c, 78, 36, 90, 64, 3, Pal.ORANGE);
    }
    private void scales(Canvas c) {
        poly(c, Pal.YELLOW, 32, 90, 68, 90, 60, 80, 40, 80);
        rect(c, 46, 22, 54, 82, 0, Pal.YELLOW);
        rect(c, 12, 24, 88, 34, 3, Pal.YELLOW);
        circle(c, 50, 20, 6, Pal.ORANGE);
        line(c, 20, 34, 9, 60);
        line(c, 20, 34, 31, 60);
        line(c, 80, 34, 69, 60);
        line(c, 80, 34, 91, 60);
        path.reset();
        path.moveTo(6, 60);
        path.lineTo(34, 60);
        path.cubicTo(34, 76, 6, 76, 6, 60);
        path.close();
        shape(c, Pal.LIME_HI);
        path.reset();
        path.moveTo(66, 60);
        path.lineTo(94, 60);
        path.cubicTo(94, 76, 66, 76, 66, 60);
        path.close();
        shape(c, Pal.LIME_HI);
    }
    private void ballot(Canvas c) {
        rect(c, 34, 10, 66, 52, 2, Pal.PAPER);
        path.reset();
        path.moveTo(41, 28);
        path.lineTo(48, 36);
        path.lineTo(59, 20);
        fat(c, Pal.GREEN, 3.4f);
        rect(c, 16, 46, 84, 88, 4, Pal.SKY);
        rect(c, 30, 52, 70, 60, 2, Pal.INK);
        star(c, 50, 74, 9, 4, Pal.YELLOW);
    }
    private void globe(Canvas c) {
        circle(c, 50, 50, 38, Pal.SKY);
        path.reset();
        path.moveTo(24, 40);
        path.cubicTo(28, 24, 46, 24, 48, 36);
        path.cubicTo(52, 46, 40, 52, 36, 62);
        path.cubicTo(30, 66, 20, 54, 24, 40);
        path.close();
        shape(c, Pal.LIME_HI);
        path.reset();
        path.moveTo(58, 56);
        path.cubicTo(70, 46, 84, 54, 78, 66);
        path.cubicTo(74, 76, 60, 74, 58, 56);
        path.close();
        shape(c, Pal.LIME_HI);
        rf.set(34, 12, 66, 88);
        sp.setStrokeWidth(THIN);
        c.drawOval(rf, sp);
        thin(c, 12, 50, 88, 50);
        sp.setStrokeWidth(SW);
    }
    private void people(Canvas c) {
        circle(c, 22, 44, 9, Pal.PINK);
        path.reset();
        path.moveTo(6, 86);
        path.cubicTo(6, 58, 38, 58, 38, 86);
        path.close();
        shape(c, Pal.PINK);
        circle(c, 78, 44, 9, Pal.SKY);
        path.reset();
        path.moveTo(62, 86);
        path.cubicTo(62, 58, 94, 58, 94, 86);
        path.close();
        shape(c, Pal.SKY);
        circle(c, 50, 32, 12, Pal.YELLOW);
        path.reset();
        path.moveTo(28, 90);
        path.cubicTo(28, 52, 72, 52, 72, 90);
        path.close();
        shape(c, Pal.LIME_HI);
    }
    private void mask(Canvas c) {
        path.reset();
        path.moveTo(50, 8);
        path.cubicTo(72, 8, 84, 16, 84, 36);
        path.cubicTo(84, 64, 68, 90, 50, 92);
        path.cubicTo(32, 90, 16, 64, 16, 36);
        path.cubicTo(16, 16, 28, 8, 50, 8);
        path.close();
        shape(c, Pal.ORANGE);
        path.reset();
        path.moveTo(26, 24);
        path.lineTo(34, 17);
        path.lineTo(42, 24);
        path.lineTo(50, 17);
        path.lineTo(58, 24);
        path.lineTo(66, 17);
        path.lineTo(74, 24);
        fat(c, Pal.YELLOW, 3f);
        poly(c, Pal.INK, 26, 40, 44, 36, 44, 46, 30, 48);
        poly(c, Pal.INK, 74, 40, 56, 36, 56, 46, 70, 48);
        line(c, 50, 42, 50, 60);
        rect(c, 38, 68, 62, 80, 2, Pal.PAPER);
        thin(c, 46, 68, 46, 80);
        thin(c, 54, 68, 54, 80);
    }
    private void speech(Canvas c) {
        bubble(c, 8, 10, 66, 48, 16, Pal.LIME_HI);
        dot(c, 24, 29, 4, Pal.INK);
        dot(c, 37, 29, 4, Pal.INK);
        dot(c, 50, 29, 4, Pal.INK);
        bubble(c, 36, 54, 92, 84, 70, Pal.YELLOW);
        thin(c, 46, 66, 82, 66);
        thin(c, 46, 74, 70, 74);
    }
    private void temple(Canvas c) {
        poly(c, Pal.YELLOW, 8, 38, 50, 12, 92, 38);
        circle(c, 50, 30, 4, Pal.INK);
        rect(c, 12, 38, 88, 46, 0, Pal.PAPER);
        for (int i = 0; i < 4; i++) {
            rect(c, 17 + i * 18, 46, 27 + i * 18, 76, 0, Pal.PAPER);
        }
        rect(c, 8, 76, 92, 84, 0, Pal.PAPER);
        rect(c, 4, 84, 96, 92, 0, Pal.PAPER);
    }
    private void orgChart(Canvas c) {
        line(c, 50, 30, 50, 42);
        line(c, 20, 42, 80, 42);
        line(c, 20, 42, 20, 56);
        line(c, 50, 42, 50, 56);
        line(c, 80, 42, 80, 56);
        rect(c, 34, 10, 66, 30, 3, Pal.YELLOW);
        dot(c, 50, 20, 5, Pal.INK);
        rect(c, 6, 56, 34, 76, 3, Pal.LIME_HI);
        rect(c, 36, 56, 64, 76, 3, Pal.SKY);
        rect(c, 66, 56, 94, 76, 3, Pal.PINK);
        thin(c, 12, 66, 28, 66);
        thin(c, 42, 66, 58, 66);
        thin(c, 72, 66, 88, 66);
    }
    private void calc(Canvas c) {
        rect(c, 20, 8, 80, 92, 8, Pal.MINT);
        rect(c, 28, 16, 72, 34, 3, Pal.PAPER);
        text(c, "123", 66, 25, 12, Pal.INK);
        for (int r = 0; r < 3; r++) {
            for (int k = 0; k < 3; k++) {
                int col = (r == 2 && k == 2) ? Pal.CORAL : Pal.PAPER;
                rect(c, 28 + k * 16, 42 + r * 16, 39 + k * 16, 53 + r * 16, 2, col);
            }
        }
        rect(c, 28, 76, 72, 84, 2, Pal.YELLOW);
    }
    private void coins(Canvas c) {
        cyl(c, 18, 58, 82, 10, 8, Pal.YELLOW, 0xFFFFF1A0);
        cyl(c, 18, 42, 82, 10, 8, Pal.YELLOW, 0xFFFFF1A0);
        cyl(c, 18, 26, 82, 10, 8, Pal.YELLOW, 0xFFFFF1A0);
        text(c, "$", 50, 34, 14, Pal.INK);
        path.reset();
        path.moveTo(12, 24);
        path.lineTo(34, 8);
        path.lineTo(40, 14);
        fat(c, Pal.LIME_HI, 3f);
    }
    private void phone(Canvas c) {
        rect(c, 26, 6, 74, 94, 9, Pal.FOREST);
        rect(c, 32, 16, 68, 80, 2, Pal.PAPER);
        rect(c, 37, 56, 45, 74, 1, Pal.LIME_HI);
        rect(c, 49, 46, 57, 74, 1, Pal.SKY);
        rect(c, 61, 36, 69, 74, 1, Pal.PINK);
        circle(c, 40, 30, 5, Pal.YELLOW);
        dot(c, 50, 87, 3, Pal.PAPER);
    }
    private void rocket(Canvas c) {
        c.save();
        c.rotate(32f, 50, 50);
        poly(c, Pal.CORAL, 36, 56, 18, 76, 18, 86, 38, 74);
        poly(c, Pal.CORAL, 64, 56, 82, 76, 82, 86, 62, 74);
        poly(c, Pal.ORANGE, 40, 72, 50, 96, 60, 72);
        poly(c, Pal.YELLOW, 45, 72, 50, 86, 55, 72);
        path.reset();
        path.moveTo(50, 6);
        path.cubicTo(72, 22, 74, 50, 64, 72);
        path.lineTo(36, 72);
        path.cubicTo(26, 50, 28, 22, 50, 6);
        path.close();
        shape(c, Pal.PAPER);
        c.save();
        c.clipRect(20, 0, 80, 26);
        fp.setColor(Pal.CORAL);
        c.drawPath(path, fp);
        c.restore();
        c.drawPath(path, sp);
        circle(c, 50, 42, 9, Pal.SKY);
        c.restore();
    }
    private void briefcase(Canvas c) {
        rect(c, 36, 14, 64, 40, 7, Pal.PAPER);
        rect(c, 10, 30, 90, 84, 8, Pal.ORANGE);
        thin(c, 10, 54, 90, 54);
        rect(c, 42, 47, 58, 63, 3, Pal.YELLOW);
    }
    private void truck(Canvas c) {
        rect(c, 6, 24, 58, 68, 3, Pal.YELLOW);
        thin(c, 6, 46, 58, 46);
        poly(c, Pal.SKY, 58, 38, 78, 38, 94, 54, 94, 68, 58, 68);
        poly(c, Pal.PAPER, 64, 44, 76, 44, 85, 55, 64, 55);
        line(c, 4, 68, 96, 68);
        circle(c, 26, 72, 10, Pal.INK);
        dot(c, 26, 72, 4, Pal.PAPER);
        circle(c, 76, 72, 10, Pal.INK);
        dot(c, 76, 72, 4, Pal.PAPER);
    }
    private void shapes(Canvas c) {
        circle(c, 34, 34, 22, Pal.PINK);
        rect(c, 46, 46, 88, 88, 3, Pal.SKY);
        poly(c, Pal.YELLOW, 10, 90, 32, 52, 54, 90);
        circle(c, 74, 22, 7, Pal.LIME_HI);
    }
    private void bulb(Canvas c) {
        path.reset();
        path.moveTo(35, 66);
        path.cubicTo(10, 48, 20, 8, 50, 8);
        path.cubicTo(80, 8, 90, 48, 65, 66);
        path.lineTo(65, 72);
        path.lineTo(35, 72);
        path.close();
        shape(c, Pal.YELLOW);
        path.reset();
        path.moveTo(42, 72);
        path.lineTo(42, 54);
        path.lineTo(46, 46);
        path.lineTo(50, 54);
        path.lineTo(54, 46);
        path.lineTo(58, 54);
        path.lineTo(58, 72);
        thinPath(c);
        rect(c, 36, 72, 64, 81, 2, Pal.GRAY);
        rect(c, 40, 81, 60, 90, 3, Pal.GRAY);
        dot(c, 30, 28, 4, Pal.PAPER);
    }
    private void chair(Canvas c) {
        rect(c, 22, 72, 31, 90, 2, Pal.INK);
        rect(c, 69, 72, 78, 90, 2, Pal.INK);
        rect(c, 24, 12, 76, 58, 12, Pal.PINK);
        rect(c, 8, 42, 24, 76, 6, Pal.CORAL);
        rect(c, 76, 42, 92, 76, 6, Pal.CORAL);
        rect(c, 14, 54, 86, 74, 6, Pal.CORAL);
        thin(c, 50, 22, 50, 46);
        dot(c, 50, 22, 3, Pal.INK);
    }
    private void play(Canvas c) {
        rect(c, 8, 16, 92, 72, 9, Pal.LILAC);
        poly(c, Pal.PAPER, 40, 28, 40, 60, 68, 44);
        line(c, 28, 84, 72, 84);
        line(c, 50, 72, 50, 84);
        sparkle(c, 82, 28, 7, Pal.YELLOW);
        sparkle(c, 18, 60, 5, Pal.PINK);
    }
    private void clapper(Canvas c) {
        rect(c, 10, 40, 90, 88, 3, Pal.FOREST);
        thin(c, 22, 56, 54, 56);
        thin(c, 22, 66, 42, 66);
        thin(c, 22, 76, 60, 76);
        c.save();
        c.rotate(-14f, 10, 40);
        rect(c, 10, 22, 90, 38, 2, Pal.PAPER);
        for (int i = 0; i < 4; i++) {
            float x = 18 + i * 19;
            poly(c, Pal.INK, x, 22, x + 9, 22, x + 3, 38, x - 6, 38);
        }
        c.restore();
        dot(c, 12, 40, 3.5f, Pal.YELLOW);
    }
    private void palette(Canvas c) {
        path.reset();
        path.moveTo(52, 10);
        path.cubicTo(18, 8, 4, 40, 12, 62);
        path.cubicTo(20, 86, 44, 88, 56, 84);
        path.cubicTo(68, 80, 58, 68, 68, 64);
        path.cubicTo(78, 60, 94, 64, 94, 46);
        path.cubicTo(94, 26, 76, 12, 52, 10);
        path.close();
        shape(c, Pal.YELLOW);
        circle(c, 38, 66, 6, hole);
        circle(c, 28, 38, 6.5f, Pal.CORAL);
        circle(c, 48, 26, 6.5f, Pal.SKY);
        circle(c, 68, 32, 6.5f, Pal.LIME_HI);
        circle(c, 78, 50, 6, Pal.PINK);
        path.reset();
        path.moveTo(58, 92);
        path.lineTo(90, 58);
        fat(c, Pal.ORANGE, 5f);
        dot(c, 58, 92, 3, Pal.INK);
    }
    private void shirt(Canvas c) {
        path.reset();
        path.moveTo(34, 12);
        path.lineTo(42, 12);
        path.quadTo(50, 26, 58, 12);
        path.lineTo(66, 12);
        path.lineTo(92, 28);
        path.lineTo(82, 48);
        path.lineTo(72, 42);
        path.lineTo(72, 88);
        path.lineTo(28, 88);
        path.lineTo(28, 42);
        path.lineTo(18, 48);
        path.lineTo(8, 28);
        path.close();
        shape(c, Pal.PINK);
        rect(c, 28, 56, 72, 68, 0, Pal.PAPER);
        star(c, 50, 62, 5, 2.2f, Pal.CORAL);
    }
    private void music(Canvas c) {
        line(c, 36, 70, 36, 24);
        line(c, 74, 62, 74, 16);
        poly(c, Pal.LIME_HI, 36, 24, 74, 16, 74, 30, 36, 38);
        oval(c, 16, 62, 40, 80, Pal.CORAL);
        oval(c, 54, 54, 78, 72, Pal.SKY);
    }
    private void board(Canvas c) {
        rect(c, 8, 12, 92, 76, 5, Pal.ORANGE);
        rect(c, 15, 19, 85, 69, 1, Pal.FOREST);
        text(c, "1+2=3", 50, 44, 20, Pal.PAPER);
        rect(c, 8, 76, 92, 84, 2, Pal.BROWN);
        rect(c, 66, 70, 80, 76, 1, Pal.PAPER);
    }
    private void letter(Canvas c) {
        rect(c, 18, 8, 82, 92, 5, Pal.PAPER);
        thin(c, 30, 8, 30, 92);
        thin(c, 30, 66, 82, 66);
        thin(c, 30, 76, 82, 76);
        text(c, "Aa", 58, 38, 34, Pal.FOREST);
        for (int i = 0; i < 4; i++) dot(c, 18, 20 + i * 20, 3, Pal.INK);
    }
    private void leaf(Canvas c) {
        line(c, 18, 82, 8, 94);
        path.reset();
        path.moveTo(18, 82);
        path.cubicTo(8, 40, 40, 10, 88, 12);
        path.cubicTo(92, 58, 58, 90, 18, 82);
        path.close();
        shape(c, Pal.LIME_HI);
        thin(c, 20, 80, 78, 22);
        thin(c, 36, 64, 34, 46);
        thin(c, 36, 64, 54, 66);
        thin(c, 50, 50, 48, 32);
        thin(c, 50, 50, 68, 52);
    }
    private void magnet(Canvas c) {
        path.reset();
        path.moveTo(24, 24);
        path.lineTo(24, 56);
        path.cubicTo(24, 94, 76, 94, 76, 56);
        path.lineTo(76, 24);
        fat(c, Pal.CORAL, 17f);
        rect(c, 14, 14, 34, 34, 1, Pal.PAPER);
        rect(c, 66, 14, 86, 34, 1, Pal.PAPER);
        thin(c, 44, 20, 56, 20);
        thin(c, 46, 28, 54, 28);
    }
    private void pencil(Canvas c) {
        c.save();
        c.rotate(35f, 50, 50);
        rect(c, 38, 0, 62, 12, 4, Pal.PINK);
        rect(c, 38, 12, 62, 20, 0, Pal.GRAY);
        rect(c, 38, 20, 62, 70, 0, Pal.YELLOW);
        thin(c, 50, 22, 50, 70);
        poly(c, Pal.PAPER, 38, 70, 62, 70, 50, 94);
        poly(c, Pal.INK, 45.5f, 84, 54.5f, 84, 50, 94);
        c.restore();
        sparkle(c, 18, 24, 8, Pal.LIME_HI);
    }
    private void blocks(Canvas c) {
        rect(c, 8, 54, 46, 92, 3, Pal.CORAL);
        text(c, "A", 27, 74, 28, Pal.PAPER);
        rect(c, 54, 54, 92, 92, 3, Pal.SKY);
        text(c, "B", 73, 74, 28, Pal.INK);
        c.save();
        c.rotate(-10f, 50, 36);
        rect(c, 31, 14, 69, 52, 3, Pal.YELLOW);
        text(c, "C", 50, 34, 28, Pal.INK);
        c.restore();
    }
    private void quill(Canvas c) {
        path.reset();
        path.moveTo(22, 88);
        path.cubicTo(10, 60, 24, 22, 88, 10);
        path.cubicTo(84, 50, 54, 78, 22, 88);
        path.close();
        shape(c, Pal.LILAC);
        line(c, 22, 88, 80, 16);
        thin(c, 44, 60, 36, 42);
        thin(c, 56, 48, 50, 30);
        thin(c, 68, 38, 66, 24);
        dot(c, 14, 92, 4, Pal.INK);
    }
    private void book(Canvas c) {
        path.reset();
        path.moveTo(6, 24);
        path.lineTo(6, 82);
        path.cubicTo(22, 78, 38, 78, 50, 90);
        path.cubicTo(62, 78, 78, 78, 94, 82);
        path.lineTo(94, 24);
        path.close();
        shape(c, Pal.GREEN);
        path.reset();
        path.moveTo(50, 30);
        path.cubicTo(36, 20, 22, 20, 11, 24);
        path.lineTo(11, 74);
        path.cubicTo(22, 70, 36, 70, 50, 82);
        path.close();
        shape(c, Pal.PAPER);
        path.reset();
        path.moveTo(50, 30);
        path.cubicTo(64, 20, 78, 20, 89, 24);
        path.lineTo(89, 74);
        path.cubicTo(78, 70, 64, 70, 50, 82);
        path.close();
        shape(c, Pal.PAPER);
        thin(c, 20, 36, 40, 40);
        thin(c, 20, 46, 40, 50);
        thin(c, 60, 40, 80, 36);
        thin(c, 60, 50, 80, 46);
        poly(c, Pal.CORAL, 64, 12, 72, 12, 72, 34, 68, 29, 64, 34); }
    private void news(Canvas c) {
        rect(c, 26, 18, 88, 90, 3, Pal.LIME_HI);
        rect(c, 10, 8, 74, 82, 3, Pal.PAPER);
        rect(c, 18, 16, 66, 30, 1, Pal.FOREST);
        rect(c, 18, 38, 40, 58, 1, Pal.SKY);
        thin(c, 46, 42, 66, 42);
        thin(c, 46, 50, 66, 50);
        thin(c, 46, 58, 60, 58);
        thin(c, 18, 66, 66, 66);
        thin(c, 18, 74, 58, 74);
        dot(c, 29, 48, 4, Pal.YELLOW);
    }
    private void wheat(Canvas c) {
        line(c, 50, 94, 50, 24);
        poly(c, Pal.GREEN, 50, 84, 28, 66, 38, 88);
        poly(c, Pal.GREEN, 50, 76, 72, 60, 64, 84);
        for (int i = 0; i < 3; i++) {
            float y = 30 + i * 14;
            c.save();
            c.rotate(-32f, 50, y);
            oval(c, 35, y - 9, 47, y + 9, Pal.YELLOW);
            c.restore();
            c.save();
            c.rotate(32f, 50, y);
            oval(c, 53, y - 9, 65, y + 9, Pal.YELLOW);
            c.restore();
        }
        oval(c, 44, 4, 56, 28, Pal.YELLOW);
        circle(c, 80, 78, 12, Pal.YELLOW);
        text(c, "$", 80, 78, 15, Pal.INK);
    }
    private void sprout(Canvas c) {
        path.reset();
        path.moveTo(50, 74);
        path.lineTo(50, 36);
        fat(c, Pal.GREEN, 5f);
        path.reset();
        path.moveTo(50, 54);
        path.cubicTo(30, 58, 14, 46, 12, 28);
        path.cubicTo(34, 26, 48, 36, 50, 54);
        path.close();
        shape(c, Pal.LIME_HI);
        path.reset();
        path.moveTo(50, 44);
        path.cubicTo(70, 46, 86, 36, 88, 16);
        path.cubicTo(66, 14, 52, 24, 50, 44);
        path.close();
        shape(c, Pal.MINT);
        path.reset();
        path.moveTo(10, 90);
        path.quadTo(50, 54, 90, 90);
        path.close();
        shape(c, Pal.BROWN);
    }
    private void barn(Canvas c) {
        path.reset();
        path.moveTo(10, 90);
        path.lineTo(10, 48);
        path.lineTo(30, 22);
        path.lineTo(70, 22);
        path.lineTo(90, 48);
        path.lineTo(90, 90);
        path.close();
        shape(c, Pal.CORAL);
        rect(c, 34, 50, 66, 90, 0, Pal.PAPER);
        thin(c, 34, 50, 66, 90);
        thin(c, 66, 50, 34, 90);
        rect(c, 41, 29, 59, 42, 0, Pal.PAPER);
        thin(c, 50, 29, 50, 42);
    }
    private void tree(Canvas c) {
        rect(c, 43, 64, 57, 92, 0, Pal.BROWN);
        poly(c, Pal.GREEN, 50, 32, 86, 72, 14, 72);
        poly(c, Pal.LIME_HI, 50, 18, 80, 52, 20, 52);
        poly(c, Pal.MINT, 50, 4, 72, 32, 28, 32);
    }
    private void boat(Canvas c) {
        line(c, 50, 14, 50, 60);
        poly(c, Pal.PAPER, 54, 16, 54, 54, 84, 54);
        poly(c, Pal.YELLOW, 46, 24, 46, 54, 24, 54);
        poly(c, Pal.CORAL, 50, 14, 64, 8, 50, 2);
        path.reset();
        path.moveTo(12, 58);
        path.lineTo(88, 58);
        path.lineTo(76, 74);
        path.lineTo(24, 74);
        path.close();
        shape(c, Pal.CORAL);
        path.reset();
        path.moveTo(4, 86);
        path.quadTo(14, 76, 24, 86);
        path.quadTo(34, 96, 44, 86);
        path.quadTo(54, 76, 64, 86);
        path.quadTo(74, 96, 84, 86);
        path.quadTo(90, 80, 96, 86);
        fat(c, Pal.SKY, 4f);
    }
    private void fish(Canvas c) {
        poly(c, Pal.ORANGE, 70, 50, 94, 30, 94, 70);
        poly(c, Pal.ORANGE, 30, 36, 44, 18, 58, 36);
        oval(c, 8, 30, 78, 70, Pal.ORANGE);
        circle(c, 26, 46, 6, Pal.PAPER);
        dot(c, 26, 46, 2.6f, Pal.INK);
        path.reset();
        path.moveTo(42, 36);
        path.quadTo(35, 50, 42, 64);
        thinPath(c);
        thin(c, 56, 42, 62, 46);
        thin(c, 56, 54, 62, 58);
        circle(c, 12, 20, 4.5f, Pal.PAPER);
        circle(c, 22, 10, 3.2f, Pal.PAPER);
    }
    private void pin(Canvas c) {
        fp.setColor(Pal.INK);
        rf.set(30, 84, 70, 96);
        c.drawOval(rf, fp);
        path.reset();
        path.moveTo(50, 90);
        path.cubicTo(20, 56, 20, 50, 20, 38);
        path.cubicTo(20, 20, 34, 8, 50, 8);
        path.cubicTo(66, 8, 80, 20, 80, 38);
        path.cubicTo(80, 50, 80, 56, 50, 90);
        path.close();
        shape(c, Pal.CORAL);
        circle(c, 50, 38, 12, Pal.PAPER);
        sparkle(c, 84, 14, 8, Pal.YELLOW);
        sparkle(c, 14, 70, 6, Pal.SKY);
    }
    private void bell(Canvas c) {
        rect(c, 46, 14, 54, 26, 1, Pal.INK);
        circle(c, 50, 14, 6, Pal.CORAL);
        path.reset();
        path.moveTo(12, 72);
        path.cubicTo(12, 24, 88, 24, 88, 72);
        path.close();
        shape(c, Pal.YELLOW);
        rect(c, 6, 72, 94, 84, 4, Pal.CORAL);
        path.reset();
        path.moveTo(26, 62);
        path.quadTo(30, 42, 46, 38);
        thinPath(c);
    }
}


