package com.sento.rateableitems;

import com.sento.rateableitems.exceptions.ErrorDetails;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RateableItemsServiceRestControllerDeleteTest extends AbstractRateableItemTestBase {
	
	
	@Autowired
	private JdbcTemplate template;
	
	@Value("${test.delete.insert.0}")
    String insert0;
	@Value("${test.delete.delete.0}")
    String delete0;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		
		template.execute(insert0);
	}
	
	@After
	public void tearDown() {
		super.setUp();
		
		template.execute(delete0);
	}


	@Test
	public void deleteRateableItem() throws Exception {
		String uri = "/v1/rateableitems/RI09499";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();

		int status = mvcResult.getResponse().getStatus();
		assertEquals(204, status);
	}
	
	@Test
	public void deleteRateableItemReturns404IfNotFound() throws Exception {
		
		ErrorDetails expected = new ErrorDetails();
		expected.setMessage("rateableItemId=RI019499");
		expected.setDetail("Rateable Item not found with this ID");
		
		String uri = "/v1/rateableitems/RI019499";
		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.delete(uri).accept(MediaType.APPLICATION_JSON_VALUE))
				.andReturn();
		
		String content = mvcResult.getResponse().getContentAsString();
		ErrorDetails error = super.mapFromJson(content, ErrorDetails.class);
		int status = mvcResult.getResponse().getStatus();

		boolean b1=expected.getDetail().equals(error.getDetail());
		boolean b2=expected.getMessage().equals(error.getMessage());
			
		assertTrue(404==status & b1 & b2);
	}
	
	
	
}
