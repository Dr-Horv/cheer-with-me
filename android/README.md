# Cheer With Me - Android

## Dev Setup
Configure mapbox: https://docs.mapbox.com/android/maps/guides/install/ <br />
Add `MAPBOX_DOWNLOADS_TOKEN=<secret-token>` to your $HOME/.gradle/gradle.properties <br />
Get a copy of the private_key.pepk from another developer (we should probably have a dev key in the repo?)

Add the ip or domain of dev backend in `network_security_config.xml`. <br />
Update the backend ip in `BackendModule.kt` 