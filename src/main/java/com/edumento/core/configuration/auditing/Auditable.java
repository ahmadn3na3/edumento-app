package com.edumento.core.configuration.auditing;

import com.edumento.core.constants.notification.EntityAction;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Created by ahmad on 4/13/16. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Auditable {
  EntityAction value();
}
