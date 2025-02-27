package gg.essential.ad.modal;

import gg.essential.ad.Draw;
import gg.essential.ad.EssentialAd;
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

    private static final ResourceLocation X_ICON = Resources.load("x.png");
    private static final ResourceLocation ESSENTIAL_LOGO = Resources.load("essential.png");
    private static final ResourceLocation REMOVE_INTEGRATION = Resources.load("removeintegration.png");

    private final Map<FeatureEntry, ResourceLocation> iconTextures = new HashMap<>();

    private final ModalData modalData;
    private final String parterModsTooltip;
    private final List<FeatureEntry> features = new ArrayList<>();

    private int featuresOffset;

    public AdModal(ModalData modalData, List<AdData.PartnerMod> partnerMods) {
        this.modalData = modalData;
        this.parterModsTooltip = getTooltip(partnerMods);

        for (ModalData.Feature feature : modalData.getFeatures()) {
            features.add(FeatureEntry.fromInfra(feature));
        }
    }

    @Override
    public void init() {
        super.init();

        int height = 49;
        height += Font.getMultilineStringHeight(modalData.getSubtitle("en_us"), 10);
        height += 7;
        featuresOffset = height;
        height += 13 * features.size();
        height += 6;
        int learnMoreOffset = height;
        height += 24;
        int installOffset = height;
        height += 20;
        height += 17;
        setDimensions(194, height);

        buttonList.add(new LinkButton(centreX - (Font.getStringWidth("Learn more") + 10) / 2, startY + learnMoreOffset, "Learn more", () -> {
            UDesktop.browse(modalData.getLink("learn_more"));
        }));

        buttonList.add(new InstallButton(centreX - 70, startY + installOffset, () -> {
            boolean success = EssentialUtil.installContainer();
            if (success) {
                ModalManager.INSTANCE.setModal(TwoButtonModal.postInstall());
            } else {
                ModalManager.INSTANCE.setModal(TwoButtonModal.installFailed());
            }
        }));

        buttonList.add(new TextureButton(centreX - 64, startY + height + 8, 128, 8, REMOVE_INTEGRATION, 0xFF5C5C5C, 0xFF757575, false, () -> {
            ModalManager.INSTANCE.setModal(TwoButtonModal.removeAds(this));
        }));
        buttonList.add(new TextureButton(startX + width - 8 - 5, startY + 8, 5, 5, X_ICON, 0xFF757575, 0xFF999999, true, () -> ModalManager.INSTANCE.setModal(null)));
    }

    @Override
    public void draw(Draw draw) {
        super.draw(draw);

        Minecraft mc = Minecraft.getMinecraft();

        // Icon Block (98px wide, 19px high)
        // shadow
        draw.rect(centreX - 49 + 1, startY + 20 + 1, centreX + 49 + 1, startY + 20 + 19 + 1, 0xFF000000);
        // background
        draw.rect(centreX - 49, startY + 20, centreX + 49, startY + 20 + 19, 0xFF1b3151);
        // icon texture
        draw.texturedRect(ESSENTIAL_LOGO, centreX - 40, startY + 20 + 7, 80, 6, 0, 0, 80, 6, 0xFFE5E5E5);

        // Tagline
        draw.multilineCentredString(modalData.getSubtitle("en_us"),
            centreX, startY + 49, 10, 0xFFBFBFBF, 0xFF000000
        );

        // Features
        {
            int maxWidth = 0;
            for (FeatureEntry feature : features) {
                maxWidth = Math.max(Font.getStringWidth(feature.getDisplayedText()), maxWidth);
            }

            int featureX = centreX - (maxWidth + 15) / 2;

            int featureY = startY + featuresOffset;

            for (FeatureEntry feature : features) {
                ResourceLocation location = iconTextures.computeIfAbsent(feature, AdModal::loadIconTexture);
                draw.texturedRect(location, featureX + 1, featureY + 1, 10, 10, 0, 0, 10, 10, 0xFF000000);
                draw.texturedRect(location, featureX, featureY, 10, 10, 0, 0, 10, 10, 0xFF0a82fd);

                int textX = featureX + 15;
                int textY = featureY + 1;

                String text = feature.text;
                FeatureTooltip tooltip = feature.tooltip;
                if (tooltip != null) {
                    int tooltipStart = tooltip.tooltipStart;
                    int tooltipEnd = tooltip.tooltipEnd;

                    String beforeTooltip = text.substring(0, tooltipStart);
                    int beforeTooltipWidth = Font.getStringWidth(beforeTooltip);
                    String withTooltip = text.substring(tooltipStart + 2, tooltipEnd);
                    int withTooltipWidth = Font.getStringWidth(withTooltip);
                    String afterTooltip = text.substring(tooltipEnd + 2);

                    if (!beforeTooltip.isEmpty()) {
                        draw.string(beforeTooltip, textX, textY, 0xFFe5e5e5, 0xFF000000);
                        textX += beforeTooltipWidth;
                    }

                    draw.string(withTooltip, textX, textY, 0xFFe5e5e5, 0xFF000000);
                    draw.rect(textX, textY + 8, textX + withTooltipWidth, textY + 9, 0xFFe5e5e5);
                    draw.rect(textX + 1, textY + 9, textX + withTooltipWidth + 1, textY + 10, 0xFF000000);

                    if (draw.hovered(textX, textY, withTooltipWidth, 10)) {
                        int finalX = textX;
                        String tooltipText;
                        if (tooltip.text.equals("MOD_PARTNERS")) {
                            tooltipText = this.parterModsTooltip;
                        } else {
                            tooltipText = tooltip.text;
                        }
                        Draw.deferred(d -> Tooltip.drawTooltip(d, tooltipText, Tooltip.Position.BELOW, finalX, textY, withTooltipWidth, 10));
                    }

                    textX += withTooltipWidth;

                    if (!afterTooltip.isEmpty()) {
                        draw.string(afterTooltip, textX, textY, 0xFFe5e5e5, 0xFF000000);
                    }
                } else {
                    draw.string(text, textX, textY, 0xFFe5e5e5, 0xFF000000);
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
                tooltip = "Partnered mods receive\na share of purchases";
                break;
            case 1:
                tooltip = "%s and other\npartnered mods receive\na share of purchases";
                break;
            case 2:
                tooltip = "%s, %s,\nand other partnered mods\nreceive a share of purchases";
                break;
            case 3:
                tooltip = "%s, %s,\n%s, and other partnered mods\nreceive a share of purchases";
                break;
            case 4:
                tooltip = "%s, %s,\n%s, %s,\nand other partnered mods\nreceive a share of purchases";
                break;
            case 5:
                tooltip = "%s, %s,\n%s, %s,\n%s, and other partnered mods\nreceive a share of purchases";
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
        iconTextures.clear();
        super.close();
    }

    private static ResourceLocation loadIconTexture(FeatureEntry feature) {
        byte[] bytes = Base64.getDecoder().decode(feature.icon);
        return Resources.load(new ByteArrayInputStream(bytes));
    }

    private static class FeatureEntry {
        private final String text;
        private final String icon;
        private final FeatureTooltip tooltip;

        public FeatureEntry(String text, String icon, FeatureTooltip tooltip) {
            this.text = text;
            this.icon = icon;
            this.tooltip = tooltip;
        }

        public String getDisplayedText() {
            if (tooltip != null) {
                return text.substring(0, tooltip.tooltipStart)
                    + text.substring(tooltip.tooltipStart + 2, tooltip.tooltipEnd)
                    + text.substring(tooltip.tooltipEnd + 2);
            } else {
                return text;
            }
        }

        public static FeatureEntry fromInfra(ModalData.Feature feature) {
            String text = feature.getText("en_us");
            String tooltipText = feature.getTooltip("en_us");

            FeatureTooltip tooltip = null;
            if (tooltipText != null) {
                tooltip = FeatureTooltip.parse(text, tooltipText);
            }
            return new FeatureEntry(text, feature.getIcon(), tooltip);
        }
    }

    private static class FeatureTooltip {
        private final String text;
        private final int tooltipStart;
        private final int tooltipEnd;

        public FeatureTooltip(String text, int tooltipStart, int tooltipEnd) {
            this.text = text;
            this.tooltipStart = tooltipStart;
            this.tooltipEnd = tooltipEnd;
        }

        public static FeatureTooltip parse(String text, String tooltipText) {
            int tooltipStart = text.indexOf("__");
            if (tooltipStart == -1) {
                EssentialAd.LOGGER.warn("Invalid tooltip for string: {}", text);
                return null;
            }
            int tooltipEnd = text.indexOf("__", tooltipStart + 2);
            if (tooltipEnd == -1) {
                EssentialAd.LOGGER.warn("Invalid tooltip for string: {}", text);
                return null;
            }
            return new FeatureTooltip(tooltipText, tooltipStart, tooltipEnd);
        }
    }
}
