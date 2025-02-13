package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.EssentialAd;
import gg.essential.ad.EssentialUtil;
import gg.essential.ad.UDesktop;
import gg.essential.ad.UResolution;
import net.minecraft.client.Minecraft;

public class TwoButtonModal extends Modal {

    private final String text;
    private final ButtonProducer leftButtonProducer;
    private final ButtonProducer rightButtonProducer;

    public TwoButtonModal(String text, ButtonProducer leftButtonProducer, ButtonProducer rightButtonProducer) {
        this.text = text;
        this.leftButtonProducer = leftButtonProducer;
        this.rightButtonProducer = rightButtonProducer;
    }

    @Override
    public void init() {
        super.init();

        int textLines = text.split("\n").length;
        int textHeight = 10 * textLines + 2 * (textLines - 1);

        setDimensions(224, 70 + textHeight);

        int buttonY = startY + 20 + textHeight + 17;
        buttonList.add(leftButtonProducer.make(centreX - 95, buttonY, 91));
        buttonList.add(rightButtonProducer.make(centreX + 4, buttonY, 91));
    }

    @Override
    public void draw(Draw draw) {
        super.draw(draw);

        draw.multilineCentredString(text, centreX, startY + 20, 12, 0xFFe5e5e5, 0xFF000000);
    }

    public static TwoButtonModal postInstall() {
        return new TwoButtonModal(
            "Essential Mod will install the next time\nyou launch the game.",
            (x, y, width) -> {
                ModalButton button = new ModalButton(x, y, width, ButtonColor.GRAY, "Quit & Install", EssentialUtil::shutdown);
                button.setTooltip("This will close your game!");
                return button;
            },
            (x, y, width) -> new ModalButton(x, y, width, ButtonColor.BLUE, "Okay", () -> ModalManager.INSTANCE.setModal(null))
        );
    }

    public static TwoButtonModal removeAds() {
        return new TwoButtonModal(
            "Do you want to remove all\n'Get Essential Mod' buttons?",
            (x, y, width) -> new ModalButton(x, y, width, ButtonColor.GRAY, "No", () -> {
                ModalManager.INSTANCE.setModal(null);
            }),
            (x, y, width) -> new ModalButton(x, y, width, ButtonColor.RED, "Yes", () -> {
                EssentialAd.CONFIG.hideButtons();
                ModalManager.INSTANCE.setModal(null);
                Minecraft.getMinecraft().currentScreen.onResize(Minecraft.getMinecraft(), UResolution.getScaledWidth(), UResolution.getScaledHeight());
            })
        );
    }

    public static TwoButtonModal installFailed() {
        return new TwoButtonModal(
            "Essential Mod failed to install.\nSomething went wrong.",
            (x, y, width) -> new ModalButton(x, y, width, ButtonColor.GRAY, "Okay", () -> {
                ModalManager.INSTANCE.setModal(null);
            }),
            (x, y, width) -> new ModalButton(x, y, width, ButtonColor.BLUE, "Download", () -> {
                UDesktop.browse("https://essential.gg/download");
                ModalManager.INSTANCE.setModal(null);
            })
        );
    }

    public interface ButtonProducer {
        ModalButton make(int x, int y, int width);
    }
}
