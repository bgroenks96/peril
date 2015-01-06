package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.RequestEvent;

public final class KickPlayerFromGameRequestEvent implements PlayerEvent, KickEvent, RequestEvent
{
  private final PlayerEvent playerEvent;
  private final KickEvent kickEvent;

  public KickPlayerFromGameRequestEvent (final Player player, final String reasonForKick)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");

    playerEvent = new DefaultPlayerEvent (player);
    kickEvent = new DefaultKickEvent (reasonForKick);
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
  public String getReasonForKick()
  {
    return kickEvent.getReasonForKick();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), kickEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameRequestEvent()
  {
    playerEvent = null;
    kickEvent = null;
  }
}
