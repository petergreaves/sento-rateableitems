package com.sento.rateableitems;

import com.sento.rateableitems.model.Organisation;
import com.sento.rateableitems.model.RateableItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RateableItemsRestControllerUpdateTest extends AbstractRateableItemTestBase {

    @Autowired
    private JdbcTemplate template;

    @Value("${test.update.insert.0}")
    String insert0;
    @Value("${test.update.delete.0}")
    String delete0;

    private RateableItem riUpdated;

    @Override
    @Before
//	@Sql(scripts = "classpath:beforeGetTestRun.sql")
    public void setUp() {
        super.setUp();

        //INSERT INTO rateableitems (rateable_item_id, owning_org_id, description, start_date, end_date, is_active)
        // VALUES ('RI09499','BM022','The product page', now(), DATE_ADD(now(), INTERVAL 10 DAY),'Y');

        Date now = new Date();

        SimpleDateFormat format = new SimpleDateFormat();

        riUpdated = new RateableItem();
        riUpdated.setRateableItemId("RI09499");
        riUpdated.setOwningOrgId("BM022");
        riUpdated.setStartDate(new Date());
        riUpdated.setEndDate(new Date());
        riUpdated.setDescription("A great review page updated");
        riUpdated.setActive(false);

        template.execute(insert0);

    }

    @After
//	@Sql(scripts = "classpath:afterGetTestRun.sql")
    public void tearDown() {

        riUpdated = null;

        template.execute(delete0);

    }

    @Test
    public void updateOrganisation() throws Exception {

        String uri = "/v1/rateableitems/RI09499";

        String inputJson = super.mapToJson(riUpdated);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        // get it back

        String sql = "SELECT * FROM RATEABLEITEMS WHERE RATEABLE_ITEM_ID = ?";

        RateableItem updatedRI = (RateableItem) template.queryForObject(sql, new Object[] { "RI09499" },
                new RowMapper() {

                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        RateableItem ri = new RateableItem();
                        ri.setOwningOrgId(rs.getString("OWNING_ORG_ID"));
                        ri.setActive((rs.getString("IS_ACTIVE")=="Y"?true:false));
                        ri.setEndDate(rs.getDate("end_date"));
                        ri.setStartDate(rs.getDate("start_date"));
                        ri.setDescription(rs.getString("description"));
                        ri.setRateableItemId(rs.getString("rateable_item_id"));
                        ri.setId(rs.getInt("id"));
                        return ri;
                    }

                });

        boolean match = (riUpdated.getOwningOrgId().equals(updatedRI.getOwningOrgId())
                & riUpdated.getDescription().equals(updatedRI.getDescription())
                & riUpdated.getStartDate().equals(updatedRI.getStartDate())
        //        & riUpdated.getEndDate().equals(updatedRI.getEndDate())
                & riUpdated.getRateableItemId().equals(updatedRI.getRateableItemId())
         //       & riUpdated.isActive() == updatedRI.isActive()
                & 0 != updatedRI.getId());
        assertTrue(match & status == 204);
    }

    //@Test
    //TODO
    public void updateOrganisationFromJson() throws Exception {

        String uri = "/v1/rateableitems/RI0930";
        String inputJson = "{\"rateableitemId\":\"RI0930\",\"owningOrgId\":\"BM022\",\"desc\":\"A description\",\"isActive\":\"Y\",\"startDate\":\"2019-01-31T15:25:47.000+0000\"}";

        //[{"rateableItemId":"RI09499","owningOrgId":"BM022","description":"The product page","startDate":"2019-01-31T15:25:47.000+0000","endDate":"2019-02-10T15:25:47.000+0000","active":true},{"rateableItemId":"BB09628","owningOrgId":"AB022","description":"The review page","startDate":"2019-01-31T15:25:47.000+0000","endDate":"2019-02-10T15:25:47.000+0000","active":true},{"rateableItemId":"R9300","owningOrgId":"BM022","description":"The detail page",
        // "startDate":"2019-01-31T15:25:47.000+0000","endDate":"2019-02-10T15:25:47.000+0000","active":true}]
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();
        // get it back

        String sql = "SELECT * FROM RATEABLEITEMS WHERE RATEABLE_ITEM_ID = ?";

        RateableItem updatedRI = (RateableItem) template.queryForObject(sql, new Object[] { "RI09499" },
                new RowMapper() {

                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        RateableItem ri = new RateableItem();
                        ri.setOwningOrgId(rs.getString("OWNING_ORG_ID"));
                        ri.setActive((rs.getString("IS_ACTIVE")=="Y"?true:false));
                        ri.setEndDate(rs.getDate("end_date"));
                        ri.setStartDate(rs.getDate("start_date"));
                        ri.setDescription(rs.getString("description"));
                        ri.setRateableItemId(rs.getString("rateable_item_id"));

                        return ri;
                    }

                });

        boolean match = ("BM022".equals(updatedRI.getOwningOrgId())
                & "".equals(updatedRI.getStartDate())
                & true==updatedRI.isActive()
                & "RI09499".equals(updatedRI.getRateableItemId())
                & "A description".equals(updatedRI.getDescription())
                & 0 != updatedRI.getId());

        assertTrue(match & status == 204);
    }

    @Test
    public void updateOrgReturns404IfNotFound() throws Exception {
        String uri = "/v1/organisations/BM099";

        riUpdated.setRateableItemId("BM099");

        String inputJson = super.mapToJson(riUpdated);
        // System.out.println(inputJson);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(404, status);
    }

    @Test
    public void updateOrgReturns405IfOrgIdMismatch() throws Exception {
        String uri = "/v1/organisations/BM099";

        String inputJson = super.mapToJson(riUpdated);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(405, status);
    }

    @Test
    public void failUpdateIfStartDateNull() throws Exception{

        riUpdated.setStartDate(null);

        String uri = "/v1/organisations/RI09499";

        String inputJson = super.mapToJson(riUpdated);
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(400, status);

    }

    @Test
    public void get400OnUpdateIfOwningOrgNull() throws Exception{

        riUpdated.setOwningOrgId(null);

        String uri = "/v1/organisations/RI09499";

        String inputJson = super.mapToJson(riUpdated);
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.put(uri).contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson))
                .andReturn();

        int status = mvcResult.getResponse().getStatus();

        assertEquals(400, status);

    }

}

