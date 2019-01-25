package com.sento.rateableitems.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sento.rateableitems.util.IsActiveAttributeConverter;

import java.util.Date;
import java.util.Objects;


import javax.persistence.*;

@Entity
@Table(name = "rateableitems")
public class RateableItem {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    public int getId() {
        return id;
    }

    @JsonIgnore
    public void setId(int id) {
        this.id = id;
    }

    public String getRateableItemId() {
        return rateableItemId;
    }

    public void setRateableItemId(String rateableItemId) {
        this.rateableItemId = rateableItemId;
    }

    @Column(name = "rateable_item_id", nullable = false, unique = true)
    private String rateableItemId;

    public String getOwningOrgId() {
        return owningOrgId;
    }

    public void setOwningOrgId(String owningOrgId) {
        this.owningOrgId = owningOrgId;
    }

    @Column(name = "owning_org_id", nullable = false)
    private String owningOrgId;


    @Column(name = "description")
    private String description;


    @Column(name = "start_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Column(name = "is_active")
    @Convert(converter= IsActiveAttributeConverter.class)
    private boolean isActive;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RateableItem that = (RateableItem) o;
        return rateableItemId.equals(that.rateableItemId) &&
                owningOrgId.equals(that.owningOrgId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rateableItemId, owningOrgId);
    }

    @Override
    public String toString() {
        return "RateableItem{" +
                "id=" + id +
                ", rateableItemId='" + rateableItemId + '\'' +
                ", owningOrgId='" + owningOrgId + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive" + isActive +
                '}';
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "end_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

}
