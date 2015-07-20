package com.forerunnergames.peril.core.shared.net.packets.person;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.shared.net.packets.AbstractAssetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

public abstract class AbstractPersonPacket extends AbstractAssetPacket implements PersonPacket
{
  private PersonIdentity identity = PersonIdentity.UNKNOWN;

  protected AbstractPersonPacket (final String name, final UUID id)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
  }

  @Override
  public PersonIdentity getIdentity ()
  {
    return identity;
  }

  @Override
  public void setIdentity (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  @Override
  public boolean has (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    return this.identity == identity;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | Person Identity: %2$s", super.toString (), identity);
  }

  @RequiredForNetworkSerialization
  private AbstractPersonPacket ()
  {
    super (null, null);

    this.identity = null;
  }
}
