package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.model.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class ChangePlayerColorRequestEvent implements PlayerColorEvent, RequestEvent
{
  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorRequestEvent (final Player player,
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
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), playerColorEvent);
  }

  // Required for network serialization
  private ChangePlayerColorRequestEvent()
  {
    playerColorEvent = null;
  }
}
