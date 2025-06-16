package com.example.eventproducer.controller;

import com.example.eventproducer.dto.MovieEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class EventProducerController {
	private final KafkaTemplate<String, String> kafkaTemplate;

	private final ObjectMapper objectMapper;

	public EventProducerController(KafkaTemplate<String, String> kafkaTemplate, final ObjectMapper objectMapper) {
		this.kafkaTemplate = kafkaTemplate;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/movie")
	public MovieEntity sendView(@RequestBody MovieEntity movie) throws JsonProcessingException {
		this.kafkaTemplate.send("movie-group", UUID.randomUUID().toString(),
				this.objectMapper.writeValueAsString(movie));
		return movie;
	}
}
