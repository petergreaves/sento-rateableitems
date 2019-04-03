package com.sento.rateableitems;

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

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

public class RateableItemsRestControllerCreateTest extends AbstractRateableItemTestBase {

	@Autowired
	private JdbcTemplate template;

	private RateableItem review;

	private final String uriPostGetAll = "/v1/rateableitems";
	private final String uriGetOneDeletePut = "/v1/rateableitems/RI0390";


	// for the duplicate check 409

	@Value("${test.create.delete.0}")
    String delete0;


    @Value("${test.create.delete.1}")
    String delete1;


    @Value("${test.create.insert.0}")
    String insert0;


    @Override
	@Before
	public void setUp() {
		super.setUp();

		review = new RateableItem();
		review.setRateableItemId("RI0390");
		review.setOwningOrgId("BM022");
		review.setDescription("The page for this great product!");

		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.YEAR, 1); // to get previous year add -1
		review.setStartDate(new Date());
		review.setEndDate(cal.getTime());

        template.execute(insert0);


    }

	@After
	public void tearDown() {

		review = null;

		template.execute(delete0);
        template.execute(delete1);

    }


	@Test
	public void createRateableItem() throws Exception {


		String inputJson = super.mapToJson(review);
		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		String location = mvcResult.getResponse().getHeader("Location");
		assertTrue(201 == status && "http://services.sento.com:7070/v1/rateableitems/RI0390".equals(location));
	}

	@Test
	public void createRateableItemFromJson() throws Exception {

String inputJson = "{\"rateableItemId\":\"RI0390\",\"owningOrgId\":\"BM022\",\"description\":\"The page for this great product!\",\"startDate\":1549206340213,\"endDate\":1580742340213,\"active\":false}";


		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		String location = mvcResult.getResponse().getHeader("Location");
		RateableItem created = super.mapFromJson(mvcResult.getResponse().getContentAsString(), RateableItem.class);
		assertTrue(201 == status
				&& "http://services.sento.com:7070/v1/rateableitems/RI0390".equals(location) & created.equals(review));
	}

	@Test
	public void receive409IfRateableItemAlreadyExistsOnPost() throws Exception {


		ErrorDetails expected = new ErrorDetails();
		expected.setMessage("rateableItemId=" + review.getRateableItemId());
		expected.setDetail("A rateable item already exists with this ID");

		String inputJson = super.mapToJson(review);
		mvc.perform(MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();
		// try to add the same org id
		MvcResult mvcResult2 = mvc.perform(
				MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();	

		int status = mvcResult2.getResponse().getStatus();
		ErrorDetails error = super.mapFromJson(mvcResult2.getResponse().getContentAsString(), ErrorDetails.class);

		assertTrue(409 == status & expected.getDetail().equals(error.getDetail())
				& expected.getMessage().equals(error.getMessage()));
	}

	//@Test
	//TODO
	public void putRIReturns405IfIdMismatch() throws Exception {


		String inputJson = super.mapToJson(review);

		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();

		assertEquals(405, status);
	}

	@Test
		public void receive405IfOrgIdNotKnownToOrgServiceOnPost() throws Exception{

		// the org ID must be known to the org service, or
        ErrorDetails expected = new ErrorDetails();
        expected.setMessage("rateableItemId=" + review.getRateableItemId() +" attributes are invalid");
        expected.setDetail("Org id in rateable item was not found.");
        review.setOwningOrgId("UNKNOWN");

        String inputJson = super.mapToJson(review);
        MvcResult mvcResult=mvc.perform(MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        ErrorDetails error = super.mapFromJson(mvcResult.getResponse().getContentAsString(), ErrorDetails.class);

        int status = mvcResult.getResponse().getStatus();

        assertTrue(405 == status & expected.getDetail().equals(error.getDetail())
                & expected.getMessage().equals(error.getMessage()));

	}

	@Test
	public void acceptValidISO8601Dates() throws Exception{

    	String sd = "\"2019-02-03T09:00:00Z\"";
    	String ed = "\"2020-02-02T08:59:59Z\"";

		String inputJson = "{\"rateableItemId\":\"RI0390\",\"owningOrgId\":\"BM022\",\"description\":\"The page for this great product!\",\"startDate\":" + sd + ",\"endDate\":"+ ed +",\"active\":false}";

		// fix the startDate to ISO8601

		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		String location = mvcResult.getResponse().getHeader("Location");
		assertTrue(201 == status && "http://services.sento.com:7070/v1/rateableitems/RI0390".equals(location));


	}
	@Test
	public void failOnImpossibleISO8601StartDate() throws Exception{

    	//an invalid date
		String sd = "\"2019-92-03T09:00:00Z\"";

		String inputJson = "{\"rateableItemId\":\"RI0390\",\"owningOrgId\":\"BM022\",\"description\":\"The page for this great product!\",\"startDate\":" + sd + ",\"active\":false}";

		MvcResult mvcResult = mvc.perform(
				MockMvcRequestBuilders.post(uriPostGetAll).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
				.andReturn();

		ErrorDetails error = super.mapFromJson(mvcResult.getResponse().getContentAsString(), ErrorDetails.class);

		int status = mvcResult.getResponse().getStatus();

		assertTrue(status==404);

	}

}
