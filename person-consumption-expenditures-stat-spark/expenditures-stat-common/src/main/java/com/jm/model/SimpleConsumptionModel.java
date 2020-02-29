package com.jm.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
* @Description:
* @Author: Jomin
* @Date: 23:36 2020/2/20 
*/ 
public class SimpleConsumptionModel {

    private static final long serialVersionUID = 1L;

    @JsonProperty("personalIdentificationNumber")
    private String personalIdentificationNumber;

    @JsonProperty("consumptionType")
    private String consumptionType;

    @JsonProperty("amount")
    private long amount;

    @JsonProperty("createTime")
    private String createTime;

    public String getPersonalIdentificationNumber() {
        return personalIdentificationNumber;
    }

    public void setPersonalIdentificationNumber(String personalIdentificationNumber) {
        this.personalIdentificationNumber = personalIdentificationNumber;
    }

    public String getConsumptionType() {
        return consumptionType;
    }

    public long getAmount() {
        return amount;
    }

    public void setConsumptionType(String consumptionType) {
        this.consumptionType = consumptionType;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SimpleConsumptionModel{" +
                "personalIdentificationNumber='" + personalIdentificationNumber + '\'' +
                ", consumptionType='" + consumptionType + '\'' +
                ", amount=" + amount +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
