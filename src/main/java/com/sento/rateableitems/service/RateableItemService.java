package com.sento.rateableitems.service;



import com.sento.rateableitems.exceptions.RateableItemAlreadyExistsException;
import com.sento.rateableitems.model.RateableItem;

import java.util.Optional;


public interface RateableItemService {

    Iterable<RateableItem> getAllRateableItems(String activeStatus);

    Optional<RateableItem> getRateableItem(String rateableItemId);


    /**
     *
     * Try to create the new rateable item, but first check for pre-existence
     *
     * @param rateableItem
     * @return the created rateableItem
     * @throws com.sento.rateableitems.exceptions.RateableItemAlreadyExistsException
     */
    RateableItem saveRateableItem(RateableItem rateableItem) throws RateableItemAlreadyExistsException;

    RateableItem updateRateableItem(RateableItem rateableitem);

    Optional<RateableItem> deleteRateableItem(String rateableItemId);
}
