package jurusanator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
/** Small toolkit that builds the neo-brutalist widgets used by every screen. */
final class Ui {
    interface IntCb {
        void on(int value);
    }
    /** A floating "window" gadget: title bar + body. Used for questions and pop-up forms. */
    static final class Window {
        final LinearLayout root;
        final LinearLayout body;
        final TextView title;
        Window(LinearLayout root, LinearLayout body, TextView title) {
            this.root = root; this.body = body; this.title = title;
        }
    }
    static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;
    final Context ctx;
    final float density;
    final float border;
    final float shadow;
    final float radius;
    Sfx sfx;
    private final Typeface fHeavy = Typeface.create("sans-serif-black", Typeface.NORMAL);
    private final Typeface fBody = Typeface.create("sans-serif-medium", Typeface.NORMAL);
    private final Typeface fMono = Typeface.create("monospace", Typeface.BOLD);
    Ui(Context ctx) {
        this.ctx = ctx;
        this.density = ctx.getResources().getDisplayMetrics().density;
        this.border = 3f * density;
        this.shadow = 5f * density;
        this.radius = 12f * density;
    }
    int dp(float v) {
        return Math.round(v * density);
    }
    // -------------------------------------------------------------------text
    private TextView text(String s, float sp, int color, Typeface face) {
        TextView t = new TextView(ctx);
        t.setText(s);
        t.setTextSize(sp);
        t.setTextColor(color);
        t.setTypeface(face);
        t.setIncludeFontPadding(false);
        return t;
    }
    TextView heavy(String s, float sp, int color) {
        return text(s, sp, color, fHeavy);
    }
    TextView body(String s, float sp, int color) {
        TextView t = text(s, sp, color, fBody);
        t.setLineSpacing(0f, 1.15f);
        return t;
    }
    TextView mono(String s, float sp, int color) {
        TextView t = text(s, sp, color, fMono);
        t.setLetterSpacing(0.04f);
        return t;
    }
    /** Hard offset text shadow (neo-brutalist). */
    void shadow(TextView t, int color, float offsetDp) {
        t.setShadowLayer(0.01f, offsetDp * density, offsetDp * density, color);
    }
    // -----------------------------------------------------------------layout
    LinearLayout vbox() {
        LinearLayout l = new LinearLayout(ctx);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    LinearLayout hbox() {
        LinearLayout l = new LinearLayout(ctx);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }
    LinearLayout.LayoutParams lp(int w, int h) {
        return new LinearLayout.LayoutParams(w, h);
    }
    /** Layout params with margins in dp. */
    LinearLayout.LayoutParams lp(int w, int h, float l, float t, float r, float b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(w, h);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }
    LinearLayout.LayoutParams weight(float l, float t, float r, float b) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, WRAP, 1f);
        p.setMargins(dp(l), dp(t), dp(r), dp(b));
        return p;
    }
    View gap(float dpHeight) {
        View v = new View(ctx);
        v.setLayoutParams(lp(1, dp(dpHeight)));
        return v;
    }
    // ------------------------------------------------------------------boxes
    /** A brutalist card. The padding keeps content away from the border and the shadow. */
    LinearLayout card(int fill, float padDp) {
        LinearLayout l = vbox();
        l.setBackground(new BrutalDrawable(fill, radius, border, shadow, Pal.INK));
        int p = dp(padDp);
        int b = Math.round(border);
        l.setPadding(b + p, b + p, b + p + Math.round(shadow), b + p + Math.round(shadow));
        return l;
    }
    /** Standard push-down button. */
    BrutalButton button(String text, int fill, int textColor, float faceHeightDp) {
        BrutalButton b = new BrutalButton(ctx, text, fill, textColor, 15f, radius * 0.8f, border, shadow);
        b.setLayoutParams(lp(MATCH, dp(faceHeightDp) + Math.round(shadow)));
        return b;
    }
    /** Small button (40dp face), width wraps its text. */
    BrutalButton small(String text, int fill, int textColor) {
        BrutalButton b = new BrutalButton(ctx, text, fill, textColor, 12f, radius * 0.6f,
                border * 0.8f, shadow * 0.7f);
        b.label.setPadding(dp(14), 0, dp(14), 0);
        b.setLayoutParams(lp(WRAP, dp(38) + Math.round(shadow * 0.7f)));
        return b;
    }
    /** Square icon button (sound / theme). */
    BrutalButton iconButton(int icon, int fill, String description) {
        BrutalButton b = new BrutalButton(ctx, "", fill, Pal.INK, 12f, radius * 0.6f,
                border * 0.8f, shadow * 0.7f);
        b.setIcon(icon);
        b.setContentDescription(description);
        int size = dp(42) + Math.round(shadow * 0.7f);
        b.setLayoutParams(lp(size, size));
        return b;
    }
    /** Tilted sticker label. */
    TextView tag(String s, int fill, float rotation) {
        TextView t = mono(s, 11f, Pal.INK);
        t.setBackground(new BrutalDrawable(fill, radius * 0.4f, border * 0.7f, shadow * 0.5f, Pal.INK));
        int h = dp(9);
        int v = dp(5);
        t.setPadding(h, v, h + Math.round(shadow * 0.5f), v + Math.round(shadow * 0.5f));
        t.setRotation(rotation);
        return t;
    }
    /** Small rounded chip (interest tags, related topics). */
    TextView chip(String s, int fill) {
        TextView t = body(s, 12f, Pal.INK);
        t.setTypeface(fMono);
        t.setBackground(new BrutalDrawable(fill, radius * 0.5f, border * 0.6f, 0f, Pal.INK));
        t.setPadding(dp(9), dp(5), dp(9), dp(5));
        return t;
    }
    /** Two-segment ID | EN switch. Calls onChange after L.id was updated. */
    LinearLayout langToggle(final Runnable onChange) {
        LinearLayout row = hbox();
        row.setBackground(new BrutalDrawable(Pal.PAPER, radius * 0.6f, border * 0.8f, shadow * 0.7f, Pal.INK));
        int pad = Math.round(border * 0.8f) + dp(2);
        row.setPadding(pad, pad, pad + Math.round(shadow * 0.7f), pad + Math.round(shadow * 0.7f));
        row.addView(segment("ID", L.id, true, onChange));
        row.addView(segment("EN", !L.id, false, onChange));
        row.setLayoutParams(lp(WRAP, dp(42) + Math.round(shadow * 0.7f)));
        return row;
    }
    private TextView segment(String label, boolean active, final boolean idValue, final Runnable onChange) {
        TextView t = mono(label, 13f, active ? Pal.LIME : Pal.INK);
        t.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(active ? Pal.FOREST : 0x00000000);
        bg.setCornerRadius(radius * 0.4f);
        t.setBackground(bg);
        t.setLayoutParams(lp(dp(40), MATCH));
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (L.id == idValue) return;
                L.id = idValue;
                if (sfx != null) sfx.toggle();
                onChange.run();
            }
        });
        return t;
    }
    /** Floating window gadget: dark-green title bar + coloured body. */
    Window window(String title, int face) {
        LinearLayout root = card(face, 0f);
        int b = Math.round(border);
        root.setPadding(b, b, b + Math.round(shadow), b + Math.round(shadow));
        LinearLayout bar = hbox();
        float r = Math.max(0f, radius - border);
        GradientDrawable barBg = new GradientDrawable();
        barBg.setColor(Pal.FOREST);
        barBg.setCornerRadii(new float[]{r, r, r, r, 0f, 0f, 0f, 0f});
        bar.setBackground(barBg);
        bar.setPadding(dp(12), 0, dp(12), 0);
        int[] dots = {Pal.CORAL, Pal.YELLOW, Pal.LIME_HI};
        for (int color : dots) {
            View dot = new View(ctx);
            GradientDrawable d = new GradientDrawable();
            d.setColor(color);
            d.setStroke(Math.round(border * 0.5f), Pal.INK);
            dot.setBackground(d);
            dot.setLayoutParams(lp(dp(11), dp(11), 0, 0, 5, 0));
            bar.addView(dot);
        }
        TextView label = mono(title, 12f, Pal.LIME);
        label.setLayoutParams(lp(WRAP, WRAP, 8, 0, 0, 0));
        bar.addView(label);
        root.addView(bar, lp(MATCH, dp(34)));
        View rule = new View(ctx);
        rule.setBackgroundColor(Pal.INK);
        root.addView(rule, lp(MATCH, Math.round(border)));
        LinearLayout body = vbox();
        body.setPadding(dp(16), dp(16), dp(16), dp(16));
        root.addView(body, lp(MATCH, WRAP));
        return new Window(root, body, label);
    }
    // -----------------------------------------------------------------inputs
    /** [ - ]   value   [ + ] row with a label on the left. */
    View stepper(String label, final int min, final int max, final int step, int start,
            final String unit, final IntCb cb) {
        final int[] value = {Math.max(min, Math.min(max, start))};
        LinearLayout row = hbox();
        TextView name = body(label, 14f, Pal.INK);
        row.addView(name, weight(0, 0, 8, 0));
        final TextView shown = heavy(value[0] + unit, 16f, Pal.INK);
        shown.setGravity(Gravity.CENTER);
        shown.setMinWidth(dp(58));
        BrutalButton minus = small("\u2212", Pal.PAPER, Pal.INK);
        BrutalButton plus = small("+", Pal.LIME, Pal.INK);
        minus.label.setPadding(dp(8), 0, dp(8), 0);
        plus.label.setPadding(dp(8), 0, dp(8), 0);
        View.OnClickListener change = null;
        minus.setOnClickListener(new StepClick(value, -step, min, max, shown, unit, cb));
        plus.setOnClickListener(new StepClick(value, step, min, max, shown, unit, cb));
        row.addView(minus);
        row.addView(shown);
        row.addView(plus);
        cb.on(value[0]);
        return row;
    }
    private final class StepClick implements View.OnClickListener {
        private final int[] value;
        private final int delta, min, max;
        private final TextView shown;
        private final String unit;
        private final IntCb cb;
        StepClick(int[] value, int delta, int min, int max, TextView shown, String unit, IntCb cb) {
            this.value = value; this.delta = delta; this.min = min; this.max = max;
            this.shown = shown; this.unit = unit; this.cb = cb;
        }
        @Override
        public void onClick(View v) {
            value[0] = Math.max(min, Math.min(max, value[0] + delta));
            shown.setText(value[0] + unit);
            if (sfx != null) sfx.tap();
            cb.on(value[0]);
        }
    }
    /** Single-choice chips in a grid with {@code cols} columns. */
    View choice(String[] labels, int selected, int cols, final IntCb cb) {
        LinearLayout box = vbox();
        final BrutalButton[] buttons = new BrutalButton[labels.length];
        final int[] sel = {selected};
        LinearLayout row = null;
        for (int i = 0; i < labels.length; i++) {
            if (i % cols == 0) {
                row = hbox();
                box.addView(row, lp(MATCH, WRAP, 0, 0, 0, 4));
            }
            final int index = i;
            BrutalButton b = new BrutalButton(ctx, labels[i], i == selected ? Pal.FOREST : Pal.PAPER,
                    i == selected ? Pal.LIME : Pal.INK, 11f, radius * 0.5f, border * 0.7f, shadow * 0.6f);
            b.label.setPadding(dp(6), dp(4), dp(6), dp(4));
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(46) + Math.round(shadow * 0.6f), 1f);
            p.setMargins(0, 0, dp(4), 0);
            row.addView(b, p);
            buttons[i] = b;
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sel[0] = index;
                    for (int k = 0; k < buttons.length; k++) {
                        boolean on = k == index;
                        buttons[k].setFace(on ? Pal.FOREST : Pal.PAPER);
                        buttons[k].label.setTextColor(on ? Pal.LIME : Pal.INK);
                    }
                    if (sfx != null) sfx.tap();
                    cb.on(index);
                }
            });
        }
        cb.on(selected);
        return box;
    }
    // -------------------------------------------------------------animations
    /** Gentle endless bob + wobble. Starts when attached, stops when detached. */
    void floatAnim(final View v, final float amplitudeDp, long periodMs, final float rotation) {
        final float baseRotation = v.getRotation();
        final float amp = amplitudeDp * density;
        final ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setDuration(periodMs);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator a) {
                float f = ((Float) a.getAnimatedValue() - 0.5f) * 2f;
                v.setTranslationY(amp * f);
                v.setRotation(baseRotation + rotation * f);
            }
        });
        v.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {
                if (!anim.isStarted()) anim.start();
            }
            @Override
            public void onViewDetachedFromWindow(View view) {
                anim.cancel();
            }
        });
        if (v.isAttachedToWindow()) anim.start();
    }
    /** Spring in from below with a little overshoot. */
    void popIn(View v, long delayMs) {
        v.setAlpha(0f);
        v.setTranslationY(dp(28));
        v.setScaleX(0.94f);
        v.setScaleY(0.94f);
        v.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f)
                .setStartDelay(delayMs).setDuration(380)
                .setInterpolator(new OvershootInterpolator(1.3f)).start();
    }
    /** Centre a view in a FrameLayout. */
    FrameLayout.LayoutParams center(int w, int h) {
        return new FrameLayout.LayoutParams(w, h, Gravity.CENTER);
    }
}


