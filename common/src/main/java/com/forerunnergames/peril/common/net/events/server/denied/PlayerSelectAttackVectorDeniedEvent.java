package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectAttackVectorDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectAttackVectorDeniedEvent extends AbstractPlayerDeniedEvent <Reason>
{
  public enum Reason
  {
    SOURCE_COUNTRY_DOES_NOT_EXIST,
    TARGET_COUNTRY_DOES_NOT_EXIST,
    NOT_OWNER_OF_SOURCE_COUNTRY,
    ALREADY_OWNER_OF_TARGET_COUNTRY,
    COUNTRIES_NOT_ADJACENT,
    INSUFFICIENT_ARMY_COUNT
  }

  public PlayerSelectAttackVectorDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (player, reason);
  }

  @RequiredForNetworkSerialization
  private PlayerSelectAttackVectorDeniedEvent ()
  {
  }
}
