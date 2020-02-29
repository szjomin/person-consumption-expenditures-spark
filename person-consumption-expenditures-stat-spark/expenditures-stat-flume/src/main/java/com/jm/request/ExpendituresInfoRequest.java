package com.jm.request;

import com.jm.model.SimpleConsumptionModel;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ExpendituresInfoRequest
{
  private long userId;

  @JsonProperty("data")
  private List<SimpleConsumptionModel> simpleConsumptionList;

  public List<SimpleConsumptionModel> getSimpleConsumptionList() {
    return simpleConsumptionList;
  }

  public void setSimpleConsumptionList(List<SimpleConsumptionModel> simpleConsumptionList) {
    this.simpleConsumptionList = simpleConsumptionList;
  }

  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }
}
