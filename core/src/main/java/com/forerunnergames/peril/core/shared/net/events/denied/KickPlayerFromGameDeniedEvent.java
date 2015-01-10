package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;

public final class KickPlayerFromGameDeniedEvent implements PlayerDeniedEvent <String>, KickEvent
{
  private final PlayerDeniedEvent <String> playerDeniedEvent;
  private final KickEvent kickEvent;

  public KickPlayerFromGameDeniedEvent (final Player player, final String reasonForKick, final String reason)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");
    Arguments.checkIsNotNull (reason, "reason");

    playerDeniedEvent = new DefaultPlayerDeniedEvent (player, reason);
    kickEvent = new DefaultKickEvent (reasonForKick);
  }

  @Override
  public String getReason()
  {
    return playerDeniedEvent.getReason();
  }

  @Override
  public Player getPlayer()
  {
    return playerDeniedEvent.getPlayer();
  }

  @Override
  public String getPlayerName()
  {
    return playerDeniedEvent.getPlayerName();
  }

  @Override
  public String getReasonForKick()
  {
    return kickEvent.getReasonForKick();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s", getClass().getSimpleName(), kickEvent, playerDeniedEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameDeniedEvent()
  {
    kickEvent = null;
    playerDeniedEvent = null;
  }
}
