package jurusanator;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import jurusanator.MajorData.Major;
import jurusanator.MajorRecommender.Recommendation;
/** Pop-up window forms (all built from the same neo-brutalist window gadget). */
final class Forms {
    private Forms() {}
    private static final long PROBE_GAP = 20L * 3600L * 1000L;
    // ------------------------------------------------------------------helpers
    /** CANCEL + OK buttons side by side. ok runs only if non-null. */
    private static void footer(final MainActivity a, Ui.Window w, String okText, final Runnable ok, boolean cancel) {
        Ui ui = a.ui;
        LinearLayout row = ui.hbox();
        if (cancel) {
            BrutalButton no = ui.button(L.t("CANCEL", "BATAL"), Pal.PAPER, Pal.INK, 44);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.back();
                    a.closeModal();
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, no.getLayoutParams().height, 1f);
            lp.rightMargin = ui.dp(8);
            row.addView(no, lp);
        }
        if (ok != null) {
            BrutalButton yes = ui.button(okText, Pal.FOREST, Pal.LIME, 44);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.ding();
                    ok.run();
                }
            });
            row.addView(yes, new LinearLayout.LayoutParams(0, yes.getLayoutParams().height, 1f));
        }
        w.body.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
    }
    static String[] skillLabels() {
        String[] labels = new String[Skills.COUNT];
        for (int i = 0; i < labels.length; i++) labels[i] = Skills.code(i) + "\n" + Skills.name(i);
        return labels;
    }
    private static TextView note(MainActivity a, String text) {
        return a.ui.body(text, 12.5f, 0xFF4A5640);
    }
    // ---------------------------------------------------------------tie-break duel
    static void duel(final MainActivity a, Recommendation first, Recommendation second) {
        Ui ui = a.ui;
        Ui.Window w = ui.window(L.t("DUEL", "DUEL"), Pal.PAPER);
        w.body.addView(ui.heavy(L.t("These two are very close. Which one excites you more?",
                "Dua jurusan ini hampir sama. Mana yang lebih membuatmu bersemangat?"), 18f, Pal.INK));
        LinearLayout row = ui.hbox();
        row.setGravity(Gravity.TOP);
        row.addView(duelCard(a, first), ui.weight(0, 0, 6, 0));
        row.addView(duelCard(a, second), ui.weight(6, 0, 0, 0));
        w.body.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 14, 0, 0));
        a.showModal(w, false);
    }
    private static View duelCard(final MainActivity a, final Recommendation rec) {
        Ui ui = a.ui;
        LinearLayout card = ui.card(Pal.LIME, 8f);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.addView(new ArtView(a, rec.major.art(), Pal.PINK), new LinearLayout.LayoutParams(ui.dp(80), ui.dp(80)));
        TextView name = ui.heavy(rec.major.name(), 14f, Pal.INK);
        name.setGravity(Gravity.CENTER);
        card.addView(name, ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        card.setClickable(true);
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.ding();
                a.closeModal();
                a.finishQuiz(rec.majorIndex);
            }
        });
        return card;
    }
    // ------------------------------------------------------ random "quick check" probe
    /** Sometimes asks about a random major to see whether the user answers consistently. */
    static void maybeProbe(MainActivity a) {
        Profile p = a.profile;
        if (!p.hasDream() || a.latestRec() == null) return;
        if (System.currentTimeMillis() - p.lastProbe < PROBE_GAP) return;
        if (a.random.nextInt(100) >= 70) return;
        probe(a);
    }
    static void probe(final MainActivity a) {
        MajorRecommender latest = a.latestRec();
        if (latest == null) {
            a.flash(L.t("Finish a quiz first.", "Selesaikan kuis dulu."));
            return;
        }
        final Ui ui = a.ui;
        final double[] profile = latest.interestProfile();
        final int index = Coach.pickProbeMajor(a.random, a.profile, profile);
        Major m = MajorData.MAJORS.get(index);
        Ui.Window w = ui.window(L.t("QUICK CHECK", "CEK CEPAT"), Pal.PAPER);
        w.body.setGravity(Gravity.CENTER_HORIZONTAL);
        w.body.addView(new ArtView(a, m.art(), Pal.YELLOW), new LinearLayout.LayoutParams(ui.dp(92), ui.dp(92)));
        TextView q = ui.heavy(L.t("How interested would you be in studying " + m.name() + "?",
                "Seberapa tertarik kamu belajar " + m.name() + "?"), 18f, Pal.INK);
        q.setGravity(Gravity.CENTER);
        w.body.addView(q, ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 4));
        w.body.addView(note(a, L.t("A random check. Answer honestly, it helps me tune the quiz for you.",
                "Pertanyaan acak. Jawab jujur, ini membantuku menyesuaikan kuis untukmu.")),
                ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 10));
        final long shown = SystemClock.elapsedRealtime();
        for (int i = 0; i < 5; i++) {
            final int answer = i;
            BrutalButton b = new BrutalButton(a, MajorData.answerLabel(i), Pal.ANSWER[i], Pal.INK, 14f,
                    ui.radius * 0.7f, ui.border * 0.8f, ui.shadow * 0.7f);
            b.setLayoutParams(ui.lp(Ui.MATCH, ui.dp(42) + Math.round(ui.shadow * 0.7f), 0, 0, 0, 4));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    answerProbe(a, index, answer, SystemClock.elapsedRealtime() - shown, profile);
                }
            });
            w.body.addView(b);
        }
        a.showModal(w, true);
    }
    private static void answerProbe(MainActivity a, int major, int answer, long ms, double[] profile) {
        Profile p = a.profile;
        long now = System.currentTimeMillis();
        int before = p.strategy;
        p.addProbe(new Profile.Probe(now, major, answer, (int) Math.min(ms, 60000L)));
        p.lastProbe = now;
        int score = Coach.assess(p, profile, now);
        if (p.probes.size() >= 3 && score >= 0) p.strategy = Coach.pickStrategy(score);
        a.saveProfile();
        a.sfx.pick(answer);
        a.closeModal();
        if (p.strategy != before) {
            a.flash(L.t("Thanks! I changed how the next quiz works. See PROGRESS.",
                    "Terima kasih! Aku mengubah cara kerja kuis berikutnya. Lihat KEMAJUAN."));
        } else {
            a.flash(L.t("Noted, thanks!", "Tercatat, terima kasih!"));
        }
        a.rerender();
    }
    // ----------------------------------------------------------------skill rating
    static void rateSkills(final MainActivity a) {
        Ui ui = a.ui;
        final Profile p = a.profile;
        Ui.Window w = ui.window(L.t("RATE YOUR SKILLS", "NILAI KEMAMPUANMU"), Pal.PAPER);
        w.body.addView(note(a, L.t("How good are you at each UTBK sub-test right now? 0 = no idea, 100 = excellent. Be honest, this is only for you.",
                "Seberapa kuat kamu di tiap subtes UTBK sekarang? 0 = belum tahu, 100 = sangat baik. Jujur ya, ini hanya untukmu.")));
        final int[] values = p.skill.clone();
        for (int i = 0; i < Skills.COUNT; i++) {
            final int skill = i;
            w.body.addView(ui.stepper(Skills.code(i) + "    " + Skills.name(i), 0, 100, 10, values[i], "",
                    new Ui.IntCb() {
                @Override
                public void on(int value) {
                    values[skill] = value;
                }
            }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 0));
        }
        footer(a, w, L.t("SAVE", "SIMPAN"), new Runnable() {
            @Override
            public void run() {
                System.arraycopy(values, 0, p.skill, 0, Skills.COUNT);
                p.rated = true;
                a.saveProfile();
                a.closeModal();
                a.rerender();
            }
        }, true);
        a.showModal(w, true);
    }
    // -------------------------------------------------------------------logging
    static void logMock(final MainActivity a) {
        log(a, true);
    }
    static void logStudy(final MainActivity a) {
        log(a, false);
    }
    private static void log(final MainActivity a, final boolean mock) {
        Ui ui = a.ui;
        Ui.Window w = ui.window(mock ? L.t("LOG A MOCK TEST", "CATAT TRY OUT")
                : L.t("LOG STUDY TIME", "CATAT WAKTU BELAJAR"), Pal.PAPER);
        w.body.addView(note(a, mock
                ? L.t("Which sub-test did you practice, and what was your score (0-100)?",
                "Subtes apa yang kamu latih, dan berapa skormu (0-100)?")
                : L.t("Which sub-test did you study, and for how long?",
                "Subtes apa yang kamu pelajari, dan berapa lama?")));
        final int[] skill = {0};
        final int[] value = {mock ? 60 : 45};
        w.body.addView(ui.choice(skillLabels(), 0, 2, new Ui.IntCb() {
            @Override
            public void on(int v) {
                skill[0] = v;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 6));
        w.body.addView(mock
                ? ui.stepper(L.t("Score", "Skor"), 0, 100, 5, 60, "", new Ui.IntCb() {
            @Override
            public void on(int v) {
                value[0] = v;
            }
        })
                : ui.stepper(L.t("Minutes", "Menit"), 15, 240, 15, 45, " min", new Ui.IntCb() {
            @Override
            public void on(int v) {
                value[0] = v;
            }
        }));
        footer(a, w, L.t("SAVE", "SIMPAN"), new Runnable() {
            @Override
            public void run() {
                a.profile.addLog(new Profile.Log(System.currentTimeMillis(),
                        mock ? Profile.LOG_MOCK : Profile.LOG_STUDY, skill[0], value[0]));
                a.saveProfile();
                a.closeModal();
                a.flash(L.t("Saved. Keep going!", "Tersimpan. Terus semangat!"));
                a.rerender();
            }
        }, true);
        a.showModal(w, true);
    }
    // ------------------------------------------------------------------edit date
    static void editDate(final MainActivity a, final Plan.Ev ev) {
        Ui ui = a.ui;
        long now = System.currentTimeMillis();
        int ymd = Plan.ymd(ev, now, a.profile);
        Ui.Window w = ui.window(L.t("EDIT DATE", "UBAH TANGGAL"), Pal.PAPER);
        w.body.addView(ui.heavy(ev.title(), 16f, Pal.INK));
        w.body.addView(note(a, L.t("Use the official date once it is announced.",
                "Pakai tanggal resmi setelah diumumkan.")), ui.lp(Ui.MATCH, Ui.WRAP, 0, 4, 0, 6));
        final int[] part = {Plan.day(ymd), Plan.month(ymd), Plan.year(ymd)};
        w.body.addView(ui.stepper(L.t("Day", "Tanggal"), 1, 31, 1, part[0], "", new Ui.IntCb() {
            @Override
            public void on(int v) {
                part[0] = v;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 6, 0, 0));
        w.body.addView(ui.stepper(L.t("Month", "Bulan"), 1, 12, 1, part[1], "", new Ui.IntCb() {
            @Override
            public void on(int v) {
                part[1] = v;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 6, 0, 0));
        w.body.addView(ui.stepper(L.t("Year", "Tahun"), 2025, 2035, 1, part[2], "", new Ui.IntCb() {
            @Override
            public void on(int v) {
                part[2] = v;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 6, 0, 0));
        footer(a, w, L.t("SAVE", "SIMPAN"), new Runnable() {
            @Override
            public void run() {
                int day = Math.min(part[0], Plan.daysInMonth(part[2], part[1]));
                a.profile.dates.put(ev.key, Plan.makeYmd(part[2], part[1], day));
                a.profile.calendar.remove(ev.key);
                a.saveProfile();
                a.closeModal();
                a.rerender();
            }
        }, true);
        a.showModal(w, true);
    }
    // --------------------------------------------------------------------outcome
    static void outcome(final MainActivity a) {
        final Ui ui = a.ui;
        Univ univ = Univ.byId(a.profile.dreamUniv);
        if (univ == null) return;
        Ui.Window w = ui.window(L.t("RESULT DAY", "HARI PENGUMUMAN"), Pal.PAPER);
        w.body.addView(ui.heavy(L.t("Did you get into " + univ.shortName + "?",
                "Apakah kamu diterima di " + univ.shortName + "?"), 18f, Pal.INK));
        w.body.addView(note(a, L.t("Answer honestly. I use this to improve my advice, nothing is shared.",
                "Jawab jujur. Ini dipakai untuk memperbaiki saranku, tidak ada yang dibagikan.")),
                ui.lp(Ui.MATCH, Ui.WRAP, 0, 4, 0, 8));
        final int[] v = {1, a.profile.route, 6, 5, 60, 1, 0};   // got(0=yes,1=no), route, hours, mocks, adherence, applied, hardest
        w.body.addView(ui.choice(new String[]{L.t("YES, I GOT IN", "YA, DITERIMA"), L.t("NOT THIS TIME", "BELUM BERHASIL")},
                1, 2, new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[0] = x;
            }
        }));
        w.body.addView(ui.mono(L.t("ROUTE YOU USED", "JALUR YANG KAMU PAKAI"), 11f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 10, 0, 4));
        w.body.addView(ui.choice(new String[]{"SNBP", "SNBT", "MANDIRI"}, a.profile.route, 3, new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[1] = x;
            }
        }));
        w.body.addView(ui.stepper(L.t("Study hours per week", "Jam belajar per minggu"), 0, 40, 1, 6, " h", new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[2] = x;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 0));
        w.body.addView(ui.stepper(L.t("Mock tests done", "Try out yang dikerjakan"), 0, 60, 1, 5, "", new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[3] = x;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        w.body.addView(ui.stepper(L.t("Followed my plan", "Mengikuti rencana"), 0, 100, 10, 60, "%", new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[4] = x;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        w.body.addView(ui.stepper(L.t("Universities applied to", "Kampus yang didaftar"), 1, 5, 1, 1, "", new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[5] = x;
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        w.body.addView(ui.mono(L.t("HARDEST PART", "BAGIAN PALING SULIT"), 11f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 10, 0, 4));
        w.body.addView(ui.choice(skillLabels(), 0, 2, new Ui.IntCb() {
            @Override
            public void on(int x) {
                v[6] = x;
            }
        }));
        footer(a, w, L.t("SAVE", "SIMPAN"), new Runnable() {
            @Override
            public void run() {
                saveOutcome(a, v);
            }
        }, true);
        a.showModal(w, true);
    }
    private static void saveOutcome(MainActivity a, int[] v) {
        Profile p = a.profile;
        boolean got = v[0] == 0;
        Coach.Fit fit = a.currentFit();
        p.outcome = new Profile.Outcome(System.currentTimeMillis(),got, v[1], v[2], v[3], v[4], v[5], v[6]);
        if (fit != null) Coach.learn(p, fit, got);
        a.saveProfile();
        a.closeModal();
        if (got) {
            a.sfx.win();
            a.flash(L.t("Congratulations!", "Selamat!"));
        } else {
            a.flash(L.t("Thanks for being honest. Let's learn from it.", "Terima kasih sudah jujur. Mari belajar darinya."));
        }
        a.rerender();
    }
}


