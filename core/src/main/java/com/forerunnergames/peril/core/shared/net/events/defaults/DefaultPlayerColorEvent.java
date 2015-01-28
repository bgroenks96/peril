package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultPlayerColorEvent implements PlayerColorEvent
{
  private final PlayerEvent playerEvent;
  private final PlayerColor currentColor;
  private final PlayerColor previousColor;

  public DefaultPlayerColorEvent (final Player player, final PlayerColor currentColor, final PlayerColor previousColor)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentColor, "currentColor");
    Arguments.checkIsNotNull (previousColor, "previousColor");

    this.playerEvent = new DefaultPlayerEvent (player);
    this.currentColor = currentColor;
    this.previousColor = previousColor;
  }

  @Override
  public PlayerColor getCurrentColor ()
  {
    return currentColor;
  }

  @Override
  public PlayerColor getPreviousColor ()
  {
    return previousColor;
  }

  @Override
  public Player getPlayer ()
  {
    return playerEvent.getPlayer ();
  }

  @Override
  public String getPlayerName ()
  {
    return playerEvent.getPlayerName ();
  }

  @Override
  public String toString ()
  {
    return String.format ("Current color: %1$s | Previous color: %2$s | %3$s", currentColor, previousColor, playerEvent);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerColorEvent ()
  {
    playerEvent = null;
    currentColor = null;
    previousColor = null;
  }
}
