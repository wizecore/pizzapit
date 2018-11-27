package pizzapit;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.wizecore.metrics.PersistenceUtil;
import com.wizecore.metrics.PersistentMetricRegistry;
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
	
	MetricRegistry metrics = new PersistentMetricRegistry();
	private Timer mp3timer;
	private Timer synthtimer;
	private Timer saytimer;
	
	@Before
	public void initVoices() {
		requestVoice.setVoiceIndex(1);
		responseVoice.setVoiceIndex(0);
		PersistenceUtil.setMetricPrefix("pizzapit");
		PersistenceUtil.setRedisAddr("pub-redis-12428.eu-central-1-1.1.ec2.redislabs.com:12428");
		PersistenceUtil.setRedisPassword("pae5Pu1aimaf");
		mp3timer = metrics.timer("mp3timer");
		synthtimer = metrics.timer("polly.synthesize");
		saytimer = metrics.timer("apiai.say");
	}

	@After
	public void tearDown() throws Exception {
	}

	protected void saysay(ConversationContext ctx, TextConversation c, String request) throws IOException {
		Context cc = null;
		
		cc = synthtimer.time();
		InputStream is = requestVoice.synthesize(request, Text2Speech.OutputFormat.MP3);
		cc.stop();
		
		cc = mp3timer.time();
		TestPolly.playMp3(is);
		cc.stop();
		
		ConversationResponse r = c.say(ctx, request);
		
		cc = synthtimer.time();
		InputStream is2 = responseVoice.synthesize(r.getPhrase(), Text2Speech.OutputFormat.MP3);
		cc.stop();
		
		cc = mp3timer.time();
		TestPolly.playMp3(is2);
		cc.stop();
	}
	
	@Test
	public void test() throws IOException {
		APIAIConversation c = new APIAIConversation() {
			protected ConversationResponse say0(String phrase) throws IOException {
				Context c = saytimer.time();
				ConversationResponse r = super.say0(phrase);
				c.stop();
				return r;
			};
		};
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
