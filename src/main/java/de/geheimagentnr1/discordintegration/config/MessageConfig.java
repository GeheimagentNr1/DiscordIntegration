package de.geheimagentnr1.discordintegration.config;

import de.geheimagentnr1.minecraft_forge_api.AbstractMod;
import de.geheimagentnr1.minecraft_forge_api.config.AbstractSubConfig;
import org.jetbrains.annotations.NotNull;


public class MessageConfig extends AbstractSubConfig {
	
	
	@NotNull
	private static final String ENABLED_KEY = "enabled";
	
	@NotNull
	private static final String MESSAGE_KEY = "message";
	
	@NotNull
	private final String enabledComment;
	
	@NotNull
	private final String messageComment;
	
	@NotNull
	private final String messageDefaultValue;
	
	MessageConfig(
		@NotNull AbstractMod _abstractMod,
		@NotNull String enabledComment,
		@NotNull String messageComment,
		@NotNull String messageDefaultValue ) {
		
		super( _abstractMod );
		this.enabledComment = enabledComment;
		this.messageComment = messageComment;
		this.messageDefaultValue = messageDefaultValue;
	}
	
	@Override
	protected void registerConfigValues() {
		
		registerConfigValue( enabledComment, ENABLED_KEY, true );
		registerConfigValue( messageComment, MESSAGE_KEY, messageDefaultValue );
	}
	
	@NotNull
	public boolean isEnabled() {
		
		return getValue( Boolean.class, ENABLED_KEY );
	}
	
	@NotNull
	public String getMessage() {
		
		return getValue( String.class, MESSAGE_KEY );
	}
}
