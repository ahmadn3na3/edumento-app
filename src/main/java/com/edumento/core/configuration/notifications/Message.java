package com.edumento.core.configuration.notifications;

import com.edumento.core.constants.Services;
import com.edumento.core.constants.notification.EntityAction;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Created by ahmad on 5/22/17. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Message {
  EntityAction entityAction();

  Services[] services();

  int indexOfId() default 0;

  int indexOfModel() default 1;

  boolean withModel() default false;
}
