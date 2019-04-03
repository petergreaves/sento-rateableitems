package com.sento.rateableitems.repository;

import com.sento.rateableitems.model.RateableItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateableItemsRepository extends CrudRepository<RateableItem, Integer>{

    public Optional<RateableItem> findByRateableItemId(String rateableItemId);
    public Optional<RateableItem> deleteByRateableItemId(String rateableItemId);

    @Query(value = "SELECT * FROM rateableitems r WHERE r.is_active = :activeFlag", nativeQuery = true)
    public List<RateableItem> findAll(@Param("activeFlag") String activeFlag);

}
