package jurusanator;
import java.util.ArrayList;
import java.util.List;
/**
 * Tiny, dependency-free RSS reader (no XML parser, so no XML security issues).
 * It only pulls out title / link / pubDate of each &lt;item&gt;. Never throws.
 */
final class Rss {
    private Rss() {}
    static final class Item {
        final String title, link, date;
        Item(String title, String link, String date) {
            this.title = title; this.link = link; this.date = date;
        }
    }
    static List<Item> parse(String xml, int max) {
        List<Item> items = new ArrayList<>();
        if (xml == null) return items;
        int pos = 0;
        while (items.size() < max) {
            int start = xml.indexOf("<item>", pos);
            if (start < 0) break;
            int end = xml.indexOf("</item>", start);
            if (end < 0) break;
            String block = xml.substring(start, end);
            String title = tag(block, "title");
            String link = tag(block, "link");
            if (!title.isEmpty() && (link.startsWith("https://") || link.startsWith("http://"))) {
                items.add(new Item(title, link, tag(block, "pubDate")));
            }
            pos = end + 7;
        }
        return items;
    }
    private static String tag(String block, String name) {
        int a = block.indexOf("<" + name + ">");
        if (a < 0) return "";
        a += name.length() + 2;
        int b = block.indexOf("</" + name + ">", a);
        if (b < 0) return "";
        String raw = block.substring(a, b).trim();
        if (raw.startsWith("<![CDATA[") && raw.endsWith("]]>")) {
            raw = raw.substring(9, raw.length() - 3);
        }
        return decode(raw).trim();
    }
    static String decode(String s) {
        String out = s.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"")
                .replace("&apos;", "'").replace("&#39;", "'").replace("&amp;", "&");
        return out.replace('\n', ' ').replace('\t', ' ');
    }
    /** Cache format: one item per line, fields split by TAB. */
    static String encode(List<Item> items) {
        StringBuilder out = new StringBuilder();
        for (Item item : items) {
            if (out.length() > 0) out.append('\n');
            out.append(item.title.replace('\t', ' ').replace('\n', ' ')).append('\t')
                    .append(item.link).append('\t').append(item.date);
        }
        return out.toString();
    }
    static List<Item> decodeCache(String text) {
        List<Item> items = new ArrayList<>();
        if (text == null || text.isEmpty()) return items;
        for (String line : text.split("\n")) {
            String[] f = line.split("\t", -1);
            if (f.length == 3 && !f[0].isEmpty() && f[1].startsWith("http")) {
                items.add(new Item(f[0], f[1], f[2]));
            }
        }
        return items;
    }
}


