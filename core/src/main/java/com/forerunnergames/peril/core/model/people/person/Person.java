package com.forerunnergames.peril.core.model.people.person;

import com.forerunnergames.peril.core.shared.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.assets.Asset;

public interface Person extends Asset, Author
{
  PersonIdentity getIdentity ();

  void setIdentity (final PersonIdentity identity);

  boolean has (PersonIdentity identity);
}
