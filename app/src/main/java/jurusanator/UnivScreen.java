package jurusanator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import jurusanator.MajorData.Major;
/** Pick the university for a chosen major. Universities known for that field come first. */
final class UnivScreen extends Screen {
    private final int major;
    UnivScreen(MainActivity activity, int major) {
        super(activity);
        this.major = major;
    }
    int major() {
        return major;
    }
    @Override
    int id() {
        return MainActivity.S_UNIV;
    }
    @Override
    boolean onBack() {
        a.sfx.back();
        MajorRecommender r = a.latestRec();
        if (r != null) a.showResults(r, false);
        else a.showHome();
        return true;
    }
    @Override
    View build() {
        final int field = Fields.of(major);
        Major m = MajorData.MAJORS.get(major);
        LinearLayout col = column();
        BrutalButton back = ui.small("\u2190 " + L.t("BACK", "KEMBALI"), Pal.PAPER, Pal.INK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
        col.addView(topBar(back, false));
        col.addView(ui.gap(14));
        LinearLayout head = ui.hbox();
        head.addView(new ArtView(a, m.art(), Pal.LIME), new LinearLayout.LayoutParams(ui.dp(84), ui.dp(84)));
        LinearLayout info = ui.vbox();
        info.addView(ui.tag(L.t("CHOOSE A UNIVERSITY", "PILIH KAMPUS"), Pal.YELLOW, -2f));
        TextView name = ui.heavy(m.name(), 24f, Pal.dark ? Pal.LIME : Pal.INK);
        ui.shadow(name, Pal.dark ? Pal.INK : Pal.LIME_HI, 2.5f);
        info.addView(name, ui.lp(Ui.WRAP, Ui.WRAP, 0, 8, 0, 0));
        head.addView(info, ui.weight(12, 0, 0, 0));
        col.addView(head, ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 10));
        col.addView(ui.body(L.t("Field: ", "Bidang: ") + Fields.name(field) + ". "
                + L.t("Universities known for this field are listed first. This is a general reputation guide, not a ranking. Always check the official program list and accreditation.",
                "Kampus yang dikenal di bidang ini ditampilkan lebih dulu. Ini panduan reputasi umum, bukan peringkat. Selalu cek daftar program studi resmi dan akreditasinya."),
                12.5f, Pal.bgText()), ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 14));
        List<Univ> list = Univ.forField(field);
        int[] bgs = {Pal.PAPER, Pal.PAPER};
        for (int i = 0; i < list.size(); i++) {
            final Univ u = list.get(i);
            boolean strong = u.isStrongIn(field);
            LinearLayout card = ui.card(strong ? Pal.LIME : bgs[i % 2], 12f);
            LinearLayout line = ui.hbox();
            LinearLayout text = ui.vbox();
            text.addView(ui.heavy(u.name, 16f, Pal.INK));
            text.addView(ui.mono(u.city + " \u00B7 " + u.type + " \u00B7 " + dots(u.selectivity), 11f, 0xFF4A5640),
                    ui.lp(Ui.WRAP, Ui.WRAP, 0, 3, 0, 0));
            line.addView(text, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            if (strong) line.addView(ui.tag(L.t("KNOWN FOR THIS", "DIKENAL DI SINI"), Pal.YELLOW, 2f));
            card.addView(line);
            card.setClickable(true);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    choose(u);
                }
            });
            col.addView(card, ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 10));
        }
        col.addView(ui.body(L.t("\u25CF\u25CF\u25CF = very competitive entry (an estimate).",
                "\u25CF\u25CF\u25CF = persaingan masuk sangat ketat (perkiraan)."), 11f, muted()));
        return page(41, col);
    }
    private static String dots(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) sb.append(i < n ? '\u25CF' : '\u25CB');
        return sb.toString();
    }
    private void choose(Univ u) {
        Profile p = a.profile;
        boolean changed = p.dreamMajor != major || p.dreamUniv != u.id;
        p.dreamMajor = major;
        p.dreamUniv = u.id;
        if (changed || p.dreamSince == 0) p.dreamSince = System.currentTimeMillis();
        a.saveProfile();
        a.sfx.ding();
        a.flash(L.t("Dream set: ", "Impian tersimpan: ") + u.shortName);
        a.showCoach(1);
    }
}


