package com.wizecore.pizzapit;

import java.util.Map;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.google.common.base.Preconditions;

/**
 * Used in conditional components to define which component is available, based on environment.
 * 
 * See {@link SetupProvider#getSetup(String, String)}
 */
public class SetupCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		SetupProvider setup = new SetupProvider();
		context.getBeanFactory().autowireBean(setup);
		Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(Setup.class.getName());
		Preconditions.checkArgument(annotationAttributes != null, "Must have @Setup annotation!");
		String env = (String) annotationAttributes.get("value");
		Preconditions.checkArgument(env != null, "@Setup annotation must have value= for required environment variable!");
		Preconditions.checkNotNull(env);
		String key = env;
		String def = null;
		if (key.indexOf(":") >= 0) {
			def = key.substring(key.indexOf(":") + 1);
			key = key.substring(0, key.indexOf(":"));
		}
		
		if (def == null) { 
			return setup.getSetup(key, null) != null;
		} else {
			return def.equals(setup.getSetup(key, null));
		}
	}
}
