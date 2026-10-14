package jurusanator;
import android.os.Handler;
import android.os.Looper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
/**
 * Daily university news from the Google News RSS search feed (HTTPS only).
 * Network runs on a background thread; the result is delivered on the main thread.
 * Any failure (offline, blocked, slow) simply reports ok=false. */
final class NewsClient {
    private NewsClient() {}
    interface Callback {
        void done(List<Rss.Item> items, boolean ok);
    }
    private static final int MAX_BYTES = 400 * 1024;
    private static final Handler MAIN = new Handler(Looper.getMainLooper());
    static void fetch(final Univ univ, final boolean indonesian, final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Rss.Item> items = new ArrayList<>();
                boolean ok = false;
                try {
                    items = fetchBlocking(univ, indonesian);
                    ok = true;
                } catch (IOException | RuntimeException e) {
                    // offline or blocked: report failure below
                }
                final List<Rss.Item> result = items;
                final boolean success = ok;
                MAIN.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.done(result, success);
                    }
                });
            }
        }, "news").start();
    }
    /** Blocking version (also used by the daily notification). Never call on the UI thread. */
    static List<Rss.Item> fetchBlocking(Univ univ, boolean indonesian) throws IOException {
        String q = URLEncoder.encode("\"" + univ.name + "\"", "UTF-8");
        String url = "https://news.google.com/rss/search?q=" + q
                + (indonesian ? "&hl=id&gl=ID&ceid=ID:id" : "&hl=en&gl=ID&ceid=ID:en");
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        try {
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android) Jurusanator");
            if (conn.getResponseCode() != 200) throw new IOException("HTTP " + conn.getResponseCode());
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0 && out.size() < MAX_BYTES) {
                out.write(buf, 0, n);
            }
            in.close();
            return Rss.parse(out.toString("UTF-8"), 6);
        } finally {
            conn.disconnect();
        }
    }
}


