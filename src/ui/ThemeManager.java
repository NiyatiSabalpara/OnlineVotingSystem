package ui;

/**
 * Global singleton that manages dark/light theme state and provides
 * all color tokens consumed by inline-styled JavaFX components.
 */
public class ThemeManager {

    public enum Theme { DARK, LIGHT }

    private static Theme current = Theme.DARK;

    public static Theme getCurrent() { return current; }
    public static boolean isDark()   { return current == Theme.DARK; }

    public static void toggle() {
        current = current == Theme.DARK ? Theme.LIGHT : Theme.DARK;
    }

    /** Returns the file: URL for the active CSS file. */
    public static String getCssUrl() {
        if (isDark()) {
            return new java.io.File("src/ui/style-dark.css").exists()
                    ? "file:src/ui/style-dark.css" : "file:ui/style-dark.css";
        } else {
            return new java.io.File("src/ui/style-light.css").exists()
                    ? "file:src/ui/style-light.css" : "file:ui/style-light.css";
        }
    }

    /** Convenience: toggle then update the scene stylesheet and swap the root. */
    public static void applyToggle(javafx.scene.Scene scene,
                                   java.util.function.Supplier<javafx.scene.Parent> factory) {
        toggle();
        scene.getStylesheets().setAll(getCssUrl());
        scene.setRoot(factory.get());
    }

    // ── Background ────────────────────────────────────────────────────────────
    public static String bgBase()      { return isDark() ? "#0d0f1a" : "#f4f6fb"; }
    public static String bgSurface()   { return isDark() ? "#13172b" : "#ffffff"; }
    public static String bgElevated()  { return isDark() ? "#1a1f38" : "#eef1f8"; }
    public static String sidebar()     { return isDark() ? "#0a0c18" : "#ffffff"; }
    public static String sidebarBorder(){ return isDark() ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.08)"; }
    public static String navBg()       { return isDark() ? "rgba(10,12,24,0.97)" : "rgba(255,255,255,0.97)"; }

    // ── Text ─────────────────────────────────────────────────────────────────
    public static String textPrimary()  { return isDark() ? "#e8eaf6" : "#1a1a2e"; }
    public static String textSecondary(){ return isDark() ? "#8892b0" : "#5a6280"; }
    public static String textMuted()    { return isDark() ? "#3d4466" : "#9aa0b4"; }

    // ── Borders / dividers ────────────────────────────────────────────────────
    public static String border()   { return isDark() ? "rgba(255,255,255,0.06)" : "rgba(0,0,0,0.08)"; }
    public static String divider()  { return isDark() ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.07)"; }

    // ── Cards ─────────────────────────────────────────────────────────────────
    public static String cardBg()     { return bgSurface(); }
    public static String cardShadow() { return isDark() ? "rgba(0,0,0,0.40)" : "rgba(0,0,0,0.07)"; }
    public static String glassCard()  {
        return "-fx-background-color: " + cardBg() + "; -fx-background-radius: 16; "
             + "-fx-border-color: " + border() + "; -fx-border-radius: 16; "
             + "-fx-border-width: 1; -fx-padding: 24 24 24 24; "
             + "-fx-effect: dropshadow(gaussian, " + cardShadow() + ", 18, 0, 0, 6);";
    }

    // ── Inputs ────────────────────────────────────────────────────────────────
    public static String inputBg()     { return isDark() ? "rgba(255,255,255,0.05)" : "rgba(0,0,0,0.04)"; }
    public static String inputBorder() { return isDark() ? "rgba(255,255,255,0.08)" : "rgba(0,0,0,0.12)"; }

    // ── Activity / table rows ─────────────────────────────────────────────────
    public static String activityBg() { return isDark() ? "rgba(255,255,255,0.02)" : "rgba(0,0,0,0.025)"; }

    // ── Accents (same for both themes) ────────────────────────────────────────
    public static String accent()      { return "#6c63ff"; }
    public static String accentCyan()  { return isDark() ? "#00d4ff" : "#0099cc"; }
    public static String accentTeal()  { return "#00c98a"; }
    public static String danger()      { return "#ff5470"; }
    public static String success()     { return "#00e5a0"; }
    public static String warning()     { return "#ffc107"; }

    // ── Portal accent secondary (lighter for dark, rich for light) ────────────
    public static String adminAccentBg()     { return isDark() ? "rgba(108,99,255,0.18)" : "rgba(108,99,255,0.10)"; }
    public static String candidateAccentBg() { return isDark() ? "rgba(0,212,255,0.12)"  : "rgba(0,153,204,0.10)"; }
    public static String voterAccentBg()     { return isDark() ? "rgba(0,229,160,0.12)"  : "rgba(0,201,138,0.10)"; }

    // ── Sidebar nav button styles ─────────────────────────────────────────────
    public static String navNormal() {
        return "-fx-background-color: transparent; -fx-text-fill: " + textSecondary() + "; "
             + "-fx-font-size: 14px; -fx-font-weight: 600; -fx-alignment: center-left; "
             + "-fx-padding: 11 14 11 14; -fx-background-radius: 10; -fx-cursor: hand; "
             + "-fx-border-color: transparent;";
    }
    public static String navHover() {
        return "-fx-background-color: " + (isDark() ? "rgba(255,255,255,0.04)" : "rgba(0,0,0,0.04)") + "; "
             + "-fx-text-fill: " + textPrimary() + "; "
             + "-fx-font-size: 14px; -fx-font-weight: 600; -fx-alignment: center-left; "
             + "-fx-padding: 11 14 11 14; -fx-background-radius: 10; -fx-cursor: hand; "
             + "-fx-border-color: transparent;";
    }
    public static String navActive(String accentHex) {
        return "-fx-background-color: " + accentHex + (isDark() ? "22" : "18") + "; "
             + "-fx-text-fill: " + accentHex + "; "
             + "-fx-font-size: 14px; -fx-font-weight: 800; -fx-alignment: center-left; "
             + "-fx-padding: 11 14 11 11; -fx-background-radius: 10; -fx-cursor: hand; "
             + "-fx-border-color: " + accentHex + "; -fx-border-width: 0 0 0 3; "
             + "-fx-border-radius: 2 10 10 2;";
    }

    // ── Theme toggle button label ─────────────────────────────────────────────
    public static String toggleLabel() {
        return isDark() ? "☀️   Light Mode" : "🌙   Dark Mode";
    }
}
