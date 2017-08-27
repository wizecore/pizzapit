package com.wizecore.pizzapit;

import java.io.IOException;
import java.io.InputStream;

/**
 * Text2speech service provider.
 * Please note that there must be heavy caching implemented on top of that.
 * The common for these services are the following:
 * <ul>
 * 	<li>High latency
 *  <li>TTS calls are expensive
 * </ul>
 * @author Ruslan
 */
public interface Text2Speech {
	
	/**
	 * Supported formats.
	 */
	enum OutputFormat {
		/**
		 * MP3 format.
		 */
		MP3,
		/**
		 * Ogg-vorbis format.
		 */
		OGG
	}
	
	/**
	 * Produce MP3 with specified phrase.
	 * 
	 * @param phrase Some sentence.
	 * @return MP3 stream (may be network bound, close afterwards)
	 */
	InputStream synthesize(String phrase, OutputFormat fmt) throws IOException;
}
