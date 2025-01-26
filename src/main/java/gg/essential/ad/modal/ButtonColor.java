package gg.essential.ad.modal;

public class ButtonColor {

    public static final ButtonColor GRAY = new ButtonColor(
        0xFF383838,
        0xFF595959,
        0xFF2B2B2B,
        0xFF595959,
        0xFF7F7F7F,
        0xFF424242
    );

    public static final ButtonColor BLUE = new ButtonColor(
        0xFF274673,
        0xFF507BBA,
        0xFF1B3151,
        0xFF164995,
        0xFF3073D4,
        0xFF0A2E62
    );

    public static final ButtonColor RED = new ButtonColor(
        0xFF9F4444,
        0xFFE96D6D,
        0xFF461F1F,
        0xFFC02525,
        0xFFE55252,
        0xFF642626
    );

    private final int buttonColor;
    private final int highlightColor;
    private final int shadowColor;
    private final int hoverButtonColor;
    private final int hoverHighlightColor;
    private final int hoverShadowColor;

    public ButtonColor(int buttonColor, int highlightColor, int shadowColor, int hoverButtonColor, int hoverHighlightColor, int hoverShadowColor) {
        this.buttonColor = buttonColor;
        this.highlightColor = highlightColor;
        this.shadowColor = shadowColor;
        this.hoverButtonColor = hoverButtonColor;
        this.hoverHighlightColor = hoverHighlightColor;
        this.hoverShadowColor = hoverShadowColor;
    }

    public int getButtonColor(boolean hovered) {
        return hovered ? hoverButtonColor : buttonColor;
    }

    public int getHighlightColor(boolean hovered) {
        return hovered ? hoverHighlightColor : highlightColor;
    }

    public int getShadowColor(boolean hovered) {
        return hovered ? hoverShadowColor : shadowColor;
    }
}
