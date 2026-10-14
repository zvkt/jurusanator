package jurusanator;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Date;
import java.util.List;
import jurusanator.MajorData.Major;
/** Start page: title, today's illustrations, start/continue, dream card, past results. */
final class HomeScreen extends Screen {
    private boolean confirmClear;
    HomeScreen(MainActivity activity) {
        super(activity);
    }
    @Override
    int id() {
        return MainActivity.S_HOME;
    }
    @Override
    View build() {
        final Profile profile = a.profile;
        LinearLayout col = column();
        TextView logo = ui.tag("jurusanator", Pal.LIME, -2f);
        col.addView(topBar(logo, true));
        col.addView(ui.gap(16));
        TextView kicker = ui.tag(L.t("FIND YOUR DIRECTION", "TEMUKAN ARAHMU"), Pal.YELLOW, -3f);
        col.addView(kicker, ui.lp(Ui.WRAP, Ui.WRAP, 2, 0, 0, 12));
        TextView title = ui.heavy("jurusanator", 40f, Pal.dark ? Pal.LIME : Pal.INK);
        ui.shadow(title, Pal.dark ? Pal.INK : Pal.LIME_HI, 3f);
        col.addView(title);
        TextView desc = ui.body(L.t("Answer a few questions, find majors that fit you, then get a plan to reach your dream university.",
                "Jawab beberapa pertanyaan, temukan jurusan yang cocok, lalu dapatkan rencana menuju kampus impianmu."),
                15f, Pal.bgText());
        col.addView(desc, ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 14));
        col.addView(artCluster());
        col.addView(ui.gap(14));
        LinearLayout chips = ui.hbox();
        chips.addView(ui.chip(L.t("12 QUESTIONS", "12 PERTANYAAN"), Pal.PAPER), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 6, 0));
        chips.addView(ui.chip(MajorData.MAJORS.size() + L.t(" MAJORS", " JURUSAN"), Pal.PINK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 6, 0));
        chips.addView(ui.chip(L.t("COACH INCLUDED", "ADA COACH"), Pal.SKY));
        col.addView(chips);
        col.addView(ui.gap(18));
        if (profile.hasDream()) col.addView(dreamCard());
        addPrimaryButtons(col);
        addPastResults(col);
        col.addView(ui.gap(18));
        BrutalButton replay = ui.small(L.t("REPLAY INTRO", "PUTAR ULANG INTRO"), Pal.PAPER, Pal.INK);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                a.showWelcome();
            }
        });
        col.addView(replay);
        TextView note = ui.body(L.t("Suggestions are a guide, not a decision. Always check official sources.",
                "Saran hanyalah panduan, bukan keputusan. Selalu cek sumber resmi."), 11f, muted());
        col.addView(note, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
        if (!a.probeChecked) {
            a.probeChecked = true;
            a.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!a.isGone() && a.modalOpen() == false) Forms.maybeProbe(a);
                }
            }, 900);
        }
        return page(11, col);
    }
    private View artCluster() {
        long today = Coach.dayIndex(System.currentTimeMillis());
        int[] bgs = {Pal.PINK, Pal.SKY, Pal.YELLOW, Pal.LILAC};
        int widthPx = getResources().getDisplayMetrics().widthPixels- ui.dp(36);
        int overlap = ui.dp(10);
        int size = Math.min(ui.dp(96), (widthPx + 3 * overlap) / 4);
        LinearLayout row = ui.hbox();
        row.setGravity(Gravity.CENTER_HORIZONTAL);
        for (int i = 0; i < 4; i++) {
            int art = (int) ((today * 7 + i * 17) % Art.COUNT);
            ArtView v = new ArtView(a, art, bgs[i]);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
            lp.leftMargin = i == 0 ? 0 : -overlap;
            v.setRotation(i % 2 == 0 ? -7f : 7f);
            row.addView(v, lp);
            ui.floatAnim(v, 4f, 1700 + i * 330, 3f);
        }
        return row;
    }
    private android.content.res.Resources getResources() {
        return a.getResources();
    }
    private void addPrimaryButtons(LinearLayout col) {
        if (a.quizInProgress()) {
            String text = L.t("CONTINUE", "LANJUTKAN") + "   " + a.rec.answeredCount() + "/" + a.rec.questionLimit();
            BrutalButton cont = ui.button(text, Pal.LIME_HI, Pal.INK, 58);
            cont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.showQuiz(false);
                }
            });
            col.addView(cont, ui.lp(Ui.MATCH, cont.getLayoutParams().height, 0, 0, 0, 8));
            BrutalButton fresh = ui.button(L.t("NEW QUIZ", "KUIS BARU"), Pal.PAPER, Pal.INK, 46);
            fresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.showQuiz(true);
                }
            });
            col.addView(fresh, ui.lp(Ui.MATCH, fresh.getLayoutParams().height, 0, 0, 0, 8));
        } else {
            BrutalButton start = ui.button(L.t("START THE QUIZ", "MULAI KUIS"), Pal.FOREST, Pal.LIME, 62);
            start.label.setTextSize(19f);
            start.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.showQuiz(true);
                }
            });
            col.addView(start, ui.lp(Ui.MATCH, start.getLayoutParams().height, 0, 0, 0, 8));
        }
        String mode = null;
        if (a.profile.strategy == Profile.STRAT_ENGAGE) {
            mode = L.t("NEXT QUIZ: SHORT MODE (8 questions, no \"don't know\")",
                    "KUIS BERIKUT: MODE SINGKAT (8 pertanyaan, tanpa \"tidak tahu\")");
        } else if (a.profile.strategy == Profile.STRAT_DEEP) {
            mode = L.t("NEXT QUIZ: DEEP MODE (16 questions + a duel)",
                    "KUIS BERIKUT: MODE MENDALAM (16 pertanyaan + duel)");
        }
        if (mode != null) {
            TextView tag = ui.tag(mode, Pal.LILAC, -1f);
            tag.setTextSize(10.5f);
            col.addView(tag, ui.lp(Ui.WRAP, Ui.WRAP, 2, 4, 0, 6));
        }
    }
    private View dreamCard() {
        Profile profile = a.profile;
        Univ u = Univ.byId(profile.dreamUniv);
        Major m = MajorData.MAJORS.get(profile.dreamMajor);
        LinearLayout card = ui.card(Pal.LIME, 14f);
        LinearLayout top = ui.hbox();
        ArtView art = new ArtView(a, m.art(), Pal.PINK);
        top.addView(art, new LinearLayout.LayoutParams(ui.dp(70), ui.dp(70)));
        LinearLayout info = ui.vbox();
        info.addView(ui.tag(L.t("YOUR DREAM", "IMPIANMU"), Pal.YELLOW, -2f));
        TextView name = ui.heavy(m.name(), 19f, Pal.INK);
        info.addView(name, ui.lp(Ui.WRAP, Ui.WRAP, 0, 8, 0, 2));
        info.addView(ui.body(u.name + " \u00B7 " + u.city, 13f, Pal.INK));
        top.addView(info, ui.lp(0, Ui.WRAP, 12, 0, 0, 0));
        ((LinearLayout.LayoutParams) info.getLayoutParams()).weight = 1f;
        card.addView(top);
        Coach.Fit fit = a.currentFit();
        if (fit != null) {
            card.addView(ui.mono(L.t("FIT POINTS ", "POIN KECOCOKAN ") + fit.total + "/100", 12f, Pal.INK),
                    ui.lp(Ui.WRAP, Ui.WRAP, 0, 12, 0, 0));
        }
        long now = System.currentTimeMillis();
        Plan.Ev next = Plan.next(profile, now);
        if (next != null) {
            int d = Plan.daysUntil(Plan.ymd(next, now, profile), now);
            card.addView(ui.body(L.t("Next: ", "Berikutnya: ") + next.title() + " \u00B7 "
                    + L.t("in " + d + " days (estimate)", d + " hari lagi (perkiraan)"), 12.5f, Pal.INK),
                    ui.lp(Ui.WRAP, Ui.WRAP, 0, 4, 0, 10));
        }
        BrutalButton open = ui.button(L.t("OPEN MY PLAN", "BUKA RENCANAKU"), Pal.FOREST, Pal.LIME, 48);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                a.showCoach(0);
            }
        });
        card.addView(open, ui.lp(Ui.MATCH, open.getLayoutParams().height, 0, 4, 0, 0));
        card.setLayoutParams(ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 14));
        return card;
    }
    private void addPastResults(LinearLayout col) {
        final List<History.Entry> list = a.store.history();
        if (list.isEmpty()) return;
        if (!a.profile.hasDream()) {
            BrutalButton pick = ui.button(L.t("PICK MY DREAM UNIVERSITY", "PILIH KAMPUS IMPIANKU"), Pal.YELLOW, Pal.INK, 48);
            pick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    MajorRecommender r = a.latestRec();
                    if (r != null) a.showResults(r, false);
                }
            });
            col.addView(pick, ui.lp(Ui.MATCH, pick.getLayoutParams().height, 0, 0, 0, 8));
        }
        LinearLayout head = ui.hbox();
        TextView label = ui.tag(L.t("PAST RESULTS", "HASIL SEBELUMNYA"), Pal.MINT, 1.5f);
        head.addView(label);
        View space = new View(a);
        head.addView(space, new LinearLayout.LayoutParams(0, 1, 1f));
        BrutalButton clear = ui.small(confirmClear ? L.t("SURE?", "YAKIN?") : L.t("CLEAR", "HAPUS"),
                confirmClear ? Pal.CORAL : Pal.PAPER, Pal.INK);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                if (confirmClear) {
                    a.store.clearHistory();
                    confirmClear = false;
                    a.rerender();
                } else {
                    confirmClear = true;
                    a.rerender();
                }
            }
        });
        head.addView(clear);
        col.addView(head, ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 8));
        int shown = 0;
        for (History.Entry entry : list) {
            if (shown >= 4) break;
            final MajorRecommender r = a.replay(entry);
            if (r == null) continue;
            List<MajorRecommender.Recommendation> recs = r.recommendations();
            if (recs.isEmpty()) continue;
            MajorRecommender.Recommendation top = recs.get(0);
            LinearLayout row = ui.card(Pal.PAPER, 10f);
            LinearLayout line = ui.hbox();
            line.addView(new ArtView(a, top.major.art(), Pal.LIME), new LinearLayout.LayoutParams(ui.dp(52), ui.dp(52)));
            LinearLayout info = ui.vbox();
            info.addView(ui.heavy(top.major.name(), 15f, Pal.INK));
            String date = DateFormat.format("d MMM yyyy", new Date(entry.time)).toString();
            info.addView(ui.mono(date + " \u00B7 " + top.matchPercent + "%", 11f, 0xFF5B6650),
                    ui.lp(Ui.WRAP, Ui.WRAP, 0, 3, 0, 0));
            line.addView(info, ui.weight(10, 0, 0, 0));
            row.addView(line);
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.showResults(r, false);
                }
            });
            col.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 10));
            shown++;
        }
    }
}


