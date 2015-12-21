package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class PlayerAttackCountryDeniedEvent extends AbstractDeniedEvent <Reason> implements PlayerDeniedEvent <Reason>
{
  private final PlayerPacket player;

  public enum Reason
  {
    SOURCE_COUNTRY_DOES_NOT_EXIST,
    TARGET_COUNTRY_DOES_NOT_EXIST,
    NOT_OWNER_OF_SOURCE_COUNTRY,
    ALREADY_OWNER_OF_TARGET_COUNTRY,
    INSUFFICIENT_ARMY_COUNT,
    INVALID_DIE_COUNT,
    COUNTRIES_NOT_ADJACENT,
    PLAYER_NOT_IN_TURN;
  }

  public PlayerAttackCountryDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (reason);

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryDeniedEvent ()
  {
    super (null);

    player = null;
  }
}
