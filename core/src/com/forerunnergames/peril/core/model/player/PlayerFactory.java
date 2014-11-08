package com.forerunnergames.peril.core.model.player;

import com.forerunnergames.peril.core.model.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;

public final class PlayerFactory
{
  public static Player create (final String name, final Id id)
  {
    Arguments.checkIsNotNull (name, "name");
    Arguments.checkIsNotNull (id, "id");

    return new DefaultPlayer (name, id, PersonIdentity.UNKNOWN, PlayerColor.UNKNOWN, PlayerTurnOrder.UNKNOWN);
  }
}
