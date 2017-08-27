package com.wizecore.pizzapit;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

/**
 * Generic configuration framework.
 * Handles @Setup annotation for different types.
 */
@Component
public class SetupProvider {
	Logger log = Logger.getLogger(getClass().getName());
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public String createSetupString(InjectionPoint p) {
		Setup s = p.getField().getAnnotation(Setup.class);
		Preconditions.checkNotNull(s, "Must have @Setup annotation");
		Preconditions.checkNotNull(s.value(), "Must have env var name");
		String key = s.value();
		String def = null;
		if (key.indexOf(":") >= 0) {
			def = key.substring(key.indexOf(":") + 1);
			key = key.substring(0, key.indexOf(":"));
		}
		return getSetup(key, def);
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Integer createSetupInteger(InjectionPoint p) {
		return Integer.parseInt(createSetupString(p));
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Long createSetupLong(InjectionPoint p) {
		return Long.parseLong(createSetupString(p));
	}
	
	@Autowired(required = false)
	ServletContext servletContext;
	
	public int getSetupInt(String key, int def) {
		return Integer.parseInt(getSetup(key, String.valueOf(def)));
	}
	
	public long getSetupLong(String key, long def) {
		return Long.parseLong(getSetup(key, String.valueOf(def)));
	}
	
	/**
	 * Searching for key -> uppercase -> s/./_/ -> in environment, in system properties.
	 * Searching for key in servlet context -> init parameters;
	 */
	public String getSetup(String key, String def) {
    	String vkey = key.toUpperCase().replace(".", "_");
    	String s = System.getenv(vkey);
    	
    	if (s == null) {
    		s = System.getProperty(vkey);
    	}
    	
    	if (s == null && servletContext != null) {
    		vkey = key.toLowerCase().replaceAll("_", ".");
    		s = servletContext.getInitParameter(key);
    	}
    	
    	if (s != null) {
    		s = s.trim();
    	}
    	
    	s = s != null ? s : def;
    	if (log.isLoggable(Level.FINE)) {
    		log.fine("Setup " + key + " = " + s);
    	}
    	return s;
    }
}
