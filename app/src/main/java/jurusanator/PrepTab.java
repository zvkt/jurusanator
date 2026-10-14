package jurusanator;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import java.util.List;
/** PREP: entrance route, dated timeline + calendar, what to study, best time to study. */
final class PrepTab extends CoachScreen.Tab {
    PrepTab(CoachScreen host) {
        super(host);
    }
    @Override
    void fill(LinearLayout col) {
        route(col);
        timeline(col);
        learn(col);
        schedule(col);
    }
    // ------------------------------------------------------------------route
    private String routeText(int route) {
        switch (route) {
            case Profile.ROUTE_SNBP:
            return L.t("SNBP is merit-based. It uses your school report card and your TKA score, and only eligible students can apply (school quota). If you pass SNBP you cannot join SNBT or Mandiri.",
                    "SNBP berbasis prestasi. Memakai nilai rapor dan skor TKA, dan hanya siswa eligible yang bisa daftar (kuota sekolah). Jika lolos SNBP, kamu tidak bisa ikut SNBT atau Mandiri.");
            case Profile.ROUTE_SNBT:
            return L.t("SNBT is test-based: the UTBK on a computer. It has 7 sub-tests: General Reasoning, General Knowledge, Reading & Writing, Quantitative Knowledge, Indonesian Literacy, English Literacy and Math Reasoning. The UTBK fee was Rp200,000 in 2026.",
                    "SNBT berbasis tes: UTBK di komputer. Ada 7 subtes: Penalaran Umum, Pengetahuan & Pemahaman Umum, Pemahaman Bacaan & Menulis, Pengetahuan Kuantitatif, Literasi Bahasa Indonesia, Literasi Bahasa Inggris dan Penalaran Matematika. Biaya UTBK 2026 sebesar Rp200.000.");
            default:
            return L.t("Mandiri is run by each university with its own tests, dates and fees. Many also use your UTBK certificate. Check the university's website.",
                    "Mandiri diselenggarakan tiap kampus dengan tes, jadwal dan biaya sendiri. Banyak yang juga memakai sertifikat UTBK. Cek situs kampusnya.");
        }
    }
    private void route(LinearLayout col) {
        LinearLayout box = box(Pal.PAPER, L.t("ENTRANCE ROUTE", "JALUR MASUK"), Pal.YELLOW);
        box.addView(ui.choice(new String[]{"SNBP", "SNBT", "MANDIRI"}, p.route, 3, new Ui.IntCb() {
            @Override
            public void on(int value) {
                if (value != p.route) {
                    p.route = value;
                    a.saveProfile();
                    a.rerender();
                }
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
        text(box, routeText(p.route), 13.5f, 10);
        text(box, L.t("Dates in this app are estimates until SNPMB announces the official schedule (snpmb.id). The official 2027 schedule had not been published when this app was written.",
                "Tanggal di aplikasi ini hanya perkiraan sampai SNPMB mengumumkan jadwal resmi (snpmb.id). Jadwal resmi 2027 belum terbit saat aplikasi ini dibuat."), 11f, 10);
        col.addView(box);
    }
    // ---------------------------------------------------------------timeline
    private void timeline(LinearLayout col) {
        LinearLayout box = box(Pal.LIME, L.t("TIMELINE", "LINIMASA"), Pal.PAPER);
        for (final Plan.Ev e : Plan.forRoute(p.route)) {
            final int ymd = Plan.ymd(e, now, p);
            int days = Plan.daysUntil(ymd, now);
            View rule = new View(a);
            rule.setBackgroundColor(0x33111111);
            box.addView(rule, ui.lp(Ui.MATCH, ui.dp(1.5f), 0, 12, 0, 0));
            box.addView(ui.heavy(e.title(), 15f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 10, 0, 0));
            mono(box, MainActivity.dateText(ymd), 11.5f, 4);
            LinearLayout chips = ui.hbox();
            chips.addView(ui.chip(Plan.isEdited(e, p) ? L.t("EDITED", "DIUBAH") : L.t("ESTIMATE", "PERKIRAAN"),
                    Plan.isEdited(e, p) ? Pal.MINT : Pal.YELLOW), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 6, 0));
            chips.addView(ui.chip(days >= 0 ? L.t("IN " + days + " DAYS", days + " HARI LAGI") : L.t("PASSED", "LEWAT"),
                    days >= 0 ? Pal.SKY : Pal.GRAY), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 6, 0));
            if (p.calendar.contains(e.key)) chips.addView(ui.chip(L.t("IN CALENDAR", "DI KALENDER"), Pal.PINK));
            box.addView(chips, ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
            text(box, e.desc(), 12f, 8);
            LinearLayout buttons = ui.hbox();
            buttons.addView(smallAction(L.t("EDIT DATE", "UBAH TANGGAL"), Pal.PAPER, new Runnable() {
                @Override
                public void run() {
                    Forms.editDate(a, e);
                }
            }), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 8, 0));
            buttons.addView(smallAction(L.t("+ CALENDAR", "+ KALENDER"), Pal.YELLOW, new Runnable() {
                @Override
                public void run() {
                    long start = Plan.utcMillis(ymd);
                    launch(CalendarHelper.insertIntent("Jurusanator:" + e.title(), e.desc(), start, start + 86400000L, true));
                }
            }));
            box.addView(buttons, ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 0));
        }
        action(box, L.t("ADD ALL TO CALENDAR + REMINDERS", "TAMBAH SEMUA KE KALENDER + PENGINGAT"), Pal.FOREST, Pal.LIME, new Runnable() {
            @Override
            public void run() {
                a.withCalendar(new Runnable() {
                    @Override
                    public void run() {
                        addAllEvents();
                    }
                });
            }
        }).label.setTextSize(12f);
        text(box, L.t("Each event gets a reminder 1 week and 1 day before.",
                "Tiap acara mendapat pengingat 1 minggu dan 1 hari sebelumnya."), 11f, 8);
        col.addView(box);
    }
    private void launch(Intent intent) {
        try {
            a.startActivity(intent);
        } catch (RuntimeException e) {
            a.flash(L.t("No calendar app found.", "Aplikasi kalender tidak ditemukan."));
        }
    }
    private void addAllEvents() {
        long cal = CalendarHelper.writableCalendar(a);
        if (cal < 0) {
            a.flash(L.t("No writable calendar found on this phone.", "Tidak ada kalender yang bisa ditulis di ponsel ini."));
            return;
        }
        int added = 0;
        for (Plan.Ev e : Plan.forRoute(p.route)) {
            int ymd = Plan.ymd(e, now, p);
            if (Plan.daysUntil(ymd, now) < 0 || p.calendar.contains(e.key)) continue;
            String title = "Jurusanator: " + e.title() + (Plan.isEdited(e, p) ? "" : L.t(" (estimate)", " (perkiraan)"));
            if (CalendarHelper.insertAllDay(a, cal, title, e.desc() + " \u2014 " + univ.shortName, ymd,
                    new int[]{10080, 1440})) {
                p.calendar.add(e.key);
                added++;
            }
        }
        a.saveProfile();
        a.flash(added > 0 ? L.t("Added " + added + " events with reminders.", added + " acara ditambahkan beserta pengingat.")
                : L.t("Nothing new to add.", "Tidak ada yang baru untuk ditambahkan."));
        a.rerender();
    }
    // ------------------------------------------------------------what to study
    private void learn(LinearLayout col) {
        LinearLayout box = box(Pal.PAPER, L.t("WHAT TO STUDY", "APA YANG DIPELAJARI"), Pal.PINK);
        if (p.route == Profile.ROUTE_SNBP) {
            text(box, L.t("SNBP uses your report card and TKA. These UTBK skills still help for TKA and as a backup plan.",
                    "SNBP memakai rapor dan TKA. Kemampuan UTBK ini tetap membantu untuk TKA dan sebagai rencana cadangan."), 12.5f, 10);
        }
        if (!(p.rated || !p.logs.isEmpty())) {
            text(box, L.t("Rate your skills first, then I can tell you which sub-tests to focus on.",
                    "Nilai kemampuanmu dulu, lalu aku bisa menentukan subtes yang perlu difokuskan."), 14f, 10);
            action(box, L.t("RATE MY SKILLS", "NILAI KEMAMPUANKU"), Pal.LIME_HI, Pal.INK, new Runnable() {
                @Override
                public void run() {
                    Forms.rateSkills(a);
                }
            });
            col.addView(box);
            return;
        }
        List<Coach.Gap> gaps = Coach.gaps(p, univ, field);
        for (int i = 0; i < 3; i++) {
            Coach.Gap g = gaps.get(i);
            View rule = new View(a);
            rule.setBackgroundColor(0x33111111);
            box.addView(rule, ui.lp(Ui.MATCH, ui.dp(1.5f), 0, 12, 0, 0));
            box.addView(ui.heavy(Skills.code(g.skill) + " \u00B7 " + Skills.name(g.skill), 15f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 10, 0, 0));
            mono(box, L.t("LEVEL " + g.level + " / NEED ABOUT " + g.need, "LEVEL " + g.level + " / BUTUH SEKITAR " + g.need), 10.5f, 4);
            box.addView(ui.tag(L.t("LIKELY QUESTIONS", "KEMUNGKINAN SOAL"), Pal.YELLOW, -1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 10, 0, 0));
            for (String m : Skills.matters(g.skill)) text(box, "\u2022 " + m, 13f, 4);
            box.addView(ui.tag(L.t("BEST WAY TO LEARN", "CARA BELAJAR TERBAIK"), Pal.LIME_HI, 1f), ui.lp(Ui.WRAP, Ui.WRAP, 0, 10, 0, 0));
            for (String h : Skills.how(g.skill)) text(box, "\u2022 " + h, 13f, 4);
        }
        col.addView(box);
    }
    // ---------------------------------------------------------------schedule
    private void schedule(LinearLayout col) {
        LinearLayout box = box(Pal.SKY, L.t("BEST TIME TO STUDY", "WAKTU BELAJAR TERBAIK"), Pal.PAPER);
        if (!p.hasDream()) return;
        text(box, L.t("When do you focus best?", "Kapan kamu paling fokus?"), 14f, 10);
        box.addView(ui.choice(new String[]{L.t("MORNING", "PAGI"), L.t("AFTERNOON", "SIANG"), L.t("EVENING", "MALAM")},
                p.bestTime, 3, new Ui.IntCb() {
            @Override
            public void on(int value) {
                if (value != p.bestTime) {
                    p.bestTime = value;
                    a.saveProfile();
                    a.rerender();
                }
            }
        }), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        action(box, host.busy == null ? L.t("READ MY CALENDAR", "BACA KALENDERKU") : L.t("READ CALENDAR AGAIN", "BACA KALENDER LAGI"),
                Pal.PAPER, Pal.INK, new Runnable() {
            @Override
            public void run() {
                a.withCalendar(new Runnable() {
                    @Override
                    public void run() {
                        host.busy = CalendarHelper.busy(a, now, now + 7 * Coach.DAY);
                        a.rerender();
                    }
                });
            }
        });
        text(box, host.busy == null
                ? L.t("Now based on your focus time only. Read your calendar and I will avoid your busy slots.",
                "Saat ini hanya berdasarkan waktu fokusmu. Baca kalendermu dan aku akan menghindari jadwal sibukmu.")
                : L.t("I checked your calendar (" + host.busy.length + " busy slots this week) and placed sessions where you are free.",
                "Aku sudah cek kalendermu (" + host.busy.length + " jadwal sibuk minggu ini) dan menaruh sesi di waktu luangmu."), 12f, 10);
        final List<Plan.Session> sessions = Plan.sessions(p, univ, now, host.busy, 7);
        if (sessions.isEmpty()) {
            text(box, L.t("No free slot found this week.", "Tidak ada waktu luang minggu ini."), 13f, 10);
        }
        for (final Plan.Session s : sessions) {
            View rule = new View(a);
            rule.setBackgroundColor(0x33111111);
            box.addView(rule, ui.lp(Ui.MATCH, ui.dp(1.5f), 0, 10, 0, 0));
            LinearLayout row = ui.hbox();
            LinearLayout info = ui.vbox();
            info.addView(ui.mono(MainActivity.timeText(s.start) + "\u2013" + MainActivity.clock(s.end), 11.5f, Pal.INK));
            info.addView(ui.heavy(Skills.code(s.skill) + " \u00B7 " + Skills.name(s.skill), 13.5f, Pal.INK), ui.lp(Ui.WRAP, Ui.WRAP, 0, 3, 0, 0));
            row.addView(info, new LinearLayout.LayoutParams(0, Ui.WRAP, 1f));
            row.addView(smallAction("+", Pal.YELLOW, new Runnable() {
                @Override
                public void run() {
                    launch(CalendarHelper.insertIntent(sessionTitle(s),Skills.how(s.skill)[0], s.start, s.end, false));
                }
            }));
            box.addView(row, ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        }
        if (!sessions.isEmpty()) {
            action(box, L.t("ADD ALL SESSIONS TO CALENDAR", "TAMBAH SEMUA SESI KE KALENDER"), Pal.FOREST, Pal.LIME, new Runnable() {
                @Override
                public void run() {
                    a.withCalendar(new Runnable() {
                        @Override
                        public void run() {
                            addSessions(sessions);
                        }
                    });
                }
            }).label.setTextSize(12f);
        }
        col.addView(box);
    }
    private String sessionTitle(Plan.Session s) {
        return L.t("Study: ", "Belajar: ") + Skills.name(s.skill);
    }
    private void addSessions(List<Plan.Session> sessions) {
        long cal = CalendarHelper.writableCalendar(a);
        if (cal < 0) {
            a.flash(L.t("No writable calendar found on this phone.", "Tidak ada kalender yang bisa ditulis di ponsel ini."));
            return;
        }
        int added = 0;
        for (Plan.Session s : sessions) {
            if (p.calendar.contains(s.key())) continue;
            if (CalendarHelper.insertTimed(a, cal, "Jurusanator \u00B7 " + sessionTitle(s), Skills.how(s.skill)[0],
                    s.start, s.end, new int[]{15})) {
                p.calendar.add(s.key());
                added++;
            }
        }
        a.saveProfile();
        a.flash(added > 0 ? L.t("Added " + added + " study sessions.", added + " sesi belajar ditambahkan.")
                : L.t("Nothing new to add.", "Tidak ada yang baru untuk ditambahkan."));
        a.rerender();
    }
}


