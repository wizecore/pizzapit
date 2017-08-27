package pizzapit;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wizecore.pizzapit.ConversationContext;
import com.wizecore.pizzapit.ConversationResponse;
import com.wizecore.pizzapit.Text2Speech;
import com.wizecore.pizzapit.TextConversation;
import com.wizecore.pizzapit.apiai.APIAIConversation;
import com.wizecore.pizzapit.aws.PollySpeechClient;

public class TestConversation {
	Logger log = LoggerFactory.getLogger(getClass());
	PollySpeechClient requestVoice = new PollySpeechClient();
	PollySpeechClient responseVoice = new PollySpeechClient();
	
	@Before
	public void initVoices() {
		requestVoice.setVoiceIndex(1);
		responseVoice.setVoiceIndex(0);
	}

	@After
	public void tearDown() throws Exception {
	}

	protected void saysay(ConversationContext ctx, TextConversation c, String request) throws IOException {
		InputStream is = requestVoice.synthesize(request, Text2Speech.OutputFormat.MP3);
		TestPolly.playMp3(is);
		
		ConversationResponse r = c.say(ctx, request);
		
		InputStream is2 = responseVoice.synthesize(r.getPhrase(), Text2Speech.OutputFormat.MP3);
		TestPolly.playMp3(is2);
	}
	
	@Test
	public void test() throws IOException {
		APIAIConversation c = new APIAIConversation();
		c.setAccessToken(System.getenv("APIAI_ACCESS_TOKEN"));
		ConversationContext ctx = c.start();
		saysay(ctx, c, "Привет!");
		saysay(ctx, c, "Заказать пиццу");
		saysay(ctx, c, "Баварская пицца");
		saysay(ctx, c, "33 см");
		saysay(ctx, c, "тонкое тесто");
		saysay(ctx, c, "пицца 3 мяса");
		saysay(ctx, c, "оформить");
		saysay(ctx, c, "да");
		
		System.out.println(ctx.getParameters());
	}

}
