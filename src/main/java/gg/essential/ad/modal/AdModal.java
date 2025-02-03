package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.EssentialUtil;
import gg.essential.ad.Resources;
import gg.essential.ad.data.AdData;
import gg.essential.ad.data.ModalData;
import gg.essential.ad.Tooltip;
import gg.essential.ad.UDesktop;
import gg.essential.ad.mc.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdModal extends Modal {

    private final Map<ModalData.Feature, ResourceLocation> iconTextures = new HashMap<>();

    private static final ResourceLocation X_ICON = Resources.load("x.png");
    private static final ResourceLocation ESSENTIAL_LOGO = Resources.load("essential.png");
    private static final ResourceLocation REMOVE_INTEGRATION = Resources.load("removeintegration.png");

    private final ModalData modalData;
    private final String tooltip;

    private int featuresOffset;

    public AdModal(ModalData modalData, List<AdData.PartnerMod> partnerMods) {
        this.modalData = modalData;
        this.tooltip = getTooltip(partnerMods);
    }

    @Override
    public void init() {
        super.init();

        int height = 49;
        height += Font.getMultilineStringHeight(modalData.getSubtitle("en_us"), 10);
        height += 7;
        featuresOffset = height;
        height += 13 * modalData.getFeatures().size();
        height += 6;
        int learnMoreOffset = height;
        height += 24;
        int installOffset = height;
        height += 20;
        height += 17;
        setDimensions(194, height);

        buttonList.add(new LinkButton(centreX - (Font.getStringWidth("Learn more") + 10) / 2, startY + learnMoreOffset, "Learn More", () -> {
            UDesktop.browse(modalData.getLink("learn_more"));
        }));

        buttonList.add(new InstallButton(centreX - 70, startY + installOffset, () -> {
            boolean success = EssentialUtil.installContainer();
            if (success) {
                ModalManager.INSTANCE.setModal(TwoButtonModal.postInstall());
            } else {
                ModalManager.INSTANCE.setModal(TwoButtonModal.errorModal());
            }
        }));

        buttonList.add(new TextureButton(centreX - 64, startY + height + 8, 128, 8, REMOVE_INTEGRATION, 0xFF5C5C5C, 0xFF757575, false, () -> {
            ModalManager.INSTANCE.setModal(TwoButtonModal.removeAds());
        }));
        buttonList.add(new TextureButton(startX + width - 8 - 5, startY + 8, 5, 5, X_ICON, 0xFF757575, 0xFF999999, true, () -> ModalManager.INSTANCE.setModal(null)));
    }

    @Override
    public void draw(Draw draw) {
        super.draw(draw);

        Minecraft mc = Minecraft.getMinecraft();

        // Icon Block (94px wide, 19px high)
        // shadow
        draw.rect(centreX - 47 + 1, startY + 20 + 1, centreX + 47 + 1, startY + 20 + 19 + 1, 0xFF000000);
        // background
        draw.rect(centreX - 47, startY + 20, centreX + 47, startY + 20 + 19, 0xFF1b3151);
        // icon texture
        draw.texturedRect(ESSENTIAL_LOGO, centreX - 29, startY + 20 + 7, 58, 6, 0, 0, 58, 6);

        // Tagline
        draw.multilineCentredString(modalData.getSubtitle("en_us"),
            centreX, startY + 49, 10, 0xFF999999, 0xFF000000
        );

        // Features
        {
            int maxWidth = 0;
            for (ModalData.Feature feature : modalData.getFeatures()) {
                maxWidth = Math.max(Font.getStringWidth(feature.getText("en_us").replaceAll("__", "")), maxWidth);
            }

            int featureX = centreX - (maxWidth + 14) / 2;

            int featureY = startY + featuresOffset;

            for (ModalData.Feature feature : modalData.getFeatures()) {
                ResourceLocation location = iconTextures.computeIfAbsent(feature, AdModal::loadIconTexture);
                draw.texturedRect(location, featureX + 1, featureY + 1, 10, 10, 0, 0, 10, 10, 0xFF000000);
                draw.texturedRect(location, featureX, featureY, 10, 10, 0, 0, 10, 10, 0xFF0a82fd);

                //fixme improve handling, caching?
                String text = feature.getText("en_us");
                String tooltip = feature.getTooltip("en_us");
                if (tooltip != null) {
                    int tooltipStart = text.indexOf("__");
                    if (tooltipStart == -1) return;
                    int tooltipEnd = text.indexOf("__", tooltipStart + 2);
                    if (tooltipEnd == -1) return;

                    String beforeTooltip = text.substring(0, tooltipStart);
                    int beforeTooltipWidth = Font.getStringWidth(beforeTooltip);
                    String withTooltip = text.substring(tooltipStart + 2, tooltipEnd);
                    int withTooltipWidth = Font.getStringWidth(withTooltip);
                    String afterTooltip = text.substring(tooltipEnd + 2);

                    int textX = featureX + 14;
                    int textY = featureY + 1;

                    if (!beforeTooltip.isEmpty()) {
                        draw.string(beforeTooltip, textX, textY, 0xFFe5e5e5, 0xFF000000);
                        textX += beforeTooltipWidth;
                    }

                    draw.string(withTooltip, textX, textY, 0xFFe5e5e5, 0xFF000000);
                    draw.rect(textX, textY + 8, textX + withTooltipWidth, textY + 9, 0xFFe5e5e5);
                    draw.rect(textX + 1, textY + 9, textX + withTooltipWidth + 1, textY + 10, 0xFF000000);

                    if (draw.hovered(textX, textY, withTooltipWidth, 10)) {
                        int finalX = textX;
                        Draw.deferred(d -> Tooltip.drawTooltip(d, this.tooltip, Tooltip.Position.BELOW, finalX, textY, withTooltipWidth, 10));
                    }

                    textX += withTooltipWidth;

                    if (!afterTooltip.isEmpty()) {
                        draw.string(afterTooltip, textX, textY, 0xFFe5e5e5, 0xFF000000);
                    }
                } else {
                    draw.string(feature.getText("en_us"), featureX + 14, featureY + 1, 0xFFe5e5e5, 0xFF000000);
                }

                featureY += 13;
            }
        }
    }

    @Override
    protected void drawBackground(Draw draw) {
        // border
        draw.rect(startX, startY, startX + width, startY + height, 0xFF1E2A3C);
        // gradient
        //#if MC>=12000
        //$$ draw.drawContext.fillGradient(startX + 1,  startY + 1, startX + width - 1, startY + height - 1, 0xFF16222F, 0xFF11151E);
        //#elseif MC>=11600
        //$$ fillGradient(draw.matrixStack, startX + 1,  startY + 1, startX + width - 1, startY + height - 1, 0xFF16222F, 0xFF11151E);
        //#else
        drawGradientRect(startX + 1,  startY + 1, startX + width - 1, startY + height - 1, 0xFF16222F, 0xFF11151E);
        //#endif
    }

    private String getTooltip(List<AdData.PartnerMod> partnerMods) {
        partnerMods = new ArrayList<>(partnerMods);
        Collections.shuffle(partnerMods);

        if (partnerMods.size() > 5) partnerMods = partnerMods.subList(0, 5);

        String tooltip;
        switch (partnerMods.size()) {
            case 0:
                tooltip = "Hmmm, it seems like you don't\nhave any partner mods installed...";
                break;
            case 1:
                tooltip = "%s\nand many more!";
                break;
            case 2:
                tooltip = "%s, %s,\nand many more!";
                break;
            case 3:
                tooltip = "%s, %s,\n%s, and many more!";
                break;
            case 4:
                tooltip = "%s, %s,\n%s, %s,\nand many more!";
                break;
            case 5:
                tooltip = "%s, %s,\n%s, %s,\n%s, and many more!";
                break;
            default:
                throw new IllegalStateException();
        }
        //fixme: is this a good idea?
        return String.format(tooltip, partnerMods.stream().map(AdData.PartnerMod::getDisplayName).toArray());
    }

    @Override
    public void close() {
        for (ResourceLocation location : iconTextures.values()) {
            Minecraft.getMinecraft().getTextureManager().deleteTexture(location);
        }
        super.close();
    }

    private static ResourceLocation loadIconTexture(ModalData.Feature feature) {
        byte[] bytes = Base64.getDecoder().decode(feature.getIcon());
        return Resources.load(new ByteArrayInputStream(bytes));
    }
}
