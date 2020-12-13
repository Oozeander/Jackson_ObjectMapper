package com.oozeander;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.oozeander.model.Car;

import lombok.Data;

public class App {
	private static final ObjectMapper OBJECT_MAPPER;
	private static final Logger LOGGER;

	static {
		LOGGER = LogManager.getLogger(App.class);
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		// Don't fail (exception) for unknown properties in JSON Mapping -> Java
		OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		// Java -> JSON
		Car first_car = new Car("Grey", "Renault");
		OBJECT_MAPPER.writeValue(new File("src/main/resources/car.json"), first_car);
		OBJECT_MAPPER.writeValue(new File("src/main/resources/cars.json"),
				Arrays.asList(new Car("Black", "BMW"), new Car("Red", "FIAT")));
		LOGGER.info(OBJECT_MAPPER.writeValueAsString(first_car));

		// JSON -> Java
		String first_car_json = "{ \"color\" : \"Black\", \"type\" : \"Fiat\"}";
		LOGGER.info(OBJECT_MAPPER.readValue(first_car_json, Car.class));
		LOGGER.info(OBJECT_MAPPER.readValue(new File("src/main/resources/car.json"), Car.class));

		// JsonNode
		String complex_car_json = "{ \"brand\" : \"Mercedes\", \"doors\" : 5,"
				+ "  \"owners\" : [\"John\", \"Jack\", \"Jill\"]," + "  \"nestedObject\" : { \"field\" : \"value\" } }";
		JsonNode jsonNode = OBJECT_MAPPER.readTree(complex_car_json);

		// JsonNode getInt()
		LOGGER.info(jsonNode.get("doors").asInt(-1));

		// JsonNode getText() in array
		LOGGER.info(jsonNode.get("owners").get(0).asText("No Value"));

		// JsonNode InnerObject
		LOGGER.info(jsonNode.get("nestedObject").get("field").asText("No Value"));

		// Array of JSONs -> List Java
		String json_car_array = "[{ \"color\" : \"Black\", \"type\" : \"BMW\" }, { \"color\" : \"Red\", \"type\" : \"FIAT\" }]";
		List<Car> cars = OBJECT_MAPPER.readValue(json_car_array, new TypeReference<List<Car>>() {
		});
		Car[] car_array = OBJECT_MAPPER.readValue(json_car_array, Car[].class);
		LOGGER.info(car_array);
		LOGGER.info(cars); // Apply for List / Set / Map
		Map<String, Object> cars_map = OBJECT_MAPPER.readValue(first_car_json,
				new TypeReference<Map<String, Object>>() {
				});
		LOGGER.info(cars_map);

		// JsonNode -> Java
		LOGGER.info(OBJECT_MAPPER.treeToValue(OBJECT_MAPPER.readTree(complex_car_json).get("nestedObject"), Obj.class));
	}

	@Data
	static class Obj {
		private String field;
	}
}