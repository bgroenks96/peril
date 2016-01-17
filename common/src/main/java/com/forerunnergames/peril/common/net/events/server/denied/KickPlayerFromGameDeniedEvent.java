package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.common.net.events.interfaces.KickEvent;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class KickPlayerFromGameDeniedEvent implements PlayerDeniedEvent <String>, KickEvent
{
  private final PlayerDeniedEvent <String> playerDeniedEvent;
  private final KickEvent kickEvent;

  public KickPlayerFromGameDeniedEvent (final PlayerPacket player, final String reasonForKick, final String reason)
  {
    Arguments.checkIsNotNull (reasonForKick, "reasonForKick");
    Arguments.checkIsNotNull (reason, "reason");

    playerDeniedEvent = new DefaultPlayerDeniedEvent (player, reason);
    kickEvent = new DefaultKickEvent (reasonForKick);
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return playerDeniedEvent.getPlayer ();
  }

  @Override
  public String getPlayerName ()
  {
    return getPlayer ().getName ();
  }

  @Override
  public String getPlayerColor ()
  {
    return getPlayer ().getColor ();
  }

  @Override
  public String getReason ()
  {
    return playerDeniedEvent.getReason ();
  }

  @Override
  public String getReasonForKick ()
  {
    return kickEvent.getReasonForKick ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {} | {}", getClass ().getSimpleName (), kickEvent, playerDeniedEvent);
  }

  @RequiredForNetworkSerialization
  private KickPlayerFromGameDeniedEvent ()
  {
    kickEvent = null;
    playerDeniedEvent = null;
  }
}
