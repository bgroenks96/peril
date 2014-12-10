package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;

public final class PlayerLeaveGameDeniedEvent extends AbstractDeniedEvent <PlayerLeaveGameDeniedEvent.REASON>
        implements PlayerEvent
{
  public enum REASON
  {
    PLAYER_DOES_NOT_EXIST
  }

  private final PlayerEvent playerEvent;

  public PlayerLeaveGameDeniedEvent (final Player player, final REASON reason)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");

    playerEvent = new DefaultPlayerEvent (player);
  }

  @Override
  public Player getPlayer()
  {
    return playerEvent.getPlayer();
  }

  @Override
  public String getPlayerName()
  {
    return playerEvent.getPlayerName();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s", getClass().getSimpleName(), playerEvent, super.toString());
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameDeniedEvent()
  {
    playerEvent = null;
  }
}