package jurusanator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
/**
 * Entrance timeline + study-session planner. Plain Java.
 *
 * IMPORTANT: the dates here are ESTIMATES. They copy the official 2026 schedule
 * (SNBP result 31 Mar, UTBK registration 25 Mar, UTBK 21-30 Apr, SNBT result 25 May)
 * and move it one year ahead. The official 2027 schedule is published by SNPMB (snpmb.id).
 * The user can edit every date in the app.
 */
final class Plan {
    private Plan() {}
    static final int R_SNBP = 1, R_SNBT = 2, R_MANDIRI = 4;
    static final int SESSION_MIN = 45;
    static final class Ev {
        final String key, en, id, descEn, descId;
        final int month, day, yearShift, routes;
        Ev(String key, String en, String id, String descEn, String descId,
                int month, int day, int yearShift, int routes) {
            this.key = key; this.en = en; this.id = id; this.descEn = descEn; this.descId = descId;
            this.month = month; this.day = day; this.yearShift = yearShift; this.routes = routes;
        }
        String title() { return L.id ? id : en; }
        String desc() { return L.id ? descId : descEn; }
    }
    static final List<Ev> EVENTS = new ArrayList<>();
    static {
        final int JAN = Calendar.JANUARY, FEB = Calendar.FEBRUARY, MAR = Calendar.MARCH,
                APR = Calendar.APRIL, MAY = Calendar.MAY, JUN = Calendar.JUNE, OCT = Calendar.OCTOBER;
        EVENTS.add(new Ev("tka", "TKA test (needed for SNBP)", "Tes TKA (syarat SNBP)",
                "Academic Skills Test. SNBP uses TKA scores. Estimated window starts late October.",
                "Tes Kemampuan Akademik. SNBP memakai nilai TKA. Perkiraan jendela mulai akhir Oktober.",
                OCT, 26, -1, R_SNBP));
        EVENTS.add(new Ev("acct", "Create your SNPMB account", "Buat akun SNPMB",
                "You need a saved SNPMB account for SNBP and UTBK-SNBT.",
                "Kamu butuh akun SNPMB yang tersimpan untuk SNBP dan UTBK-SNBT.",
                JAN, 12, 0, R_SNBP | R_SNBT));
        EVENTS.add(new Ev("snbp_reg", "SNBP registration", "Pendaftaran SNBP",
                "Only for eligible students. Choose your programs carefully.",
                "Hanya untuk siswa eligible. Pilih program studi dengan cermat.",
                FEB, 3, 0, R_SNBP));
        EVENTS.add(new Ev("snbp_res", "SNBP result", "Pengumuman SNBP",
                "If you pass SNBP you cannot join SNBT or Mandiri.",
                "Jika lolos SNBP, kamu tidak bisa ikut SNBT atau Mandiri.",
                MAR, 31, 0, R_SNBP));
        EVENTS.add(new Ev("utbk_reg", "UTBK-SNBT registration", "Pendaftaran UTBK-SNBT",
                "Register and pay the UTBK fee on time.",
                "Daftar dan bayar biaya UTBK tepat waktu.",
                MAR, 25, 0, R_SNBT));
        EVENTS.add(new Ev("utbk", "UTBK test day", "Hari UTBK",
                "The computer-based test. Rest well the night before.",
                "Tes berbasis komputer. Istirahat cukup malam sebelumnya.",
                APR, 21, 0, R_SNBT));
        EVENTS.add(new Ev("utbk_res", "SNBT result", "Pengumuman SNBT",
                "Download your UTBK certificate afterwards.",
                "Setelah itu unduh sertifikat UTBK.",
                MAY, 25, 0, R_SNBT));
        EVENTS.add(new Ev("cert", "Download your UTBK certificate", "Unduh sertifikat UTBK",
                "The certificate with your sub-test scores is needed for Mandiri and re-registration.",
                "Sertifikat berisi nilai tiap subtes, dibutuhkan untuk Mandiri dan daftar ulang.",
                JUN, 2, 0, R_SNBT | R_MANDIRI));
        EVENTS.add(new Ev("mandiri", "Mandiri: check your university", "Mandiri: cek kampusmu",
                "Each university sets its own Mandiri schedule and test. Check its website.",
                "Tiap kampus punya jadwal dan tes Mandiri sendiri. Cek situs kampusnya.",
                JUN, 1, 0, R_MANDIRI));
    }
    static List<Ev> forRoute(int route) {
        int bit = route == Profile.ROUTE_SNBP ? R_SNBP : route == Profile.ROUTE_SNBT ? R_SNBT : R_MANDIRI;
        List<Ev> out = new ArrayList<>();
        for (Ev e : EVENTS) {
            if ((e.routes & bit) != 0) out.add(e);
        }
        return out;
    }
    /** Students who start university in this year. From August on it is next year. */
    static int cycleYear(long now) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        int year = c.get(Calendar.YEAR);
        return c.get(Calendar.MONTH) >= Calendar.AUGUST ? year + 1 : year;
    }
    /** Date of an event as yyyymmdd: the user's edit if there is one, else the estimate. */
    static int ymd(Ev e, long now, Profile p) {
        Integer saved = p.dates.get(e.key);
        if (saved != null && saved > 19000101) return saved;
        int year = cycleYear(now) + e.yearShift;
        return year * 10000 + (e.month + 1) * 100 + e.day;
    }
    static boolean isEdited(Ev e, Profile p) {
        return p.dates.containsKey(e.key);
    }
    static int year(int ymd) { return ymd / 10000; }
    static int month(int ymd) { return (ymd / 100) % 100; }     // 1..12
    static int day(int ymd) { return ymd % 100; }
    static int makeYmd(int year, int month1, int day) {
        return year * 10000 + month1 * 100 + day;
    }
    static int daysInMonth(int year, int month1) {
        Calendar c = new GregorianCalendar(year, month1 - 1, 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    /** Midnight UTC of that date: what all-day calendar events need. */
    static long utcMillis(int ymd) {
        Calendar c = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        c.clear();
        c.set(year(ymd), month(ymd) - 1, day(ymd), 0, 0, 0);
        return c.getTimeInMillis();
    }
    static long localMillis(int ymd, int hour, int minute) {
        Calendar c = new GregorianCalendar(year(ymd), month(ymd) - 1, day(ymd), hour, minute, 0);
        return c.getTimeInMillis();
    }
    /** Whole days from today to that date (negative = already passed). */
    static int daysUntil(int ymd, long now) {
        Calendar today = Calendar.getInstance();
        today.setTimeInMillis(now);
        int todayYmd = today.get(Calendar.YEAR) * 10000 + (today.get(Calendar.MONTH) + 1) * 100
                + today.get(Calendar.DAY_OF_MONTH);
        long a = utcMillis(todayYmd);
        long b = utcMillis(ymd);
        return (int) Math.round((b - a) / 86400000.0);
    }
    /** Next upcoming event of the route, or null when all have passed. */
    static Ev next(Profile p, long now) {
        Ev best = null;
        int bestDays = Integer.MAX_VALUE;
        for (Ev e : forRoute(p.route)) {
            int d = daysUntil(ymd(e, now, p), now);
            if (d >= 0 && d < bestDays) {
                best = e;
                bestDays = d;
            }
        }
        return best;
    }
    // -------------------------------------------------------------study sessions
    static final class Session {
        final long start, end;
        final int skill;
        Session(long start, long end, int skill) {
            this.start = start; this.end = end; this.skill = skill;
        }
        String key() { return "s" + start; }
    }
    /** {first start hour*60, last end hour*60} in minutes for the preferred time of day. */
    static int[] window(int bestTime) {
        switch (bestTime) {
            case 0: return new int[]{6 * 60, 11 * 60 + 30};
            case 1: return new int[]{13 * 60, 17 * 60 + 30};
            default: return new int[]{18 * 60 + 30, 21 * 60 + 30};
        }
    }
    /**
     * One session per day (two when the target test is near), 45 minutes each, weakest skills
     * first. Prefers the user's best time of day, and avoids busy times when known.
     * @param busy list of {start, end} millis from the calendar, or null if unknown
     */
    static List<Session> sessions(Profile p, Univ univ, long now, long[][] busy, int days) {
        List<Session> out = new ArrayList<>();
        if (!p.hasDream() || univ == null) return out;
        List<Coach.Gap> gaps = Coach.gaps(p, univ, Fields.of(p.dreamMajor));
        int[] rotation = {0, 1, 0, 2, 1, 0};
        List<long[]> taken = new ArrayList<>();
        if (busy != null) {
            for (long[] b : busy) taken.add(b);
        }
        Ev target = p.route == Profile.ROUTE_SNBP ? null : findEvent("utbk");
        int perDay = 1;
        if (target != null && daysUntil(ymd(target, now, p), now) <= 45) perDay = 2;
        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(now);
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        int n = 0;
        for (int d = 0; d < days; d++) {
            for (int k = 0; k < perDay; k++) {
                Session s = findSlot(day, p.bestTime, now, taken, k == 1);
                if (s == null) continue;
                int skill = gaps.get(rotation[n % rotation.length] % gaps.size()).skill;
                n++;
                Session placed = new Session(s.start, s.end, skill);
                out.add(placed);
                taken.add(new long[]{placed.start, placed.end});
            }
            day.add(Calendar.DAY_OF_MONTH, 1);
        }
        return out;
    }
    static Ev findEvent(String key) {
        for (Ev e : EVENTS) {
            if (e.key.equals(key)) return e;
        }
        return null;
    }
    private static Session findSlot(Calendar day, int bestTime, long now, List<long[]> taken, boolean other) {
        int[] order = other ? new int[]{(bestTime + 1) % 3, (bestTime + 2) % 3, bestTime}
        : new int[]{bestTime, 2, 1, 0};
        long pad = 10 * 60000L;
        for (int w : order) {
            int[] win = window(w);
            for (int m = win[0]; m + SESSION_MIN <= win[1]; m += 30) {
                Calendar c = (Calendar) day.clone();
                c.set(Calendar.HOUR_OF_DAY, m / 60);
                c.set(Calendar.MINUTE, m % 60);
                long start = c.getTimeInMillis();
                long end = start + SESSION_MIN * 60000L;
                if (start < now + pad) continue;
                boolean free = true;
                for (long[] b : taken) {
                    if (start < b[1] + pad && end + pad > b[0]) {
                        free = false;
                        break;
                    }
                }
                if (free) return new Session(start, end, 0);
            }
        }
        return null;
    }
}


