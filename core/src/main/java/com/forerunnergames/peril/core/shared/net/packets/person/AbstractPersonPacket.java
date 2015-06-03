package com.forerunnergames.peril.core.shared.net.packets.person;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.shared.net.packets.AbstractAssetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPersonPacket extends AbstractAssetPacket implements PersonPacket
{
  private PersonIdentity identity = PersonIdentity.UNKNOWN;

  protected AbstractPersonPacket (final String name, final int id)
  {
    super (name, id);

    Arguments.checkIsNotNull (name, "name");
  }

  @Override
  public void setIdentity (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  @Override
  public PersonIdentity getIdentity ()
  {
    return identity;
  }

  @Override
  public boolean has (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    return this.identity == identity;
  }

  @RequiredForNetworkSerialization
  private AbstractPersonPacket ()
  {
    super (null, 0);

    this.identity = null;
  }
}