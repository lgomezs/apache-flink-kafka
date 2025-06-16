package com.example.flinkjob;

import com.example.flinkjob.dto.MovieEventDeserializer;
import com.example.flinkjob.entity.MovieEntity;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.core.datastream.sink.JdbcSink;
import org.apache.flink.connector.jdbc.datasource.statements.JdbcQueryStatement;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serial;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FlinkJobApp {

	public static void main(String[] args) throws Exception {

		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// 1. Kafka Source
		final KafkaSource<MovieEntity> kafkaSource = KafkaSource.<MovieEntity>builder()
				.setBootstrapServers("kafka:9092").setTopics("movie-group").setGroupId("movie-group")
				.setStartingOffsets(OffsetsInitializer.earliest())
				.setValueOnlyDeserializer(new MovieEventDeserializer()).build();

		final JdbcExecutionOptions executionOptions = JdbcExecutionOptions.builder().withBatchSize(1000)
				.withBatchIntervalMs(5000) // <-- flush cada 5 segundos
				.withMaxRetries(3).build();

		final JdbcConnectionOptions connectionOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
				.withUrl("jdbc:postgresql://postgres:5432/movies").withDriverName("org.postgresql.Driver")
				.withUsername("user").withPassword("pass").build();

		final JdbcSink<MovieEntity> jdbcSink = JdbcSink.<MovieEntity>builder()
				.withQueryStatement(new JdbcQueryStatement<>() {
					@Serial
					private static final long serialVersionUID = -2419343508839456546L;

					@Override
					public String query() {
						return "INSERT INTO public.movies (title, year, genre) VALUES (?, ?, ?)";
					}

					@Override
					public void statement(PreparedStatement preparedStatement, MovieEntity movieEntity)
							throws SQLException {
						preparedStatement.setString(1, movieEntity.getTitle());
						preparedStatement.setInt(2, movieEntity.getYear());
						preparedStatement.setString(3, movieEntity.getGenre());
					}
				}).withExecutionOptions(executionOptions).buildAtLeastOnce(connectionOptions);

		env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "Kafka Source").map(movie -> {
			System.out.println(">>> Película recibida: " + movie);
			return movie;
		}).sinkTo(jdbcSink);

		env.execute("Kafka to PostgreSQL with JdbcSink MIGUEL");
	}

}
