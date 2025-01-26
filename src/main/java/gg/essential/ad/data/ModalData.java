package gg.essential.ad.data;

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
