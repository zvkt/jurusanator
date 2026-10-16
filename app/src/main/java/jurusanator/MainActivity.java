package jurusanator;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
/**
 * The host: owns the shared services (store, sound, profile), the current screen, pop-up
 * windows and flash messages. Screens call the show*() methods to navigate.
 */
public final class MainActivity extends Activity {
    static final int S_WELCOME = 0, S_HOME = 1, S_QUIZ = 2, S_RESULTS = 3, S_UNIV = 4, S_COACH = 5;
    private static final int REQ_NOTIF = 4412;
    private static final String KEY_SCREEN = "screen";
    private static final String KEY_TAB = "tab";
    private static final String KEY_MAJOR = "major";
    Ui ui;
    Store store;
    Sfx sfx;
    Profile profile;
    MajorRecommender rec;
    int quizStrategy;
    final List<Long> answerMs = new ArrayList<>();
    final Handler handler = new Handler(Looper.getMainLooper());
    final Random random = new Random();
    boolean probeChecked;
    private FrameLayout screenHolder, modalHolder, flashHolder;
    private Screen current;
    private boolean destroyed;
    private boolean modalLocked;
    private Runnable afterCalendarPermission;
    // -----------------------------------------------------------------lifecycle
    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        store = new Store(this);
        String lang = Locale.getDefault().getLanguage();
        L.id = store.langId("id".equals(lang) || "in".equals(lang));
        Pal.dark = store.dark();
        sfx = new Sfx();
        sfx.setEnabled(store.sound());
        ui = new Ui(this);
        ui.sfx = sfx;
        profile = store.profile();
        FrameLayout root = new FrameLayout(this);
        screenHolder = new FrameLayout(this);
        modalHolder = new FrameLayout(this);
        flashHolder = new FrameLayout(this);
        root.addView(screenHolder, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        root.addView(modalHolder, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        root.addView(flashHolder, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        flashHolder.setClickable(false);
        setContentView(root);
        applySystemBars();
        restoreQuiz();
        DailyReceiver.schedule(this, profile.dailyNews && DailyReceiver.canNotify(this));
        int screen = saved == null ? -1 : saved.getInt(KEY_SCREEN, -1);
        if (screen == S_QUIZ && rec.answeredCount() > 0) {
            showQuiz(false);
        } else if (screen == S_RESULTS && latestRec() != null) {
            showResults(latestRec(), false);
        } else if (screen == S_COACH && profile.hasDream()) {
            showCoach(saved.getInt(KEY_TAB, 0));
        } else if (screen == S_UNIV && saved.getInt(KEY_MAJOR, -1) >= 0) {
            showUniv(saved.getInt(KEY_MAJOR, 0));
        } else if (saved == null && !store.seenWelcome()) {
            showWelcome();
        } else {
            showHome();
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        if (current != null) {
            out.putInt(KEY_SCREEN, current.id());
            if (current instanceof CoachScreen) out.putInt(KEY_TAB, ((CoachScreen) current).tab());
            if (current instanceof UnivScreen) out.putInt(KEY_MAJOR, ((UnivScreen) current).major());
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveProfile();
    }
    @Override
    protected void onDestroy() {
        destroyed = true;
        handler.removeCallbacksAndMessages(null);
        sfx.release();
        super.onDestroy();
    }
    boolean isCurrent(Screen s) {
        return !isGone() && current == s;
    }
    boolean isGone() {
        return destroyed || isFinishing();
    }
    @Override
    public void onBackPressed() {
        if (modalHolder.getChildCount() > 0) {
            if (!modalLocked) closeModal();
            return;
        }
        if (current != null && current.onBack()) return;
        super.onBackPressed();
    }
    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] results) {
        super.onRequestPermissionsResult(code, permissions, results);
        boolean granted = results.length > 0;
        for (int r : results) {
            if (r != 0) granted = false;
        }
        if (code == CalendarHelper.REQUEST) {
            Runnable next = afterCalendarPermission;
            afterCalendarPermission = null;
            if (granted && next != null) {
                next.run();
            } else if (!granted) {
                flash(L.t("No calendar access. You can still add events one by one.",
                        "Tanpa akses kalender. Kamu tetap bisa menambah acara satu per satu."));
            }
        } else if (code == REQ_NOTIF) {
            profile.dailyNews = granted;
            saveProfile();
            DailyReceiver.schedule(this, granted);
            rerender();
        }
    }
    // ----------------------------------------------------------------navigation
    private void setScreen(Screen s) {
        closeModal();
        current = s;
        screenHolder.removeAllViews();
        screenHolder.addView(s.build(), new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
    }
    /** Rebuild the current screen (after a language / theme / state change). */
    void rerender() {
        if (current == null) return;
        closeModal();
        screenHolder.removeAllViews();
        screenHolder.addView(current.build(), new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
    }
    void showWelcome() {
        setScreen(new Screen(this) {
            @Override
            View build() {
                FrameLayout box = new FrameLayout(a);
                box.addView(new WelcomeView(a, a.sfx, new WelcomeView.Listener() {
                    @Override
                    public void onFinish() {
                        store.markWelcomeSeen();
                        showHome();
                    }
                }), new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Ui.WRAP, Ui.WRAP,
                        Gravity.TOP | Gravity.END);
                lp.setMargins(0, ui.dp(36), ui.dp(14), 0);
                box.addView(toolbarRight(false), lp);
                return box;
            }
            @Override
            int id() { return S_WELCOME; }
        });
    }
    void showHome() {
        setScreen(new HomeScreen(this));
    }
    /** @param fresh true = start a new quiz, false = continue the one in progress. */
    void showQuiz(boolean fresh) {
        if (fresh) {
            quizStrategy = profile.strategy;
            rec = new MajorRecommender(Coach.questionsFor(quizStrategy), newSeed());
            answerMs.clear();
            store.clearProgress();
        }
        setScreen(new QuizScreen(this));
    }
    void showResults(MajorRecommender done, boolean celebrate) {
        setScreen(new ResultScreen(this, done, celebrate));
    }
    void showUniv(int majorIndex) {
        setScreen(new UnivScreen(this, majorIndex));
    }
    void showCoach(int tab) {
        if (!profile.hasDream()) {
            showHome();
            return;
        }
        setScreen(new CoachScreen(this, tab));
    }
    // ------------------------------------------------------------------quiz data
    private void restoreQuiz() {
        Store.Progress p = store.progress();
        quizStrategy = profile.strategy;
        rec = new MajorRecommender(Coach.questionsFor(quizStrategy), newSeed());
        answerMs.clear();
        if (p == null) return;
        MajorRecommender r = new MajorRecommender(p.limit, p.seed, false);
        try {
            for (int answer : p.answers) {
                if (r.isComplete()) break;
                r.answer(answer);
            }
        } catch (RuntimeException e) {
            return;
        }
        r.enableEarlyStopping();
        rec = r;
        quizStrategy = p.strategy;
        for (int i = 0; i < r.answeredCount(); i++) {
            answerMs.add(i < p.ms.length ? p.ms[i] : 0L);
        }
    }
    void persistQuiz() {
        if (rec.answeredCount() == 0 || rec.isComplete()) {
            store.clearProgress();
        } else {
            store.setProgress(rec.questionLimit(), quizStrategy, rec.answerHistory(), answerMs, rec.seed());
        }
    }
    boolean quizInProgress() {
        return rec != null && rec.answeredCount() > 0 && !rec.isComplete();
    }
    /** A versioned non-zero seed keeps older saved quizzes on their original question bank. */
    private static long newSeed() {
        long s = new java.util.Random().nextLong()
                & (MajorRecommender.EXPANDED_QUESTION_BANK_SEED_FLAG - 1L);
        return MajorRecommender.EXPANDED_QUESTION_BANK_SEED_FLAG | (s == 0L ? 1L : s);
    }
    /** Re-create a finished quiz from a saved history entry. Null if it cannot be replayed. */
    MajorRecommender replay(History.Entry entry) {
        try {
            MajorRecommender r = new MajorRecommender(entry.answers.length, entry.seed, false);
            for (int answer : entry.answers) r.answer(answer);
            if (entry.duel >= 0) r.setDuelWinner(entry.duel);
            return r.isComplete() ? r : null;
        } catch (RuntimeException e) {
            return null;
        }
    }
    MajorRecommender latestRec() {
        List<History.Entry> list = store.history();
        return list.isEmpty() ? null : replay(list.get(0));
    }
    /** Called when the last question is answered. DEEP mode may first ask a tie-break duel. */
    void completeQuiz() {
        List<MajorRecommender.Recommendation> recs = rec.recommendations();
        if (quizStrategy == Profile.STRAT_DEEP && recs.size() >= 2
                && recs.get(0).matchPercent - recs.get(1).matchPercent <= 10) {
            Forms.duel(this, recs.get(0), recs.get(1));
            return;
        }
        finishQuiz(-1);
    }
    void finishQuiz(int duelWinner) {
        long now = System.currentTimeMillis();
        if (duelWinner >= 0) rec.setDuelWinner(duelWinner);
        int[] answers = rec.answerHistory();
        long[] ms = new long[answerMs.size()];
        for (int i = 0; i < ms.length; i++) ms[i] = answerMs.get(i);
        profile.quiz = Coach.quizStats(answers, ms, rec.askedIndexes(), now);
        int score = Coach.assess(profile, rec.interestProfile(), now);
        profile.strategy = Coach.pickStrategy(score);
        store.addHistory(new History.Entry(now, answers, duelWinner, rec.seed()));
        store.clearProgress();
        saveProfile();
        MajorRecommender finished = rec;
        showResults(finished, true);
    }
    /** Fit points of the dream university + major, or null when none is chosen. */
    Coach.Fit currentFit() {
        Univ u = Univ.byId(profile.dreamUniv);
        if (u == null || profile.dreamMajor < 0) return null;
        MajorRecommender r = latestRec();
        int pct = r == null ? 50 : r.matchPercentFor(profile.dreamMajor);
        return Coach.fit(pct, u, Fields.of(profile.dreamMajor), profile, System.currentTimeMillis());
    }
    void saveProfile() {
        store.saveProfile(profile);
    }
    // -------------------------------------------------------------------settings
    void applySystemBars() {
        int bg = Pal.bg();
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        View decor = getWindow().getDecorView();
        int flags = decor.getSystemUiVisibility();
        int light = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                | (android.os.Build.VERSION.SDK_INT >= 26 ? View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR : 0);
        decor.setSystemUiVisibility(Pal.dark ? (flags & ~light) : (flags | light));
    }
    /** Language switch + sound + (optional) theme buttons. */
    LinearLayout toolbarRight(boolean withTheme) {
        LinearLayout row = ui.hbox();
        row.addView(ui.langToggle(new Runnable() {
            @Override
            public void run() {
                store.setLangId(L.id);
                rerender();
            }
        }), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 8, 0));
        final BrutalButton sound = ui.iconButton(sfx.isEnabled() ? Icons.SOUND_ON : Icons.SOUND_OFF, Pal.PAPER,
                L.t("Sound", "Suara"));
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean on = !sfx.isEnabled();
                sfx.setEnabled(on);
                store.setSound(on);
                sound.setIcon(on ? Icons.SOUND_ON : Icons.SOUND_OFF);
                if (on) sfx.toggle();
            }
        });
        row.addView(sound);
        if (withTheme) {
            BrutalButton theme = ui.iconButton(Pal.dark ? Icons.SUN : Icons.MOON, Pal.PAPER,
                    L.t("Light / dark green", "Hijau terang / gelap"));
            theme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Pal.dark = !Pal.dark;
                    store.setDark(Pal.dark);
                    sfx.toggle();
                    applySystemBars();
                    rerender();
                }
            });
            row.addView(theme, ui.lp(Ui.WRAP, Ui.WRAP, 8, 0, 0, 0));
        }
        return row;
    }
    // ----------------------------------------------------------------pop-ups
    /** Show a window gadget on a dim background. Tap outside = close (if dismissable). */
    void showModal(Ui.Window window, boolean dismissable) {
        modalHolder.removeAllViews();
        modalLocked = !dismissable;
        FrameLayout scrim = new FrameLayout(this);
        scrim.setBackgroundColor(0xAA111111);
        scrim.setClickable(true);
        if (dismissable) {
            scrim.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeModal();
                }
            });
        }
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        FrameLayout inner = new FrameLayout(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Ui.MATCH, Ui.WRAP, Gravity.CENTER);
        lp.setMargins(ui.dp(18), ui.dp(24), ui.dp(18), ui.dp(24));
        window.root.setClickable(true);
        inner.addView(window.root, lp);
        scroll.addView(inner, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        scrim.addView(scroll, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        modalHolder.addView(scrim, new FrameLayout.LayoutParams(Ui.MATCH, Ui.MATCH));
        window.root.setAlpha(0f);
        window.root.setScaleX(0.85f);
        window.root.setScaleY(0.85f);
        window.root.setTranslationY(ui.dp(40));
        window.root.animate().alpha(1f).scaleX(1f).scaleY(1f).translationY(0f).setDuration(320)
                .setInterpolator(new OvershootInterpolator(1.2f)).start();
        sfx.pop(3);
    }
    void closeModal() {
        modalLocked = false;
        modalHolder.removeAllViews();
    }
    boolean modalOpen() {
        return modalHolder.getChildCount() > 0;
    }
    /** Short message at the bottom of the screen. */
    void flash(String message) {
        if (isGone()) return;
        flashHolder.removeAllViews();
        TextView tag = ui.tag(message, Pal.YELLOW, -1.5f);
        tag.setTextSize(12f);
        tag.setMaxWidth(getResources().getDisplayMetrics().widthPixels - ui.dp(40));
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Ui.WRAP, Ui.WRAP,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        lp.bottomMargin = ui.dp(28);
        flashHolder.addView(tag, lp);
        tag.setAlpha(0f);
        tag.setTranslationY(ui.dp(20));
        tag.animate().alpha(1f).translationY(0f).setDuration(220).start();
        handler.removeCallbacks(clearFlash);
        handler.postDelayed(clearFlash, 3200);
    }
    private final Runnable clearFlash = new Runnable() {
        @Override
        public void run() {
            if (!isGone()) flashHolder.removeAllViews();
        }
    };
    void requestNotifications() {
        if (android.os.Build.VERSION.SDK_INT >= 33 && !DailyReceiver.canNotify(this)) {
            requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS},REQ_NOTIF);
        } else {
            profile.dailyNews = true;
            saveProfile();
            DailyReceiver.schedule(this, true);
            rerender();
        }
    }
    /** Run {@code action} once the calendar permission is available (asks if needed). */
    void withCalendar(Runnable action) {
        if (CalendarHelper.hasPermission(this)) {
            action.run();
        } else {
            afterCalendarPermission = action;
            CalendarHelper.request(this);
        }
    }
    // ----------------------------------------------------------------utilities
    void openUrl(String url) {
        try {
            if (!url.startsWith("https://") && !url.startsWith("http://")) return;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (RuntimeException e) {
            flash(L.t("No app can open this link.", "Tidak ada aplikasi untuk membuka tautan ini."));
        }
    }
    void search(String site, String query) {
        try {
            String q = java.net.URLEncoder.encode(query, "UTF-8");
            if ("youtube".equals(site)) openUrl("https://www.youtube.com/results?search_query=" + q);
            else openUrl("https://www.google.com/search?q=" + q);
        } catch (java.io.UnsupportedEncodingException e) {
            // UTF-8 always exists
        }
    }
    static Locale locale() {
        return L.id ? Locale.forLanguageTag("id-ID") : Locale.ENGLISH;
    }
    /** "Tue, 21 Apr 2027" from yyyymmdd. */
    static String dateText(int ymd) {
        Calendar c = new GregorianCalendar(Plan.year(ymd), Plan.month(ymd) - 1, Plan.day(ymd), 12, 0);
        return new SimpleDateFormat("EEE, d MMM yyyy", locale()).format(new Date(c.getTimeInMillis()));
    }
    static String timeText(long millis) {
        return new SimpleDateFormat("EEE d MMM, HH:mm", locale()).format(new Date(millis));
    }
    static String clock(long millis) {
        return new SimpleDateFormat("HH:mm", Locale.US).format(new Date(millis));
    }
}
