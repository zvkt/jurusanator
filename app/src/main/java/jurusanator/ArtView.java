package jurusanator;
import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
/** Shows the sticker illustration of one major. Size comes from the layout. */
final class ArtView extends View {
    private final Art art = new Art();
    private int artId;
    private int bg;
    ArtView(Context context, int artId, int bg) {
        super(context);
        this.artId = artId;
        this.bg = bg;
    }
    void set(int artId, int bg) {
        this.artId = artId;
        this.bg = bg;
        invalidate();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) return;
        float size = Math.min(w, h);
        art.sticker(canvas, artId, bg, w / 2f, h / 2f, size);
    }
}


