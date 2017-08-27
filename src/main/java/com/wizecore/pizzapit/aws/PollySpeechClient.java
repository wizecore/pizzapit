package com.wizecore.pizzapit.aws;

import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.SynthesizeSpeechRequest;
import com.amazonaws.services.polly.model.SynthesizeSpeechResult;
import com.amazonaws.services.polly.model.Voice;
import com.wizecore.pizzapit.Setup;
import com.wizecore.pizzapit.Text2Speech;

/**
 * Implementation of TTS service using <a href="https://aws.amazon.com/ru/polly/">Amazon Polly</a> service.
 * For this service to work following environment variables can be set:
 * 
 * <ul>
 * 	<li>AWS_ACCESS_KEY_ID - AWS access key
 *  <li>AWS_SECRET_ACCESS_KEY - AWS access token (password)
 *  <li>AWS_DEFAULT_REGION - Default region to use
 *  <li>AWS_POLLY_VOICE_LANG - Voice language code, see <a href="http://docs.aws.amazon.com/polly/latest/dg/API_Voice.html">documentation</a>.
 *  <li>AWS_POLLY_VOICE_INDEX - Voice index in language, if applicable (default first, i.e. 0)
 * </ul>
 * @author Ruslan
 *
 */
public class PollySpeechClient implements Text2Speech {
	
	@Setup("AWS_DEFAULT_REGION:us-east-1")
	private String region = "us-east-1";
	
	@Setup("AWS_POLLY_VOICE_LANG:ru-RU")
	private String voiceLanguage = "ru-RU";
	
	@Setup("AWS_POLLY_VOICE_INDEX:0")
	private int voiceIndex = 0;

	private Voice voice;
	private AmazonPollyClient polly;
	
	public void init() {
		if (polly == null) {
			Region reg = Region.getRegion(Regions.fromName(region));
			
			// create an Amazon Polly client in a specific region
			AmazonPollyClient c = new AmazonPollyClient(new DefaultAWSCredentialsProviderChain(), new ClientConfiguration());
			c.setRegion(reg);
			
			// Create describe voices request.
			DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();
			describeVoicesRequest.setLanguageCode(voiceLanguage);
	
			// Synchronously ask Amazon Polly to describe available TTS voices.
			DescribeVoicesResult describeVoicesResult = c.describeVoices(describeVoicesRequest);
			voice = describeVoicesResult.getVoices().get(voiceIndex);
			polly = c;
		}
	}

	public InputStream synthesize(String text, OutputFormat f) throws IOException {
		if (f == null) {
			f = OutputFormat.MP3;
		}
		
		init();
		com.amazonaws.services.polly.model.OutputFormat fmt = f == OutputFormat.MP3 ? com.amazonaws.services.polly.model.OutputFormat.Mp3 : com.amazonaws.services.polly.model.OutputFormat.Ogg_vorbis;
		SynthesizeSpeechRequest synthReq = new SynthesizeSpeechRequest().withText(text).withVoiceId(voice.getId()).withOutputFormat(fmt);
		SynthesizeSpeechResult synthRes = polly.synthesizeSpeech(synthReq);
		return synthRes.getAudioStream();
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getVoiceLanguage() {
		return voiceLanguage;
	}

	public void setVoiceLanguage(String voiceLanguage) {
		this.voiceLanguage = voiceLanguage;
	}

	public int getVoiceIndex() {
		return voiceIndex;
	}

	public void setVoiceIndex(int voiceIndex) {
		this.voiceIndex = voiceIndex;
	}
}
