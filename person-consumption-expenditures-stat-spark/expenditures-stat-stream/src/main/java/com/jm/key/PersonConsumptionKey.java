package com.jm.key;

import scala.math.Ordered;

import java.io.Serializable;

public class PersonConsumptionKey implements Ordered<PersonConsumptionKey>, Serializable
{
  public Long personalId;
  public String consumptionType;
  public String createTime;

  @Override
  public int compareTo(PersonConsumptionKey that)
  {
    if (this.personalId == that.getPersonalId()) {
      if (this.consumptionType.compareTo(that.getConsumptionType())==0) {
        return this.createTime.compareTo(that.getCreateTime());
      } else {
        return this.consumptionType.compareTo(that.getConsumptionType());
      }
    } else {
      Long n = this.personalId - that.getPersonalId();
      return n > 0 ? 1 : (n == 0 ? 0 : -1);
    }
  }

  @Override
  public int compare(PersonConsumptionKey that)
  {
    return this.compareTo(that);
  }

  @Override
  public boolean $greater(PersonConsumptionKey that)
  {
    if (this.compareTo(that) > 0) {
      return true;
    }

    return false;
  }

  @Override
  public boolean $less(PersonConsumptionKey that)
  {
    if (this.compareTo(that) < 0) {
      return true;
    }

    return false;
  }

  @Override
  public boolean $less$eq(PersonConsumptionKey that)
  {
    if (this.compareTo(that) <= 0) {
      return true;
    }

    return false;
  }

  @Override
  public boolean $greater$eq(PersonConsumptionKey that) {
    if (this.compareTo(that) >= 0) {
      return true;
    }

    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PersonConsumptionKey that = (PersonConsumptionKey) o;

    if (personalId != null ? !personalId.equals(that.personalId) : that.personalId != null) return false;
    if (consumptionType != null ? !consumptionType.equals(that.consumptionType) : that.consumptionType != null) return false;
    return !(createTime != null ? !createTime.equals(that.createTime) : that.createTime != null);

  }

  @Override
  public int hashCode() {
    int result = personalId != null ? personalId.hashCode() : 0;
    result = 31 * result + (consumptionType != null ? consumptionType.hashCode() : 0);
    result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
    return result;
  }

  public Long getPersonalId() {
    return personalId;
  }

  public void setPersonalId(Long personalId) {
    this.personalId = personalId;
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
}
