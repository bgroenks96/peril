package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseDeniedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerReinforceCountriesResponseDeniedEvent
        implements PlayerResponseDeniedEvent <PlayerReinforceCountriesResponseDeniedEvent.Reason>
{
  public enum Reason
  {
    COUNTRY_ARMY_COUNT_OVERFLOW,
    COUNTRY_ARMY_COUNT_UNDERFLOW,
    NOT_OWNER_OF_COUNTRY,
    COUNTRY_UNAVAILABLE,
    COUNTRY_DOES_NOT_EXIST,
    INSUFFICIENT_ARMIES_IN_HAND,
    REINFORCEMENT_NOT_ALLOWED,
    TRADE_IN_NOT_ALLOWED,
    CARDS_NOT_IN_HAND,
    TOO_MANY_CARDS_IN_HAND,
    INVALID_CARD_SET
  }

  private final PlayerPacket player;
  private final Reason reason;

  public PlayerReinforceCountriesResponseDeniedEvent (final PlayerPacket player,
                                                      final PlayerReinforceCountriesResponseDeniedEvent.Reason reason)
  {
    this.player = player;
    this.reason = reason;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public PlayerReinforceCountriesResponseDeniedEvent.Reason getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Reason: [{}]", getClass ().getSimpleName (), player.getName (),
                           reason.toString ());
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceCountriesResponseDeniedEvent ()
  {
    player = null;
    reason = null;
  }
}
