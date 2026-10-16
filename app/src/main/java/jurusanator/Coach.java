package jurusanator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import jurusanator.MajorData.Major;
/**
 * The coaching brain. Plain Java, no Android. Everything here is a transparent
 * rule or formula (NOT machine learning), so it can be read, tested and tuned.
 */
final class Coach {
    private Coach() {}
    static final long DAY = 86400000L;
    // diagnosis flags (why the app thinks the user may not be serious / what it changed)
    static final int F_FAST = 1, F_STRAIGHT = 2, F_UNSURE = 4, F_TWIN = 8, F_PROBE = 16, F_IDLE = 32;
    // reasons for "why you may not have got in"
    static final int R_READY = 1, R_WEAK = 2, R_HOURS = 3, R_MOCKS = 4, R_ADHERE = 5, R_TREND = 6,
            R_SINGLE = 7, R_SERIOUS = 8, R_COMPETE = 9, R_PEERS = 10;
    /** Pairs of questions that ask nearly the same thing (indexes in MajorData.QUESTIONS). */
    private static final int[][] TWINS = {{9, 10}, {2, 14}, {3, 20}, {19, 22}, {7, 11}, {8, 12}};
    // ------------------------------------------------------------------skills
    /** Current level (0..100) of each skill: self rating, then moved by mock-test scores. */
    static int[] levels(Profile p) {
        int[] level = p.skill.clone();
        boolean[] seen = new boolean[Skills.COUNT];
        for (Profile.Log l : p.logs) {
            if (l.type != Profile.LOG_MOCK) continue;
            double a = seen[l.skill] ? 0.5 : 0.6;
            seen[l.skill] = true;
            level[l.skill] = (int) Math.round(level[l.skill] * (1 - a) + l.value * a);
        }
        return level;
    }
    /** Level we would like to see for a skill (rough guide, grows with selectivity). */
    static int need(Univ u, int field, int skill) {
        return Math.min(100, 40 + 15 * Fields.need(field, skill) + (u.selectivity - 1) * 5);
    }
    static final class Gap {
        final int skill, level, need, gap, weight;
        Gap(int skill, int level, int need, int weight) {
            this.skill = skill; this.level = level; this.need = need;
            this.gap = need - level; this.weight = weight;
        }
    }
    /** All skills, biggest gap first. A negative gap = a strength. */
    static List<Gap> gaps(Profile p, Univ u, int field) {
        int[] level = levels(p);
        List<Gap> list = new ArrayList<>();
        for (int s = 0; s < Skills.COUNT; s++) {
            list.add(new Gap(s, level[s], need(u, field, s), Fields.need(field, s)));
        }
        Collections.sort(list, new Comparator<Gap>() {
            @Override
            public int compare(Gap a, Gap b) {
                if (a.gap != b.gap) return b.gap - a.gap;
                return b.weight - a.weight;
            }
        });
        return list;
    }
    /** 0..100: how ready the user is, weighted by how much each skill matters. */
    static int readiness(Profile p, Univ u, int field) {
        int[] level = levels(p);
        double sum = 0, weights = 0;
        for (int s = 0; s < Skills.COUNT; s++) {
            int w = Fields.need(field, s);
            if (w <= 0) continue;
            sum += w * Math.min(1.0, level[s] / (double) need(u, field, s));
            weights += w;
        }
        return weights == 0 ? 0 : (int) Math.round(100 * sum / weights);
    }
    // --------------------------------------------------------------------- fit
    static final class Fit {
        final int total, interest, univ, ready, commit;
        Fit(int total, int interest, int univ, int ready, int commit) {
            this.total = total; this.interest = interest; this.univ = univ;
            this.ready = ready; this.commit = commit;
        }
    }
    /** "Points" for a university + major: a weighted mix of 4 factors (weights are learned). */
    static Fit fit(int interestPct, Univ u, int field, Profile p, long now) {
        int univScore = u.isStrongIn(field) ? 100 : 55;
        int ready = readiness(p, u, field);
        int commit = commitment(p, now);
        float[] w = p.weights;
        double total = w[0] * interestPct + w[1] * univScore + w[2] * ready + w[3] * commit;
        return new Fit(Profile.clamp((int) Math.round(total), 0, 100), interestPct, univScore, ready, commit);
    }
    static int commitment(Profile p, long now) {
        int serious = p.seriousness >= 0 ? p.seriousness : 60;
        int act = Math.min(100, studyMinutes(p, now, 14) * 100 / 480);
        return (int) Math.round(0.6 * serious + 0.4 * act);
    }
    // ----------------------------------------------------------quiz seriousness
    static Profile.QuizStats quizStats(int[] answers, long[] ms, int[] askedIdx, long now) {
        int n = answers.length;
        if (n == 0) return null;
        long total = 0;
        int counted = 0;
        for (int i = 0; i < n && i < ms.length; i++) {
            if (ms[i] > 0) {
                total += Math.min(ms[i], 30000L);
                counted++;
            }
        }
        int avg = counted == 0 ? 0 : (int) (total / counted);
        int[] count = new int[5];
        for (int a : answers) {
            if (a >= 0 && a < 5) count[a]++;
        }
        int max = 0;
        for (int c : count) max = Math.max(max, c);
        int pairs = 0, mismatch = 0;
        for (int[] twin : TWINS) {
            int x = indexOf(askedIdx, twin[0]);
            int y = indexOf(askedIdx, twin[1]);
            if (x >= 0 && y >= 0 && x < n && y < n) {
                pairs++;
                if (Math.abs(answers[x] - answers[y]) >= 3) mismatch++;
            }
        }
        return new Profile.QuizStats(now, avg, count[2] * 100 / n, max * 100 / n,
                pairs == 0 ? -1 : mismatch * 100 / pairs);
    }
    private static int indexOf(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }
    /** How alike the user's interests and a major are: 0..1. */
    static double similarity(double[] userProfile, Major major) {
        double[] m = major.interests();
        double diff = 0;
        for (int i = 0; i < 6; i++) diff += Math.abs(userProfile[i] / 100.0 * 5.0 - m[i]);
        return 1.0 - diff / 6.0 / 5.0;
    }
    /** Correlation between the user's "random major" answers and real similarity. NaN = not enough. */
    static double probeConsistency(List<Profile.Probe> probes, double[] userProfile) {
        int n = probes.size();
        if (n < 3) return Double.NaN;
        double[] v = new double[n];
        double[] s = new double[n];
        for (int i = 0; i < n; i++) {
            Profile.Probe pr = probes.get(i);
            v[i] = 1.0 - pr.answer * 0.5;
            s[i] = similarity(userProfile, MajorData.MAJORS.get(pr.major));
        }
        double mv = mean(v), ms = mean(s), cov = 0, vv = 0, vs = 0;
        for (int i = 0; i < n; i++) {
            cov += (v[i] - mv) * (s[i] - ms);
            vv += (v[i] - mv) * (v[i] - mv);
            vs += (s[i] - ms) * (s[i] - ms);
        }
        if (vv < 1e-9 || vs < 1e-9) {
            return mv > 0.5 ? -0.6 : 0.0;   // says "yes" to everything = suspicious
        }
        return cov / Math.sqrt(vv * vs);
    }
    private static double mean(double[] a) {
        double sum = 0;
        for (double x : a) sum += x;
        return sum / a.length;
    }
    /**
     * Seriousness 0..100 from: answer speed, straight-lining, "don't know" share,
     * contradictions, the random-major check, and recent activity.
     * Stores score + flags on the profile. Returns -1 when there is no evidence yet.
     */
    static int assess(Profile p, double[] userProfile, long now) {
        int s = 100;
        int flags = 0;
        boolean evidence = false;
        Profile.QuizStats q = p.quiz;
        if (q != null) {
            evidence = true;
            if (q.avgMs > 0 && q.avgMs < 900) { s -= 35; flags |= F_FAST; }
            else if (q.avgMs > 0 && q.avgMs < 1500) { s -= 15; flags |= F_FAST; }
            if (q.samePct >= 85) { s -= 30; flags |= F_STRAIGHT; }
            else if (q.samePct >= 70) { s -= 12; flags |= F_STRAIGHT; }
            if (q.unsurePct > 40) { s -= 15; flags |= F_UNSURE; }
            if (q.twinPct >= 50) { s -= 20; flags |= F_TWIN; }
            else if (q.twinPct >= 34) { s -= 8; flags |= F_TWIN; }
        }
        double c = userProfile == null ? Double.NaN : probeConsistency(p.probes, userProfile);
        if (!Double.isNaN(c)) {
            evidence = true;
            if (c < 0) { s -= 25; flags |= F_PROBE; }
            else if (c < 0.3) { s -= 10; flags |= F_PROBE; }
            else s += 5;
        }
        int days = activeDays(p, now, 14);
        if (days >= 3) {
            evidence = true;
            s += 10;
        } else if (days == 0 && p.dreamSince > 0 && now - p.dreamSince > 7 * DAY) {
            evidence = true;
            s -= 8;
            flags |= F_IDLE;
        }
        p.flags = flags;
        p.seriousness = evidence ? Profile.clamp(s, 0, 100) : -1;
        return p.seriousness;
    }
    /** Low score = shorter, forced-choice quiz. High score = deeper quiz with a duel. */
    static int pickStrategy(int seriousness) {
        if (seriousness < 0) return Profile.STRAT_STANDARD;
        if (seriousness < 50) return Profile.STRAT_ENGAGE;
        if (seriousness >= 75) return Profile.STRAT_DEEP;
        return Profile.STRAT_STANDARD;
    }
    /** Number of questions each strategy asks. */
    static int questionsFor(int strategy) {
        switch (strategy) {
            case Profile.STRAT_ENGAGE: return 8;
            case Profile.STRAT_DEEP: return MajorRecommender.DEFAULT_QUESTIONS;
            default: return MajorRecommender.DEFAULT_QUESTIONS;
        }
    }
    /** Choose a major to ask about. Alternates between similar and dissimilar ones. */
    static int pickProbeMajor(Random rnd, Profile p, double[] userProfile) {
        int n = MajorData.MAJORS.size();
        double[] sims = new double[n];
        double[] sorted = new double[n];
        for (int i = 0; i < n; i++) {
            sims[i] = similarity(userProfile, MajorData.MAJORS.get(i));
            sorted[i] = sims[i];
        }
        java.util.Arrays.sort(sorted);
        double median = sorted[n / 2];
        int low = 0, high = 0;
        Set<Integer> recent = new HashSet<>();
        int from = Math.max(0, p.probes.size() - 5);
        for (int i = 0; i < p.probes.size(); i++) {
            Profile.Probe pr = p.probes.get(i);
            if (sims[pr.major] < median) low++; else high++;
            if (i >= from) recent.add(pr.major);
        }
        boolean wantLow = low <= high;
        for (int tries = 0; tries < 60; tries++) {
            int idx = rnd.nextInt(n);
            if (idx == p.dreamMajor || recent.contains(idx)) continue;
            if ((sims[idx] < median) == wantLow) return idx;
        }
        return (p.dreamMajor + 1 + rnd.nextInt(n - 1)) % n;
    }
    // -------------------------------------------------------------development
    static int dayIndex(long t) {
        return (int) ((t + TimeZone.getDefault().getOffset(t)) / DAY);
    }
    static int studyMinutes(Profile p, long now, int days) {
        long from = now - days * DAY;
        int sum = 0;
        for (Profile.Log l : p.logs) {
            if (l.type == Profile.LOG_STUDY && l.time >= from && l.time <= now) sum += l.value;
        }
        return sum;
    }
    static int activeDays(Profile p, long now, int days) {
        Set<Integer> set = new HashSet<>();
        long from = now - days * DAY;
        for (Profile.Log l : p.logs) {
            if (l.time >= from && l.time <= now) set.add(dayIndex(l.time));
        }
        return set.size();
    }
    /** Days in a row (ending today or yesterday) with at least one log. */
    static int streak(Profile p, long now) {
        Set<Integer> set = new HashSet<>();
        for (Profile.Log l : p.logs) set.add(dayIndex(l.time));
        int day = dayIndex(now);
        if (!set.contains(day)) day--;
        int count = 0;
        while (set.contains(day)) {
            count++;
            day--;
        }
        return count;
    }
    /** Average mock score per day, oldest first: {dayStartMillis, average}. */
    static List<float[]> mockSeries(Profile p) {
        List<float[]> out = new ArrayList<>();
        int currentDay = Integer.MIN_VALUE;
        double sum = 0;
        int count = 0;
        long time = 0;
        for (Profile.Log l : p.logs) {
            if (l.type != Profile.LOG_MOCK) continue;
            int d = dayIndex(l.time);
            if (d != currentDay && count > 0) {
                out.add(new float[]{time, (float) (sum / count)});
                sum = 0;
                count = 0;
            }
            currentDay = d;
            time = l.time;
            sum += l.value;
            count++;
        }
        if (count > 0) out.add(new float[]{time, (float) (sum / count)});
        return out;
    }
    /** Study minutes per 7-day bucket, oldest first, last bucket = the 7 days up to now. */
    static int[] weeklyMinutes(Profile p, long now, int weeks) {
        int[] out = new int[weeks];
        for (Profile.Log l : p.logs) {
            if (l.type != Profile.LOG_STUDY || l.time > now) continue;
            int back = (int) ((now - l.time) / (7 * DAY));
            if (back >= 0 && back < weeks) out[weeks - 1 - back] += l.value;
        }
        return out;
    }
    // -----------------------------------------------------------------outcome
    static final class Reason {
        final int code, weight, skill, a, b;
        Reason(int code, int weight, int skill, int a, int b) {
            this.code = code; this.weight = weight; this.skill = skill; this.a = a; this.b = b;
        }
    }
    /** Ranked guesses for why the user did not get in (only when the outcome is "no"). */
    static List<Reason> diagnose(Profile p, Univ u, int field, Peers.Benchmark bench, Fit fit, long now) {
        List<Reason> out = new ArrayList<>();
        Profile.Outcome o = p.outcome;
        if (o == null || o.got) return out;
        if (fit.ready < 75) out.add(new Reason(R_READY, 100 - fit.ready, 0, fit.ready, 75));
        List<Gap> gaps = gaps(p, u, field);
        Gap worst = gaps.get(0);
        if (worst.gap > 15) out.add(new Reason(R_WEAK, Math.min(100, worst.gap * 2), worst.skill, worst.level, worst.need));
        if (o.hours < bench.hours) out.add(new Reason(R_HOURS, Math.min(100, (bench.hours - o.hours) * 9), 0, o.hours, bench.hours));
        if (o.mocks < bench.mocks) out.add(new Reason(R_MOCKS, Math.min(100, (bench.mocks - o.mocks) * 6), 0, o.mocks, bench.mocks));
        if (o.adherence < 60) out.add(new Reason(R_ADHERE, 100 - o.adherence, 0, o.adherence, 60));
        List<float[]> series = mockSeries(p);
        if (series.size() >= 4 && series.get(series.size() - 1)[1] <= series.get(0)[1]) {
            out.add(new Reason(R_TREND, 40, 0, Math.round(series.get(0)[1]), Math.round(series.get(series.size() - 1)[1])));
        }
        if (o.applied <= 1) out.add(new Reason(R_SINGLE, 45, 0, o.applied, 3));
        if (p.seriousness >= 0 && p.seriousness < 60) out.add(new Reason(R_SERIOUS, 60 - p.seriousness, 0, p.seriousness, 60));
        if (u.selectivity == 3 && fit.total < 75) out.add(new Reason(R_COMPETE, 30, 0, fit.total, 75));
        if (fit.ready < bench.skill - 5) out.add(new Reason(R_PEERS, bench.skill - fit.ready, 0, fit.ready, bench.skill));
        Collections.sort(out, new Comparator<Reason>() {
            @Override
            public int compare(Reason a, Reason b) {
                return b.weight - a.weight;
            }
        });
        return out.size() > 5 ? new ArrayList<>(out.subList(0, 5)) : out;
    }
    private static final String[] FACTOR_EN = {"interest match", "university fit", "readiness", "commitment"};
    private static final String[] FACTOR_ID = {"kecocokan minat", "kecocokan kampus", "kesiapan", "komitmen"};
    /**
     * Learn from the outcome. Got in: keep the algorithm, nudge weights by ~1-2%.
     * Did not get in: move more weight to the weakest factor and switch to the DEEP strategy.
     * Writes human-readable change notes into profile.changes.
     */
    static void learn(Profile p, Fit fit, boolean got) {
        float[] w = p.weights;
        float[] old = w.clone();
        float[] v = {fit.interest, fit.univ, fit.ready, fit.commit};
        if (got) {
            float mean = (v[0] + v[1] + v[2] + v[3]) / 4f;
            for (int i = 0; i < 4; i++) w[i] += 0.02f * (v[i] - mean) / 100f;
            p.addChange("You got in: the algorithm stays. I only nudged the weights by 1-2%.",
                    "Kamu diterima: algoritma tetap. Bobot hanya digeser sekitar 1-2%.");
        } else {
            float[] d = new float[4];
            float sum = 0;
            int lowest = 0;
            for (int i = 0; i < 4; i++) {
                d[i] = Math.max(0f, 70f - v[i]);
                sum += d[i];
                if (v[i] < v[lowest]) lowest = i;
            }
            if (sum == 0f) {
                d[lowest] = 1f;
                sum = 1f;
            }
            for (int i = 0; i < 4; i++) w[i] += 0.12f * d[i] / sum;
            p.strategy = Profile.STRAT_DEEP;
            p.addChange("Not accepted: I re-evaluated the whole formula and the next quiz will be deeper.",
                    "Belum diterima: aku mengevaluasi seluruh rumus dan kuis berikutnya akan lebih mendalam.");
        }
        float total = 0f;
        for (int i = 0; i < 4; i++) {
            w[i] = Math.max(0.08f, w[i]);
            total += w[i];
        }
        for (int i = 0; i < 4; i++) w[i] /= total;
        int top = 0;
        for (int i = 1; i < 4; i++) {
            if (Math.abs(w[i] - old[i]) > Math.abs(w[top] - old[top])) top = i;
        }
        int from = Math.round(old[top] * 100), to = Math.round(w[top] * 100);
        p.addChange("Weight of " + FACTOR_EN[top] + ": " + from + "% to " + to + "%.",
                "Bobot " + FACTOR_ID[top] + ": " + from + "% menjadi " + to + "%.");
    }
}

