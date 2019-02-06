package com.sento.rateableitems.controllers;


import com.sento.rateableitems.exceptions.ErrorDetails;
import com.sento.rateableitems.exceptions.InvalidRateableItemException;
import com.sento.rateableitems.exceptions.RateableItemAlreadyExistsException;
import com.sento.rateableitems.exceptions.RateableItemNotFoundException;
import com.sento.rateableitems.model.RateableItem;
import com.sento.rateableitems.service.RateableItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Optional;

@RestController
public class RateableItemServiceController {

    Logger logger = LoggerFactory.getLogger(RateableItemServiceController.class);

    @Value("${service.external.address}")
    String host;

    @Value("${service.external.port}")
    String port;

    @Value("${service.base.path}")
    String basePath;

    @Autowired
    RateableItemService rateableItemService;

    @RequestMapping(value="/v1/rateableitems",method = RequestMethod.GET)
    public ResponseEntity<Iterable<RateableItem>> getRateableItems(@RequestParam("include-inactive") Optional<Boolean> includeInactive) {

        logger.debug("GET received");

        Iterable<RateableItem> allRateableItems = rateableItemService.getAllRateableItems("Y");

        if (allRateableItems.iterator().hasNext()) {
            return new ResponseEntity<Iterable<RateableItem>> (allRateableItems, HttpStatus.OK);
        }
        else{
            logger.debug("No rateableitems returned for GET /");
            return new ResponseEntity<Iterable<RateableItem>>(HttpStatus.NO_CONTENT);

        }
    }

    @RequestMapping(value="/v1/rateableitems/{rateableItemId}",method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?>  getRateableItemById(@PathVariable("rateableItemId") String rateableItemId) {

        Optional<RateableItem> rateableItem= rateableItemService.getRateableItem(rateableItemId);


        if (rateableItem.isPresent()) {
            return new ResponseEntity<RateableItem> (rateableItem.get(), HttpStatus.OK);
        }
        else {
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "rateableItemId="+rateableItemId, "RateableItem not found with this ID");

            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value="/v1/rateableitems/{rateableItemId}",method = RequestMethod.DELETE)
    @Transactional
    public ResponseEntity<?> deleteRateableItemById(@PathVariable("rateableItemId") String rateableItemId) {

        Optional<RateableItem> rateableItem=rateableItemService.deleteRateableItem(rateableItemId);

        RateableItem deleted = null;
        if (rateableItem.isPresent()) {
            deleted = rateableItem.get();
            logger.debug("RateableItem found to delete for id = " + rateableItemId);
            return new ResponseEntity<RateableItem> (deleted, HttpStatus.NO_CONTENT);

        }
        else {
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "orgId="+rateableItemId, "RateableItem not found with this ID");
            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(value="/v1/rateableitems",method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createRateableItem(@Valid @RequestBody RateableItem newRateableItem)  {

        final String rateableItemId = (newRateableItem.getRateableItemId()!=null?newRateableItem.getRateableItemId():"null ID");

        try{

            RateableItem rateableItem=rateableItemService.saveRateableItem(newRateableItem);
            return new ResponseEntity<RateableItem>(rateableItem, createLocationHeader(rateableItemId), HttpStatus.CREATED);

        }catch(RateableItemAlreadyExistsException ex) {
            logger.error("Attempted to create duplicate rateableItem : " +rateableItemId);
            ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), "A rateable item already exists with this ID");

            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.CONFLICT);

        }
        catch(InvalidRateableItemException ioe) {

            logger.error("Attempt to create invalid rateableItem " +rateableItemId +" - " +ioe.getMessage());
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "rateableItemId="+rateableItemId + " attributes are invalid" , ioe.getMessage());

            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
        }

    }

    @RequestMapping(value="/v1/rateableitems/{rateableItemId}",method = RequestMethod.PUT)
    public ResponseEntity<?> updateRateableItem(@PathVariable("rateableItemId") String rateableItemId, @Valid @RequestBody RateableItem newRateableItem) {

        RateableItem rateableItem;
        logger.info("Update for rateable item in PUT for id "+ newRateableItem.getRateableItemId());

        final String updatedRateableIdFromBody = (newRateableItem.getRateableItemId()!=null?newRateableItem.getRateableItemId():"null ID");

        if (! updatedRateableIdFromBody.equalsIgnoreCase(rateableItemId)) {
            logger.error("Update failed - path/body rateable item id mismatch for "+ rateableItemId);
            ErrorDetails errorDetails = new ErrorDetails(new Date(), rateableItemId, "Update failed - path/body rateable id mismatch");

            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
        }

        try{
            rateableItem=rateableItemService.updateRateableItem(newRateableItem);
        }
        catch(RateableItemNotFoundException ex) {
            logger.error("Attempted to update non-existent rateable item : " +rateableItemId);
            ErrorDetails errorDetails = new ErrorDetails(new Date(), "rateableItemId="+rateableItemId, "No such Rateable item found with this ID");

            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.NOT_FOUND);

        }
        catch(InvalidRateableItemException ioe) {

            logger.error("Attempted to update with invalid org attributes : " +rateableItemId);
            ErrorDetails errorDetails = new ErrorDetails(new Date(), rateableItemId, "RateableItem attributes are invalid");

            return new ResponseEntity<ErrorDetails>(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
        }


        return new ResponseEntity<RateableItem>(rateableItem, createLocationHeader(rateableItem.getRateableItemId()), HttpStatus.NO_CONTENT);


    }

    private HttpHeaders createLocationHeader(String orgId) {

        URI location = null;
        try {
            location = new URI(host+":"+port+basePath+orgId);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setLocation(location);
        return responseHeaders;

    }


}
