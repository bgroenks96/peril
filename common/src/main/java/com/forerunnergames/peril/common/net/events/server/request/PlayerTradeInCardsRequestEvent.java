package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerTradeInCardsRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final int nextCardTradeInBonus;
  private final ImmutableSet <CardSetPacket> tradeInMatches;
  private final boolean tradeInRequired;

  public PlayerTradeInCardsRequestEvent (final PlayerPacket player,
                                         final int nextCardTradeInBonus,
                                         final ImmutableSet <CardSetPacket> tradeInMatches,
                                         final boolean tradeInRequired)
  {
    super (player);

    Arguments.checkIsNotNegative (nextCardTradeInBonus, "nextCardTradeInBonus");
    Arguments.checkIsNotNull (tradeInMatches, "tradeInMatches");
    Arguments.checkHasNoNullElements (tradeInMatches, "tradeInMatches");

    this.nextCardTradeInBonus = nextCardTradeInBonus;
    this.tradeInMatches = tradeInMatches;
    this.tradeInRequired = tradeInRequired;
  }

  public int getNextCardTradeInBonus ()
  {
    return nextCardTradeInBonus;
  }

  public ImmutableSet <CardSetPacket> getMatches ()
  {
    return tradeInMatches;
  }

  public boolean isTradeInRequired ()
  {
    return tradeInRequired;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | NextCardTradeInBonus: {} | TradeInMatches: [{}] | TradeInRequired: {}",
                           super.toString (), nextCardTradeInBonus, tradeInMatches, tradeInRequired);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsRequestEvent ()
  {
    nextCardTradeInBonus = 0;
    tradeInMatches = null;
    tradeInRequired = false;
  }
}
