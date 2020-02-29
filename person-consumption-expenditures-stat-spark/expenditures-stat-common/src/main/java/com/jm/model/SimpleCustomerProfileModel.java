package com.jm.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
* @Description:
* @Author: Jomin
* @Date: 23:36 2020/2/20 
*/ 
public class SimpleCustomerProfileModel {

    @JsonProperty("personalIdentificationNumber")
    private String personalIdentificationNumber;

    @JsonProperty("career")
    private String career;

    @JsonProperty("age")
    private String age;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("state")
    private String state;

    @JsonProperty("city")
    private String city;

    @JsonProperty("annualIncome")
    private long annualIncome;

    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
        this.personalIdentificationNumber = personalIdentificationNumber;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public long getAnnualIncome() {
        return annualIncome;
    }

    public void setAnnualIncome(long annualIncome) {
        this.annualIncome = annualIncome;
    }
}
