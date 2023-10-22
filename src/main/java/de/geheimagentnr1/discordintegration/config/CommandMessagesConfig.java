package de.geheimagentnr1.discordintegration.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.Logger;


public class CommandMessagesConfig {
	
	
	private final ForgeConfigSpec.ConfigValue<String> only_management_command_hint_message;
	
	private final ForgeConfigSpec.ConfigValue<String> link_created_result_message;
	
	private final ForgeConfigSpec.ConfigValue<String> link_already_exists_result_message;
	
	private final ForgeConfigSpec.ConfigValue<String> link_removed_result_message;
	
	private final ForgeConfigSpec.ConfigValue<String> link_invalid_discord_member_id_error_message;
	
	private final ForgeConfigSpec.ConfigValue<String> link_commands_use_if_whitelist_is_disabled_error_message;
	
	private final ForgeConfigSpec.ConfigValue<String> unknown_command_error_message;
	
	private final ForgeConfigSpec.ConfigValue<String> invalid_permissions_error_message;
	
	//package-private
	CommandMessagesConfig( ForgeConfigSpec.Builder builder ) {
		
		builder.comment( "Command message settings" )
			.push( "messages" );
		only_management_command_hint_message = builder.comment(
				"Message that is shown in the help command as a command " +
					"description, if a command can only be executed by a Discord user with the management role" )
			.define( "only_management_command_hint_message", "Only usable by users with the management role" );
		link_created_result_message = builder.comment( "Result message shown, when a Discord user is successfully " +
				"linked with a Minecraft account. " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%player% = Player name)" )
			.define(
				"link_created_result_message",
				"Created Linking between Discord account \"%username%\" and Minecraft account \"%player%\""
			);
		link_already_exists_result_message = builder.comment(
				"Result message shown, when a Discord user link with a " +
					"Minecraft account already exists. " +
					"(Available parameters: %username% = Discord username, " +
					"%nickname% = Discord nickname, " +
					"%player% = Player name)" )
			.define(
				"link_already_exists_result_message",
				"Linking between Discord account \"%username%\" and Minecraft account \"%player%\" already exists"
			);
		link_removed_result_message = builder.comment(
				"Result message shown, when a Minecraft account is successfully unlinked from a " +
					"Discord user. " +
					"(Available parameters: %username% = Discord username, " +
					"%nickname% = Discord nickname, " +
					"%player% = Player name)" )
			.define(
				"link_removed_result_message",
				"Removed Linking between Discord account \"%username%\" and Minecraft account \"%player%\""
			);
		link_invalid_discord_member_id_error_message = builder.comment(
				"Message that is shown, when a user could not be found for the Discord server" )
			.define(
				"link_invalid_discord_member_id_error_message",
				"Discord Member does not exist or Discord context is unloadable"
			);
		link_commands_use_if_whitelist_is_disabled_error_message = builder.comment(
			"Message that is shown, when the link or unlink commands are executed, " +
				"but the whitelist management in Discord is disabled."
		).define(
			"link_commands_use_if_whitelist_is_disabled_error_message",
			"This command is only usable, if the whitelist management in Discord is enabled (whitelist.enabled = true)."
			);
		unknown_command_error_message = builder.comment( "Error message shown, when a unknown command is entered " +
				"in the Discord chat " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%new_line% = New line)" )
			.define( "unknown_command_error_message", "%nickname%%new_line%Error: Unknown Command" );
		invalid_permissions_error_message = builder.comment( "Error message shown, when a user without the " +
				"management" +
				" " +
				"role tries to execute a management command " +
				"(Available parameters: %username% = Discord username, " +
				"%nickname% = Discord nickname, " +
				"%new_line% = New line)" )
			.define(
				"invalid_permissions_error_message",
				"%nickname%%new_line%Error: Invalid permissions, only users with the management role can use this " +
					"command."
			);
		builder.pop();
	}
	
	public String getOnlyManagementCommandHintMessage() {
		
		return only_management_command_hint_message.get();
	}
	
	public String getLinkCreatedResultMessage() {
		
		return link_created_result_message.get();
	}
	
	public String getLinkAlreadyExistsResultMessage() {
		
		return link_already_exists_result_message.get();
	}
	
	public String getLinkRemovedResultMessage() {
		
		return link_removed_result_message.get();
	}
	
	public String getLinkInvalidDiscordMemberIdErrorMessage() {
		
		return link_invalid_discord_member_id_error_message.get();
	}
	
	public String getLinkCommandsUseIfWhitelistIsDisabledErrorMessage() {
		
		return link_commands_use_if_whitelist_is_disabled_error_message.get();
	}
	
	public String getUnknownCommandErrorMessage() {
		
		return unknown_command_error_message.get();
	}
	
	public String getInvalidPermissionsErrorMessage() {
		
		return invalid_permissions_error_message.get();
	}
	
	//package-private
	void printConfig( Logger logger ) {
		
		logger.info(
			"{} = {}",
			only_management_command_hint_message.getPath(),
			only_management_command_hint_message.get()
		);
		logger.info( "{} = {}", link_created_result_message.getPath(), link_created_result_message.get() );
		logger.info(
			"{} = {}",
			link_already_exists_result_message.getPath(),
			link_already_exists_result_message.get()
		);
		logger.info( "{} = {}", link_removed_result_message.getPath(), link_removed_result_message.get() );
		logger.info(
			"{} = {}",
			link_invalid_discord_member_id_error_message.getPath(),
			link_invalid_discord_member_id_error_message.get()
		);
		logger.info( "{} = {}", unknown_command_error_message.getPath(), unknown_command_error_message.get() );
		logger.info( "{} = {}", invalid_permissions_error_message.getPath(), invalid_permissions_error_message.get() );
	}
}
