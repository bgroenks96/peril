package com.forerunnergames.peril.core.model.people.person;

import com.forerunnergames.tools.common.Asset;
import com.forerunnergames.tools.common.Author;

public interface Person extends Asset, Author
{
  public PersonIdentity getIdentity();
  public boolean has (PersonIdentity identity);
  public void setIdentity (final PersonIdentity identity);
}
