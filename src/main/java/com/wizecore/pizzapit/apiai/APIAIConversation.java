package com.wizecore.pizzapit.apiai;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.wizecore.pizzapit.ConversationContext;
import com.wizecore.pizzapit.ConversationResponse;
import com.wizecore.pizzapit.Setup;
import com.wizecore.pizzapit.TextConversation;

import ai.api.AIConfiguration;
import ai.api.AIConfiguration.SupportedLanguages;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * Conversation processing using API.AI service.
 * 
 * @author Ruslan
 */
public class APIAIConversation implements TextConversation {
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * Language for conversation.
	 */
	@Setup("APIAI_LANG:ru")
	protected String lang = "ru";
	
	/**
	 * Access token (either client or developer).
	 */
	@Setup("APIAI_ACCESS_TOKEN")
	protected String accessToken = "deadbeef";

	protected AIDataService dataService;
	protected AIConfiguration configuration;
	
	public ConversationContext start() {
		return new APIAIContext();
	}
	
	protected ConversationResponse say0(String phrase) throws IOException {
		if (dataService == null) {
			configuration = new AIConfiguration(accessToken, SupportedLanguages.fromLanguageTag(lang));
			dataService = new AIDataService(configuration);
		}
		
		try {
			AIRequest request = new AIRequest(phrase);
			AIResponse response = dataService.request(request);
			return new APIAIResponse(response.getResult().getFulfillment().getSpeech(), response.getResult().getParameters());
		} catch (Exception e) {
			throw new IOException("Failed say:" + phrase + ", exception: " + e, e);
		}
	}

	@Override
	public ConversationResponse say(ConversationContext ctx, String phrase) throws IOException {
		log.info("Requesting " + phrase);
		ConversationResponse r = say0(phrase);
		String text = prepareResponseText(ctx, r.getPhrase());
		mergeResponse(ctx, r);
		log.info("Got response: " + text + ", " + r.getParameters());
		return new APIAIResponse(text, r.getParameters());
	}

	/**
	 * Merge variables from single response into current context and process any commands available.
	 * The following <i>magic</i> names are recognised:
	 * <ul>
	 * 	<li>j_changelast[-nnn] - Expected value to be in format &lt;var&gt;:&lt;value&gt;. Replaces last value in array designated by &lt;var&gt;. 
	 * </ul>
	 */
	public void mergeResponse(ConversationContext ctx, ConversationResponse r) {
		Map<String, JsonElement> merged = ctx.getParameters();
		
		for (Iterator<String> it = r.getParameters().keySet().iterator(); it.hasNext();) {
			String k = it.next();
			// Actions
			if (k.startsWith("j_")) {
				String act = k.substring(2);
				if (act.indexOf("-") > 0) {
					act = act.substring(0, act.indexOf("-"));
				}
				String arg = r.getParameters().get(k).getAsString();
				if (act.equals("changelast")) {
					String[] args = arg.split("\\:");
					String var = args[0];
					String val = args[1];
					JsonArray a = (JsonArray) merged.get(var);
					String oval = a.get(a.size() - 1).toString();
					a.remove(a.size() - 1);
					a.add(new JsonPrimitive(val));
					log.info("Replaced " + var + " last value " + oval + " with " + val);
				}
				
				// Remove j_
				it.remove();
			} else {
				JsonElement ov = merged.get(k);
				JsonElement v = r.getParameters().get(k);
				if (ov instanceof JsonArray) {
					JsonArray ova = (JsonArray) ov;
					if (v instanceof JsonArray) {
						JsonArray va = (JsonArray) v;
						ova.addAll(va);
					} else {	
						ova.add(v);
					}
				} else {
					merged.put(k, v);
				}
			}
		}
	}

	/**
	 * Preprocess response phrase.
	 * Known macroses:
	 * <ul>
	 * 	<li>[sum:var-name] - Calculates sum in all values in specified variable.
	 * </ul>
	 */
	public String prepareResponseText(ConversationContext ctx, String rtext) {
		Map<String, JsonElement> merged = ctx.getParameters();
		Pattern pat = Pattern.compile("\\[sum:([a-zA-Z0-9_]+)\\]");
		Matcher m = pat.matcher(rtext);
		while (m.find()) {
			String var = m.group(1);
			JsonArray a = (JsonArray) merged.get(var);
			BigDecimal sum = new BigDecimal(0);
			for (int i = 0; i < a.size(); i++) {
				sum = sum.add(new BigDecimal(a.get(i).getAsString()));
			}
			log.info("Calculated sum: " + sum + " for " + var);
			rtext = rtext.substring(0,  m.start()) + sum.toString() + rtext.substring(m.end());
			m = pat.matcher(rtext);
		}
		return rtext;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String token) {
		this.accessToken = token;
	}
}
