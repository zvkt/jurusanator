package jurusanator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/**
 * Built-in university list. EDIT THIS FILE to add or fix universities.
 *
 * - strong         : fields the university is generally known for (a rough reputation
 *                   guide, NOT an official ranking or a full list of programs).
 * - selectivity : 1..3, how competitive entry is generally assumed to be (an estimate).
 * - facts          : short, stable facts. Always double-check on the official website.
 */
final class Univ {
    final int id;
    final String name;
    final String shortName;
    final String city;
    final String type;           // PTN / PTS / Poltek
    final int selectivity;
    final int strong;            // bit mask of Fields.*
    final String[] factsEn;
    final String[] factsId;
    private Univ(int id, String name, String shortName, String city, String type, int selectivity,
            int strong, String[] factsEn, String[] factsId) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.city = city;
        this.type = type;
        this.selectivity = selectivity;
        this.strong = strong;
        this.factsEn = factsEn;
        this.factsId = factsId;
    }
    boolean isStrongIn(int field) {
        return (strong & (1 << field)) != 0;
    }
    String fact(int index) {
        String[] src = L.id ? factsId : factsEn;
        return src[Math.abs(index) % src.length];
    }
    int factCount() {
        return factsEn.length;
    }
    /** Fields this university is known for, as a readable list. */
    String strongNames() {
        StringBuilder out = new StringBuilder();
        for (int f = 0; f < Fields.COUNT; f++) {
            if (isStrongIn(f)) {
                if (out.length() > 0) out.append(", ");
                out.append(Fields.name(f));
            }
        }
        return out.toString();
    }
    // ----------------------------------------------------------------list
    private static int m(int... fields) {
        int mask = 0;
        for (int f : fields) mask |= 1 << f;
        return mask;
    }
    private static final int ALL = (1 << Fields.COUNT) - 1;
    private static final List<Univ> LIST = new ArrayList<>();
    private static void add(String name, String shortName, String city, String type, int sel, int strong,
            String en1, String id1, String en2, String id2) {
        LIST.add(new Univ(LIST.size(), name, shortName, city, type, sel, strong,
                new String[]{en1, en2}, new String[]{id1, id2}));
    }
    static {
        final int C = Fields.COMPUTING, S = Fields.SCIENCE, E = Fields.ENGINEERING, H = Fields.HEALTH,
                SO = Fields.SOCIAL, HU = Fields.HUMANITIES, B = Fields.BUSINESS, A = Fields.ARTS,
                ED = Fields.EDUCATION, AG = Fields.AGRI;
        add("Universitas Indonesia", "UI", "Depok", "PTN", 3, m(C, S, E, H, SO, HU, B),
                "Its roots go back to the Dokter Djawa School, founded in 1849.",
                "Akarnya berasal dari Sekolah Dokter Djawa yang berdiri tahun 1849.",
                "The main campus is in Depok, West Java.",
                "Kampus utamanya berada di Depok, Jawa Barat.");
        add("Institut Teknologi Bandung", "ITB", "Bandung", "PTN", 3, m(C, S, E, A, B),
                "Founded on 3 July 1920 as De Technische Hoogeschool te Bandoeng.",
                "Berdiri 3 Juli 1920 sebagai De Technische Hoogeschool te Bandoeng.",
                "Indonesia's first president, Sukarno, graduated here in 1926 as a civil engineer.",
                "Presiden pertama Indonesia, Sukarno, lulus di sini tahun 1926 sebagai insinyur sipil.");
        add("Universitas Gadjah Mada", "UGM", "Yogyakarta", "PTN", 3, m(C, S, E, H, SO, HU, B, AG),
                "Founded on 19 December 1949 in Yogyakarta.",
                "Berdiri pada 19 Desember 1949 di Yogyakarta.",
                "Yogyakarta is famous as a student city.",
                "Yogyakarta terkenal sebagai kota pelajar.");
        add("Institut Teknologi Sepuluh Nopember", "ITS", "Surabaya", "PTN", 2, m(C, S, E, A, B, AG),
                "Founded on 10 November 1957, which is Indonesia's Heroes' Day.",
                "Berdiri pada 10 November 1957, bertepatan dengan Hari Pahlawan.",
                "The campus is in Sukolilo, Surabaya.",
                "Kampusnya berada di Sukolilo, Surabaya.");
        add("Universitas Airlangga", "Unair", "Surabaya", "PTN", 2, m(C, S, H, SO, HU, B, AG),
                "Founded in 1954; its medical roots go back to a school started in 1913.",
                "Berdiri tahun 1954; akar pendidikan kedokterannya berasal dari sekolah tahun 1913.",
                "It has campuses in several parts of Surabaya.",
                "Memiliki kampus di beberapa bagian Kota Surabaya.");
        add("IPB University", "IPB", "Bogor", "PTN", 2, m(AG, S, C, B, H),
                "Founded in 1963 as the Bogor Agricultural Institute.",
                "Berdiri tahun 1963 sebagai Institut Pertanian Bogor.",
                "The main campus is in Dramaga, Bogor.",
                "Kampus utamanya berada di Dramaga, Bogor.");
        add("Universitas Diponegoro", "Undip", "Semarang", "PTN", 2, ALL & ~m(A, ED),
                "Founded in 1957 in Semarang.",
                "Berdiri tahun 1957 di Semarang.",
                "The main campus is in Tembalang, Semarang.",
                "Kampus utamanya berada di Tembalang, Semarang.");
        add("Universitas Padjadjaran", "Unpad", "Bandung", "PTN", 2, m(H, SO, HU, B, AG, S),
                "Founded on 11 September 1957.",
                "Berdiri pada 11 September 1957.",
                "It has a large campus in Jatinangor.",
                "Memiliki kampus besar di Jatinangor.");
        add("Universitas Brawijaya", "UB", "Malang", "PTN", 2, m(E, AG, H, SO, B, S, C, HU),
                "Founded in 1963 in Malang, East Java.",
                "Berdiri tahun 1963 di Malang, Jawa Timur.",
                "Malang is a popular student city in East Java.",
                "Malang adalah kota pelajar yang populer di Jawa Timur.");
        add("Universitas Sebelas Maret", "UNS", "Surakarta", "PTN", 2, ALL,
                "Founded on 11 March 1976 in Surakarta (Solo).",
                "Berdiri pada 11 Maret 1976 di Surakarta (Solo).",
                "Its name refers to 11 March, the date of the Supersemar letter (1966).",
                "Namanya merujuk pada 11 Maret, tanggal Surat Perintah Sebelas Maret (1966).");
        add("Universitas Hasanuddin", "Unhas", "Makassar", "PTN", 2, m(H, AG, E, SO, S, B, HU),
                "Founded on 10 September 1956 in Makassar.",
                "Berdiri pada 10 September 1956 di Makassar.",
                "It is one of the biggest universities in eastern Indonesia.",
                "Salah satu universitas terbesar di Indonesia bagian timur.");
        add("Universitas Udayana", "Unud", "Bali", "PTN", 2, m(B, H, AG, E, S, SO, HU, C),
                "Founded on 29 September 1962 in Bali.",
                "Berdiri pada 29 September 1962 di Bali.",
                "A main campus is at Bukit Jimbaran, Bali.",
                "Salah satu kampus utamanya ada di Bukit Jimbaran, Bali.");
        add("Universitas Negeri Surabaya", "Unesa", "Surabaya", "PTN", 1, m(ED, A, SO, HU, E, B, S, C),
                "It started as a teacher-training institute (IKIP) in 1964.",
                "Bermula sebagai institut pendidikan guru (IKIP) tahun 1964.",
                "It is a state university based in Surabaya.",
                "Universitas negeri yang berbasis di Surabaya.");
        add("Universitas Pendidikan Indonesia", "UPI", "Bandung", "PTN", 2, m(ED, S, HU, A, C, B),
                "Its roots are in teacher education from 1954.",
                "Akarnya adalah pendidikan guru sejak tahun 1954.",
                "It became a university (UPI) in 1999.",
                "Menjadi universitas (UPI) pada tahun 1999.");
        add("Universitas Negeri Yogyakarta", "UNY", "Yogyakarta", "PTN", 1, m(ED, S, A, E, HU, SO),
                "It started as IKIP Yogyakarta in 1964.",
                "Bermula sebagai IKIP Yogyakarta tahun 1964.",
                "It is a state university in Yogyakarta.",
                "Universitas negeri di Yogyakarta.");
        add("Universitas Andalas", "Unand", "Padang", "PTN", 2, m(H, AG, S, E, SO, HU, B, C),
                "Founded on 13 September 1955 in Padang, West Sumatra.",
                "Berdiri pada 13 September 1955 di Padang, Sumatera Barat.",
                "The main campus is at Limau Manis.",
                "Kampus utamanya berada di Limau Manis.");
        add("Universitas Sriwijaya", "Unsri", "Palembang", "PTN", 1, m(E, H, AG, SO, B, S, ED, C),
                "Founded on 3 November 1960 in South Sumatra.",
                "Berdiri pada 3 November 1960 di Sumatera Selatan.",
                "A large campus is in Indralaya.",
                "Memiliki kampus besar di Indralaya.");
        add("Bina Nusantara University", "Binus", "Jakarta", "PTS", 1, m(C, B, A, HU),
                "It started in 1974 as a computer course.",
                "Bermula tahun 1974 sebagai kursus komputer.",
                "It is a private university with several campuses.",
                "Universitas swasta dengan beberapa kampus.");
        add("Telkom University", "Tel-U", "Bandung", "PTS", 1, m(C, E, B, A, HU),
                "Formed in 2013 from several Telkom-related institutes.",
                "Terbentuk tahun 2013 dari beberapa institut yang terkait Telkom.",
                "It is a private university based in Bandung.",
                "Universitas swasta yang berbasis di Bandung.");
        add("Universitas Kristen Petra", "Petra", "Surabaya", "PTS", 1, m(E, B, A, C, HU),
                "Founded in 1961 in Surabaya.",
                "Berdiri tahun 1961 di Surabaya.",
                "It is a private university in Surabaya.",
                "Universitas swasta di Surabaya.");
        add("Universitas Islam Indonesia", "UII", "Yogyakarta", "PTS", 1, m(SO, E, H, B, S, C),
                "Its origins go back to 8 July 1945.",
                "Asal-usulnya berawal dari 8 Juli 1945.",
                "It is one of the oldest private universities in Indonesia.",
                "Salah satu universitas swasta tertua di Indonesia.");
        add("Politeknik Elektronika Negeri Surabaya", "PENS", "Surabaya", "Poltek", 1, m(C, E),
                "A state polytechnic focused on electronics and IT.",
                "Politeknik negeri yang berfokus pada elektronika dan TI.",
                "It is based in Surabaya, on the ITS campus area.",
                "Berlokasi di Surabaya, di area kampus ITS.");
    }
    static List<Univ> all() {
        return Collections.unmodifiableList(LIST);
    }
    static Univ byId(int id) {
        if (id < 0 || id >= LIST.size()) return null;
        return LIST.get(id);
    }
    /** All universities, those strong in {@code field} first, then by name. */
    static List<Univ> forField(final int field) {
        List<Univ> sorted = new ArrayList<>(LIST);
        Collections.sort(sorted, new Comparator<Univ>() {
            @Override
            public int compare(Univ a, Univ b) {
                int sa = a.isStrongIn(field) ? 0 : 1;
                int sb = b.isStrongIn(field) ? 0 : 1;
                if (sa != sb) return sa - sb;
                return a.name.compareTo(b.name);
            }
        });
        return sorted;
    }
}


