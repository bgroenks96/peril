package com.forerunnergames.peril.core.model.people.person;

import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.assets.Asset;

public interface Person extends Asset, Author
{
  public PersonIdentity getIdentity ();

  public void setIdentity (final PersonIdentity identity);

  public boolean has (PersonIdentity identity);
}
