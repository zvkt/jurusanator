package jurusanator;
/**
 * The 7 UTBK-SNBT sub-tests (structure checked against 2026 sources):
 * Tes Potensi Skolastik (PU, PPU, PBM, PK) + Tes Literasi (LBI, LBE, PM).
 * Used for the weakness / strength detector and the study plan. */
final class Skills {
    private Skills() {}
    static final int PU = 0, PPU = 1, PBM = 2, PK = 3, LBI = 4, LBE = 5, PM = 6;
    static final int COUNT = 7;
    private static final String[] CODE = {"PU", "PPU", "PBM", "PK", "LBI", "LBE", "PM"};
    private static final String[] NAME_EN = {
        "General Reasoning", "General Knowledge", "Reading & Writing",
                "Quantitative Knowledge", "Indonesian Literacy", "English Literacy", "Math Reasoning"};
    private static final String[] NAME_ID = {
        "Penalaran Umum", "Pengetahuan & Pemahaman Umum", "Pemahaman Bacaan & Menulis",
                "Pengetahuan Kuantitatif", "Literasi Bahasa Indonesia", "Literasi Bahasa Inggris",
                "Penalaran Matematika"};
    /** What the questions are usually about (the "matter"). */
    private static final String[][] MATTER_EN = {
        {"Inductive: find the pattern or rule", "Deductive: logic, conclusions, syllogisms", "Quantitative reasoning: number puzzles"},
                {"Vocabulary and word meaning", "General knowledge on society and culture", "Understanding how language is used"},
                {"Spelling and standard punctuation", "Standard grammar and sentence structure", "Paragraph order and coherence"},
                {"Arithmetic and algebra basics", "Basic geometry and ratios", "Number logic under time pressure"},
                {"Main idea and supporting facts", "Author's opinion and conclusions", "Complex multi-paragraph texts"},
                {"Academic reading passages", "Vocabulary in context", "Inference and author's purpose"},
                {"Word problems from daily life", "Reading graphs, tables, and data", "Applying formulas to new situations"}};
    private static final String[][] MATTER_ID = {
        {"Induktif: mencari pola atau aturan", "Deduktif: logika, kesimpulan, silogisme", "Penalaran kuantitatif: teka-teki angka"},
                {"Kosakata dan makna kata", "Pengetahuan umum sosial-budaya", "Memahami penggunaan bahasa"},
                {"Ejaan dan tanda baca baku", "Tata bahasa dan struktur kalimat baku", "Urutan dan kepaduan paragraf"},
                {"Dasar aritmetika dan aljabar", "Geometri dasar dan perbandingan", "Logika angka dengan waktu terbatas"},
                {"Gagasan utama dan fakta pendukung", "Opini penulis dan kesimpulan", "Teks kompleks beberapa paragraf"},
                {"Bacaan akademik berbahasa Inggris", "Kosakata dalam konteks", "Inferensi dan tujuan penulis"},
                {"Soal cerita kehidupan sehari-hari", "Membaca grafik, tabel, dan data", "Menerapkan rumus pada situasi baru"}};
    /** Best ways to learn it. */
    private static final String[][] HOW_EN = {
        {"Do timed sets of 10 and write down the rule you used.", "Sort your mistakes by type: induction, deduction, numbers.", "Redo missed questions 2 days later."},
                {"Read one news or long-form article every day.", "Keep a vocabulary list and review it every 3 days.", "Take short quizzes on social and cultural topics."},
                {"Learn the standard spelling rules a little at a time.", "Practice ordering shuffled paragraphs.", "Find and fix errors in your own writing."},
                {"Keep a one-page formula sheet and quiz yourself.", "Drill mental math for 10 minutes a day.", "Do 20-question sets in 20 minutes."},
                {"Read the questions first, then skim the text.", "Summarize each paragraph in one line.", "Practice with long editorials."},
                {"Read short academic articles every day.", "Guess new words from context before checking.", "Time yourself: 20 questions in 20 minutes."},
                {"Turn each story into an equation before solving.", "Practice reading graphs and tables fast.", "Estimate first, then calculate to check."}};
    private static final String[][] HOW_ID = {
        {"Kerjakan set 10 soal berwaktu dan tulis aturan yang dipakai.", "Kelompokkan kesalahan: induktif, deduktif, angka.", "Ulangi soal yang salah 2 hari kemudian."},
                {"Baca satu artikel berita atau artikel panjang setiap hari.", "Buat daftar kosakata dan ulas tiap 3 hari.", "Latihan kuis singkat topik sosial-budaya."},
                {"Pelajari kaidah ejaan baku sedikit demi sedikit.", "Latihan menyusun paragraf acak.", "Cari dan perbaiki kesalahan di tulisanmu sendiri."},
                {"Buat lembar rumus satu halaman lalu uji dirimu.", "Latih hitung cepat 10 menit per hari.", "Kerjakan set 20 soal dalam 20 menit."},
                {"Baca pertanyaan dulu, lalu pindai teksnya.", "Ringkas tiap paragraf dalam satu baris.", "Latihan dengan tajuk rencana yang panjang."},
                {"Baca artikel akademik pendek setiap hari.", "Tebak kata baru dari konteks sebelum mengecek.", "Catat waktu: 20 soal dalam 20 menit."},
                {"Ubah soal cerita jadi persamaan sebelum menghitung.", "Latih membaca grafik dan tabel dengan cepat.", "Perkirakan dulu, lalu hitung untuk mengecek."}};
    static String code(int s) {
        return CODE[s];
    }
    static String name(int s) {
        return L.id ? NAME_ID[s] : NAME_EN[s];
    }
    static String[] matters(int s) {
        return L.id ? MATTER_ID[s] : MATTER_EN[s];
    }
    static String[] how(int s) {
        return L.id ? HOW_ID[s] : HOW_EN[s];
    }
}


