package com.sento.rateableitems.model;





public class Organisation {
    @Override
    public String toString() {
        return "Organisation [orgId=" + orgId + ", name=" + name + ", contactName=" + contactName
                + ", contactEmail=" + contactEmail + ", contactPhone=" + contactPhone + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Organisation other = (Organisation) obj;
        if (orgId == null) {
            if (other.orgId != null)
                return false;
        } else if (!orgId.equals(other.orgId))
            return false;
        return true;
    }


    String orgId;

    public String getOrgId() {
        return orgId;
    }


    public void setOrgId(String org_id) {
        this.orgId = org_id;
    }


    String name;


    String contactName;


    String contactEmail;


    String contactPhone;





    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

}