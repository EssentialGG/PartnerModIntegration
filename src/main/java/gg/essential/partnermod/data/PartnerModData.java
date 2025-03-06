package gg.essential.partnermod.data;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

public class PartnerModData {
    private ModalData modal;
    @SerializedName("partnered_mods")
    private List<PartnerMod> partneredMods;

    public PartnerModData(ModalData modal, List<PartnerMod> partneredMods) {
        this.modal = modal;
        this.partneredMods = partneredMods;
    }

    public static class PartnerMod {
        private String id;
        @SerializedName("display_name")
        private String displayName;

        public String getId() {
            return id;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    
    public ModalData getModal() {
        return modal;
    }

    public List<PartnerMod> getPartneredMods() {
        return Collections.unmodifiableList(partneredMods);
    }
}
