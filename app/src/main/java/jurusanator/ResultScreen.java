package jurusanator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import jurusanator.MajorData.Major;
import jurusanator.MajorRecommender.Recommendation;
/**
 * Results: colourful match cards with an illustration each, counting percentages,
 * confetti, and an interest "stats" card with a fun nickname. */
final class ResultScreen extends Screen {
    private static final int[] CARD = {Pal.LIME, Pal.YELLOW, Pal.PINK, Pal.SKY};
    private static final String[] NICK_EN = {"The Builder", "The Thinker", "The Creator", "The Helper", "The Leader", "The Organizer"};
    private static final String[] NICK_ID = {"Si Pembangun", "Si Pemikir", "Si Kreator", "Si Penolong", "Si Pemimpin", "Si Penata"};
    private final MajorRecommender r;
    private final boolean fresh;
    private boolean celebrate;
    ResultScreen(MainActivity activity, MajorRecommender done, boolean celebrate) {
        super(activity);
        this.r = done;
        this.fresh = celebrate;
        this.celebrate = celebrate;
    }
    @Override
    int id() {
        return MainActivity.S_RESULTS;
    }
    @Override
    boolean onBack() {
        a.sfx.back();
        a.showHome();
        return true;
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
        col.addView(ui.gap(16));
        col.addView(ui.tag(L.t("YOUR RESULTS", "HASILMU"), Pal.YELLOW, -3f), ui.lp(Ui.WRAP, Ui.WRAP, 2, 0, 0, 12));
        TextView title = ui.heavy(L.t("Your top matches", "Jurusan paling cocok"), 30f, Pal.dark ? Pal.LIME : Pal.INK);
        ui.shadow(title, Pal.dark ? Pal.INK : Pal.LIME_HI, 3f);
        col.addView(title);
        col.addView(ui.body(L.t("Based on your answers. Tap PLAN THIS to choose a university and start your plan.",
                "Berdasarkan jawabanmu. Ketuk RENCANAKAN untuk memilih kampus dan memulai rencanamu."),
                14f, Pal.bgText()), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 16));
        if (r.stoppedEarly()) {
            col.addView(ui.body(L.t("The quiz ended early because the same major was the unique #1 for the last 4 answers, after at least 12 answers.",
                    "Kuis selesai lebih awal karena jurusan yang sama menjadi satu-satunya peringkat #1 selama 4 jawaban terakhir, setelah sedikitnya 12 jawaban."),
                    12f, Pal.bgText()), ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 14));
        }
        List<Recommendation> recs = r.recommendations();
        if (recs.isEmpty()) {
            LinearLayout empty = ui.card(Pal.PAPER, 16f);
            empty.addView(ui.heavy(L.t("No strong match yet", "Belum ada yang kuat cocok"), 18f, Pal.INK));
            empty.addView(ui.body(L.t("Your answers did not point to a clear direction. Try again and use more 'Yes' or 'Maybe' for things you enjoy.",
                    "Jawabanmu belum menunjuk arah yang jelas. Coba lagi dan pilih 'Ya' atau 'Mungkin' untuk hal yang kamu sukai."),
                    13.5f, Pal.INK), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
            col.addView(empty, ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 16));
        }
        for (int i = 0; i < recs.size(); i++) {
            col.addView(matchCard(i, recs.get(i), false));
        }
        col.addView(matchCard(recs.size(), r.wildcard(), true));
        col.addView(statsCard());
        if (fresh) col.addView(coachNote());
        addButtons(col);
        FrameLayout page = page(31, col);
        if (celebrate) {
            celebrate = false;
            page.addView(new ConfettiView(a), new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
            a.sfx.win();
        }
        return page;
    }
    private View matchCard(final int rank, final Recommendation rec, final boolean wild) {
        final Major m = rec.major;
        boolean hero = rank == 0 && !wild;
        LinearLayout card = ui.card(wild ? Pal.LILAC : CARD[Math.min(rank, CARD.length - 1)], 14f);
        LinearLayout top = ui.hbox();
        int artSize = ui.dp(hero ? 104 : 72);
        ArtView art = new ArtView(a, m.art(), hero ? Pal.PINK : Pal.LIME);
        top.addView(art, new LinearLayout.LayoutParams(artSize, artSize));
        if (hero) ui.floatAnim(art, 3f, 1900, 4f);
        LinearLayout info = ui.vbox();
        String badge = wild ? L.t("+1 WILDCARD \u00B7 TRY IT TOO", "+1 KARTU LIAR \u00B7 COBA JUGA")
                : hero ? L.t("#1 BEST MATCH", "#1 PALING COCOK") : "#" + (rank + 1);
        info.addView(ui.tag(badge, hero ? Pal.PAPER : Pal.LIME, hero ? -2f : 1.5f));
        info.addView(ui.heavy(m.name(), hero ? 22f : 18f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 8, 0, 2));
        info.addView(ui.mono(m.altName(), 11f, 0xFF4A5640));
        top.addView(info, ui.weight(12, 0, 0, 0));
        card.addView(top);
        card.addView(ui.body(m.description(), 13.5f, Pal.INK), ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 10));
        LinearLayout pct = ui.hbox();
        final TextView number = ui.heavy("0%", hero ? 34f : 26f, Pal.INK);
        number.setMinWidth(ui.dp(hero ? 92 : 70));
        pct.addView(number);
        BarView bar = new BarView(a, Pal.FOREST, ui.border * 0.8f, ui.dp(6));
        pct.addView(bar, new LinearLayout.LayoutParams(0, ui.dp(hero ? 26 : 20), 1f));
        card.addView(pct);
        bar.animateTo(rec.matchPercent / 100f, 250 + rank * 140L);
        ValueAnimator count = ValueAnimator.ofInt(0, (int) rec.matchPercent);
        count.setDuration(900);
        count.setStartDelay(250 + rank * 140L);
        count.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator va) {
                number.setText(va.getAnimatedValue() + "%");
            }
        });
        count.start();
        LinearLayout chips = ui.hbox();
        if (rec.interests.isEmpty()) {
            chips.addView(ui.chip(L.t("OVERALL PROFILE", "PROFIL KESELURUHAN"), Pal.PAPER));
        } else {
            for (int idx : rec.interests) {
                chips.addView(ui.chip(MajorData.interestName(idx).toUpperCase(MainActivity.locale()), Pal.INTEREST[idx]),
                        ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 5, 0));
            }
        }
        card.addView(chips, ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 10));
        BrutalButton plan = ui.button(L.t("PLAN THIS", "RENCANAKAN INI"), Pal.FOREST, Pal.LIME, 44);
        plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                a.showUniv(rec.majorIndex);
            }
        });
        card.addView(plan, ui.lp(Ui.MATCH, plan.getLayoutParams().height, 0, 0, 0, 0));
        card.setLayoutParams(ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 16));
        ui.popIn(card, rank * 110L);
        return card;
    }
    private View statsCard() {
        double[] prof = r.interestProfile();
        int top = 0;
        for (int i = 1; i < 6; i++) {
            if (prof[i] > prof[top]) top = i;
        }
        boolean explorer = prof[top] < 55;
        LinearLayout card = ui.card(Pal.PAPER, 14f);
        card.addView(ui.tag(L.t("YOUR INTEREST MAP", "PETA MINATMU"), Pal.MINT, -1.5f));
        LinearLayout vibe = ui.hbox();
        vibe.addView(ui.mono(L.t("YOU ARE", "KAMU ADALAH"), 11f, 0xFF4A5640));
        card.addView(vibe, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 4));
        String nick = explorer ? L.t("The Explorer", "Si Penjelajah") : (L.id ? NICK_ID[top] : NICK_EN[top]);
        TextView nickView = ui.heavy(nick.toUpperCase(MainActivity.locale()), 26f, Pal.INK);
        nickView.setBackground(new BrutalDrawable(explorer ? Pal.LILAC : Pal.INTEREST[top], ui.radius * 0.5f,
                ui.border * 0.8f, ui.shadow * 0.6f, Pal.INK));
        int pad = ui.dp(10);
        nickView.setPadding(pad, ui.dp(6), pad + Math.round(ui.shadow * 0.6f), ui.dp(6) + Math.round(ui.shadow * 0.6f));
        nickView.setRotation(-1.5f);
        card.addView(nickView, ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 0, 14));
        for (int i = 0; i < 6; i++) {
            LinearLayout row = ui.hbox();
            TextView label = ui.mono(MajorData.interestName(i).toUpperCase(MainActivity.locale()), 10.5f, Pal.INK);
            row.addView(label, new LinearLayout.LayoutParams(ui.dp(104), Ui.WRAP));
            BarView bar = new BarView(a, Pal.INTEREST[i], ui.border * 0.8f, ui.dp(5));
            row.addView(bar, new LinearLayout.LayoutParams(0, ui.dp(22), 1f));
            int value = (int) Math.round(prof[i]);
            TextView num = ui.mono(value + "%", 11.5f, Pal.INK);
            num.setGravity(android.view.Gravity.END);
            row.addView(num, new LinearLayout.LayoutParams(ui.dp(44), Ui.WRAP));
            card.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 8));
            bar.animateTo(value / 100f, 400 + i * 90L);
        }
        card.addView(ui.body(L.t("50% means neutral. Higher means you lean towards it.",
                "50% berarti netral. Makin tinggi, makin kamu condong ke sana."), 11.5f, 0xFF4A5640),
                ui.lp(Ui.MATCH, Ui.WRAP, 0, 2, 0, 0));
        card.setLayoutParams(ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 16));
        return card;
    }
    /** Tells the user what the coach concluded about this quiz (transparent strategy change). */
    private View coachNote() {
        Profile p = a.profile;
        LinearLayout card = ui.card(Pal.LILAC, 12f);
        card.addView(ui.tag(L.t("COACH NOTE", "CATATAN COACH"), Pal.PAPER, 1.5f));
        String text;
        if (p.seriousness < 0) {
            text = L.t("Not enough data yet to judge how focused your answers were.",
                    "Belum cukup data untuk menilai seberapa fokus jawabanmu.");
        } else {
            String mode = p.strategy == Profile.STRAT_ENGAGE
                    ? L.t("Next quiz: SHORT mode, 8 questions without \"don't know\".",
                    "Kuis berikutnya: mode SINGKAT, 8 pertanyaan tanpa \"tidak tahu\".")
                    : p.strategy == Profile.STRAT_DEEP
                    ? L.t("Next quiz: DEEP mode, up to 24 questions plus a duel.",
                    "Kuis berikutnya: mode MENDALAM, maksimal 24 pertanyaan plus duel.")
                    : L.t("Next quiz: standard mode.", "Kuis berikutnya: mode standar.");
            text = L.t("Focus score of your answers: ", "Skor fokus jawabanmu: ") + p.seriousness + "/100. " + mode;
        }
        card.addView(ui.body(text, 13f, Pal.INK), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        card.setLayoutParams(ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 16));
        return card;
    }
    private void addButtons(LinearLayout col) {
        BrutalButton share = ui.button(L.t("SHARE RESULTS", "BAGIKAN HASIL"), Pal.LIME, Pal.INK, 48);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                share();
            }
        });
        col.addView(share, ui.lp(Ui.MATCH, share.getLayoutParams().height, 0, 0, 0, 8));
        BrutalButton again = ui.button(L.t("PLAY AGAIN", "ULANGI KUIS"), Pal.PAPER, Pal.INK, 48);
        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                a.showQuiz(true);
            }
        });
        col.addView(again, ui.lp(Ui.MATCH, again.getLayoutParams().height, 0, 0, 0, 8));
        if (a.profile.hasDream()) {
            BrutalButton plan = ui.button(L.t("OPEN MY PLAN", "BUKA RENCANAKU"), Pal.FOREST, Pal.LIME, 48);
            plan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.showCoach(0);
                }
            });
            col.addView(plan, ui.lp(Ui.MATCH, plan.getLayoutParams().height, 0, 0, 0, 8));
        }
    }
    private void share() {
        StringBuilder sb = new StringBuilder(L.t("My top majors on Jurusanator:\n", "Jurusan teratasku di Jurusanator:\n"));
        List<Recommendation> recs = r.recommendations();
        for (int i = 0; i < recs.size(); i++) {
            sb.append(i + 1).append(". ").append(recs.get(i).major.name()).append(" (")
                    .append(recs.get(i).matchPercent).append("%)\n");
        }
        Intent send = new Intent(Intent.ACTION_SEND);
        send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT, sb.toString());
        try {
            a.startActivity(Intent.createChooser(send, L.t("Share results", "Bagikan hasil")));
        } catch (RuntimeException e) {
            a.flash(L.t("No app can share this.", "Tidak ada aplikasi untuk berbagi."));
        }
    }
}
