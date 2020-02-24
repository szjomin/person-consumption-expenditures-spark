package com.jm.vo;

import com.jm.model.SimpleConsumptionModel;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class PersonConsumptionInfo {

	@JsonProperty("data")
	private List<SimpleConsumptionModel> simpleConsumptionList;

	public List<SimpleConsumptionModel> getSimpleConsumptionList() {
		return simpleConsumptionList;
	}

	public void setSimpleConsumptionList(List<SimpleConsumptionModel> simpleConsumptionList) {
		this.simpleConsumptionList = simpleConsumptionList;
	}

}
