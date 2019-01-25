package com.sento.rateableitems.service;

import com.sento.rateableitems.exceptions.InvalidRateableItemException;
import com.sento.rateableitems.exceptions.RateableItemAlreadyExistsException;
import com.sento.rateableitems.model.Organisation;
import com.sento.rateableitems.model.RateableItem;
import com.sento.rateableitems.repository.RateableItemsRepository;
import com.sento.rateableitems.util.OrganisationServiceRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    public RateableItem updateRateableItem(RateableItem rateableitem) {
        return null;
    }

    @Override
    public Optional<RateableItem> deleteRateableItem(String rateableItemId) {
        return Optional.empty();
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
}
