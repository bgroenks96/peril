package com.forerunnergames.peril.core.shared.net.packets.person;

import com.forerunnergames.peril.core.shared.net.packets.AssetPacket;
import com.forerunnergames.tools.common.Author;

public interface PersonPacket extends AssetPacket, Author
{
  PersonIdentity getIdentity ();

  void setIdentity (final PersonIdentity identity);

  boolean has (PersonIdentity identity);
}
