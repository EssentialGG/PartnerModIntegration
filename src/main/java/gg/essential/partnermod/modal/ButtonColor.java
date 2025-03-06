package gg.essential.partnermod.modal;

public class ButtonColor {

    public static final ButtonColor GRAY = new ButtonColor(
        0xFF323232,
        0xFF5C5C5C,
        0xFF000000,
        0xFF474747,
        0xFF757575,
        0xFF000000
    );

    public static final ButtonColor BLUE = new ButtonColor(
        0xFF223F69,
        0xFF3671C7,
        0xFF000000,
        0xFF2A5695,
        0xFF5490E8,
        0xFF000000
    );

    public static final ButtonColor RED = new ButtonColor(
        0xFF461F1F,
        0xFF8B3636,
        0xFF000000,
        0xFF642626,
        0xFF9F4444,
        0xFF000000
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
