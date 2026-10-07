Update the Discord library JDA to version 6
Fix messages of other bots not being transmitted to Minecraft (transmit_bot_messages)
Make "/discord linkings" only available for admins (permission level 3) in game: "/discord linkings link|unlink <player> <discordMemberId>" could be used without operator permission, "/discord linkings link|unlink <player>" without a Discord ID still works for commands sent from Discord
Add a warning on startup, if whitelist.single_linking_management_role_id lets every member (de)activate linkings (@everyone role or same role as whitelist.role_id) or is no role of the Discord server
Fix the server not stopping, if Discord does not shut down in time
Fix Discord users being able to inject own text component elements (for example click events running commands) into the Minecraft chat, if chat.use_raw_message_format_discord_to_minecraft is enabled
