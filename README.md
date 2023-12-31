# extension-discord

## Setup

This module is part of the plugin-project [1]. You can use it as standalone project or as bundle.

### Standalone

Clone this project and enter your gitlab credentials in your gradle user home
properties (`~/.gradle/gradle.propreties`):

```
timesnakeUser=<user>
timesnakePassword=<access_token>

timesnakePluginDir=<plugins_dir>
```

Replace `<user>` with your gitlab username and `<access_token>` with an access-token.
You can optionally replace `<plugins_dir>` with a directory to export the plugin directly (therefore run the
gradle `exportAsPlugin` task).

### Bundle

To use this project in the multimodule plugin-project, read the setup guide in the root module [1].

## Testing

To test this module, you must set up a test server, therefore read regarding guide in the root module [1].

## Usage

[root module](https://git.timesnake.de/timesnake/minecraft/plugin-root-project)
The Discord extension is controlled via channel messages. This requires a ChannelDiscordMessage to
be sent via the
ChannelSystem like this:\
```Network.getChannel().sendMessage(new ChannelDiscordMessage<>(...));```

The ChannelDiscordMessage takes three arguments:

- ```String category```: The discord channel category, this message is affecting
- ```MessageType<Value> type```: The type of message being sent
- ```Value value```: The payload, containing important information regarding the message type

All currently available MessageTypes and their usage are explained below.

### MessageType.Discord.MOVE_TEAMS:

This message is used if you want to:

- Create a new category (with channels inside it)
- Add new channels to an already existing category
- Move users to channels within a specified category
- Do all of the above simultaneously

The value of the MOVE_TEAMS message is an instance of a `DiscordChannelMessage.Allocation` object,
which has to be
initialized with a Map, mapping a String (name of channel within the category) to a list of UUIDs.

The extension will:

- Create the category specified in the constructor of `ChannelDiscordMessage` (if it does not exist)
- Create voice channels named according to the Key Values of the Map (if they don't exist)
- Move all users specified by the UUID to their specific voice channel (if they are online and
  registered)

### MessageType.Discord.DESTROY_TEAMS:

This message is used if you want to:

- Delete a whole category and all channels inside it
- Delete specific channels within a specific category

The value of the DESTROY_TEAMS message is a list of Strings.

The extension will:

- Delete all channels within the category specified in the constructor of `ChannelDiscordMessage` as
  well as the
  category itself if the given list is empty
- Delete just the specified channels by name

All users currently connected to a deleted channel are sent to a fallback channel specified in the
config file (coming
soon)

## Code Style

The code style guide can be found in the plugin root project [1].

## License

The source is licensed under the GNU GPLv2 license that can be found in the [LICENSE](LICENSE)
  file.

[1] https://git.timesnake.de/timesnake/minecraft/plugin-root-project