package de.geheimagentnr1.discordintegration.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;


public class MessageUtil {
	
	
	@NotNull
	private static final String PARAMETER_CHARACTER = "%";
	
	@NotNull
	public static String replaceParameters( @NotNull String message, @NotNull Map<String, String> parameters ) {
		
		return parameters.entrySet().stream().reduce(
			message,
			( newMessage, parameter ) -> newMessage.replace(
				PARAMETER_CHARACTER + parameter.getKey() + PARAMETER_CHARACTER,
				parameter.getValue()
			),
			( message1, message2 ) -> message1
		);
	}
}
