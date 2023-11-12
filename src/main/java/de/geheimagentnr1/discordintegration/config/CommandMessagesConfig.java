package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class CommandMessagesConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String ONLY_MANAGEMENT_COMMAND_HINT_MESSAGE_KEY = "only_management_command_hint_message";
	
	@NotNull
	private static final String LINK_CREATED_RESULT_MESSAGE_KEY = "link_created_result_message";
	
	@NotNull
	private static final String LINK_ALREADY_EXISTS_RESULT_MESSAGE_KEY = "link_already_exists_result_message";
	
	@NotNull
	private static final String LINK_REMOVED_RESULT_MESSAGE_KEY = "link_removed_result_message";
	
	@NotNull
	private static final String LINK_INVALID_DISCORD_MEMBER_ID_ERROR_MESSAGE_KEY =
		"link_invalid_discord_member_id_error_message";
	
	@NotNull
	private static final String LINK_COMMANDS_USE_IF_WHITELIST_IS_DISABLED_ERROR_MESSAGE_KEY =
		"link_commands_use_if_whitelist_is_disabled_error_message";
	
	@NotNull
	private static final String UNKNOWN_COMMAND_ERROR_MESSAGE_KEY = "unknown_command_error_message";
	
	@NotNull
	private static final String INVALID_PERMISSIONS_ERROR_MESSAGE_KEY = "invalid_permissions_error_message";
	
	CommandMessagesConfig( @NotNull AbstractMod _abstractMod ) {
		
		super( _abstractMod );
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue(
			"Message that is shown in the help command as a command " +
				"description, if a command can only be executed by a Discord user with the management role",
			ONLY_MANAGEMENT_COMMAND_HINT_MESSAGE_KEY,
			"Only usable by users with the management role"
		);
		registerConfigValue(
			"Result message shown, when a Discord user is successfully linked with a Minecraft account. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%player% = Player name)",
			LINK_CREATED_RESULT_MESSAGE_KEY,
			"Created Linking between Discord account \"%username%\" and Minecraft account \"%player%\""
		);
		registerConfigValue(
			"Result message shown, when a Discord user link with a Minecraft account already exists. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%player% = Player name)",
			LINK_ALREADY_EXISTS_RESULT_MESSAGE_KEY,
			"Linking between Discord account \"%username%\" and Minecraft account \"%player%\" already exists"
		);
		registerConfigValue(
			"Result message shown, when a Minecraft account is successfully unlinked from a Discord user. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%player% = Player name)",
			LINK_REMOVED_RESULT_MESSAGE_KEY,
			"Removed Linking between Discord account \"%username%\" and Minecraft account \"%player%\""
		);
		registerConfigValue(
			"Message that is shown, when a user could not be found for the Discord server",
			LINK_INVALID_DISCORD_MEMBER_ID_ERROR_MESSAGE_KEY,
			"Discord Member does not exist or Discord context is unloadable"
		);
		registerConfigValue(
			"Message that is shown, when the link or unlink commands are executed, " +
				"but the whitelist management in Discord is disabled.",
			LINK_COMMANDS_USE_IF_WHITELIST_IS_DISABLED_ERROR_MESSAGE_KEY,
			"This command is only usable, if the whitelist management in Discord is enabled (whitelist.enabled = " +
				"true)."
		);
		registerConfigValue(
			"Error message shown, when a unknown command is entered in the Discord chat " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%new_line% = New line)",
			UNKNOWN_COMMAND_ERROR_MESSAGE_KEY,
			"%nickname%%new_line%Error: Unknown Command"
		);
		registerConfigValue(
			"Error message shown, when a user without the management " +
				"role tries to execute a management command " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%new_line% = New line)",
			INVALID_PERMISSIONS_ERROR_MESSAGE_KEY,
			"%nickname%%new_line%Error: Invalid permissions, only users with the management role can use this " +
				"command."
		);
	}
	
	@NotNull
	public String getOnlyManagementCommandHintMessage() {
		
		return getValue( String.class, ONLY_MANAGEMENT_COMMAND_HINT_MESSAGE_KEY );
	}
	
	@NotNull
	public String getLinkCreatedResultMessage() {
		
		return getValue( String.class, LINK_CREATED_RESULT_MESSAGE_KEY );
	}
	
	@NotNull
	public String getLinkAlreadyExistsResultMessage() {
		
		return getValue( String.class, LINK_ALREADY_EXISTS_RESULT_MESSAGE_KEY );
	}
	
	@NotNull
	public String getLinkRemovedResultMessage() {
		
		return getValue( String.class, LINK_REMOVED_RESULT_MESSAGE_KEY );
	}
	
	@NotNull
	public String getLinkInvalidDiscordMemberIdErrorMessage() {
		
		return getValue( String.class, LINK_INVALID_DISCORD_MEMBER_ID_ERROR_MESSAGE_KEY );
	}
	
	@NotNull
	public String getLinkCommandsUseIfWhitelistIsDisabledErrorMessage() {
		
		return getValue( String.class, LINK_COMMANDS_USE_IF_WHITELIST_IS_DISABLED_ERROR_MESSAGE_KEY );
	}
	
	@NotNull
	public String getUnknownCommandErrorMessage() {
		
		return getValue( String.class, UNKNOWN_COMMAND_ERROR_MESSAGE_KEY );
	}
	
	@NotNull
	public String getInvalidPermissionsErrorMessage() {
		
		return getValue( String.class, INVALID_PERMISSIONS_ERROR_MESSAGE_KEY );
	}
}
