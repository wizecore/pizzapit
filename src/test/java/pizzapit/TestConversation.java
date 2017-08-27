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
		c.say(ctx, "������!");
		c.say(ctx, "�������� �����");
		c.say(ctx, "��������� �����");
		c.say(ctx, "33 ��");
		c.say(ctx, "������ �����");
		c.say(ctx, "����� 3 ����");
		c.say(ctx, "��������");
		c.say(ctx, "��");
		
		System.out.println(ctx.getParameters());
	}

}
