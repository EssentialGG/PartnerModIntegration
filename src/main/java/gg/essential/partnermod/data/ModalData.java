/*
 * Copyright © 2025 ModCore Inc. All rights reserved.
 *
 * This code is part of ModCore Inc.’s Essential Partner Mod Integration
 * repository and is protected under copyright. For the full license, see:
 * https://github.com/EssentialGG/EssentialPartnerMod/tree/main/LICENSE
 *
 * You may modify, fork, and use the Mod, but may not retain ownership of
 * accepted contributions, claim joint ownership, or use Essential’s trademarks.
 */

package gg.essential.partnermod.data;

import java.util.List;
import java.util.Map;

public class ModalData {
    private Map<String, String> subtitle;
    private List<Feature> features;
    private Map<String, String> links;

    public static class Feature {
        private String icon;
        private Map<String, String> text;
        private Map<String, String> tooltip;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getText(String language) {
            return text.get(language);
        }

        public String getTooltip(String language) {
            return tooltip == null ? null : tooltip.get(language);
        }
    }

    public String getSubtitle(String language) {
        return subtitle.get(language);
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public String getLink(String key) {
        return links.get(key);
    }
}
