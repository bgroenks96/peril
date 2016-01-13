package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerTradeInCardsResponseSuccessEvent extends AbstractPlayerArmiesChangedEvent
        implements PlayerResponseSuccessEvent
{
  private final CardSetPacket tradeIn;
  private final int tradeInBonus;

  public PlayerTradeInCardsResponseSuccessEvent (final PlayerPacket player,
                                                 final CardSetPacket tradeIn,
                                                 final int tradeInBonus)
  {
    super (player, tradeInBonus);

    this.tradeIn = tradeIn;
    this.tradeInBonus = tradeInBonus;
  }

  public CardSetPacket getTradeIn ()
  {
    return tradeIn;
  }

  public int getTradeInBonus ()
  {
    return tradeInBonus;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | TradeIn: [{}] | TradeInBonus: [{}]", super.toString (), tradeIn, tradeInBonus);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsResponseSuccessEvent ()
  {
    tradeIn = null;
    tradeInBonus = 0;
  }
}
