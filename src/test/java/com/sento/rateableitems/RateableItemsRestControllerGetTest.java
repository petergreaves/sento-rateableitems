package com.sento.rateableitems;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sento.rateableitems.exceptions.ErrorDetails;
import com.sento.rateableitems.model.RateableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RateableItemsRestControllerGetTest extends AbstractRateableItemTestBase {

	@Autowired
	private JdbcTemplate template;

	private RateableItem testRateableItem;

	private final String uriGetBase = "/v1/rateableitems";
	private final String rateableItemToGet="RI09499";

	@Value("${test.get.insert.0}")
    String insert0;
	@Value("${test.get.insert.1}")
	String insert1;
	@Value("${test.get.insert.2}")
	String insert2;
	@Value("${test.get.insert.3}")
	String insert3;

	private final String[] inserts = new String[4];

    @Value("${test.get.delete.0}")
    String delete0;


    @Override
	@Before
	public void setUp() {
		super.setUp();


		inserts[0] = insert0;
		inserts[1] = insert1;
		inserts[2] = insert2;
		inserts[3] = insert3;


		Stream.of(inserts).forEach(template::execute);
		//template.execute(insert0);

		// create an object for comparison in single get
		testRateableItem = new RateableItem();
		testRateableItem.setRateableItemId("RI09499");
		testRateableItem.setOwningOrgId("BM022");

	}

	@After
	public void tearDown() {

		template.execute(delete0);

    }

	@Test
	public void getAllActiveRateableItemsWithQSParam() throws Exception {

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uriGetBase+"?include-inactive=false").accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		RateableItem[] rateableItems = super.mapFromJson(content, RateableItem[].class);
		int status = mvcResult.getResponse().getStatus();

		assertTrue(status==200 & (rateableItems.length == 3));
	}


	@Test
	public void getAllActiveRateableItems() throws Exception {

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uriGetBase).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		RateableItem[] rateableItems = super.mapFromJson(content, RateableItem[].class);
		int status = mvcResult.getResponse().getStatus();

		assertTrue(status==200 & (rateableItems.length == 3));
	}
	@Test
	public void getAllRateableItems() throws Exception { // only returns active by default

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uriGetBase).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		RateableItem[] rateableItems = super.mapFromJson(content, RateableItem[].class);
		int status = mvcResult.getResponse().getStatus();

		assertTrue(status==200 & (rateableItems.length == 3));
	}

	@Test
	public void getSingleRateableItem() throws Exception {
		String target=uriGetBase+"/"+rateableItemToGet;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(target).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();
		RateableItem rateableItem = super.mapFromJson(content, RateableItem.class);

		assertTrue(200 == status & rateableItem.equals(testRateableItem));
	}

	//@Test
	public void inactiveRIsAreReturnedOnlyIfRequested() throws Exception{

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uriGetBase).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		String content = mvcResult.getResponse().getContentAsString();
		RateableItem[] rateableItems = super.mapFromJson(content, RateableItem[].class);
		int status = mvcResult.getResponse().getStatus();

		assertTrue(status==200 & rateableItems.length == 1 );//& rateableItems[0].);

	}


	@Test
	public void receive404IfRateableItemDoesNotExist() throws Exception {

		String target=uriGetBase+"/"+"nosuchitemID";
		ErrorDetails expected = new ErrorDetails();
		expected.setMessage("rateableItemId=nosuchitemID");
		expected.setDetail("RateableItem not found with this ID");

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(target).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		ErrorDetails error = super.mapFromJson(mvcResult.getResponse().getContentAsString(), ErrorDetails.class);

		assertTrue(404 == status & expected.getDetail().equals(error.getDetail())
				& expected.getMessage().equals(error.getMessage()));
	}

	@Test
	public void ensureDatetimesAreISO8601() throws Exception{

		String target=uriGetBase+"/"+rateableItemToGet;
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(target).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		String content = mvcResult.getResponse().getContentAsString();

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode = mapper.readTree(content);
		JsonNode startDateNode = jsonNode.get("startDate");
		String startDateAsText = startDateNode.asText();
		JsonNode endDateNode = jsonNode.get("endDate");
		String endDateAsText = endDateNode.asText();

		boolean isValid = true;
		LocalDate dt;
		try {
			dt = LocalDate.parse(startDateAsText, DateTimeFormatter.ISO_DATE_TIME);
			dt=LocalDate.parse(endDateAsText, DateTimeFormatter.ISO_DATE_TIME);
		}
		catch(DateTimeParseException dtpe){

			isValid=false;

		}

		assertTrue(isValid);

	}


}
