package jurusanator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import jurusanator.MajorData.Major;
/**
 * The coach hub for the dream university + major. Four tabs: * TODAY (fact, news, "talked by", related topics), FIT (points, strengths/weaknesses, peers),
 * PREP (entrance route, calendar, what and when to study), PROGRESS (development,
 * commitment check, result day).
 */
final class CoachScreen extends Screen {
    static final int T_TODAY = 0, T_FIT = 1, T_PREP = 2, T_PROGRESS = 3;
    // state that must survive a re-render
    int newsStatus;                          // 0 idle, 1 loading, 2 fresh, 3 offline (cached), 4 failed
    List<Rss.Item> newsItems = new ArrayList<>();
    long[][] busy;                           // calendar busy times, null = unknown
    private int tab;
    CoachScreen(MainActivity activity, int tab) {
        super(activity);
        this.tab = Math.max(0, Math.min(3, tab));
    }
    int tab() {
        return tab;
    }
    @Override
    int id() {
        return MainActivity.S_COACH;
    }
    @Override
    boolean onBack() {
        a.sfx.back();
        a.showHome();
        return true;
    }
    /** Base for the four tab contents. */
    abstract static class Tab {
        final MainActivity a;
        final Ui ui;
        final CoachScreen host;
        final Profile p;
        final Univ univ;
        final Major major;
        final int field;
        final long now = System.currentTimeMillis();
        Tab(CoachScreen host) {
            this.host = host;
            this.a = host.a;
            this.ui = host.ui;
            this.p = a.profile;
            this.univ = Univ.byId(p.dreamUniv);
            this.major = MajorData.MAJORS.get(p.dreamMajor);
            this.field = Fields.of(p.dreamMajor);
        }
        abstract void fill(LinearLayout col);
        /** A white card with a tilted tag on top. */
        LinearLayout box(int face, String tagText, int tagFill) {
            LinearLayout card = ui.card(face, 14f);
            card.addView(ui.tag(tagText, tagFill, -1.5f));
            card.setLayoutParams(ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 16));
            return card;
        }
        TextView text(LinearLayout parent, String s, float sp, float topDp) {
            TextView t = ui.body(s, sp, Pal.INK);
            parent.addView(t, ui.lp(Ui.MATCH, Ui.WRAP, 0, topDp, 0, 0));
            return t;
        }
        TextView mono(LinearLayout parent, String s, float sp, float topDp) {
            TextView t = ui.mono(s, sp, 0xFF4A5640);
            parent.addView(t, ui.lp(Ui.MATCH, Ui.WRAP, 0, topDp, 0, 0));
            return t;
        }
        BrutalButton action(LinearLayout parent, String label, int fill, int color, final Runnable run) {
            BrutalButton b = ui.button(label, fill, color, 44);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    run.run();
                }
            });
            parent.addView(b, ui.lp(Ui.MATCH, b.getLayoutParams().height, 0, 10, 0, 0));
            return b;
        }
        BrutalButton smallAction(String label, int fill, final Runnable run) {
            BrutalButton b = ui.small(label, fill, Pal.INK);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    run.run();
                }
            });
            return b;
        }
        /** Label + animated bar + value, one row. */
        void barRow(LinearLayout parent, String label, int color, float fraction, String value, long delay) {
            LinearLayout row = ui.hbox();
            TextView l = ui.mono(label, 10.5f, Pal.INK);
            row.addView(l, new LinearLayout.LayoutParams(ui.dp(118), Ui.WRAP));
            BarView bar = new BarView(a, color, ui.border * 0.8f, ui.dp(5));
            row.addView(bar, new LinearLayout.LayoutParams(0, ui.dp(20), 1f));
            TextView v = ui.mono(value, 11f, Pal.INK);
            v.setGravity(android.view.Gravity.END);
            row.addView(v, new LinearLayout.LayoutParams(ui.dp(58), Ui.WRAP));
            parent.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
            bar.animateTo(fraction, delay);
        }
    }
    @Override
    View build() {
        LinearLayout col = column();
        BrutalButton home = ui.small("\u2190 " + L.t("HOME", "BERANDA"), Pal.PAPER, Pal.INK);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        col.addView(topBar(home, false));
        col.addView(ui.gap(14));
        col.addView(header());
        col.addView(tabs());
        col.addView(ui.gap(16));
        Tab content;
        switch (tab) {
            case T_FIT: content = new FitTab(this); break;
            case T_PREP: content = new PrepTab(this); break;
            case T_PROGRESS: content = new ProgressTab(this); break;
            default: content = new TodayTab(this); break;
        }
        content.fill(col);
        return page(52 + tab, col);
    }
    private View header() {
        Profile p = a.profile;
        Univ u = Univ.byId(p.dreamUniv);
        Major m = MajorData.MAJORS.get(p.dreamMajor);
        LinearLayout card = ui.card(Pal.LIME, 12f);
        LinearLayout row = ui.hbox();
        row.addView(new ArtView(a, m.art(), Pal.PINK), new LinearLayout.LayoutParams(ui.dp(62), ui.dp(62)));
        LinearLayout info = ui.vbox();
        info.addView(ui.heavy(m.name(), 18f, Pal.INK));
        info.addView(ui.body(u.name + " \u00B7 " + u.city, 12.5f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 2, 0, 0));
        Coach.Fit f = a.currentFit();
        if (f != null) {
            info.addView(ui.mono(L.t("FIT POINTS ", "POIN KECOCOKAN ") + f.total + "/100", 11f, Pal.INK),
                    ui.lp(Ui.WRAP, Ui.WRAP, 0, 4, 0, 0));
        }
        row.addView(info, ui.weight(12, 0, 0, 0));
        card.addView(row);
        card.setLayoutParams(ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 14));
        return card;
    }
    private View tabs() {
        String[] names = {L.t("TODAY", "HARI INI"), L.t("FIT", "COCOK"), L.t("PREP", "PERSIAPAN"), L.t("PROGRESS", "KEMAJUAN")};
        LinearLayout row = ui.hbox();
        for (int i = 0; i < names.length; i++) {
            final int index = i;
            boolean on = i == tab;
            BrutalButton b = new BrutalButton(a, names[i], on ? Pal.FOREST : Pal.PAPER, on ? Pal.LIME : Pal.INK,
                    10.5f, ui.radius * 0.5f, ui.border * 0.8f, ui.shadow * 0.6f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ui.dp(44) + Math.round(ui.shadow * 0.6f), 1f);
            lp.rightMargin = i < names.length - 1 ? ui.dp(5) : 0;
            row.addView(b, lp);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tab == index) return;
                    tab = index;
                    a.sfx.tap();
                    a.rerender();
                }
            });
        }
        return row;
    }
}


