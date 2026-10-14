package jurusanator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;
/**
 * A chunky tappable box. Pressing it slides it onto its own shadow * (the classic neo-brutalist "push down" effect). Can also show a drawn icon.
 */
class BrutalButton extends FrameLayout {
    private final BrutalDrawable bg;
    private final float shadow;
    private final float border;
    final TextView label;
    private int icon = Icons.NONE;
    BrutalButton(Context context, CharSequence text, int fill, int textColor, float textSp,
            float radius, float border, float shadow) {
        super(context);
        this.shadow = shadow;
        this.border = border;
        bg = new BrutalDrawable(fill, radius, border, shadow, Pal.INK);
        setBackground(bg);
        setClickable(true);
        setFocusable(true);
        label = new TextView(context);
        label.setText(text);
        label.setTextColor(textColor);
        label.setTextSize(textSp);
        label.setTypeface(Typeface.create("sans-serif-black",Typeface.NORMAL));
        label.setGravity(Gravity.CENTER);
        label.setIncludeFontPadding(false);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.rightMargin = Math.round(shadow);
        lp.bottomMargin = Math.round(shadow);
        addView(label, lp);
    }
    void setFace(int color) {
        bg.setFill(color);
    }
    void setIcon(int type) {
        icon = type;
        invalidate();
    }
    void setText(CharSequence text) {
        label.setText(text);
    }
    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        bg.setFlat(pressed);
        setTranslationX(pressed ? shadow : 0f);
        setTranslationY(pressed ? shadow : 0f);
    }
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (icon != Icons.NONE) {
            float w = getWidth() - shadow;
            float h = getHeight() - shadow;
            float r = Math.min(w, h) * 0.3f;
            Icons.draw(canvas, icon, w / 2f, h / 2f, r, Math.max(2f, border * 0.7f));
        }
    }
}


