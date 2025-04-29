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

// Stub class so Forge doesn't complain that there's no mod for the toml entry when running standalone jars for testing
// but at the same time it can easily be excluded so Forge doesn't complain about there being no toml entry for the mod
// when bundled in another mod jar.
// Legacy Forge doesn't complain about the toml, but it would complain if there are multiple mods with the same id, so
// same procedure there too.
package gg.essential.partnermod;

//#if FORGE
import net.minecraftforge.fml.common.Mod;

//#if MC>=11600
//$$ @Mod("essential_partner_mod")
//#else
@Mod(modid = "essential_partner_mod")
//#endif
//#endif
public class EssentialPartnerMod {
}
