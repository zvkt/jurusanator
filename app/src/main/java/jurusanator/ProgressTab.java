package jurusanator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
/** PROGRESS: your development, the commitment check, and what happened on result day. */
final class ProgressTab extends CoachScreen.Tab {
    ProgressTab(CoachScreen host) {
        super(host);
    }
    @Override
    void fill(LinearLayout col) {
        development(col);
        commitment(col);
        outcome(col);
    }
    // ------------------------------------------------------------development
    private void development(LinearLayout col) {
        LinearLayout box = box(Pal.LIME, L.t("YOUR DEVELOPMENT", "PERKEMBANGANMU"), Pal.YELLOW);
        int streak = Coach.streak(p, now);
        int week = Coach.weeklyMinutes(p, now, 1)[0];
        LinearLayout stats = ui.hbox();
        stats.addView(stat(String.valueOf(streak), L.t("DAY STREAK", "HARI BERUNTUN")), ui.weight(0, 0, 6, 0));
        stats.addView(stat(String.valueOf(week), L.t("MIN THIS WEEK", "MENIT MINGGU INI")), ui.weight(6, 0, 0, 0));
        box.addView(stats, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
        List<float[]> series = Coach.mockSeries(p);
        box.addView(ui.tag(L.t("MOCK TEST SCORES", "SKOR TRY OUT"), Pal.PAPER, -1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 14, 0, 0));
        if (series.isEmpty()) {
            text(box, L.t("No mock tests yet. Log one to see your trend.", "Belum ada try out. Catat satu untuk melihat tren."), 13f, 8);
        } else {
            float[] values = new float[series.size()];
            for (int i = 0; i < values.length; i++) values[i] = series.get(i)[1];
            ChartView line = new ChartView(a, false, Pal.FOREST, ui.border * 0.8f, ui.dp(8));
            line.setValues(values, 100f);
            box.addView(line, ui.lp(Ui.MATCH, ui.dp(120), 0, 8, 0, 0));
            if (values.length >= 2) {
                int first = Math.round(values[0]);
                int last = Math.round(values[values.length - 1]);
                mono(box, L.t("First " + first + " \u2192 latest " + last + " (" + (last >= first ? "+" : "") + (last - first) + ")",
                        "Pertama " + first + " \u2192 terbaru " + last + " (" + (last >= first ? "+" : "") + (last - first) + ")"), 11f, 6);
            }
        }
        int[] weekly = Coach.weeklyMinutes(p, now, 6);
        float[] bars = new float[weekly.length];
        float max = 60f;
        for (int i = 0; i < bars.length; i++) {
            bars[i] = weekly[i];
            max = Math.max(max, bars[i]);
        }
        box.addView(ui.tag(L.t("STUDY MINUTES PER WEEK", "MENIT BELAJAR PER MINGGU"), Pal.PAPER, 1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 14, 0, 0));
        ChartView chart = new ChartView(a, true, Pal.PINK, ui.border * 0.8f, ui.dp(8));
        chart.setValues(bars, max);
        box.addView(chart, ui.lp(Ui.MATCH, ui.dp(100), 0, 8, 0, 0));
        mono(box, L.t("Oldest week on the left, this week on the right.", "Minggu terlama di kiri, minggu ini di kanan."), 10.5f, 6);
        LinearLayout row = ui.hbox();
        row.addView(smallAction(L.t("LOG MOCK TEST", "CATAT TRY OUT"), Pal.YELLOW, new Runnable() {
            @Override
            public void run() {
                Forms.logMock(a);
            }
        }), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 8, 0));
        row.addView(smallAction(L.t("LOG STUDY", "CATAT BELAJAR"), Pal.PAPER, new Runnable() {
            @Override
            public void run() {
                Forms.logStudy(a);
            }
        }));
        box.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 14, 0, 0));
        col.addView(box);
    }
    private View stat(String number, String label) {
        LinearLayout s = ui.card(Pal.PAPER, 8f);
        s.addView(ui.heavy(number, 30f, Pal.INK));
        s.addView(ui.mono(label, 9.5f, 0xFF4A5640), ui.lp(Ui.WRAP, Ui.WRAP, 0, 2, 0, 0));
        return s;
    }
    // -------------------------------------------------------------commitment
    private void commitment(LinearLayout col) {
        LinearLayout box = box(Pal.PAPER, L.t("COMMITMENT CHECK", "CEK KOMITMEN"), Pal.LILAC);
        text(box, L.t("I watch how you answer, and sometimes ask about a random major, to see how serious and consistent you are. If something looks off I change how the quiz works.",
                "Aku memperhatikan caramu menjawab, dan kadang bertanya tentang jurusan acak, untuk melihat seberapa serius dan konsisten kamu. Jika ada yang janggal, aku mengubah cara kerja kuis."), 12.5f, 10);
        if (p.seriousness < 0) {
            text(box, L.t("Not enough data yet. Finish a quiz and answer a few quick checks.",
                    "Belum cukup data. Selesaikan kuis dan jawab beberapa cek cepat."), 13.5f, 12);
        } else {
            barRow(box, L.t("FOCUS SCORE", "SKOR FOKUS"), p.seriousness >= 75 ? Pal.LIME_HI : p.seriousness >= 50 ? Pal.YELLOW : Pal.CORAL,
                    p.seriousness / 100f, p.seriousness + "/100", 100);
        }
        int checks = p.probes.size();
        mono(box, L.t("QUICK CHECKS ANSWERED: ", "CEK CEPAT DIJAWAB: ") + checks, 10.5f, 10);
        boolean any = false;
        String[][] flagText = {
            {L.t("Your answers were very fast.", "Jawabanmu sangat cepat.")},
                    {L.t("Many answers were identical.", "Banyak jawaban yang sama.")},
                    {L.t("Many \"don't know\" answers.", "Banyak jawaban \"tidak tahu\".")},
                    {L.t("Similar questions got opposite answers.", "Pertanyaan mirip dijawab berlawanan.")},
                    {L.t("Answers about random majors did not match your profile.", "Jawaban jurusan acak tidak cocok dengan profilmu.")},
                    {L.t("No activity for a while.", "Lama tidak ada aktivitas.")}};
        for (int i = 0; i < flagText.length; i++) {
            if ((p.flags & (1 << i)) != 0) {
                if (!any) {
                    box.addView(ui.tag(L.t("WHAT I NOTICED", "YANG KUAMATI"), Pal.CORAL, -1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 12, 0, 0));
                    any = true;
                }
                text(box, "\u2022 " + flagText[i][0], 13f, 6);
            }
        }
        box.addView(ui.tag(L.t("MY STRATEGY NOW", "STRATEGIKU SEKARANG"), Pal.YELLOW, 1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 14, 0, 0));
        String strategy;
        switch (p.strategy) {
            case Profile.STRAT_ENGAGE:
            strategy = L.t("SHORT MODE. The quiz may have felt slow or unclear, or answers were rushed. Next quiz: 8 questions, no \"don't know\", so each answer must count.",
                    "MODE SINGKAT. Kuis mungkin terasa lambat atau kurang jelas, atau jawaban terburu-buru. Kuis berikutnya: 8 pertanyaan, tanpa \"tidak tahu\", jadi tiap jawaban harus bermakna.");
            break;
            case Profile.STRAT_DEEP:
            strategy = L.t("DEEP MODE. You look focused, so I am going deeper: up to 24 questions and a head-to-head duel when two majors are close. The quiz may finish early if one major clearly leads.",
                    "MODE MENDALAM. Kamu terlihat fokus, jadi aku masuk lebih dalam: maksimal 24 pertanyaan dan duel saat dua jurusan hampir sama. Kuis bisa selesai lebih awal jika satu jurusan unggul jelas.");
            break;
            default:
            strategy = L.t("STANDARD MODE. Up to 24 questions; the quiz may finish early if one major clearly leads.",
                    "MODE STANDAR. Maksimal 24 pertanyaan; kuis bisa selesai lebih awal jika satu jurusan unggul jelas.");
            break;
        }
        text(box, strategy, 13.5f, 6);
        action(box, L.t("QUICK CHECK NOW", "CEK CEPAT SEKARANG"), Pal.LIME_HI, Pal.INK, new Runnable() {
            @Override
            public void run() {
                Forms.probe(a);
            }
        });
        col.addView(box);
    }
    // ----------------------------------------------------------------outcome
    private void outcome(LinearLayout col) {
        LinearLayout box = box(Pal.YELLOW, L.t("RESULT DAY", "HARI PENGUMUMAN"), Pal.PAPER);
        Profile.Outcome o = p.outcome;
        if (o == null) {
            text(box, L.t("When results are out, tell me if you got into " + univ.shortName + ". If yes, I keep what works. If not, I look for why and fix my own formula.",
                    "Saat hasil keluar, beri tahu aku apakah kamu diterima di " + univ.shortName + ". Jika ya, aku pertahankan yang berhasil. Jika belum, aku cari sebabnya dan memperbaiki rumusku sendiri."), 14f, 10);
        } else if (o.got) {
            box.addView(ui.heavy(L.t("YOU GOT IN!", "KAMU DITERIMA!"), 26f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 12, 0, 0));
            text(box, L.t("Congratulations! I keep the algorithm and only nudge its weights a little.",
                    "Selamat! Aku mempertahankan algoritma dan hanya menggeser bobotnya sedikit."), 14f, 6);
        } else {
            reasons(box);
        }
        changes(box);
        action(box, o == null ? L.t("RESULTS ARE OUT", "HASIL SUDAH KELUAR") : L.t("RECORD A NEW RESULT", "CATAT HASIL BARU"),
                Pal.FOREST, Pal.LIME, new Runnable() {
            @Override
            public void run() {
                Forms.outcome(a);
            }
        });
        col.addView(box);
    }
    private void reasons(LinearLayout box) {
        Coach.Fit fit = Coach.fit(a.latestRec() == null ? 50 : a.latestRec().matchPercentFor(p.dreamMajor), univ, field, p, now);
        Peers.Source src = Peers.source();
        Peers.Benchmark bench = src.accepted(univ, p.dreamMajor);
        List<Coach.Reason> list = Coach.diagnose(p, univ, field, bench, fit, now);
        box.addView(ui.heavy(L.t("LET'S FIND WHY", "MARI CARI SEBABNYA"), 22f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 12, 0, 0));
        text(box, L.t("These are my best guesses from the data you gave me, strongest first. They are patterns to work on, not a verdict on you.",
                "Ini dugaan terbaikku dari data yang kamu beri, dari yang terkuat. Ini pola untuk diperbaiki, bukan vonis untukmu."), 12.5f, 6);
        if (list.isEmpty()) {
            text(box, L.t("I could not find a clear weak spot. Sometimes the competition is simply tough. Consider backup options.",
                    "Aku tidak menemukan titik lemah yang jelas. Kadang persaingannya memang berat. Pertimbangkan pilihan cadangan."), 13.5f, 10);
        }
        for (Coach.Reason r : list) {
            TextView t = text(box, "\u2022 " + reasonText(r), 13.5f, 10);
            t.setLineSpacing(0f, 1.1f);
            BarView bar = new BarView(a, r.weight >= 60 ? Pal.CORAL : Pal.ORANGE, ui.border * 0.7f, ui.dp(4));
            box.addView(bar, ui.lp(Ui.MATCH, ui.dp(12), 0, 4, 0, 0));
            bar.animateTo(r.weight / 100f, 200);
        }
        box.addView(ui.tag(src.isReal() ? L.t("COMPARED WITH ACCEPTED STUDENTS", "DIBANDINGKAN DENGAN YANG DITERIMA")
                : L.t("BENCHMARK \u00B7 DEMO", "PATOKAN \u00B7 DEMO"), Pal.PAPER, -1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 14, 0, 0));
        text(box, L.t("Accepted students for " + major.name() + " at " + univ.shortName + " (assumed): readiness about " + bench.skill
                + "%, about " + bench.hours + " study hours a week, about " + bench.mocks + " mock tests.",
                "Siswa diterima untuk " + major.name() + " di " + univ.shortName + " (asumsi): kesiapan sekitar " + bench.skill
                + "%, sekitar " + bench.hours + " jam belajar per minggu, sekitar " + bench.mocks + " try out."), 12.5f, 6);
        if (!src.isReal()) {
            text(box, L.t("These numbers are assumptions, not real data. Comparing with real accepted students of the same major and university needs shared data from other users (see the README).",
                    "Angka ini asumsi, bukan data nyata. Membandingkan dengan siswa yang benar-benar diterima di jurusan dan kampus yang sama butuh data bersama dari pengguna lain (lihat README)."), 11f, 6);
        }
    }
    private String reasonText(Coach.Reason r) {
        switch (r.code) {
            case Coach.R_READY:
            return L.t("Readiness was " + r.a + "%. I would like " + r.b + "% or more.", "Kesiapan " + r.a + "%. Aku ingin " + r.b + "% atau lebih.");
            case Coach.R_WEAK:
            return L.t("Weakest area: " + Skills.name(r.skill) + " at " + r.a + ", about " + r.b + " needed.",
                    "Area terlemah: " + Skills.name(r.skill) + " di " + r.a + ", butuh sekitar " + r.b + ".");
            case Coach.R_HOURS:
            return L.t("You studied " + r.a + " h/week; the benchmark is about " + r.b + ".", "Kamu belajar " + r.a + " jam/minggu; patokannya sekitar " + r.b + ".");
            case Coach.R_MOCKS:
            return L.t("You did " + r.a + " mock tests; the benchmark is about " + r.b + ".", "Kamu mengerjakan " + r.a + " try out; patokannya sekitar " + r.b + ".");
            case Coach.R_ADHERE:
            return L.t("You followed the plan " + r.a + "% of the time.", "Kamu mengikuti rencana " + r.a + "% dari waktu.");
            case Coach.R_TREND:
            return L.t("Your mock scores did not rise (" + r.a + " \u2192 " + r.b + ").", "Skor try outmu tidak naik (" + r.a + " \u2192 " + r.b + ").");
            case Coach.R_SINGLE:
            return L.t("You applied to only " + r.a + " university. A backup choice lowers the risk.", "Kamu hanya mendaftar ke " + r.a + " kampus. Pilihan cadangan menurunkan risiko.");
            case Coach.R_SERIOUS:
            return L.t("Your focus score was " + r.a + "/100.", "Skor fokusmu " + r.a + "/100.");
            case Coach.R_COMPETE:
            return L.t("A very competitive choice while your fit was " + r.a + "/100.", "Pilihan yang sangat ketat sementara kecocokanmu " + r.a + "/100.");
            default:
            return L.t("Readiness " + r.a + "% against about " + r.b + "% for accepted students.", "Kesiapan " + r.a + "% dibanding sekitar " + r.b + "% untuk yang diterima.");
        }
    }
    private void changes(LinearLayout box) {
        if (p.changes.isEmpty()) return;
        box.addView(ui.tag(L.t("WHAT I CHANGED", "YANG KUUBAH"), Pal.LIME_HI, 1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 14, 0, 0));
        for (String[] c : p.changes) {
            text(box, "\u2022 " + (L.id ? c[1] : c[0]), 13f, 6);
        }
    }
}

