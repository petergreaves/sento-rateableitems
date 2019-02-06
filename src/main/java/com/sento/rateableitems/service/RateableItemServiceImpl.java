package com.sento.rateableitems.service;

import com.sento.rateableitems.exceptions.InvalidRateableItemException;
import com.sento.rateableitems.exceptions.RateableItemAlreadyExistsException;
import com.sento.rateableitems.exceptions.RateableItemNotFoundException;
import com.sento.rateableitems.model.Organisation;
import com.sento.rateableitems.model.RateableItem;
import com.sento.rateableitems.repository.RateableItemsRepository;
import com.sento.rateableitems.util.OrganisationServiceRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service("rateableItemService")
public class RateableItemServiceImpl implements RateableItemService {

    @Autowired
    private OrganisationServiceRestTemplate orgServiceRestTemplate;


    @Autowired
    private RateableItemsRepository rateableItemsRepository;

    @Override
    public Iterable<RateableItem> getAllRateableItems(String activeStatus) {

        Iterable<RateableItem> rateableItems = rateableItemsRepository.findAll(activeStatus);


        return rateableItems;
    }

    @Override
    public Optional<RateableItem> getRateableItem(String rateableItemId) {
        Optional<RateableItem> ri = rateableItemsRepository.findByRateableItemId(rateableItemId);

        return ri;
    }

    @Override
    public RateableItem saveRateableItem(RateableItem rateableItem) throws RateableItemAlreadyExistsException, InvalidRateableItemException {

        // let's just check the dates

        if (!datesAreValid(rateableItem.getStartDate(), rateableItem.getEndDate())){

            throw new InvalidRateableItemException("Start date must be before end date.");
        }

        boolean okToCreate = true;
        RateableItem createdRateableItem = null;

        String id = rateableItem.getRateableItemId();


        // first does this already exist?
        Optional<RateableItem> alreadyExistsOpt = rateableItemsRepository.findByRateableItemId(id);

        if (alreadyExistsOpt.isPresent()){

            throw new RateableItemAlreadyExistsException("rateableItemId="+id);
        }

        try{
            Organisation org = getOrganisationFromOrgService(rateableItem.getOwningOrgId());
        }
        catch(InvalidRateableItemException ie){

            throw ie;
        }



        if (okToCreate){

            createdRateableItem = rateableItemsRepository.save(rateableItem);
        }

        return createdRateableItem;
    }

    @Override
    public RateableItem updateRateableItem(RateableItem rateableItem) {

        // let's just check the dates

        if (!datesAreValid(rateableItem.getStartDate(), rateableItem.getEndDate())){

            throw new InvalidRateableItemException("Start date must be before end date.");
        }


        // what org id is being updated?
        String id = rateableItem.getRateableItemId();

        RateableItem riToBeUpdated;
        RateableItem updatedRI;

        Optional<RateableItem> testForRI = rateableItemsRepository.findByRateableItemId(id);

        // if it exists, update the ID to match the existing internal ID
        // and then save it. otherwise no such element

        try {
            riToBeUpdated = testForRI.get();
            rateableItem.setId(riToBeUpdated.getId());
            updatedRI=rateableItemsRepository.save(rateableItem);

        } catch (NoSuchElementException nse) // no existing org, so an error on an update
        {
            throw new RateableItemNotFoundException("Rateable item doesn't exist to update.");

        }

        return updatedRI;

    }

    @Override
    public Optional<RateableItem> deleteRateableItem(String rateableItemId) {
        Optional<RateableItem> opt = rateableItemsRepository.findByRateableItemId(rateableItemId);

        if (opt.isPresent()) {
            rateableItemsRepository.deleteByRateableItemId(rateableItemId);
        }
        else {

            throw new RateableItemNotFoundException("rateableItemId=" + rateableItemId);

        }
        return opt;
    }


    private Organisation getOrganisationFromOrgService(String orgID) throws InvalidRateableItemException{
        Organisation org = null;

        try{

             org = orgServiceRestTemplate.getOrganisation(orgID);
        }
        catch(InvalidRateableItemException e) {

            throw e;
        }

        return org;
    }

    private boolean datesAreValid(Date startDate, Date endDate){

        //if there is no end date, we are ok with that

        boolean result = true;

        if (null==endDate && null!=startDate){

            return result;
        }

        // of the end date is before the start date, that is an error

        if (startDate.compareTo(endDate) >0){

           result = false;

        }

            return result;


    }
}
