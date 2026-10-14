package jurusanator;
import static jurusanator.MajorData.MAJORS;
import static jurusanator.MajorData.QUESTIONS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import jurusanator.MajorData.Major;
import jurusanator.MajorData.Question;
/**
 * Adaptive question picker + scorer. The scoring maths is the original one.
 * New: configurable length (the coach can change strategy), interest profile for the
 * stats screen, per-major match lookup, and an optional "duel" tie-break bonus.
 */
public final class MajorRecommender {
    public static final int DEFAULT_QUESTIONS = 12;
    private static final int TOP_RESULTS = 4;
    private static final double DUEL_BONUS = 0.06;
    private static final double RELATED_BONUS = 0.45;
    private static final int RANDOM_CHOICES = 3;
    private static final int WILDCARD_POOL = 6;
    private static final double[] ANSWER_WEIGHTS = {
        2.0,   // yes
                1.0,   // maybe
                0.0,   // don't know
                -1.0,   // maybe not
                -2.0    // no
    };
    private final int limit;
    /** 0 = classic order (tests, old data). Any other value = random but related questions, repeatable. */
    private final long seed;
    private final Map<Question, Integer> answers = new LinkedHashMap<>();
    private final Set<Question> asked = new HashSet<>();
    private final Map<Integer, Double> bonus = new HashMap<>();
    public MajorRecommender() {
        this(DEFAULT_QUESTIONS);
    }
    public MajorRecommender(int limit) {
        this(limit, 0L);
    }
    public MajorRecommender(int limit, long seed) {
        this.limit = Math.max(1, limit);
        this.seed = seed;
    }
    public long seed() {
        return seed;
    }
    public int questionLimit() {
        return Math.min(limit, QUESTIONS.size());
    }
    public int answeredCount() {
        return answers.size();
    }
    public boolean isComplete() {
        return answeredCount() >= questionLimit();
    }
    public Question nextQuestion() {
        if (isComplete()) {
            return null;
        }
        if (seed == 0L) {
            return classicQuestion();
        }
        return relatedRandomQuestion();
    }
    /** Always the single most useful question (stable, used when seed == 0). */
    private Question classicQuestion() {
        Question next = null;
        double highestValue = Double.NEGATIVE_INFINITY;
        for (Question question : QUESTIONS) {
            if (asked.contains(question)) {
                continue;
            }
            double value = questionValue(question);
            if (value > highestValue) {
                highestValue = value;
                next = question;
            }
        }
        return next;
    }
    /**
     * Picks at random among the few best questions, and prefers ones that are related to
     * the question the user just answered. Same seed + same answers = same questions, so a
     * saved quiz can always be replayed.
     */
    private Question relatedRandomQuestion() {
        Question last = null;
        for (Question question : answers.keySet()) {
            last = question;
        }
        List<Question> pool = new ArrayList<>();
        final Map<Question, Double> score = new HashMap<>();
        for (Question question : QUESTIONS) {
            if (asked.contains(question)) {
                continue;
            }
            pool.add(question);
            score.put(question, questionValue(question) + RELATED_BONUS * overlap(last, question));
        }
        if (pool.isEmpty()) {
            return null;
        }
        Collections.sort(pool, new Comparator<Question>() {
            @Override
            public int compare(Question first, Question second) {
                return Double.compare(score.get(second), score.get(first));
            }
        });
        int choices = Math.min(RANDOM_CHOICES, pool.size());
        Random random = new Random(seed * 31L + answers.size() * 7919L + 17L);
        return pool.get(random.nextInt(choices));
    }
    /** 0..1: how much two questions talk about the same interests. */
    private static double overlap(Question first, Question second) {
        if (first == null || second == null) {
            return 0.0;
        }
        double[] a = first.interests();
        double[] b = second.interests();
        double shared = 0.0;
        double total = 0.0;
        for (int i = 0; i < a.length; i++) {
            shared += Math.min(Math.abs(a[i]), Math.abs(b[i]));
            total += Math.max(Math.abs(a[i]), Math.abs(b[i]));
        }
        return total == 0.0 ? 0.0 : shared / total;
    }
    public void answer(int answerIndex) {
        if (answerIndex < 0 || answerIndex >= ANSWER_WEIGHTS.length) {
            throw new IllegalArgumentException("Answer index must be between 0 and 4.");
        }
        Question question = nextQuestion();
        if (question == null) {
            throw new IllegalStateException("All questions have already been answered.");
        }
        asked.add(question);
        answers.put(question, answerIndex);
    }
    public void undoLastAnswer() {
        if (answers.isEmpty()) {
            return;
        }
        Question lastQuestion = null;
        for (Question question : answers.keySet()) {
            lastQuestion = question;
        }
        answers.remove(lastQuestion);
        asked.remove(lastQuestion);
    }
    public int[] answerHistory() {
        int[] history = new int[answers.size()];
        int index = 0;
        for (int answer : answers.values()) {
            history[index++] = answer;
        }
        return history;
    }
    /** Indexes (in MajorData.QUESTIONS) of the asked questions, in asking order. */
    public int[] askedIndexes() {
        int[] result = new int[answers.size()];
        int i = 0;
        for (Question question : answers.keySet()) {
            result[i++] = QUESTIONS.indexOf(question);
        }
        return result;
    }
    /** Duel tie-break: the user picked this major over a close rival. */
    public void setDuelWinner(int majorIndex) {
        bonus.put(majorIndex, DUEL_BONUS);
    }
    /** Ranked list of every major with a positive match (not cut to the top 4). */
    private List<Recommendation> rankAll() {
        List<Recommendation> ranked = new ArrayList<>();
        for (int index = 0; index < MAJORS.size(); index++) {
            Major major = MAJORS.get(index);
            long matchPercent = percentFor(index);
            if (matchPercent > 0) {
                ranked.add(new Recommendation(
                        major,
                        index,
                        matchPercent,
                        explainFit(major)
                        ));
            }
        }
        Collections.sort(ranked, new Comparator<Recommendation>() {
            @Override
            public int compare(Recommendation first, Recommendation second) {
                int byPercent = Long.compare(second.matchPercent, first.matchPercent);
                return byPercent != 0
                        ? byPercent
                        : first.major.name().compareTo(second.major.name());
            }
        });
        return ranked;
    }
    /**
     * One extra major that is always added at the very end of the results. It comes from just
     * below the top list (so it is still a sensible match) and is picked at random, repeatably.
     */
    public Recommendation wildcard() {
        if (!isComplete()) {
            throw new IllegalStateException("Answer all questions before requesting results.");
        }
        final Map<Integer, Long> percent = new HashMap<>();
        List<Integer> order = new ArrayList<>();
        for (int index = 0; index < MAJORS.size(); index++) {
            percent.put(index, percentFor(index));
            order.add(index);
        }
        Collections.sort(order, new Comparator<Integer>() {
            @Override
            public int compare(Integer first, Integer second) {
                int byPercent = Long.compare(percent.get(second), percent.get(first));
                return byPercent != 0 ? byPercent : first.compareTo(second);
            }
        });
        Set<Integer> top = new HashSet<>();
        for (Recommendation rec : recommendations()) {
            top.add(rec.majorIndex);
        }
        List<Integer> pool = new ArrayList<>();
        for (int index : order) {
            if (!top.contains(index)) {
                pool.add(index);
            }
            if (pool.size() == WILDCARD_POOL) {
                break;
            }
        }
        int pick = seed == 0L ? 0 : new Random(seed * 131L + 3L).nextInt(pool.size());
        int majorIndex = pool.get(pick);
        Major major = MAJORS.get(majorIndex);
        return new Recommendation(major, majorIndex, percent.get(majorIndex), explainFit(major));
    }
    public List<Recommendation> recommendations() {
        if (!isComplete()) {
            throw new IllegalStateException("Answer all questions before requesting results.");
        }
        List<Recommendation> ranked = rankAll();
        if (ranked.size() > TOP_RESULTS) {
            return new ArrayList<>(ranked.subList(0, TOP_RESULTS));
        }
        return ranked;
    }
    /** Match percent (0..100) of any major, even one that is not in the top 4. */
    public int matchPercentFor(int majorIndex) {
        return (int) percentFor(majorIndex);
    }
    private long percentFor(int majorIndex) {
        Major major = MAJORS.get(majorIndex);
        double extra = bonus.containsKey(majorIndex) ? bonus.get(majorIndex) : 0.0;
        long percent = Math.round((score(major) + extra) * 100);
        return Math.max(0, Math.min(100, percent));
    }
    /**
     * How strongly the user leans to each of the six interests, 0..100
     * (50 = neutral / "don't know").
     */
    public double[] interestProfile() {
        double[] result = new double[6];
        for (int i = 0; i < 6; i++) {
            double sum = 0.0;
            double max = 0.0;
            for (Map.Entry<Question, Integer> entry : answers.entrySet()) {
                double weight = entry.getKey().interests()[i];
                sum += ANSWER_WEIGHTS[entry.getValue()] * weight;
                max += 2.0 * Math.abs(weight);
            }
            result[i] = max == 0.0 ? 50.0 : (sum / max + 1.0) * 50.0;
        }
        return result;
    }
    private double questionValue(Question question) {
        double[] weights = question.interests();
        double value = 0.0;
        for (int i = 0; i < weights.length; i++) {
            double uncertainty = 1.0 / (1.0 + evidenceForInterest(i));
            value += Math.abs(weights[i]) * uncertainty;
        }
        return value;
    }
    private double evidenceForInterest(int interestIndex) {
        double evidence = 0.0;
        for (Map.Entry<Question, Integer> entry : answers.entrySet()) {
            double[] weights = entry.getKey().interests();
            evidence += Math.abs(weights[interestIndex])
                    * Math.abs(ANSWER_WEIGHTS[entry.getValue()]);
        }
        return evidence;
    }
    private double score(Major major) {
        double[] profile = major.interests();
        double total = 0.0;
        double possible = 0.0;
        for (Map.Entry<Question, Integer> entry : answers.entrySet()) {
            double[] weights = entry.getKey().interests();
            double questionFit = 0.0;
            double questionWeight = 0.0;
            for (int i = 0; i < profile.length; i++) {
                questionFit += weights[i] * profile[i];
                questionWeight += Math.abs(weights[i]) * 5.0;
            }
            if (questionWeight == 0.0) {
                continue;
            }
            double answerWeight = ANSWER_WEIGHTS[entry.getValue()];
            total += answerWeight * questionFit / questionWeight;
            possible += Math.abs(answerWeight);
        }
        return possible == 0.0 ? 0.0 : total / possible;
    }
    /** Up to three interest indexes (0..5) that explain the match; may be empty. */
    private List<Integer> explainFit(Major major) {
        double[] profile = major.interests();
        double[] evidence = new double[profile.length];
        for (Map.Entry<Question, Integer> entry : answers.entrySet()) {
            double answerWeight = ANSWER_WEIGHTS[entry.getValue()];
            double[] questionWeights = entry.getKey().interests();
            for (int i = 0; i < profile.length; i++) {
                evidence[i] += answerWeight * questionWeights[i];
            }
        }
        List<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < evidence.length; i++) {
            indexes.add(i);
        }
        Collections.sort(indexes, new Comparator<Integer>() {
            @Override
            public int compare(Integer first, Integer second) {
                return Double.compare(
                        evidence[second] * profile[second],
                        evidence[first] * profile[first]
                        );
            }
        });
        List<Integer> explanation = new ArrayList<>();
        for (int index : indexes) {
            if (evidence[index] > 0 && profile[index] >= 3) {
                explanation.add(index);
            }
            if (explanation.size() == 3) {
                break;
            }
        }
        return explanation;
    }
    public static final class Recommendation {
        public final Major major;
        public final int majorIndex;
        public final long matchPercent;
        /** Interest indexes (0..5) behind the match. Empty = "overall profile". */
        public final List<Integer> interests;
        private Recommendation(Major major, int majorIndex, long matchPercent, List<Integer> interests) {
            this.major = major;
            this.majorIndex = majorIndex;
            this.matchPercent = matchPercent;
            this.interests = interests;
        }
    }
}


