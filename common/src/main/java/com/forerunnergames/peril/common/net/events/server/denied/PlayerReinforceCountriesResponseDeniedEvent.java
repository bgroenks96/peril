package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseDeniedEvent;

public final class PlayerReinforceCountriesResponseDeniedEvent
        implements ResponseDeniedEvent <PlayerReinforceCountriesResponseDeniedEvent.Reason>
{
  private final PlayerPacket player;
  private final Reason reason;

  public enum Reason
  {
    COUNTRY_DOES_NOT_EXIST,
    NOT_OWNER_OF_COUNTRY,
    INSUFFICIENT_ARMIES_IN_HAND,
    REINFORCEMENT_NOT_ALLOWED,
    TRADE_IN_NOT_ALLOWED,
    CARDS_NOT_IN_HAND,
    TOO_MANY_CARDS_IN_HAND,
    INVALID_CARD_SET;
  }

  public PlayerReinforceCountriesResponseDeniedEvent (final PlayerPacket player, final Reason reason)
  {
    this.player = player;
    this.reason = reason;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public Reason getReason ()
  {
    return reason;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Reason: [{}]", this.getClass ().getSimpleName (), player.getName (),
                           reason.toString ());
  }
}
