package com.wizecore.pizzapit.apiai;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;
import com.wizecore.pizzapit.ConversationContext;

public class APIAIContext implements ConversationContext {

	protected Map<String, JsonElement> params = new HashMap<>();

	@Override
	public Map<String, JsonElement> getParameters() {
		return params;
	}
}
