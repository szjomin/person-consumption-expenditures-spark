package com.jm.model;

import org.codehaus.jackson.annotate.JsonProperty;

/**
* @Description:
* @Author: Jomin
* @Date: 23:36 2020/2/20 
*/ 
public class SimpleConsumptionModel implements Comparable<SimpleConsumptionModel>{

    private static final long serialVersionUID = 1L;

    @JsonProperty("personalId")
    private long personalId;

    @JsonProperty("consumptionType")
    private String consumptionType;

    @JsonProperty("amount")
    private long amount;

    @JsonProperty("createTime")
    private String createTime;

    public long getPersonalId() {
        return personalId;
    }

    public void setPersonalId(long personalId) {
        this.personalId = personalId;
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
    public int compareTo(SimpleConsumptionModel o)
    {
        if (amount == o.getAmount()) {
            return 0;
        } else if (amount > o.getAmount()) {
            return -1;
        } else {
            return 1;
        }
    }
}
