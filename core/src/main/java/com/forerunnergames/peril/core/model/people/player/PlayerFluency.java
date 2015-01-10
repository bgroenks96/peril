package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class PlayerFluency
{
  public static PlayerColor colorOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getColor ();
  }

  public static PlayerColor withColorOf (final Player player)
  {
    return colorOf (player);
  }

  public static PlayerTurnOrder turnOrderOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getTurnOrder ();
  }

  private PlayerFluency ()
  {
    Classes.instantiationNotAllowed ();
  }
}
