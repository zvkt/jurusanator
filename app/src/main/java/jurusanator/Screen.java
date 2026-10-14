package jurusanator;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
/** One full-screen page. build() may be called again (language / theme change). */
abstract class Screen {
    final MainActivity a;
    final Ui ui;
    Screen(MainActivity activity) {
        this.a = activity;
        this.ui = activity.ui;
    }
    abstract View build();
    /** Return true if the back press was handled here. */
    boolean onBack() {
        return false;
    }
    /** Screen id saved for state restore (see MainActivity.S_*). */
    abstract int id();

    /** Standard scrolling column with side padding. */
    final LinearLayout column() {
        LinearLayout col = ui.vbox();
        col.setPadding(ui.dp(18), ui.dp(14), ui.dp(18), ui.dp(30));
        return col;
    }
    /** Drifting backdrop + scrolling content. */
    final FrameLayout page(long seed, LinearLayout content) {
        FrameLayout root = new FrameLayout(a);
        root.addView(new FloatingBackdrop(a, seed), new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        ScrollView scroll = new ScrollView(a);
        scroll.setFillViewport(true);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.addView(content, new ScrollView.LayoutParams(Ui.MATCH, Ui.WRAP));
        root.addView(scroll, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        return root;
    }
    /** Header row: left view + flexible space + language/sound(/theme) buttons. */
    final LinearLayout topBar(View left, boolean withTheme) {
        LinearLayout top = ui.hbox();
        top.addView(left);
        View space = new View(a);
        top.addView(space, new LinearLayout.LayoutParams(0, 1, 1f));
        top.addView(a.toolbarRight(withTheme));
        return top;
    }
    final int muted() {
        return Pal.bgMuted();
    }
}


