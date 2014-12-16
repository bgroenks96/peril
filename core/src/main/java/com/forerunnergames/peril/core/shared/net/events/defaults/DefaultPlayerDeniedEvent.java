package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class DefaultPlayerDeniedEvent implements PlayerDeniedEvent <String>
{
  private final PlayerEvent playerEvent;
  private final DeniedEvent <String> deniedEvent;

  public DefaultPlayerDeniedEvent (final Player player, final String reason)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reason, "reason");

    playerEvent = new DefaultPlayerEvent (player);
    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason()
  {
    return deniedEvent.getReason();
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
    return String.format ("%1$s | %2$s", playerEvent, deniedEvent);
  }

  @RequiredForNetworkSerialization
  private DefaultPlayerDeniedEvent()
  {
    playerEvent = null;
    deniedEvent = null;
  }
}
