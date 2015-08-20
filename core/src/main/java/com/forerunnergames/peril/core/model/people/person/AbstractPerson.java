package com.forerunnergames.peril.core.model.people.person;

import com.forerunnergames.peril.core.shared.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.assets.AbstractAsset;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPerson extends AbstractAsset implements Person, Author
{
  private PersonIdentity identity = PersonIdentity.UNKNOWN;

  protected AbstractPerson (final String name, final Id id, final PersonIdentity identity)
  {
    super (name, id);

    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  @RequiredForNetworkSerialization
  protected AbstractPerson ()
  {
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
}
