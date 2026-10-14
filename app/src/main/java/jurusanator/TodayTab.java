package jurusanator;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
/** TODAY: a fact about the university, today's news, "talked by" links, related topics. */
final class TodayTab extends CoachScreen.Tab {
    TodayTab(CoachScreen host) {
        super(host);
    }
    @Override
    void fill(LinearLayout col) {
        fact(col);
        news(col);
        talkedBy(col);
        topics(col);
    }
    // -------------------------------------------------------------------fact
    private void fact(LinearLayout col) {
        LinearLayout box = box(Pal.YELLOW, L.t("FACT OF THE DAY", "FAKTA HARI INI"), Pal.PAPER);
        TextView t = text(box, univ.fact(Coach.dayIndex(now)), 16f, 12);
        t.setLineSpacing(0f, 1.2f);
        mono(box, univ.name, 11f, 10);
        text(box, L.t("Based on public information. Please verify on the official website.",
                "Berdasarkan informasi publik. Mohon cek di situs resmi."), 11f, 4);
        col.addView(box);
    }
    // -------------------------------------------------------------------news
    private String todayKey() {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(now);
        int ymd = c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH) + 1) * 100 + c.get(Calendar.DAY_OF_MONTH);
        return ymd + "|" + (L.id ? "id" : "en");
    }
    private void news(LinearLayout col) {
        LinearLayout box = box(Pal.PAPER, L.t("NEWS \u00B7 TODAY", "BERITA \u00B7 HARI INI"), Pal.SKY);
        if (host.newsStatus == 0) loadNews(false);
        String status;
        switch (host.newsStatus) {
            case 1: status = L.t("Loading today's news...", "Memuat berita hari ini..."); break;
            case 2: status = L.t("Updated today", "Diperbarui hari ini"); break;
            case 3: status = L.t("Offline: showing saved news", "Offline: menampilkan berita tersimpan"); break;
            default: status = L.t("Could not load news. Check your connection.", "Gagal memuat berita. Cek koneksimu."); break;
        }
        mono(box, status, 10.5f, 8);
        if (host.newsStatus == 2 && host.newsItems.isEmpty()) {
            text(box, L.t("No news found for today.", "Tidak ada berita untuk hari ini."), 13f, 8);
        }
        int shown = 0;
        for (final Rss.Item item : host.newsItems) {
            if (shown++ >= 4) break;
            TextView row = ui.body(item.title, 13.5f, Pal.INK);
            row.setPadding(0, ui.dp(10), 0, ui.dp(10));
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.openUrl(item.link);
                }
            });
            View rule = new View(a);
            rule.setBackgroundColor(0x33111111);
            box.addView(rule, ui.lp(Ui.MATCH, ui.dp(1.5f), 0, shown == 1 ? 6 : 0, 0, 0));
            box.addView(row);
        }
        LinearLayout buttons = ui.hbox();
        buttons.addView(smallAction(L.t("REFRESH", "MUAT ULANG"), Pal.LIME, new Runnable() {
            @Override
            public void run() {
                loadNews(true);
                a.rerender();
            }
        }), ui.lp(Ui.WRAP, Ui.WRAP, 0, 0, 8, 0));
        String alert = p.dailyNews ? L.t("DAILY ALERT: ON", "NOTIF HARIAN: AKTIF") : L.t("DAILY ALERT: OFF", "NOTIF HARIAN: MATI");
        buttons.addView(smallAction(alert, p.dailyNews ? Pal.YELLOW : Pal.PAPER, new Runnable() {
            @Override
            public void run() {
                if (p.dailyNews) {
                    p.dailyNews = false;
                    a.saveProfile();
                    DailyReceiver.schedule(a, false);
                    a.rerender();
                } else {
                    a.requestNotifications();
                }
            }
        }));
        box.addView(buttons, ui.lp(Ui.MATCH, Ui.WRAP, 0, 12, 0, 0));
        text(box, L.t("News comes from Google News search results. A daily alert at about 07:30 shows the top headline.",
                "Berita dari hasil pencarian Google News. Notifikasi harian sekitar 07.30 menampilkan berita teratas."), 10.5f, 8);
        col.addView(box);
    }
    private void loadNews(boolean force) {
        final String today = todayKey();
        String cache = a.store.newsCache(univ.id);
        if (!force && cache.startsWith(today + "\n")) {
            host.newsItems = Rss.decodeCache(cache.substring(today.length() + 1));
            host.newsStatus = 2;
            return;
        }
        host.newsStatus = 1;
        final CoachScreen h = host;
        final int univId = univ.id;
        NewsClient.fetch(univ, L.id, new NewsClient.Callback() {
            @Override
            public void done(List<Rss.Item> items, boolean ok) {
                if (a.isGone()) return;
                if (ok) {
                    h.newsItems = items;
                    h.newsStatus = 2;
                    a.store.setNewsCache(univId, today + "\n" + Rss.encode(items));
                } else {
                    String saved = a.store.newsCache(univId);
                    int nl = saved.indexOf('\n');
                    List<Rss.Item> old = nl < 0 ? new ArrayList<Rss.Item>() : Rss.decodeCache(saved.substring(nl + 1));
                    h.newsItems = old;
                    h.newsStatus = old.isEmpty() ? 4 : 3;
                }
                if (a.isCurrent(h) && h.tab() == CoachScreen.T_TODAY) a.rerender();
            }
        });
    }
    // --------------------------------------------------------------talked by
    private void talkedBy(LinearLayout col) {
        LinearLayout box = box(Pal.LILAC, L.t("TALKED BY", "DIBICARAKAN OLEH"), Pal.PAPER);
        text(box, L.t("Hear it from real students and alumni, not just brochures.",
                "Dengar langsung dari mahasiswa dan alumni, bukan cuma brosur."), 14f, 10);
        final String who = major.name() + " " + univ.shortName;
        box.addView(link(L.t("STUDENT STORIES (YOUTUBE)", "CERITA MAHASISWA (YOUTUBE)"), "youtube",
                who + (L.id ? " cerita mahasiswa" : " student life")), ui.lp(Ui.MATCH, Ui.WRAP, 0, 10, 0, 0));
        box.addView(link(L.t("ALUMNI & CAREERS", "ALUMNI & KARIER"), "web",
                (L.id ? "alumni karier " : "alumni careers ") + who), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        box.addView(link(L.t("STUDENT DISCUSSIONS", "DISKUSI MAHASISWA"), "web",
                who + (L.id ? " diskusi mahasiswa" : " students discussion")), ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        col.addView(box);
    }
    private View link(String label, final String site, final String query) {
        BrutalButton b = ui.button(label, Pal.PAPER, Pal.INK, 42);
        b.label.setTextSize(12f);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a.sfx.tap();
                a.search(site, query);
            }
        });
        return b;
    }
    // -----------------------------------------------------------------topics
    private void topics(LinearLayout col) {
        LinearLayout box = box(Pal.MINT, L.t("RELATED TOPICS", "TOPIK TERKAIT"), Pal.PAPER);
        text(box, L.t("Good things to explore before you start " + major.name() + ". Tap one to search.",
                "Hal bagus untuk dijelajahi sebelum mulai kuliah " + major.name() + ". Ketuk untuk mencari."), 13.5f, 10);
        for (final String topic : Fields.topics(field)) {
            TextView chip = ui.chip(topic, Pal.PAPER);
            chip.setTextSize(13f);
            chip.setClickable(true);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    a.sfx.tap();
                    a.search("web", topic + " " + major.name());
                }
            });
            box.addView(chip, ui.lp(Ui.MATCH, Ui.WRAP, 0, 8, 0, 0));
        }
        col.addView(box);
    }
}


