package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerAttackCountryResponseDeniedEvent.Reason;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerAttackCountryResponseDeniedEvent extends AbstractDeniedEvent <Reason>
        implements PlayerResponseDeniedEvent <Reason>
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
    COUNTRIES_NOT_ADJACENT;
  }

  public PlayerAttackCountryResponseDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    super (reason);

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return getPlayer ().getName ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: [{}]", super.toString (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryResponseDeniedEvent ()
  {
    player = null;
  }
}
