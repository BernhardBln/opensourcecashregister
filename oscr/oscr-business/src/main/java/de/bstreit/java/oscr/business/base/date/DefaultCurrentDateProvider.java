package de.bstreit.java.oscr.business.base.date;

import java.util.Date;

import javax.inject.Named;

@Named
public class DefaultCurrentDateProvider implements ICurrentDateProvider {

  @Override
  public Date getCurrentDate() {
    return new Date();
  }

}
