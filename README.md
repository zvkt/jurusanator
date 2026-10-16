# jurusanator

id // en

## Bahasa Indonesia

**jurusanator** adalah aplikasi Android untuk membantu calon mahasiswa mengeksplorasi jurusan kuliah yang mungkin cocok dengan minat mereka, lalu menyusun rencana awal menuju universitas impian. Aplikasi ini dibuat dengan Java dan API Android, tanpa Kotlin maupun pustaka pihak ketiga.

> Hasil kuis adalah bahan pertimbangan, bukan keputusan atau jaminan penerimaan. Periksa selalu informasi dan jadwal resmi dari universitas serta SNPMB.

### Fitur

- Kuis minat adaptif dengan hingga 24 pertanyaan dalam mode standar maupun mendalam. Mode singkat berisi 8 pertanyaan. Setelah sedikitnya 12 jawaban, kuis standar/mendalam dapat selesai lebih awal jika jurusan yang sama menjadi satu-satunya peringkat #1 selama empat jawaban berturut-turut. Mode mendalam juga dapat menambahkan duel pilihan saat hasil teratas berdekatan.
- Bank kuis mencakup 12 pertanyaan baru: masing-masing dua pertanyaan khusus untuk enam pilar RIASEC (Realistis, Investigatif, Artistik, Sosial, Kewirausahaan, dan Konvensional).
- Rekomendasi jurusan teratas dan satu jurusan **+1 WILDCARD** untuk membantu mengeksplorasi pilihan lain. Tersedia 67 jurusan dengan ilustrasi khusus.
- Riwayat hasil dan kuis yang belum selesai disimpan di perangkat. Kuis yang sudah dimulai dapat dilanjutkan.
- **Coach** untuk melihat kecocokan dengan kombinasi jurusan-universitas pilihan, kekuatan dan area yang perlu disiapkan, serta rencana belajar dan progres.
- Pilihan jalur SNBP, SNBT, atau mandiri; linimasa jadwal yang dapat diedit; dan opsi menambahkan acara serta pengingat ke kalender.
- Berita universitas dari Google News RSS, dengan cache agar berita yang pernah dimuat dapat dilihat kembali saat offline. Notifikasi berita harian bersifat opsional.
- Antarmuka bahasa Indonesia dan Inggris, tema terang/gelap, serta efek suara yang dapat dimatikan.

### Cara kerja aplikasi

1. Jawab pertanyaan tentang minat dan preferensi. Pilihan jawaban bergerak dari “ya” hingga “tidak”; ada juga opsi “tidak tahu” pada mode standar dan mendalam.
2. Mesin rekomendasi lokal mencocokkan pola jawaban dengan profil minat tiap jurusan. Pertanyaan berikutnya dipilih secara adaptif dari pertanyaan yang dinilai berguna dan berkaitan dengan jawaban sebelumnya. Mode standar dan mendalam dapat menanyakan hingga 24 pertanyaan; mode singkat menanyakan 8.
3. Setelah sedikitnya 12 jawaban, mode standar/mendalam selesai lebih awal jika jurusan yang sama menjadi satu-satunya peringkat pertama selama empat jawaban berturut-turut. Jika tidak, kuis berlanjut hingga batas modenya. Aplikasi lalu mengurutkan jurusan berdasarkan skor kecocokan, menampilkan hasil teratas, dan menambahkan satu wildcard. Pada mode mendalam, aplikasi dapat meminta pengguna memilih di antara dua hasil teratas jika skornya berdekatan.
4. Pilih jurusan dan universitas impian untuk membuka Coach. Nilai kecocokan Coach adalah ringkasan panduan, bukan probabilitas resmi untuk diterima. Kuis dan perhitungan rekomendasi berjalan di perangkat; aplikasi tidak mengirim jawaban kuis ke layanan AI.

Berita memerlukan koneksi internet. Fitur kalender dan notifikasi meminta izin Android saat diperlukan; keduanya opsional. Pilihan bahasa, tema, suara, profil, progres, dan riwayat disimpan secara lokal di perangkat menggunakan penyimpanan aplikasi.

### Mengunduh APK dari GitHub Actions

Workflow **Android APK** berjalan saat ada push ke repositori dan juga dapat dijalankan secara manual. Untuk mengunduh hasil build:

1. Buka halaman [Actions](https://github.com/zvkt/jurusanator/actions/workflows/android-apk.yml).
2. Pilih run **Android APK** yang sudah selesai dengan status berhasil. Untuk membuat build baru secara manual, pilih **Run workflow** jika tombol tersebut tersedia.
3. Di halaman detail run, cari bagian **Artifacts**, lalu unduh `jurusanator-debug-apk`.
4. Ekstrak file ZIP yang diunduh. Di dalamnya terdapat `app-debug.apk`.
5. Buka APK di perangkat Android dan ikuti petunjuk pemasangan. Jika diminta, izinkan aplikasi sumber tersebut memasang aplikasi yang tidak dikenal, lalu matikan kembali izin itu setelah selesai bila diinginkan.

APK Actions tersebut adalah **debug build untuk pengujian**, bukan rilis bertanda tangan untuk distribusi melalui Google Play. Akses artifact dan lama ketersediaannya mengikuti pengaturan GitHub Actions pada repositori.

### Memasang dan menjalankan dari source

**Persyaratan:** JDK 17, Gradle 8.9, Android SDK Platform 35, dan Android Build Tools 35.0.0. Aplikasi menargetkan SDK 34 dan mendukung Android dengan API level 23 atau lebih baru (Android 6.0+).

Di Android Studio, buka folder repositori, sinkronkan proyek Gradle, lalu jalankan konfigurasi `app` pada emulator atau perangkat Android.

Untuk membangun APK debug dari terminal, jalankan dari root repositori:

```sh
gradle --no-daemon assembleDebug
```

APK hasil build tersedia di:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Batasan dan catatan data

- Benchmark teman sebaya dan gambaran siswa yang diterima merupakan **asumsi demo**, bukan data penerimaan resmi atau data nyata dari mahasiswa.
- Tanggal penerimaan yang ditampilkan adalah perkiraan dan dapat diedit. Cocokkan dengan pengumuman resmi SNPMB dan situs universitas.
- Berita disediakan oleh hasil pencarian Google News RSS; isi dan ketersediaannya bergantung pada layanan tersebut dan koneksi internet.
- Rekomendasi dan skor tidak menjamin kecocokan akademik, kelulusan seleksi, atau penerimaan di universitas.

### Pengungkapan penggunaan AI

Pengembangan jurusanator dibantu oleh agen AI, termasuk model GPT dan Claude. Keluaran berbantuan AI ditinjau dan disunting selama proses pengembangan. Aplikasi tidak menggunakan GPT, Claude, atau layanan AI lain untuk memproses jawaban maupun menghasilkan rekomendasi saat digunakan.

### Peta kode

Kode utama berada di `app/src/main/java/jurusanator`. Logika rekomendasi dan data jurusan berada terutama di `MajorRecommender` dan `MajorData`; penyimpanan lokal di `Store`; dan layar aplikasi pada kelas-kelas `*Screen` serta tab Coach.

---

## English

**jurusanator** is an Android app that helps prospective students explore college majors that may fit their interests and build an initial plan toward a dream university. It is written in Java using Android platform APIs, with no Kotlin or third-party libraries.

> Quiz results are for guidance, not a decision or a guarantee of admission. Always check official university and SNPMB information and dates.

### Features

- An adaptive interest quiz with up to 24 questions in standard and deep modes. Short mode has 8 questions. After at least 12 answers, a standard/deep quiz can finish early if the same major is the unique #1 for four consecutive answers. Deep mode may also add a head-to-head choice when top results are close.
- The quiz bank includes 12 new questions: two dedicated questions for each of the six RIASEC pillars (Realistic, Investigative, Artistic, Social, Enterprising, and Conventional).
- Top major recommendations plus one **+1 WILDCARD** major to encourage exploring another option. The app includes 67 majors, each with a custom illustration.
- Quiz history and unfinished quizzes are saved on the device. You can resume a quiz you have started.
- **Coach** screens for your selected major-university combination, strengths and areas to prepare, plus a study plan and progress tracking.
- SNBP, SNBT, or independent-admission routes; an editable admissions timeline; and options to add events and reminders to your calendar.
- University news from Google News RSS, with a cache so previously loaded news can be revisited offline. Daily news notifications are optional.
- Indonesian and English interfaces, light and dark themes, and sound effects that can be muted.

### How the app works

1. Answer questions about your interests and preferences. Answer choices range from “yes” to “no”; “don't know” is also available in standard and deep modes.
2. An on-device recommendation engine compares your answer pattern with each major's interest profile. It adaptively chooses the next question from useful questions related to your previous answer. Standard and deep modes can ask up to 24 questions; short mode asks 8.
3. After at least 12 answers, standard/deep mode finishes early if the same major is the unique first-place result for four consecutive answers. Otherwise, the quiz continues to that mode's limit. The app then ranks majors by match score, displays the leading results, and adds one wildcard. In deep mode, it may ask you to choose between the top two when their scores are close.
4. Choose a dream major and university to open Coach. Coach fit points are a guidance summary, not an official admission probability. The quiz and recommendation calculations run on the device; quiz answers are not sent to an AI service.

News requires an internet connection. Calendar and notification features request Android permissions when needed; both are optional. Language, theme, sound, profile, progress, and history are stored locally on the device using app storage.

### Downloading an APK from GitHub Actions

The **Android APK** workflow runs on pushes to the repository and can also be started manually. To download its build:

1. Open the [Actions page](https://github.com/zvkt/jurusanator/actions/workflows/android-apk.yml).
2. Select a completed **Android APK** run with a successful status. To create a new build manually, select **Run workflow** if that button is available.
3. On the run details page, find **Artifacts** and download `jurusanator-debug-apk`.
4. Extract the downloaded ZIP file. It contains `app-debug.apk`.
5. Open the APK on your Android device and follow the installation prompts. If prompted, allow that source to install unknown apps; you can turn that permission off again afterward if you prefer.

The Actions APK is a **debug build for testing**, not a signed release for distribution through Google Play. Artifact access and availability are subject to the repository's GitHub Actions settings.

### Building and running from source

**Requirements:** JDK 17, Gradle 8.9, Android SDK Platform 35, and Android Build Tools 35.0.0. The app targets SDK 34 and supports Android API level 23 or later (Android 6.0+).

In Android Studio, open the repository folder, sync the Gradle project, and run the `app` configuration on an emulator or Android device.

To build a debug APK from a terminal, run this from the repository root:

```sh
gradle --no-daemon assembleDebug
```

The built APK is located at:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Limitations and data notes

- Peer benchmarks and accepted-student profiles are **demo assumptions**, not official admissions figures or real student data.
- Admissions dates are estimates and can be edited. Verify them against official SNPMB announcements and university websites.
- News is provided through Google News RSS search results; its content and availability depend on that service and your internet connection.
- Recommendations and scores do not guarantee academic fit, selection success, or university admission.

### AI use disclosure

The development of jurusanator was assisted by AI agents, including models such as GPT and Claude. AI-assisted output was reviewed and edited during development. The app does not use GPT, Claude, or another AI service to process answers or generate recommendations while it is running.

### Code map

The main source code is in `app/src/main/java/jurusanator`. Recommendation logic and major data are primarily in `MajorRecommender` and `MajorData`; local storage is in `Store`; and app screens are implemented in the `*Screen` classes and Coach tabs.
