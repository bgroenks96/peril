package com.forerunnergames.peril.core.shared.net.events.defaults;

import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class DefaultPlayerDeniedEvent implements PlayerDeniedEvent
{
  private final PlayerEvent playerEvent;
  private final DeniedEvent deniedEvent;

  public DefaultPlayerDeniedEvent (final Player player, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    playerEvent = new DefaultPlayerEvent (player);
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
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
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s | %2$s", playerEvent.toString(), deniedEvent.toString());
  }

  // Required for network serialization
  private DefaultPlayerDeniedEvent()
  {
    playerEvent = null;
    deniedEvent = null;
  }
}
