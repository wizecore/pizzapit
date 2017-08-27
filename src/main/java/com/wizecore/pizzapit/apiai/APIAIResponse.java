package com.wizecore.pizzapit.apiai;

import java.util.Map;

import com.google.gson.JsonElement;
import com.wizecore.pizzapit.ConversationResponse;

public class APIAIResponse implements ConversationResponse {

	protected String phrase;
	protected Map<String,JsonElement> params;
	
	public APIAIResponse(String phrase, Map<String, JsonElement> params) {
		this.phrase = phrase;
		this.params = params;
	}
	
	@Override
	public Map<String, JsonElement> getParameters() {
		return params;
	}
	
	@Override
	public String getPhrase() {
		return phrase;
	}
}
