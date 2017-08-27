package com.wizecore.pizzapit;

import java.io.IOException;

import javax.validation.constraints.NotNull;

/**
 * Conversation engine implementation. 
 * 
 * @author Ruslan
 */
public interface TextConversation {

	/**
	 * Issue one phrase to conversation engine.
	 * 
	 * @param ctx Context in which conversation is taking place.
	 * @param phrase Text phrase to say.
	 * @return Response from engine.
	 */
	ConversationResponse say(@NotNull ConversationContext ctx, @NotNull String phrase) throws IOException;
	
	/**
	 * Start engine, produce context.
	 * @return Ready to use conversation context.
	 */
	ConversationContext start();
}
