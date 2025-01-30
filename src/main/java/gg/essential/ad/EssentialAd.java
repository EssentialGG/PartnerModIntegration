package gg.essential.ad;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.essential.ad.data.AdData;
import gg.essential.ad.modal.AdModal;
import gg.essential.ad.modal.ModalManager;
import gg.essential.ad.modal.TwoButtonModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

//#if MC>=11600
//$$ import net.minecraft.client.gui.widget.Widget;
//#endif

//#if FORGE
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

//#if MC>=11600
//#else
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
//#endif

//#if MC>=11800
//$$ import net.minecraftforge.client.event.ScreenEvent;
//#else
import net.minecraftforge.client.event.GuiScreenEvent;
//#endif
//#endif

//#if FABRIC
//$$ import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
//$$ import net.fabricmc.fabric.api.client.screen.v1.Screens;
//#endif

public class EssentialAd {

    public static final Logger LOGGER = LogManager.getLogger("EssentialAd");
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final AdConfig CONFIG = AdConfig.load();

    private static final Set<String> MAIN_MENU_BUTTONS = new HashSet<>(Collections.singletonList("menu.multiplayer"));
    private static final Set<String> PAUSE_MENU_BUTTONS = new HashSet<>(Arrays.asList(
        //#if MC>=12100
        //$$ "menu.server_links", // 1.21+, replaces the report bugs buttons when a server uses server links
        //#endif
        //#if MC>=11600
        //$$ "menu.reportBugs" // 1.16+
        //#else
        "menu.shareToLan" // 1.12.2 and 1.8.9
        //#endif
    ));

    private final CompletableFuture<AdData> adDataFuture = EssentialAPI.fetchAdData();
    private List<AdData.PartnerMod> partnerMods = null;

    public EssentialAd() {
        //#if FORGE
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModalManager.INSTANCE);
        //#else
        //$$ ScreenEvents.AFTER_INIT.register(this::afterScreenInit);
        //$$ ModalManager.INSTANCE.registerEvents();
        //#endif
    }

    private void createButton(
        GuiScreen screen,
        //#if MC>=11600
        //$$ List<Widget> buttonList,
        //#else
        List<GuiButton> buttonList,
        //#endif
        BiConsumer<GuiButton, Integer> adder
    ) {
        if (EssentialUtil.isEssentialLoaded()) return;
        if (CONFIG.shouldHideButtons()) return;

        int width = screen.width;
        int height = screen.height;

        int texYOffset;
        String tooltip;
        int x;
        int y;
        int index = buttonList.size();
        if (screen instanceof GuiMainMenu) {
            texYOffset = 0;
            tooltip = "Enhanced Minecraft\nfeatures, with Essential";

            GuiButton multiplayerButton = UButton.findButton(buttonList, MAIN_MENU_BUTTONS);

            if (multiplayerButton != null) {
                x = UButton.getX(multiplayerButton) + UButton.getWidth(multiplayerButton) + 4;
                y = UButton.getY(multiplayerButton);
                index = buttonList.indexOf(multiplayerButton) + 1;
            } else {
                // Fallback, position using vanilla positioning
                x = width / 2 + 100 + 4;
                y = height / 4 + 48 + 24;
            }
        } else if (screen instanceof GuiIngameMenu) {
            if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                // On a server
                texYOffset = 40;
                tooltip = "Stay connected with friends,\nall inside of Minecraft";
            } else {
                // In singleplayer
                texYOffset = 20;
                tooltip = "Host worlds for free, invite\nfriends, and play together";
            }

            GuiButton reportBugs = UButton.findButton(buttonList, PAUSE_MENU_BUTTONS);

            if (reportBugs != null) {
                x = UButton.getX(reportBugs) + UButton.getWidth(reportBugs) + 4;
                y = UButton.getY(reportBugs);
                index = buttonList.indexOf(reportBugs) + 1;
            } else {
                // Fallback, position using vanilla positioning
                x = width / 2 + 100 + 4;
                y = height / 4 + 72 - 16;
            }
        } else {
            return;
        }

        adder.accept(new AdButton(x, y, texYOffset, button ->
        {
            //#if MC>=11600
            //$$ // Delay since the element is focused after the click is processed.
            //$$ Minecraft.getInstance().enqueue(() -> screen.setListener(null));
            //#endif
            if (EssentialUtil.installationCompleted()) {
                ModalManager.INSTANCE.setModal(TwoButtonModal.postInstall());
            } else {
                AdData data;
                try {
                    data = adDataFuture.join();
                } catch (CompletionException e) {
                    ModalManager.INSTANCE.setModal(TwoButtonModal.errorModal()); // fixme, replace with better wording
                    return;
                }
                ModalManager.INSTANCE.setModal(new AdModal(data.getModal(), getPartnerMods(data)));
            }
        }, tooltip), index);
    }

    //#if FORGE
    @SubscribeEvent
    public void screenInitEvent(
        //#if MC>=11900
        //$$ ScreenEvent.Init.Post event
        //#elseif MC>=11800
        //$$ ScreenEvent.InitScreenEvent.Post event
        //#else
        GuiScreenEvent.InitGuiEvent.Post event
        //#endif
    ) {
        //#if MC>=11800
        //$$ createButton(event.getScreen(), event.getListenersList().stream().filter(AbstractWidget.class::isInstance).map(AbstractWidget.class::cast).toList(), ((button, __) -> event.addListener(button)));
        //#elseif MC>=11700
        //$$ createButton(event.getGui(), event.getWidgetList().stream().filter(AbstractWidget.class::isInstance).map(AbstractWidget.class::cast).toList(), ((button, __) -> event.addWidget(button)));
        //#elseif MC>=11600
        //$$ createButton(event.getGui(), event.getWidgetList(), ((button, __) -> event.addWidget(button)));
        //#elseif MC>=11200
        createButton(event.getGui(), event.getButtonList(), ((button, idx) -> event.getButtonList().add(idx, button)));
        //#else
        //$$ createButton(event.gui, event.buttonList, ((button, idx) -> event.buttonList.add(idx, button)));
        //#endif
    }
    //#else
    //$$ private void afterScreenInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
    //$$     createButton(screen, Screens.getButtons(screen), (button, idx) -> {
    //$$         Screens.getButtons(screen).add(idx, button);
    //$$     });
    //$$ }
    //#endif

    //#if MC<11600
    @SubscribeEvent
    public void buttonPressed(GuiScreenEvent.ActionPerformedEvent.Post event) {
        //#if MC>=11200
        GuiButton button = event.getButton();
        //#else
        //$$ GuiButton button = event.button;
        //#endif
        if (button instanceof AdButton) {
            ((AdButton) button).onPress();
        }
    }
    //#endif

    private List<AdData.PartnerMod> getPartnerMods(AdData data) {
        if (partnerMods != null) return partnerMods;
        return partnerMods = data.getPartneredMods().stream()
            .filter(mod -> ModLoaderUtil.isModLoaded(mod.getId()))
            .collect(Collectors.toList());
    }

}
