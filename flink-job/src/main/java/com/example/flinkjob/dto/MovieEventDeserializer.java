package com.example.flinkjob.dto;

import com.example.flinkjob.entity.MovieEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MovieEventDeserializer implements DeserializationSchema<MovieEntity> {
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public MovieEntity deserialize(byte[] message) throws IOException {
		final String json = new String(message, StandardCharsets.UTF_8);
		System.out.println("🔍 Deserializando mensaje JSON: " + json);
		return this.objectMapper.readValue(json, MovieEntity.class);
	}

	@Override
	public boolean isEndOfStream(MovieEntity nextElement) {
		return false;
	}

	@Override
	public TypeInformation<MovieEntity> getProducedType() {
		return TypeInformation.of(MovieEntity.class);
	}
}
