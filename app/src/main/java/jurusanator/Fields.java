package jurusanator;
/**
 * 10 study fields. Every major belongs to one field. A field decides:
 * the related topics to explore, and which UTBK skills matter most (a rough guide).
 */
final class Fields {
    private Fields() {}
    static final int COMPUTING = 0, SCIENCE = 1, ENGINEERING = 2, HEALTH = 3, SOCIAL = 4,
            HUMANITIES = 5, BUSINESS = 6, ARTS = 7, EDUCATION = 8, AGRI = 9;
    static final int COUNT = 10;
    /** Field of each major, in MajorData.MAJORS order (67 entries). */
    private static final int[] FIELD_OF = {
        0, 0, 0, 0, 0, 0,            // Informatika .. Statistika
                1, 1, 1, 1, 1,              // Matematika .. Bioteknologi
                2, 2, 2, 2, 2, 2, 2, 2,     // Teknik Elektro .. PWK
                3, 3, 3, 3, 3, 3,           // Kedokteran .. Kedokteran Gigi
                4,                          // Psikologi
                3, 3,                       // Kebidanan, Fisioterapi
                4, 4, 4, 4,                 // Hukum, Politik, HI, Sosiologi
                5, 5,                       // Antropologi, Komunikasi
                4,                          // Administrasi Publik
                6, 6, 6, 6, 6, 6, 6,        // Manajemen .. Logistik
                7, 7, 7, 7, 7, 7, 7, 7,     // DKV .. Musik
                8, 8, 8, 8, 8, 8,           // Pendidikan x6
                5, 5, 5,                    // Sastra x2, Jurnalistik
                9, 9, 9, 9, 9, 9,           // Agribisnis .. Perikanan
                6, 6                        // Pariwisata, Perhotelan
    };
    private static final String[] NAME_EN = {
        "Computing & Data", "Science", "Engineering & Built Environment", "Health",
                "Social & Law", "Humanities & Media", "Business & Hospitality", "Arts & Design",
                "Education", "Agriculture & Marine"};
    private static final String[] NAME_ID = {
        "Komputasi & Data", "Sains", "Teknik & Lingkungan Binaan", "Kesehatan",
                "Sosial & Hukum", "Humaniora & Media", "Bisnis & Perhotelan", "Seni & Desain",
                "Pendidikan", "Pertanian & Kelautan"};
    private static final String[][] TOPICS_EN = {
        {"Algorithms & data structures", "Programming (Python/Java)", "Databases & SQL", "Statistics & probability", "AI & machine learning basics"},
                {"Calculus & linear algebra", "Designing experiments", "Scientific writing", "Lab safety & measurement", "Data analysis"},
                {"Physics: mechanics & electricity", "Calculus", "Technical drawing & CAD", "Materials & energy", "Project management basics"},
                {"Human anatomy & physiology", "Biology & biochemistry", "Public health basics", "Medical ethics & communication", "Research methods"},
                {"Constitution & public policy", "Social theory", "Research methods", "Argumentation & debate", "Current affairs & ethics"},
                {"Academic writing", "Media & communication", "Literature & criticism", "Cultural studies", "Languages & translation"},
                {"Basic accounting", "Microeconomics", "Marketing basics", "Spreadsheets & finance", "Leadership & teamwork"},
                {"Design principles & color theory", "Building a portfolio", "Sketching & drawing", "Design software", "Art & design history"},
                {"Learning theories", "Classroom management", "Subject knowledge", "Child development", "Presentation skills"},
                {"Plant & animal biology", "Soil & water science", "Sustainable farming", "Agribusiness basics", "Climate & environment"}};
    private static final String[][] TOPICS_ID = {
        {"Algoritma & struktur data", "Pemrograman (Python/Java)", "Basis data & SQL", "Statistika & probabilitas", "Dasar AI & machine learning"},
                {"Kalkulus & aljabar linear", "Merancang eksperimen", "Penulisan ilmiah", "Keselamatan lab & pengukuran", "Analisis data"},
                {"Fisika: mekanika & listrik", "Kalkulus", "Gambar teknik & CAD", "Material & energi", "Dasar manajemen proyek"},
                {"Anatomi & fisiologi manusia", "Biologi & biokimia", "Dasar kesehatan masyarakat", "Etika & komunikasi medis", "Metode penelitian"},
                {"Konstitusi & kebijakan publik", "Teori sosial", "Metode penelitian", "Argumentasi & debat", "Isu terkini & etika"},
                {"Penulisan akademik", "Media & komunikasi", "Sastra & kritik", "Kajian budaya", "Bahasa & penerjemahan"},
                {"Akuntansi dasar", "Mikroekonomi", "Dasar pemasaran", "Spreadsheet & keuangan", "Kepemimpinan & kerja tim"},
                {"Prinsip desain & teori warna", "Membangun portofolio", "Sketsa & menggambar", "Perangkat lunak desain", "Sejarah seni & desain"},
                {"Teori belajar", "Manajemen kelas", "Penguasaan materi", "Perkembangan anak", "Keterampilan presentasi"},
                {"Biologi tumbuhan & hewan", "Ilmu tanah & air", "Pertanian berkelanjutan", "Dasar agribisnis", "Iklim & lingkungan"}};
    /**
     * Suggested emphasis (0..3) per skill: PU, PPU, PBM, PK, LBI, LBE, PM.
     * This is a rough study guide, NOT an official requirement.
     */
    private static final int[][] NEED = {
        {3, 1, 1, 3, 1, 2, 3},     // computing
                {3, 1, 1, 3, 1, 2, 3},     // science
                {3, 1, 1, 3, 1, 1, 3},     // engineering
                {2, 2, 2, 2, 2, 2, 2},     // health
                {2, 3, 2, 1, 3, 2, 1},     // social & law
                {2, 3, 3, 1, 3, 3, 1},     // humanities
                {2, 2, 2, 2, 2, 2, 2},     // business
                {2, 2, 2, 1, 2, 2, 1},     // arts
                {2, 2, 3, 2, 3, 2, 2},     // education
                {2, 2, 1, 2, 2, 1, 2}};    // agri & marine
    /** Field of a major (by its index in MajorData.MAJORS). Safe for any index. */
    static int of(int majorIndex) {
        if (majorIndex < 0 || majorIndex >= FIELD_OF.length) return COMPUTING;
        return FIELD_OF[majorIndex];
    }
    static String name(int field) {
        return L.id ? NAME_ID[field] : NAME_EN[field];
    }
    static String[] topics(int field) {
        return L.id ? TOPICS_ID[field] : TOPICS_EN[field];
    }
    /** Emphasis 0..3 for a skill. */
    static int need(int field, int skill) {
        return NEED[field][skill];
    }
}


