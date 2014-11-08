package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerDeniedEvent;
import com.forerunnergames.tools.common.Arguments;

public final class KickPlayerFromGameDeniedEvent implements PlayerDeniedEvent, KickEvent
{
  private final PlayerDeniedEvent playerDeniedEvent;
  private final KickEvent kickEvent;

  public KickPlayerFromGameDeniedEvent (final Player player, final String reasonForKick, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    playerDeniedEvent = new DefaultPlayerDeniedEvent (player, reasonForDenial);
    kickEvent = new DefaultKickEvent (reasonForKick);
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
  public String getReasonForDenial()
  {
    return playerDeniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s",
            getClass().getSimpleName(), kickEvent.toString(), playerDeniedEvent.toString());
  }

  // Required for network serialization
  private KickPlayerFromGameDeniedEvent()
  {
    kickEvent = null;
    playerDeniedEvent = null;
  }
}