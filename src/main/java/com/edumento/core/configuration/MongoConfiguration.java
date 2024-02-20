package com.edumento.core.configuration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.convert.CustomConversions.StoreConversions;

import com.edumento.core.util.JSR310DateConverters;

/** Created by ahmad on 3/8/17. */
@Configuration
public class MongoConfiguration {

	@Bean(name = "customConversionsMongo")
	public CustomConversions customConversions() {
		List<Converter<?, ?>> converters = new ArrayList<>();
		converters.add(JSR310DateConverters.DateToZonedDateTimeConverter.INSTANCE);
		converters.add(JSR310DateConverters.ZonedDateTimeToDateConverter.INSTANCE);
		converters.add(JSR310DateConverters.DateToLocalDateConverter.INSTANCE);
		converters.add(JSR310DateConverters.LocalDateToDateConverter.INSTANCE);
		converters.add(JSR310DateConverters.DateToLocalDateTimeConverter.INSTANCE);
		converters.add(JSR310DateConverters.LocalDateTimeToDateConverter.INSTANCE);
		return new CustomConversions(StoreConversions.NONE, converters);
	}
}
