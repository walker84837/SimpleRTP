# SimpleRtp

> A Bukkit plugin for random teleportation to safe locations

SimpleRtp is a plugin for Bukkit that allows players to teleport to random safe locations within a specified range. This plugin is designed to provide a fun and exciting way for players to explore their Minecraft world.

## Installing / Getting started

To install SimpleRtp, simply download the plugin and place it in your Bukkit server's plugins directory. Then, restart your server and the plugin will be enabled.

```shell
wget https://github.com/walker84837/SimpleRtp/releases/download/v0.1.0/SimpleRtp.jar
mv SimpleRtp.jar /path/to/bukkit/plugins/
```

Once installed, players can use the `/rtp` command to teleport to a random safe location.

## Developing

To develop SimpleRtp further, you can clone the repository and build the plugin using Gradle.

```shell
git clone https://github.com/walker84837/SimpleRtp.git
cd SimpleRtp/
./gradlew build
```

This will create a `SimpleRtp.jar` file in the `build/libs` directory, which you can then use to update your server.

### Building

To build the plugin, you will need to have Gradle installed on your system. Once you have cloned the repository, you can build the plugin using the following command:

```shell
./gradlew build
```

This will create a `SimpleRtp.jar` file in the `target` directory.

## Features

SimpleRtp provides the following features:

* Random teleportation to safe locations within a specified range
* Configurable minimum and maximum range
* Configurable maximum attempts to find a safe location
* Support for multiple worlds

## Configuration

SimpleRtp can be configured using the following options:

|Value|Type|Default|Description|
|---|---|---|
|min-range|Integer|3000|The minimum range in blocks that the player will be teleported from the spawn point.|
|max-range|Integer|World border size / 2|The maximum range in blocks that the player will be teleported from the spawn point.|
|max-attempts|Integer|50|The maximum number of attempts to find a safe location.|

## Contributing

If you'd like to contribute to SimpleRtp, please fork the repository and use a feature branch. Pull requests are warmly welcome. Please see the `CONTRIBUTING.md` file for more information on how to contribute.

## Links

* Project homepage: https://github.com/walker84837/SimpleRtp/
* Repository: https://github.com/walker84837/SimpleRtp/
* Issue tracker: https://github.com/walker84837/SimpleRtp/issues

## Licensing

The code in this project is licensed under the GPLv3 license. You can find the text version of the license in the `LICENSE` file.
