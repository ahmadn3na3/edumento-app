package com.edumento.core.util;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public final class JSR310DateTimeSerializer extends JsonSerializer<TemporalAccessor> {

  public static final JSR310DateTimeSerializer INSTANCE = new JSR310DateTimeSerializer();
  private static final DateTimeFormatter ISOFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.of("Z"));

  private JSR310DateTimeSerializer() {
  }

  @Override
  public void serialize(
      TemporalAccessor value, JsonGenerator generator, SerializerProvider serializerProvider)
      throws IOException {
    generator.writeString(ISOFormatter.format(value));
  }
}
