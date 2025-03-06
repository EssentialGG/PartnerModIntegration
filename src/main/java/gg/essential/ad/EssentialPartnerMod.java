// Stub class so Forge doesn't complain that there's no mod for the toml entry when running standalone jars for testing
// but at the same time it can easily be excluded so Forge doesn't complain about there being no toml entry for the mod
// when bundled in another mod jar.
// Legacy Forge doesn't complain about the toml, but it would complain if there are multiple mods with the same id, so
// same procedure there too.
package gg.essential.ad;

//#if FORGE
import net.minecraftforge.fml.common.Mod;

//#if MC>=11600
//$$ @Mod("essential-partner-mod")
//#else
@Mod(modid = "essential-partner-mod")
//#endif
//#endif
public class EssentialPartnerMod {
}
