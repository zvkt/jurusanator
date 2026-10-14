package jurusanator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/** FIT: university points, your best and weakest areas, and how you compare with peers. */
final class FitTab extends CoachScreen.Tab {
    FitTab(CoachScreen host) {
        super(host);
    }
    @Override
    void fill(LinearLayout col) {
        Coach.Fit fit = a.currentFit();
        if (fit == null) return;
        points(col, fit);
        strengths(col);
        peers(col, fit);
        BrutalButton change = ui.button(L.t("CHANGE UNIVERSITY", "GANTI KAMPUS"), Pal.PAPER, Pal.INK, 44);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                a.showUniv(p.dreamMajor);
            }
        });
        col.addView(change, ui.lp(Ui.MATCH, change.getLayoutParams().height, 0, 0, 0, 8));
    }
    // -----------------------------------------------------------------points
    private void points(LinearLayout col, Coach.Fit f) {
        LinearLayout box = box(Pal.LIME, L.t("UNIVERSITY POINTS", "POIN KAMPUS"), Pal.YELLOW);
        LinearLayout big = ui.hbox();
        big.addView(ui.heavy(String.valueOf(f.total), 60f, Pal.INK));
        big.addView(ui.mono("/100", 14f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 4, 24, 0, 0));
        box.addView(big, ui.lp(Ui.MATCH, Ui.WRAP, 0, 6, 0, 0));
        String verdict = f.total >= 80 ? L.t("STRONG FIT", "SANGAT COCOK")
                : f.total >= 65 ? L.t("PROMISING", "MENJANJIKAN")
                : f.total >= 50 ? L.t("NEEDS WORK", "PERLU USAHA")
                : L.t("A LONG WAY TO GO", "MASIH JAUH");
        box.addView(ui.heavy(verdict, 18f, Pal.INK));
        float[] w = p.weights;
        barRow(box, L.t("INTEREST MATCH", "MINAT") + " \u00D7" + Math.round(w[0] * 100) + "%", Pal.PINK, f.interest / 100f, f.interest + "", 100);
        barRow(box, L.t("UNIVERSITY FIT", "KAMPUS") + " \u00D7" + Math.round(w[1] * 100) + "%", Pal.SKY, f.univ / 100f, f.univ + "", 180);
        barRow(box, L.t("READINESS", "KESIAPAN") + " \u00D7" + Math.round(w[2] * 100) + "%", Pal.ORANGE, f.ready / 100f, f.ready + "", 260);
        barRow(box, L.t("COMMITMENT", "KOMITMEN") + " \u00D7" + Math.round(w[3] * 100) + "%", Pal.LILAC, f.commit / 100f, f.commit + "", 340);
        text(box, L.t("Points = interest from your quiz, how well the university fits this field, how ready you are, and how committed you look. The weights adapt after your result day.",
                "Poin = minat dari kuismu, kecocokan kampus dengan bidang ini, kesiapanmu, dan komitmenmu. Bobot menyesuaikan setelah hari pengumuman."), 11.5f, 12);
        col.addView(box);
    }
    // -------------------------------------------------------- best and weakest
    private void strengths(LinearLayout col) {
        LinearLayout box = box(Pal.PAPER, L.t("BEST & WEAKEST", "TERBAIK & TERLEMAH"), Pal.PINK);
        boolean hasData = p.rated || !p.logs.isEmpty();
        if (!hasData) {
            text(box, L.t("Rate yourself on the 7 UTBK sub-tests and I will find your best and weakest areas for this major.",
                    "Nilai dirimu di 7 subtes UTBK dan aku akan mencari bagian terbaik dan terlemahmu untuk jurusan ini."), 14f, 10);
            action(box, L.t("RATE MY SKILLS", "NILAI KEMAMPUANKU"), Pal.LIME_HI, Pal.INK, new Runnable() {
                @Override
                public void run() {
                    Forms.rateSkills(a);
                }
            });
        } else {
            List<Coach.Gap> gaps = Coach.gaps(p, univ, field);
            StringBuilder weak = new StringBuilder();
            for (int i = 0; i < 2 && gaps.get(i).gap > 0; i++) {
                if (weak.length() > 0) weak.append(", ");
                weak.append(Skills.name(gaps.get(i).skill));
            }
            StringBuilder best = new StringBuilder();
            for (int i = gaps.size() - 1, n = 0; i >= 0 && n < 2 && gaps.get(i).gap <= 0; i--, n++) {
                if (best.length() > 0) best.append(", ");
                best.append(Skills.name(gaps.get(i).skill));
            }
            box.addView(label(L.t("BEST", "TERBAIK"), Pal.LIME_HI), ui.lp(Ui.WRAP, Ui.WRAP, 0, 12, 0, 0));
            text(box, best.length() > 0 ? best.toString()
                    : L.t("Nothing above my rough guide yet. Keep practicing.", "Belum ada yang melewati panduanku. Terus berlatih."), 14f, 4);
            box.addView(label(L.t("WEAKEST", "TERLEMAH"), Pal.CORAL), ui.lp(Ui.WRAP, Ui.WRAP, 0, 12, 0, 0));
            text(box, weak.length() > 0 ? weak.toString()
                    : L.t("No weak spot against my rough guide. Great!", "Tidak ada titik lemah menurut panduanku. Bagus!"), 14f, 4);
            int[] level = Coach.levels(p);
            long delay = 100;
            for (int s = 0; s < Skills.COUNT; s++) {
                int need = Coach.need(univ, field, s);
                boolean ok = level[s] >= need;
                barRow(box, Skills.code(s) + " " + level[s] + "/" + need, ok ? Pal.LIME_HI : Pal.CORAL, level[s] / 100f, ok ? "OK" : "-" + (need - level[s]), delay);
                delay += 70;
            }
            text(box, L.t("The needed level is my rough guide for this field and university, not an official requirement.",
                    "Level yang dibutuhkan adalah panduan kasarku untuk bidang dan kampus ini, bukan syarat resmi."), 11f, 10);
            LinearLayout row = ui.hbox();
            row.addView(smallAction(L.t("UPDATE RATINGS", "UBAH NILAI"), Pal.LIME, new Runnable() {
                @Override
                public void run() {
                    Forms.rateSkills(a);
                }
            }), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 8, 0));
            row.addView(smallAction(L.t("LOG MOCK TEST", "CATAT TRY OUT"), Pal.YELLOW, new Runnable() {
                @Override
                public void run() {
                    Forms.logMock(a);
                }
            }));
            box.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
        }
        // about the university itself
        box.addView(label(L.t("UNIVERSITY KNOWN FOR", "KAMPUS DIKENAL DI"), Pal.SKY), ui.lp(Ui.WRAP, Ui.WRAP, 0, 16, 0, 0));
        text(box, univ.strongNames(), 13.5f, 4);
        if (!univ.isStrongIn(field)) {
            text(box, L.t("Heads-up: " + univ.shortName + " is not mainly known for " + Fields.name(field)
                    + ". Check that it offers this program and look at its accreditation.",
                    "Perhatian: " + univ.shortName + " tidak terutama dikenal di bidang " + Fields.name(field)
                    + ". Pastikan program ini tersedia dan cek akreditasinya."), 12.5f, 6);
        }
        col.addView(box);
    }
    private TextView label(String s, int fill) {
        return ui.tag(s, fill, -1f);
    }
    // ------------------------------------------------------------------peers
    private void peers(LinearLayout col, Coach.Fit fit) {
        Peers.Source src = Peers.source();
        LinearLayout box = box(Pal.PAPER, src.isReal() ? L.t("PEERS", "TEMAN SEJALUR") : L.t("PEERS \u00B7 DEMO", "TEMAN \u00B7 DEMO"), Pal.CORAL);
        final List<Peers.Peer> list = new ArrayList<>(src.all(univ, p.dreamMajor));
        Collections.sort(list, new Comparator<Peers.Peer>() {
            @Override
            public int compare(Peers.Peer x, Peers.Peer y) {
                return y.score - x.score;
            }
        });
        int mine = fit.ready;
        int beat = Peers.beatPercent(list, mine);
        text(box, L.t("You beat", "Kamu mengalahkan"), 13f, 10);
        box.addView(ui.heavy(beat + "%", 44f, Pal.INK));
        text(box, L.t("of " + list.size() + " other students aiming for " + major.name() + " at " + univ.shortName + ", by readiness.",
                "dari " + list.size() + " siswa lain yang membidik " + major.name() + " di " + univ.shortName + ", berdasarkan kesiapan."), 13f, 0);
        // insert "YOU" into the sorted list, then show a window of 5 rows around it
        int rank = 0;
        while (rank < list.size() && list.get(rank).score > mine) rank++;
        int total = list.size() + 1;
        int from = Math.max(0, Math.min(rank - 2, total - 5));
        int to = Math.min(total, from + 5);
        for (int i = from; i < to; i++) {
            if (i == rank) {
                peerRow(box, "#" + (i + 1), L.t("YOU", "KAMU"), mine, true);
            } else {
                Peers.Peer other = list.get(i < rank ? i : i - 1);
                peerRow(box, "#" + (i + 1), other.name, other.score, false);
            }
        }
        if (!src.isReal()) {
            TextView demo = ui.tag(L.t("DEMO: these peers are simulated, not real people.",
                    "DEMO: teman ini simulasi, bukan orang sungguhan."), Pal.YELLOW, -1f);
            demo.setTextSize(10f);
            box.addView(demo, ui.lp(Ui.WRAP, Ui.WRAP, 0, 14, 0, 0));
            text(box, L.t("Real comparison needs a server that stores anonymous scores. See the README to connect one.",
                    "Perbandingan nyata butuh server yang menyimpan skor anonim. Lihat README untuk menyambungkannya."), 11f, 6);
        }
        col.addView(box);
    }
    private void peerRow(LinearLayout parent, String rank, String name, int score, boolean me) {
        LinearLayout row = ui.hbox();
        if (me) {
            row.setBackground(new BrutalDrawable(Pal.LIME, ui.radius * 0.4f, ui.border * 0.6f, 0f, Pal.INK));
            row.setPadding(ui.dp(6), ui.dp(4), ui.dp(6), ui.dp(4));
        }
        row.addView(ui.mono(rank, 10.5f, Pal.INK), new LinearLayout.LayoutParams(ui.dp(34), Ui.WRAP));
        row.addView(ui.mono(name, 11f, Pal.INK), new LinearLayout.LayoutParams(ui.dp(104), Ui.WRAP));
        BarView bar = new BarView(a, me ? Pal.FOREST : Pal.SKY, ui.border * 0.7f, ui.dp(4));
        row.addView(bar, new LinearLayout.LayoutParams(0, ui.dp(16), 1f));
        TextView v = ui.mono(String.valueOf(score), 11f, Pal.INK);
        v.setGravity(android.view.Gravity.END);
        row.addView(v, new LinearLayout.LayoutParams(ui.dp(34), Ui.WRAP));
        parent.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 6, 0, 0));
        bar.animateTo(score / 100f, 150);
    }
}


