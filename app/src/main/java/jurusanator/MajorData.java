package jurusanator;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/** * Question bank + major profiles (Holland RIASEC), now in Indonesian and English. * The numbers are exactly the original ones; only text, descriptions and art were added.
 */
public final class MajorData {
    private MajorData() {}


    @SafeVarargs
    private static <T> List<T> immutableList(T... values) {
        return Collections.unmodifiableList(Arrays.asList(values));
    }


    // Holland RIASEC:
    // Realistic, Investigative, Artistic,
    // Social, Enterprising, Conventional.


    public static final class Question {
        private final String promptId;
        private final String promptEn;
        private final double[] interests;
        public Question(String promptId, String promptEn, double[] interests) {
            if (interests.length != 6) {
                throw new IllegalArgumentException(
                        "A question must have exactly 6 interest weights."
                        );
            }
            this.promptId = promptId;
            this.promptEn = promptEn;
            this.interests = interests.clone();
        }


        /** Text in the language the user picked. */
        public String prompt() {
            return L.t(promptEn, promptId);
        }


        public double[] interests() {
            return interests.clone();
        }
    }


    public static final class Major {
        private final String nameId;
        private final String nameEn;
        private final String descId;
        private final String descEn;
        private final int art;
        private final double[] interests;


        public Major(String nameId, String nameEn, String descId, String descEn,
                int art, double[] interests) {
            if (interests.length != 6) {
                throw new IllegalArgumentException(
                        "A major must have exactly 6 interest scores."
                        );
            }
            this.nameId = nameId;
            this.nameEn = nameEn;
            this.descId = descId;
            this.descEn = descEn;
            this.art = art;
            this.interests = interests.clone();
        }


        /** Name in the language the user picked. */
        public String name() {
            return L.t(nameEn, nameId);
        }


        /** Name in the other language (shown small under the main name). */
        public String altName() {
            return L.t(nameId, nameEn);
        }


        public String description() {
            return L.t(descEn, descId);
        }


        /** Illustration id, see {@link Art}. */
        public int art() {
            return art;
        }


        public double[] interests() {
            return interests.clone();
        }
    }


    public static final List<String> INTERESTS_ID = immutableList(
            "Realistis", "Investigatif", "Artistik", "Sosial", "Kewirausahaan", "Konvensional"
            );


    public static final List<String> INTERESTS_EN = immutableList(
            "Realistic", "Investigative", "Artistic", "Social", "Enterprising", "Conventional"
            );
    public static String interestName(int index) {
        return L.id ? INTERESTS_ID.get(index) : INTERESTS_EN.get(index);
    }


    private static final String[] ANSWERS_ID = {"Ya", "Mungkin", "Tidak tahu", "Mungkin tidak", "Tidak"};
    private static final String[] ANSWERS_EN = {"Yes", "Maybe", "Don't know", "Maybe not", "No"};


    public static int answerCount() {
        return ANSWERS_EN.length;
    }


    public static String answerLabel(int index) {
        return L.id ? ANSWERS_ID[index] : ANSWERS_EN[index];
    }


    public static final List<Question> QUESTIONS = immutableList(
            new Question("Saya senang membangun, memperbaiki, atau merakit sesuatu.",
            "I enjoy building, fixing, or assembling things.",
            new double[]{1, 0, 0, 0, 0, 0}),

            new Question("Saya senang memecahkan masalah logika yang sulit.",
            "I enjoy solving tough logic problems.",
            new double[]{0, 1, 0, 0, 0, 0}),

            new Question("Saya senang menggambar, mendesain, atau membuat karya seni visual.",
            "I enjoy drawing, designing, or making visual art.",
            new double[]{0, 0, 1, 0, 0, 0}),

            new Question("Saya senang mengajar atau menjelaskan sesuatu kepada orang lain.",
            "I enjoy teaching or explaining things to others.",
            new double[]{0, 0, 0, 1, 0, 0}),

            new Question("Saya senang memimpin kelompok atau mengatur acara.",
            "I enjoy leading a group or organizing events.",
            new double[]{0, 0, 0, 0, 1, 0}),

            new Question("Saya senang mengatur arsip, jadwal, atau data.",
            "I enjoy organizing files, schedules, or data.",
            new double[]{0, 0, 0, 0, 0, 1}),

            new Question("Saya senang memahami cara kerja mesin dan perangkat.",
            "I enjoy figuring out how machines and devices work.",
            new double[]{1, 1, 0, 0, 0, 0}),

            new Question("Saya senang matematika dan penalaran kuantitatif.",
            "I enjoy math and quantitative reasoning.",
            new double[]{0, 1, 0, 0, 0, 0.5}),

            new Question("Saya senang melakukan eksperimen dan menguji hipotesis.",
            "I enjoy running experiments and testing hypotheses.",
            new double[]{0, 1, 0, 0, 0, 0}),

            new Question("Saya senang membuat program atau mengembangkan perangkat lunak.",
            "I enjoy writing programs or developing software.",
            new double[]{0, 1, 0, 0, 0, 0.5}),

            new Question("Saya senang mempelajari cara kerja komputer dan jaringan.",
            "I enjoy learning how computers and networks work.",
            new double[]{0, 1, 0, 0, 0, 0.5}),

            new Question("Saya senang menemukan pola dalam data.",
            "I enjoy finding patterns in data.",
            new double[]{0, 1, 0, 0, 0, 0.5}),

            new Question("Saya senang membaca tentang sains dan penemuan baru.",
            "I enjoy reading about science and new discoveries.",
            new double[]{0, 1, 0, 0, 0, 0}),

            new Question("Saya senang merancang situs web, aplikasi, atau produk digital.",
            "I enjoy designing websites, apps, or digital products.",
            new double[]{0, 1, 1, 0, 0, 0}),

            new Question("Saya senang membuat ilustrasi, poster, atau tata letak desain.",
            "I enjoy making illustrations, posters, or design layouts.",
            new double[]{0, 0, 1, 0, 0, 0}),

            new Question("Saya senang fotografi, video, animasi, atau bercerita melalui visual.",
            "I enjoy photography, video, animation, or telling stories through visuals.",
            new double[]{0, 0, 1, 0, 0, 0}),

            new Question("Saya senang menulis cerita, artikel, atau konten kreatif.",
            "I enjoy writing stories, articles, or creative content.",
            new double[]{0, 0, 1, 1, 0, 0}),

            new Question("Saya senang bermusik, tampil di depan umum, atau mengekspresikan kreativitas.",
            "I enjoy making music, performing in public, or expressing creativity.",
            new double[]{0, 0, 1, 1, 0, 0}),

            new Question("Saya ingin memahami perilaku dan emosi manusia.",
            "I want to understand human behavior and emotions.",
            new double[]{0, 1, 0, 1, 0, 0}),

            new Question("Saya senang membantu orang menyelesaikan masalah pribadi atau sosial.",
            "I enjoy helping people solve personal or social problems.",
            new double[]{0, 0, 0, 1, 0, 0}),

            new Question("Saya senang menjelaskan pelajaran atau membantu orang lain belajar.",
            "I enjoy explaining lessons or helping others learn.",
            new double[]{0, 0, 0, 1, 0, 0}),

            new Question("Saya tertarik pada kesehatan, biologi, atau tubuh manusia.",
            "I'm interested in health, biology, or the human body.",
            new double[]{0, 1, 0, 1, 0, 0}),

            new Question("Saya nyaman merawat orang yang membutuhkan bantuan.",
            "I'm comfortable caring for people who need help.",
            new double[]{0, 0, 0, 1, 0, 0}),

            new Question("Saya senang membahas hukum, etika, dan isu sosial.",
            "I enjoy discussing law, ethics, and social issues.",
            new double[]{0, 1, 0, 1, 0, 0}),

            new Question("Saya senang berdebat dan mempertahankan pendapat dengan argumen.",
            "I enjoy debating and defending my opinions with arguments.",
            new double[]{0, 1, 0, 0, 1, 0}),

            new Question("Saya tertarik pada dunia bisnis dan cara kerja organisasi.",
            "I'm interested in business and how organizations work.",
            new double[]{0, 0, 0, 0, 1, 1}),

            new Question("Saya senang merencanakan proyek dan mengoordinasikan orang lain.",
            "I enjoy planning projects and coordinating other people.",
            new double[]{0, 0, 0, 0.5, 1, 1}),

            new Question("Saya tertarik pada pemasaran dan cara memahami kebutuhan pelanggan.",
            "I'm interested in marketing and understanding what customers need.",
            new double[]{0, 0, 1, 0, 1, 0}),

            new Question("Saya senang berwirausaha dan mengembangkan ide bisnis.",
            "I enjoy being entrepreneurial and developing business ideas.",
            new double[]{0, 0, 0, 0, 1, 0}),

            new Question("Saya senang mengelola anggaran dan informasi keuangan.",
            "I enjoy managing budgets and financial information.",
            new double[]{0, 0, 0, 0, 1, 1}),

            new Question("Saya senang menggunakan spreadsheet dan mengelola informasi yang terstruktur.",
            "I enjoy using spreadsheets and managing structured information.",
            new double[]{0, 0.5, 0, 0, 0, 1}),

            new Question("Saya tertarik pada bangunan, infrastruktur, dan konstruksi.",
            "I'm interested in buildings, infrastructure, and construction.",
            new double[]{1, 1, 0, 0, 0, 0}),

            new Question("Saya senang memahami sistem kelistrikan dan elektronika.",
            "I enjoy understanding electrical systems and electronics.",
            new double[]{1, 1, 0, 0, 0, 0}),

            new Question("Saya senang memahami material, mesin, dan proses manufaktur.",
            "I enjoy understanding materials, machines, and manufacturing processes.",
            new double[]{1, 1, 0, 0, 0, 0}),

            new Question("Saya tertarik pada lingkungan dan sumber daya alam.",
            "I'm interested in the environment and natural resources.",
            new double[]{1, 1, 0, 0, 0, 0}),

            new Question("Saya senang mempelajari organisme hidup dan proses biologis.",
            "I enjoy studying living organisms and biological processes.",
            new double[]{0, 1, 0, 0, 0, 0}),

            new Question("Saya senang mempelajari kimia dan memahami reaksi antarzat.",
            "I enjoy studying chemistry and understanding how substances react.",
            new double[]{0, 1, 0, 0, 0, 0}),

            new Question("Saya senang mempelajari masyarakat, budaya, dan sejarah manusia.",
            "I enjoy studying society, culture, and human history.",
            new double[]{0, 1, 0.5, 1, 0, 0}),

            new Question("Saya senang mempelajari bahasa dan berkomunikasi dengan orang dari budaya berbeda.",
            "I enjoy learning languages and communicating with people from different cultures.",
            new double[]{0, 0, 1, 1, 0, 0}),

            new Question("Saya senang menganalisis masalah ekonomi dan kebijakan publik.",
            "I enjoy analyzing economic problems and public policy.",
            new double[]{0, 1, 0, 0, 1, 0.5}),

            new Question("Saya senang mengambil keputusan berdasarkan bukti dan hasil penelitian.",
            "I enjoy making decisions based on evidence and research.",
            new double[]{0, 1, 0, 0, 0, 0.5}),

            new Question("Saya senang mencari solusi praktis untuk masalah di dunia nyata.",
            "I enjoy finding practical solutions to real-world problems.",
            new double[]{1, 1, 0, 0, 0, 0}),

            new Question("Saya senang bekerja dalam tim untuk mencapai tujuan bersama.",
            "I enjoy working in a team to reach a shared goal.",
            new double[]{0, 0, 0, 1, 1, 0}),

            new Question("Saya senang mengerjakan tugas yang kompleks secara mandiri.",
            "I enjoy working on complex tasks on my own.",
            new double[]{0, 1, 1, 0, 0, 0}),

            new Question("Saya senang mengikuti prosedur yang jelas dan memeriksa detail.",
            "I enjoy following clear procedures and checking details.",
            new double[]{0, 0, 0, 0, 0, 1}),

            new Question("Saya senang mempresentasikan ide dan meyakinkan orang lain.",
            "I enjoy presenting ideas and persuading others.",
            new double[]{0, 0, 1, 0.5, 1, 0}),

            new Question("Saya senang menggabungkan pengetahuan teknis dengan kebutuhan bisnis.",
            "I enjoy combining technical knowledge with business needs.",
            new double[]{0, 1, 0, 0, 1, 1}),

            new Question("Saya senang mengeksplorasi ide baru meskipun jawabannya belum pasti.",
            "I enjoy exploring new ideas even when the answer isn't certain.",
            new double[]{0, 1, 1, 0, 0, 0})
            );


    // Profil jurusan merupakan perkiraan pada skala 0 sampai 5.
    // Urutan: R, I, A, S, E, C.


    public static final List<Major> MAJORS = immutableList(
            new Major("Informatika", "Informatics",
            "Merancang software, algoritma, dan sistem cerdas.",
            "Build software, algorithms, and smart systems.",
            Art.LAPTOP, new double[]{1, 5, 1, 1, 2, 3}),

            new Major("Ilmu Komputer", "Computer Science",
            "Teori komputasi, AI, dan cara kerja komputer.",
            "Computing theory, AI, and how computers think.",
            Art.CHIP, new double[]{1, 5, 1, 1, 1, 3}),

            new Major("Sistem Informasi", "Information Systems",
            "Menghubungkan teknologi dengan kebutuhan bisnis.",
            "Connect technology with business needs.",
            Art.DATABASE, new double[]{1, 4, 1, 2, 4, 4}),

            new Major("Teknologi Informasi", "Information Technology",
            "Jaringan, server, dan infrastruktur digital.",
            "Networks, servers, and digital infrastructure.",
            Art.NETWORK, new double[]{2, 4, 1, 1, 2, 3}),

            new Major("Sains Data", "Data Science",
            "Mengolah data besar menjadi wawasan.",
            "Turn big data into insight.",
            Art.CHART, new double[]{0, 5, 1, 1, 2, 4}),

            new Major("Statistika", "Statistics",
            "Membaca pola dan ketidakpastian lewat angka.",
            "Read patterns and uncertainty through numbers.",
            Art.BELL_CURVE, new double[]{0, 5, 0, 1, 1, 4}),

            new Major("Matematika", "Mathematics",
            "Logika, pembuktian, dan keindahan angka.",
            "Logic, proofs, and the beauty of numbers.",
            Art.PI, new double[]{0, 5, 0, 0, 1, 2}),

            new Major("Fisika", "Physics",
            "Memahami hukum alam dari atom hingga galaksi.",
            "Understand nature's laws, from atoms to galaxies.",
            Art.ATOM, new double[]{1, 5, 0, 0, 1, 1}),

            new Major("Kimia", "Chemistry",
            "Meneliti zat dan reaksinya di laboratorium.",
            "Explore substances and reactions in the lab.",
            Art.FLASK, new double[]{1, 5, 0, 0, 1, 2}),

            new Major("Biologi", "Biology",
            "Mempelajari makhluk hidup dan ekosistemnya.",
            "Study living things and their ecosystems.",
            Art.CELL, new double[]{1, 5, 1, 1, 1, 1}),

            new Major("Bioteknologi", "Biotechnology",
            "Memakai sel hidup untuk obat dan inovasi.",
            "Use living cells to make medicine and innovations.",
            Art.DNA, new double[]{1, 5, 0, 1, 1, 2}),

            new Major("Teknik Elektro", "Electrical Engineering",
            "Listrik, elektronika, dan sistem kendali.",
            "Electricity, electronics, and control systems.",
            Art.BOLT, new double[]{4, 5, 0, 0, 1, 2}),

            new Major("Teknik Mesin", "Mechanical Engineering",
            "Merancang mesin dan sistem energi.",
            "Design machines and energy systems.",
            Art.GEAR, new double[]{5, 4, 0, 0, 1, 2}),

            new Major("Teknik Sipil", "Civil Engineering",
            "Membangun jembatan, jalan, dan gedung.",
            "Build bridges, roads, and buildings.",
            Art.HELMET, new double[]{5, 4, 0, 0, 2, 2}),

            new Major("Teknik Industri", "Industrial Engineering",
            "Membuat proses produksi lebih efisien.",
            "Make production and processes more efficient.",
            Art.FACTORY, new double[]{3, 4, 0, 1, 3, 4}),

            new Major("Teknik Kimia", "Chemical Engineering",
            "Mengubah bahan mentah jadi produk skala besar.",
            "Turn raw materials into products at scale.",
            Art.TANK, new double[]{3, 5, 0, 0, 1, 2}),

            new Major("Teknik Lingkungan", "Environmental Engineering",
            "Mengelola limbah, air, dan udara bersih.",
            "Tackle waste, water, and clean air.",
            Art.WINDMILL, new double[]{3, 4, 0, 1, 1, 2}),

            new Major("Arsitektur", "Architecture",
            "Merancang ruang yang indah dan fungsional.",
            "Design beautiful, functional spaces.",
            Art.BLUEPRINT, new double[]{3, 3, 5, 1, 2, 2}),

            new Major("Perencanaan Wilayah dan Kota", "Urban & Regional Planning",
            "Menata kota dan wilayah agar nyaman dihuni.",
            "Plan cities and regions for better living.",
            Art.CITY, new double[]{2, 3, 2, 2, 3, 2}),

            new Major("Kedokteran", "Medicine",
            "Mendiagnosis penyakit dan merawat pasien.",
            "Diagnose illness and care for patients.",
            Art.MEDKIT, new double[]{0, 4, 0, 5, 1, 2}),

            new Major("Keperawatan", "Nursing",
            "Merawat pasien dengan empati dan keterampilan klinis.",
            "Care for patients with empathy and clinical skill.",
            Art.HEART, new double[]{1, 2, 0, 5, 1, 3}),

            new Major("Farmasi", "Pharmacy",
            "Meneliti, meracik, dan mengelola obat.",
            "Research, prepare, and manage medicines.",
            Art.PILL, new double[]{0, 5, 0, 2, 1, 3}),

            new Major("Kesehatan Masyarakat", "Public Health",
            "Mencegah penyakit dan menjaga kesehatan komunitas.",
            "Prevent disease and protect community health.",
            Art.SHIELD, new double[]{0, 3, 0, 4, 2, 2}),

            new Major("Gizi", "Nutrition",
            "Mengatur makanan untuk hidup yang lebih sehat.",
            "Use food and nutrition for healthier lives.",
            Art.APPLE, new double[]{0, 3, 0, 4, 1, 2}),

            new Major("Kedokteran Gigi", "Dentistry",
            "Merawat gigi, mulut, dan senyum pasien.",
            "Care for teeth, mouths, and smiles.",
            Art.TOOTH, new double[]{2, 4, 1, 4, 1, 2}),

            new Major("Psikologi", "Psychology",
            "Memahami pikiran, perilaku, dan emosi manusia.",
            "Understand minds, behavior, and emotions.",
            Art.BRAIN, new double[]{0, 4, 1, 5, 2, 1}),

            new Major("Kebidanan", "Midwifery",
            "Mendampingi ibu dan bayi sejak kehamilan.",
            "Support mothers and babies from pregnancy onward.",
            Art.BOTTLE, new double[]{0, 2, 0, 5, 1, 2}),

            new Major("Fisioterapi", "Physiotherapy",
            "Memulihkan gerak dan fungsi tubuh.",
            "Restore movement and body function.",
            Art.DUMBBELL, new double[]{3, 3, 0, 5, 1, 1}),

            new Major("Ilmu Hukum", "Law",
            "Memahami aturan, keadilan, dan argumentasi.",
            "Understand rules, justice, and argumentation.",
            Art.SCALES, new double[]{0, 3, 1, 2, 4, 2}),

            new Major("Ilmu Politik", "Political Science",
            "Menganalisis kekuasaan, negara, dan kebijakan.",
            "Analyze power, government, and policy.",
            Art.BALLOT, new double[]{0, 3, 1, 2, 4, 1}),

            new Major("Hubungan Internasional", "International Relations",
            "Mempelajari diplomasi dan hubungan antarnegara.",
            "Study diplomacy and how countries interact.",
            Art.GLOBE, new double[]{0, 3, 2, 3, 4, 1}),

            new Major("Sosiologi", "Sociology",
            "Meneliti masyarakat dan perubahan sosial.",
            "Study society and social change.",
            Art.PEOPLE, new double[]{0, 3, 1, 4, 1, 1}),

            new Major("Antropologi", "Anthropology",
            "Menjelajahi budaya dan asal-usul manusia.",
            "Explore human cultures and origins.",
            Art.MASK, new double[]{0, 3, 2, 4, 1, 1}),

            new Major("Ilmu Komunikasi", "Communication Studies",
            "Merangkai pesan untuk media dan publik.",
            "Craft messages for media and audiences.",
            Art.SPEECH, new double[]{0, 2, 4, 4, 3, 1}),

            new Major("Administrasi Publik", "Public Administration",
            "Mengelola layanan dan kebijakan pemerintah.",
            "Manage government services and policy.",
            Art.TEMPLE, new double[]{0, 2, 0, 3, 3, 4}),

            new Major("Manajemen", "Management",
            "Memimpin tim dan mengatur sumber daya.",
            "Lead teams and organize resources.",
            Art.ORGCHART, new double[]{0, 2, 1, 2, 5, 4}),

            new Major("Akuntansi", "Accounting",
            "Mencatat, menganalisis, dan mengaudit keuangan.",
            "Record, analyze, and audit finances.",
            Art.CALC, new double[]{0, 3, 0, 0, 2, 5}),

            new Major("Ekonomi", "Economics",
            "Memahami pasar, harga, dan keputusan manusia.",
            "Understand markets, prices, and choices.",
            Art.COINS, new double[]{0, 5, 0, 1, 3, 3}),

            new Major("Bisnis Digital", "Digital Business",
            "Membangun bisnis lewat teknologi dan e-commerce.",
            "Grow businesses with tech and e-commerce.",
            Art.PHONE, new double[]{0, 3, 2, 2, 5, 3}),

            new Major("Kewirausahaan", "Entrepreneurship",
            "Mengubah ide menjadi usaha nyata.",
            "Turn ideas into real ventures.",
            Art.ROCKET, new double[]{1, 2, 2, 2, 5, 2}),

            new Major("Administrasi Bisnis", "Business Administration",
            "Mengatur operasional dan administrasi perusahaan.",
            "Run company operations and administration.",
            Art.BRIEFCASE, new double[]{0, 2, 1, 2, 4, 4}),

            new Major("Logistik", "Logistics",
            "Mengatur pergerakan barang dari hulu ke hilir.",
            "Move goods smoothly through supply chains.",
            Art.TRUCK, new double[]{2, 2, 0, 1, 3, 5}),

            new Major("DKV", "Visual Communication Design",
            "Menyampaikan pesan lewat desain visual.",
            "Communicate ideas through visual design.",
            Art.SHAPES, new double[]{0, 1, 5, 2, 2, 1}),

            new Major("Desain Produk", "Product Design",
            "Merancang produk yang berguna dan menarik.",
            "Design products that are useful and lovable.",
            Art.BULB, new double[]{3, 2, 5, 1, 2, 1}),

            new Major("Desain Interior", "Interior Design",
            "Menata ruang agar nyaman dan estetik.",
            "Shape interiors that feel good and look great.",
            Art.CHAIR, new double[]{2, 2, 5, 1, 2, 2}),

            new Major("Animasi", "Animation",
            "Menghidupkan karakter dan cerita bergerak.",
            "Bring characters and stories to life.",
            Art.PLAY, new double[]{0, 1, 5, 1, 1, 1}),

            new Major("Film dan Televisi", "Film & Television",
            "Bercerita lewat kamera, suara, dan penyuntingan.",
            "Tell stories with camera, sound, and editing.",
            Art.CLAPPER, new double[]{0, 1, 5, 2, 2, 1}),

            new Major("Seni Rupa", "Fine Arts",
            "Berkarya lewat lukisan, patung, dan eksplorasi seni.",
            "Create through painting, sculpture, and exploration.",
            Art.PALETTE, new double[]{1, 1, 5, 1, 1, 0}),

            new Major("Desain Fashion", "Fashion Design",
            "Merancang busana dan tren gaya.",
            "Design clothing and style trends.",
            Art.SHIRT, new double[]{1, 1, 5, 1, 2, 1}),

            new Major("Musik", "Music",
            "Berkarya, tampil, dan mempelajari musik.",
            "Compose, perform, and study music.",
            Art.MUSIC, new double[]{0, 1, 5, 2, 1, 0}),

            new Major("Pendidikan Matematika", "Mathematics Education",
            "Jadi guru yang membuat matematika menyenangkan.",
            "Become a teacher who makes math click.",
            Art.BOARD, new double[]{0, 4, 0, 5, 1, 2}),

            new Major("Pendidikan Bahasa", "Language Education",
            "Mengajar bahasa dan cara berkomunikasi.",
            "Teach language and communication.",
            Art.LETTER, new double[]{0, 2, 2, 5, 1, 1}),

            new Major("Pendidikan Biologi", "Biology Education",
            "Mengajarkan kehidupan dan alam dengan seru.",
            "Teach life and nature in fun ways.",
            Art.LEAF, new double[]{0, 4, 0, 5, 1, 1}),

            new Major("Pendidikan Fisika", "Physics Education",
            "Menjelaskan hukum alam lewat eksperimen.",
            "Explain the laws of nature through experiments.",
            Art.MAGNET, new double[]{0, 4, 0, 5, 1, 1}),

            new Major("Pendidikan Guru Sekolah Dasar", "Primary School Teacher Education",
            "Mendidik anak-anak di tahun-tahun awal sekolah.",
            "Teach children in their first school years.",
            Art.PENCIL, new double[]{0, 2, 1, 5, 1, 2}),

            new Major("Pendidikan Anak Usia Dini", "Early Childhood Education",
            "Membimbing anak usia dini lewat bermain dan belajar.",
            "Guide young children through play and learning.",
            Art.BLOCKS, new double[]{0, 1, 1, 5, 1, 1}),

            new Major("Sastra Inggris", "English Literature",
            "Menyelami bahasa, sastra, dan budaya berbahasa Inggris.",
            "Dive into English language, literature, and culture.",
            Art.QUILL, new double[]{0, 2, 4, 3, 1, 1}),

            new Major("Sastra Indonesia", "Indonesian Literature",
            "Mengkaji bahasa dan karya sastra Indonesia.",
            "Study Indonesian language and literature.",
            Art.BOOK, new double[]{0, 2, 5, 3, 1, 1}),

            new Major("Jurnalistik", "Journalism",
            "Mencari, menulis, dan menyajikan berita.",
            "Find, write, and publish the news.",
            Art.NEWS, new double[]{0, 2, 4, 3, 2, 1}),

            new Major("Agribisnis", "Agribusiness",
            "Mengelola usaha pertanian dari kebun ke pasar.",
            "Run farm-based business from field to market.",
            Art.WHEAT, new double[]{3, 2, 1, 1, 4, 3}),

            new Major("Agroteknologi", "Agrotechnology",
            "Meningkatkan hasil tanaman dengan sains.",
            "Grow better crops with science.",
            Art.SPROUT, new double[]{4, 3, 1, 0, 1, 1}),

            new Major("Peternakan", "Animal Husbandry",
            "Mengelola ternak dan produksi pangan hewani.",
            "Raise livestock and animal-based food.",
            Art.BARN, new double[]{4, 2, 0, 1, 1, 1}),

            new Major("Kehutanan", "Forestry",
            "Menjaga hutan dan sumber daya alamnya.",
            "Protect forests and their resources.",
            Art.TREE, new double[]{4, 3, 1, 1, 1, 1}),

            new Major("Ilmu Kelautan", "Marine Science",
            "Meneliti laut dan kehidupan di dalamnya.",
            "Research the ocean and its life.",
            Art.BOAT, new double[]{4, 4, 0, 1, 1, 1}),

            new Major("Perikanan", "Fisheries",
            "Mengelola perikanan dan budidaya air.",
            "Manage fisheries and aquaculture.",
            Art.FISH, new double[]{4, 3, 0, 1, 2, 1}),

            new Major("Pariwisata", "Tourism",
            "Merancang pengalaman dan destinasi wisata.",
            "Design travel experiences and destinations.",
            Art.PIN, new double[]{0, 1, 2, 3, 4, 2}),

            new Major("Perhotelan", "Hospitality Management",
            "Melayani tamu dan mengelola hotel.",
            "Serve guests and manage hotels.",
            Art.BELL, new double[]{0, 1, 1, 4, 3, 3})
            );
}


