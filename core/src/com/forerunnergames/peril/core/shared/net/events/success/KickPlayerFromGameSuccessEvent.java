package com.forerunnergames.peril.core.shared.net.events.success;

import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.SuccessEvent;

public final class KickPlayerFromGameSuccessEvent implements PlayerEvent, KickEvent, SuccessEvent
{
  private final PlayerEvent playerEvent;
  private final KickEvent kickEvent;

  public KickPlayerFromGameSuccessEvent (final Player player, final String reasonForKick)
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
    return String.format ("%1$s: | %2$s | %3$s", getClass().getSimpleName(), playerEvent, kickEvent);
  }

  // Required for network serialization
  private KickPlayerFromGameSuccessEvent()
  {
    playerEvent = null;
    kickEvent = null;
  }
}