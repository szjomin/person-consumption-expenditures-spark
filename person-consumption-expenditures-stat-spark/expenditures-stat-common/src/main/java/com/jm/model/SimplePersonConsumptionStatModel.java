package com.jm.model;
/** 
* @Description: Stat model
* @Author: Jomin
* @Date: 16:42 2020/3/1
*/ 
public class SimplePersonConsumptionStatModel extends BaseModel
{
  private String personId;
  private String consumptionType;
  private String createTime;
  private long amount;

  public String getPersonId() {
    return personId;
  }

  public void setPersonId(String personId) {
    this.personId = personId;
  }

  public String getConsumptionType() {
    return consumptionType;
  }

  public void setConsumptionType(String consumptionType) {
    this.consumptionType = consumptionType;
  }

  public String getCreateTime() {
    return createTime;
  }

  public void setCreateTime(String createTime) {
    this.createTime = createTime;
  }

  public long getAmount() {
    return amount;
  }

  public void setAmount(long amount) {
    this.amount = amount;
  }
}
