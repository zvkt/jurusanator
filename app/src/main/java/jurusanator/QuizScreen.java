package jurusanator;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jurusanator.MajorData.Question;
/**
 * One question at a time, shown as a floating "window" gadget that bobs, springs in and flies
 * out. Tapping an answer moves on at once; Back undoes the last answer.
 */
final class QuizScreen extends Screen {
    private static final String[] HINTS = {"++", "+", "0", "\u2212", "\u2212\u2212"};
    private boolean busy;
    private boolean animate = true;
    private long shownAt;
    QuizScreen(MainActivity activity) {
        super(activity);
    }
    @Override
    int id() {
        return MainActivity.S_QUIZ;
    }
    @Override
    boolean onBack() {
        goBack();
        return true;
    }
    private void goBack() {
        if (busy) return;
        a.sfx.back();
        if (a.rec.answeredCount() > 0) {
            a.rec.undoLastAnswer();
            if (!a.answerMs.isEmpty()) a.answerMs.remove(a.answerMs.size() - 1);
            a.persistQuiz();
            animate = true;
            a.rerender();
        } else {
            a.showHome();
        }
    }
    @Override
    View build() {
        final MajorRecommender rec = a.rec;
        final Question q = rec.nextQuestion();
        if (q == null) {
            a.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (!a.isGone() && a.rec.isComplete()) a.completeQuiz();
                }
            });
            return new View(a);
        }
        LinearLayout col = column();
        BrutalButton back = ui.small("\u2190 " + L.t("BACK", "KEMBALI"), Pal.PAPER, Pal.INK);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
        col.addView(topBar(back, false));
        col.addView(ui.gap(16));
        int done = rec.answeredCount();
        int total = rec.questionLimit();
        String number = String.format(Locale.US, "%02d / %02d", done + 1, total);
        col.addView(ui.mono(L.t("QUESTION ", "PERTANYAAN ") + number, 12f, Pal.bgText()));
        col.addView(progress(done, total), ui.lp(Ui.MATCH, ui.dp(16), 0, 8, 0, 0));
        if (a.quizStrategy != Profile.STRAT_STANDARD) {
            String mode = a.quizStrategy == Profile.STRAT_ENGAGE
                    ? L.t("SHORT MODE: quick and simple", "MODE SINGKAT: cepat dan sederhana")
                    : L.t("DEEP MODE: more questions + a duel", "MODE MENDALAM: lebih banyak pertanyaan + duel");
            TextView tag = ui.tag(mode, Pal.LILAC, -1f);
            tag.setTextSize(10f);
            col.addView(tag, ui.lp(Ui.WRAP, Ui.WRAP, 2, 12, 0, 0));
        }
        col.addView(ui.gap(22));
        // the floating question window
        final Ui.Window window = ui.window(L.t("QUESTION ", "PERTANYAAN ") + String.format(Locale.US, "%02d", done + 1), Pal.PAPER);
        TextView text = ui.heavy(q.prompt(), 22f, Pal.INK);
        text.setLineSpacing(0f, 1.12f);
        window.body.addView(text);
        TextView hint = ui.mono(L.t("Pick the answer closest to you.", "Pilih jawaban yang paling mendekati dirimu."),
                11f, 0xFF5B6650);
        window.body.addView(hint, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
        FrameLayout bob = new FrameLayout(a);
        bob.addView(window.root, new FrameLayout.LayoutParams(Ui.MATCH, Ui.WRAP));
        bob.setRotation(-1.5f);
        col.addView(bob, ui.lp(Ui.MATCH, Ui.WRAP, 0, 0, 0, 22));
        ui.floatAnim(bob, 5f, 2600, 0.8f);
        if (animate) {
            window.root.setAlpha(0f);
            window.root.setTranslationY(ui.dp(80));
            window.root.setScaleX(0.85f);
            window.root.setScaleY(0.85f);
            window.root.setRotation(6f);
            window.root.animate().alpha(1f).translationY(0f).scaleX(1f).scaleY(1f).rotation(0f)
                    .setDuration(420).setInterpolator(new OvershootInterpolator(1.3f)).start();
            a.sfx.pop(done % 8);
        }
        // answers
        final List<BrutalButton> all = new ArrayList<>();
        int[] options = a.quizStrategy == Profile.STRAT_ENGAGE ? new int[]{0, 1, 3, 4} : new int[]{0, 1, 2, 3, 4};
        int index = 0;
        for (final int option : options) {
            final BrutalButton b = new BrutalButton(a, MajorData.answerLabel(option), Pal.ANSWER[option],
                    Pal.INK, 17f, ui.radius * 0.8f, ui.border, ui.shadow);
            b.label.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            b.label.setPadding(ui.dp(18), 0, ui.dp(60), 0);
            TextView mark = ui.mono(HINTS[option], 15f, Pal.INK);
            FrameLayout.LayoutParams ml = new FrameLayout.LayoutParams(Ui.WRAP, Ui.WRAP,
                    Gravity.END | Gravity.CENTER_VERTICAL);
            ml.rightMargin = ui.dp(16) + Math.round(ui.shadow);
            ml.bottomMargin = Math.round(ui.shadow);
            b.addView(mark, ml);
            b.setLayoutParams(ui.lp(Ui.MATCH, ui.dp(52) + Math.round(ui.shadow), 0, 0, 0, 6));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAnswer(option, b, window.root, all);
                }
            });
            if (animate) {
                b.setAlpha(0f);
                b.setTranslationX(ui.dp(60)); b.animate().alpha(1f).translationX(0f).setStartDelay(160 + index * 55L).setDuration(280)
                        .setInterpolator(new OvershootInterpolator(1.1f)).start();
            }
            all.add(b);
            col.addView(b);
            index++;
        }
        animate = false;
        shownAt = SystemClock.elapsedRealtime();
        return page(23, col);
    }
    private View progress(int done, int total) {
        LinearLayout row = ui.hbox();
        for (int i = 0; i < total; i++) {
            View seg = new View(a);
            int color = i < done ? Pal.FOREST : (i == done ? Pal.YELLOW : Pal.PAPER);
            seg.setBackground(new BrutalDrawable(color, ui.dp(3), ui.border * 0.7f, 0f, Pal.INK));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, Ui.MATCH, 1f);
            lp.setMargins(ui.dp(1.5f), 0, ui.dp(1.5f), 0);
            row.addView(seg, lp);
        }
        return row;
    }
    private void onAnswer(final int option, BrutalButton tapped, View card, List<BrutalButton> all) {
        if (busy) return;
        busy = true;
        final long ms = SystemClock.elapsedRealtime() - shownAt;
        a.sfx.pick(option);
        for (BrutalButton b : all) {
            if (b != tapped) b.animate().alpha(0.3f).setDuration(120).start();
        } tapped.animate().scaleX(1.03f).scaleY(1.03f).setDuration(120).start();
        card.animate().translationY(-ui.dp(70)).alpha(0f).rotation(-8f).setStartDelay(80).setDuration(200).start();
        a.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                busy = false;
                if (!a.isGone()) commit(option, ms);
            }
        }, 300);
    }
    private void commit(int option, long ms) {
        try {
            a.rec.answer(option);
        } catch (RuntimeException e) {
            a.showHome();
            return;
        }
        a.answerMs.add(ms);
        a.persistQuiz();
        animate = true;
        if (a.rec.isComplete()) a.completeQuiz();
        else a.rerender();
    }
}


