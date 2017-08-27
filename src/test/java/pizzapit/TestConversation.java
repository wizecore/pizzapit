package pizzapit;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wizecore.pizzapit.ConversationContext;
import com.wizecore.pizzapit.apiai.APIAIConversation;
import com.wizecore.pizzapit.aws.PollySpeechClient;

public class TestConversation {
	Logger log = LoggerFactory.getLogger(getClass());
	PollySpeechClient requestVoice = new PollySpeechClient();
	PollySpeechClient responseVoice = new PollySpeechClient();
	
	@Before
	public void initVoices() {
		requestVoice.setVoiceIndex(0);
		responseVoice.setVoiceIndex(1);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws IOException {
		APIAIConversation c = new APIAIConversation();
		c.setAccessToken(System.getenv("APIAI_ACCESS_TOKEN"));
		ConversationContext ctx = c.start();
		c.say(ctx, "Привет!");
		c.say(ctx, "Заказать пиццу");
		c.say(ctx, "Баварская пицца");
		c.say(ctx, "33 см");
		c.say(ctx, "тонкое тесто");
		c.say(ctx, "пицца 3 мяса");
		c.say(ctx, "оформить");
		c.say(ctx, "да");
		
		System.out.println(ctx.getParameters());
	}

}
