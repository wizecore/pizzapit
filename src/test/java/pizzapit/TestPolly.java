package pizzapit;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Test;

import com.wizecore.pizzapit.Text2Speech;
import com.wizecore.pizzapit.aws.PollySpeechClient;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

public class TestPolly {

	@After
	public void tearDown() throws Exception {
	}

	PollySpeechClient speech = new PollySpeechClient();
	
	public static void playMp3(InputStream speechStream) {
		try {
			AdvancedPlayer player = new AdvancedPlayer(speechStream,
					javazoom.jl.player.FactoryRegistry.systemRegistry().createAudioDevice());
	
			player.setPlayBackListener(new PlaybackListener() {
				@Override
				public void playbackStarted(PlaybackEvent evt) {
					System.out.println("Playback started");
				}
				
				@Override
				public void playbackFinished(PlaybackEvent evt) {
					System.out.println("Playback finished");
				}
			});
			
			
			// play it!
			player.play();
			speechStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Speech failed: " + e, e);
		}
	}
	
	@Test
	public void test() throws JavaLayerException, IOException {
		InputStream is = speech.synthesize("������! ���� ����������� Amazon Polly!", Text2Speech.OutputFormat.MP3);
		playMp3(is);
	}
}
