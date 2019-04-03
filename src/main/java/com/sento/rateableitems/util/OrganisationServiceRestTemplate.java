package com.sento.rateableitems.util;


import com.sento.rateableitems.exceptions.InvalidRateableItemException;
import com.sento.rateableitems.model.Organisation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganisationServiceRestTemplate {


    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(){

      //  return new RestTemplate();
        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder();
        RestTemplate restTemplate = restTemplateBuilder
                    .errorHandler(new OrgServiceResponseErrorHandler())
                    .build();

        return restTemplate;

    }

    @Value("${organisations.service.uri}")
    private String orgServiceURI;


    @Autowired
    private RestTemplate restTemplate;

    public Organisation getOrganisation(String organisationId) throws InvalidRateableItemException {

        Organisation org;

        ResponseEntity<Organisation> restExchange =
                restTemplate.exchange(
                        orgServiceURI+"/{organisationId}",
                        HttpMethod.GET,
                        null, Organisation.class, organisationId);

        if (restExchange.getStatusCode() == HttpStatus.NOT_FOUND){

            throw new InvalidRateableItemException("org id " + organisationId + " does not exist");
        }
        else{
            org =restExchange.getBody();
        }
        return org;
    }

}
