package jurusanator;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Other users. A real version needs a server. Until one exists the app uses
 * {@link Demo}, a simulated cohort that is ALWAYS labelled "demo" in the UI.
 * To go real: write another Source that talks to your backend and return it from source().
 */
final class Peers {
    private Peers() {}
    static final class Peer {
        final String name;
        final int score;     // readiness 0..100
        final int hours;     // study hours per week
        Peer(String name, int score, int hours) {
            this.name = name; this.score = score; this.hours = hours;
        }
    }
    /** What accepted students looked like (assumed numbers in demo mode). */
    static final class Benchmark {
        final int skill, hours, mocks;
        Benchmark(int skill, int hours, int mocks) {
            this.skill = skill; this.hours = hours; this.mocks = mocks;
        }
    }
    interface Source {
        boolean isReal();
        List<Peer> all(Univ univ, int major);
        Benchmark accepted(Univ univ, int major);
    }
    static Source source() {
        return new Demo();
    }
    /** Share of peers (0..100) that the given readiness score beats. */
    static int beatPercent(List<Peer> peers, int myScore) {
        if (peers.isEmpty()) return 0;
        int beaten = 0;
        for (Peer p : peers) {
            if (myScore > p.score) beaten++;
        }
        return Math.round(100f * beaten / peers.size());
    }
    static final class Demo implements Source {
        private static final String[] ANIMALS = {
            "Kancil", "Capybara", "Otter", "Panda", "Koala", "Elang", "Rusa", "Penyu"};
        @Override
        public boolean isReal() {
            return false;
        }
        @Override
        public List<Peer> all(Univ univ, int major) {
            Random rnd = new Random(univ.id * 1009L + major * 31L + 7L);
            double mean = 42 + 7 * univ.selectivity;
            double hoursMean = 5 + 1.5 * univ.selectivity;
            List<Peer> peers = new ArrayList<>();
            for (int i = 0; i < 60; i++) {
                int score = Profile.clamp((int) Math.round(mean + rnd.nextGaussian() * 12), 5, 99);
                int hours = Profile.clamp((int) Math.round(hoursMean + rnd.nextGaussian() * 2.5), 0, 25);
                String name = ANIMALS[rnd.nextInt(ANIMALS.length)] + "-" + (10 + rnd.nextInt(90));
                peers.add(new Peer(name, score, hours));
            }
            return peers;
        }
        @Override
        public Benchmark accepted(Univ univ, int major) {
            int sel = univ.selectivity;
            return new Benchmark(Profile.clamp(55 + 11 * sel, 60, 92), 6 + 2 * sel, 6 + 3 * sel);
        }
    }
}


