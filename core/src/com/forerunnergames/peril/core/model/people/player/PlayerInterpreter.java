package com.forerunnergames.peril.core.model.people.player;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;

public final class PlayerInterpreter
{
  public static String nameOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getName();
  }

  public static Id idOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getId();
  }

  public static int idValueOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getId().value();
  }

  public static PlayerColor colorOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getColor();
  }

  public static PlayerColor withColorOf (final Player player)
  {
    return colorOf (player);
  }

  public static PlayerTurnOrder turnOrderOf (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.getTurnOrder();
  }
}
