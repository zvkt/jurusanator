package jurusanator;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Looper;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * Sound effects built from maths (square / sine waves + noise): no audio files needed.
 * Safety: every call is wrapped, at most a few sounds play at once, and a failing audio
 * device only means silence, never a crash.
 */
final class Sfx {
    private static final int RATE = 22050;
    private static final int MAX_LIVE = 6;
    private static final float[] SCALE = {523f, 587f, 659f, 698f, 784f, 880f, 988f, 1047f};
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final List<AudioTrack> live = new ArrayList<>();
    private final short[] tap, back, slam, ding, win, whoosh, toggle;
    private final short[][] pick = new short[5][];
    private final short[][] pop = new short[8][];
    private boolean enabled = true;
    private boolean released;
    Sfx() {
        tap = new Wave().note(660, 520, 55, Wave.SQUARE, 0.30f, 1.6f).done();
        toggle = new Wave().note(500, 760, 70, Wave.SQUARE, 0.28f, 1.4f).done();
        back = new Wave().note(520, 330, 90, Wave.SQUARE, 0.30f, 1.4f).done();
        float[] pitch = {880, 740, 600, 480, 360};
        for (int i = 0; i < 5; i++) {
            pick[i] = new Wave().note(pitch[i], pitch[i] * 1.25f, 60, Wave.SQUARE, 0.30f, 1.3f)
                    .note(pitch[i] * 1.25f, pitch[i] * 1.5f, 80, Wave.SQUARE, 0.28f, 1.8f).done();
        }
        for (int i = 0; i < 8; i++) {
            pop[i] = new Wave().note(SCALE[i] * 0.8f, SCALE[i] * 1.6f, 90, Wave.SINE, 0.45f, 2.0f).done();
        }
        slam = new Wave().note(180, 45, 260, Wave.SINE, 0.80f, 1.3f).noise(120, 0.35f).done();
        ding = new Wave().note(1047, 1047, 140, Wave.SINE, 0.40f, 1.2f)
                .note(1319, 1319, 320, Wave.SINE, 0.40f, 1.6f).done();
        win = new Wave().note(523, 523, 110, Wave.SQUARE, 0.28f, 1.0f)
                .note(659, 659, 110, Wave.SQUARE, 0.28f, 1.0f)
                .note(784, 784, 110, Wave.SQUARE, 0.28f, 1.0f)
                .note(1047, 1047, 130, Wave.SQUARE, 0.30f, 1.0f)
                .note(784, 784, 90, Wave.SQUARE, 0.26f, 1.0f)
                .note(1047, 1047, 420, Wave.SQUARE, 0.32f, 1.6f).done();
        whoosh = new Wave().noiseSweep(380, 0.40f).done();
    }
    void setEnabled(boolean value) {
        enabled = value;
    }
    boolean isEnabled() {
        return enabled;
    }
    void tap() { play(tap); }
    void toggle() { play(toggle); }
    void back() { play(back); }
    void slam() { play(slam); }
    void ding() { play(ding); }
    void win() { play(win); }
    void whoosh() { play(whoosh); }
    void pick(int answerIndex) {
        if (answerIndex >= 0 && answerIndex < pick.length) play(pick[answerIndex]);
    }
    void pop(int step) {
        play(pop[Math.max(0, Math.min(pop.length - 1, step))]);
    }
    /** Stop everything. Call from onDestroy. */
    void release() {
        released = true;
        handler.removeCallbacksAndMessages(null);
        for (AudioTrack track : new ArrayList<>(live)) {
            free(track);
        }
        live.clear();
    }
    private void play(short[] pcm) {
        if (!enabled || released || pcm == null || pcm.length == 0 || live.size() >= MAX_LIVE) {
            return;
        }
        try {
            final AudioTrack track = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build())
                    .setAudioFormat(new AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(RATE)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build())
                    .setBufferSizeInBytes(pcm.length * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build();
            if (track.getState() != AudioTrack.STATE_INITIALIZED) {
                track.release();
                return;
            }
            track.write(pcm, 0, pcm.length);
            track.play();
            live.add(track);
            long ms = pcm.length * 1000L / RATE + 150L;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    live.remove(track);
                    free(track);
                }
            }, ms);
        } catch (RuntimeException e) {
            // Sound is optional. Stay silent instead of crashing.
        }
    }
    private static void free(AudioTrack track) {
        try {
            track.stop();
        } catch (RuntimeException ignored) {
            // already stopped
        }
        try {
            track.release();
        } catch (RuntimeException ignored) {
            // already released
        }
    }
    /** Builds a mono 16-bit buffer from notes. */
    private static final class Wave {
        static final int SINE = 0, SQUARE = 1;
        private float[] data = new float[RATE / 2];
        private int len;
        private final Random rnd = new Random(7);
        private void ensure(int extra) {
            if (len + extra > data.length) {
                float[] bigger = new float[Math.max(data.length * 2, len + extra)];
                System.arraycopy(data, 0, bigger, 0, len);
                data = bigger;
            }
        }
        Wave note(float f0, float f1, int ms, int shape, float vol, float decay) {
            int n = RATE * ms / 1000;
            ensure(n);
            double phase = 0;
            for (int i = 0; i < n; i++) {
                float x = i / (float) n;
                double f = f0 + (f1 - f0) * x;
                phase += 2 * Math.PI * f / RATE;
                float s = (float) Math.sin(phase);
                if (shape == SQUARE) s = s >= 0 ? 0.6f : -0.6f;
                float attack = Math.min(1f, i / (RATE * 0.004f));
                float env = attack * (float) Math.pow(1f - x, decay);
                data[len++] = s * env * vol;
            }
            return this;
        }
        Wave noise(int ms, float vol) {
            int n = RATE * ms / 1000;
            ensure(n);
            // mixes into the start of the previous sound
            int start = Math.max(0, len - n);
            for (int i = 0; i < n; i++) {
                float env = (float) Math.pow(1f - i / (float) n, 2.0);
                data[start + i] += (rnd.nextFloat() * 2f - 1f) * env * vol;
            }
            return this;
        }
        Wave noiseSweep(int ms, float vol) {
            int n = RATE * ms / 1000;
            ensure(n);
            float y = 0f;
            for (int i = 0; i < n; i++) {
                float x = i / (float) n;
                float a = 0.03f + 0.5f * x * x;
                y += a * ((rnd.nextFloat() * 2f - 1f) - y);
                float env = (float) Math.sin(Math.PI * x);
                data[len++] = y * env * vol * 2f;
            }
            return this;
        }
        short[] done() {
            short[] out = new short[len];
            for (int i = 0; i < len; i++) {
                float v = Math.max(-1f, Math.min(1f, data[i]));
                out[i] = (short) (v * 32000f);
            }
            return out;
        }
    }
}


