package de.bstreit.java.oscr.initialdata.initialdata;

import javax.inject.Named;

import de.bstreit.java.oscr.business.user.User;
import de.bstreit.java.oscr.initialdata.AbstractDataContainer;


@Named
public class Users extends AbstractDataContainer<User> {

  public static final User john = new User("John Doe");


  @Override
  public Class<User> getType() {
    return User.class;
  }


}
