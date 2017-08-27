package com.wizecore.pizzapit;

import java.util.Map;

import com.google.gson.JsonElement;

/**
 * Single conversation context.
 * 
 * @author Ruslan
 */
public interface ConversationContext {
	/**
	 * Retained conversation parameters.
	 * 
	 * Parameters are merged by the following rules:
	 * <ul>
	 * 	<li>Primitives - replaced.
	 *  <li>Arrays - new values added to the end.
	 * </ul>
	 */
	Map<String, JsonElement> getParameters();
}
