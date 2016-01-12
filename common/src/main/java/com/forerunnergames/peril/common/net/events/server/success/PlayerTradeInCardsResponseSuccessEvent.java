package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerTradeInCardsResponseSuccessEvent implements PlayerResponseSuccessEvent
{
  private final PlayerPacket player;
  private final CardSetPacket tradeIn;
  private final int tradeInBonus;

  public PlayerTradeInCardsResponseSuccessEvent (final PlayerPacket player, final CardSetPacket tradeIn, final int tradeInBonus)
  {
    this.player = player;
    this.tradeIn = tradeIn;
    this.tradeInBonus = tradeInBonus;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
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
    return Strings.format ("{}: Player: [{}] | TradeIn: [{}] | TradeInBonus: [{}]", getClass ().getSimpleName (),
                           player, tradeIn, tradeInBonus);
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsResponseSuccessEvent ()
  {
    player = null;
    tradeIn = null;
    tradeInBonus = 0;
  }
}
