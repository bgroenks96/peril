package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.model.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

public final class ChangePlayerColorSuccessEvent implements PlayerColorEvent, SuccessEvent
{
  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorSuccessEvent (final Player player,
                                        final PlayerColor currentColor,
                                        final PlayerColor previousColor)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentColor, "color");
    Arguments.checkIsNotNull (previousColor, "previousColor");

    playerColorEvent = new DefaultPlayerColorEvent (player, currentColor, previousColor);
  }

  @Override
  public Player getPlayer()
  {
    return playerColorEvent.getPlayer();
  }

  @Override
  public String getPlayerName()
  {
    return playerColorEvent.getPlayerName();
  }

  @Override
  public PlayerColor getCurrentColor()
  {
    return playerColorEvent.getCurrentColor();
  }

  @Override
  public PlayerColor getPreviousColor()
  {
    return playerColorEvent.getPreviousColor();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), playerColorEvent.toString());
  }

  // Required for network serialization
  private ChangePlayerColorSuccessEvent()
  {
    playerColorEvent = null;
  }
}
