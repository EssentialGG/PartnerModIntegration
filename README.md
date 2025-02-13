# Essential Ad Mod

## Release Process
1. Bump `version` in `build.gradle.kts`.
2. Push `vX.X.X` tag.
3. Update changelog in documentation repo, inform partners of the new version.

## Overrides

There are a number of ways to test new configurations for the ad mod.

The `essential.ad.api` JVM property sets the endpoint to use for the Essential API. For example,
`-Dessential.ad.api=https://api.lon-dev.modcore.dev` can be used to use the dev API, which uses
the `develop` branch of the configuration repository.

The configuration from the configuration repository (the `partner/modal` folder) can be placed
into the `.minecraft/config/essentialad/override/` folder, and they will override the configuration
loaded from the API.

The entire API response can be overridden by placing a `data.override.json` file in
the `.minecraft/config/essentialad/` folder. This allows adding extra partner mods for testing.
Use the current [production](https://api.essential.gg/v1/mod-partner/modal) or
[staging](https://api.lon-dev.modcore.dev/v1/mod-partner/modal) API response as a starting point.
