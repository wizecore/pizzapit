package com.wizecore.pizzapit;

import java.util.Map;

import com.google.gson.JsonElement;

/**
 * Single response to some phrase.
 * 
 * @author Ruslan
 */
public interface ConversationResponse {
	/**
	 * Response phrase. Can be null.
	 */
	String getPhrase();

	/**
	 * Response return parameters. Optionally enrich phrase with metadata.
	 */
	Map<String, JsonElement> getParameters();
}
