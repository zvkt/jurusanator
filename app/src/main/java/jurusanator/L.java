package jurusanator;
/** Tiny two-language helper: L.t("English", "Indonesia"). */ final class L {
    private L() {}
    /** true = Bahasa Indonesia, false = English. */
    static boolean id = true;
    static String t(String en, String idn) {
        return id ? idn : en;
    }
}


