package jurusanator;
/**
 * Colour palette. Plain Java (no Android imports).
 * Brand = soft lime + light green + dark green. Accents are used for fun stickers.
 */
final class Pal {
    private Pal() {}
    /** true = "dark green" theme, false = "light green" theme. */
    static boolean dark = false;
    static final int INK      = 0xFF111111; // borders + hard shadows
    static final int PAPER    = 0xFFFFFDF0; // card face
    static final int LIME     = 0xFFC8F169; // soft lime (main brand colour)
    static final int LIME_HI = 0xFFA6E636; // stronger lime
    static final int MINT     = 0xFFA8E6B8; // light green
    static final int GREEN    = 0xFF3DA96A; // mid green
    static final int FOREST   = 0xFF174D35; // dark green
    static final int YELLOW   = 0xFFFFE15A;
    static final int PINK     = 0xFFFF9FC6;
    static final int ORANGE   = 0xFFFFAA5A;
    static final int SKY      = 0xFF8AD7F8;
    static final int LILAC    = 0xFFC9B8FF;
    static final int CORAL    = 0xFFFF7A68;
    static final int BROWN    = 0xFFB9855A;
    static final int GRAY     = 0xFFD9DCD2;
    /** One colour per Holland interest: R, I, A, S, E, C. */
    static final int[] INTEREST = {ORANGE, SKY, PINK, YELLOW, LILAC, MINT};
    /** Answer colours: Yes, Maybe, Don't know, Maybe not, No. */
    static final int[] ANSWER = {LIME_HI, MINT, PAPER, ORANGE, CORAL};
    static int bg()        { return dark ? 0xFF1F3D2D : 0xFFEAF6CF; }
    static int bgText()    { return dark ? 0xFFEAF6CF : INK; }
    static int bgMuted() { return dark ? 0xFFB4D2A0 : 0xFF3E5A33; }
    static int dots()      { return dark ? 0xFF2F5A43 : 0xFFD0E6A0; } }


