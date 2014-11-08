package com.forerunnergames.peril.core.model.person;

import com.forerunnergames.tools.common.AbstractAsset;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Id;

public abstract class AbstractPerson extends AbstractAsset implements Person, Author
{
  private PersonIdentity identity;

  protected AbstractPerson (final String name, final Id id, final PersonIdentity identity)
  {
    super (name, id);

    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  @Override
  public PersonIdentity getIdentity()
  {
    return identity;
  }

  @Override
  public boolean is (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    return this.identity.equals (identity);
  }

  @Override
  public void setIdentity (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    this.identity = identity;
  }

  // Required for network serialization
  protected AbstractPerson()
  {
  }
}
