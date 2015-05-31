package com.forerunnergames.peril.core.shared.net.packets.person;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.shared.net.packets.AssetPacket;

public interface PersonPacket extends AssetPacket
{
  PersonIdentity getIdentity ();

  void setIdentity (final PersonIdentity identity);

  boolean has (PersonIdentity identity);
}
