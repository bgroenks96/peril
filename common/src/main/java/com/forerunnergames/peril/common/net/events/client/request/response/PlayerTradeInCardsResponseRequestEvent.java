package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerTradeInCardsResponseRequestEvent implements ResponseRequestEvent
{
  private final CardSetPacket tradeIn;

  public PlayerTradeInCardsResponseRequestEvent (final CardSetPacket tradeIn)
  {
    Arguments.checkIsNotNull (tradeIn, "tradeIn");

    this.tradeIn = tradeIn;
  }

  public CardSetPacket getTradeIn ()
  {
    return tradeIn;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: TradeIn: [{}]", getClass ().getSimpleName (), tradeIn);
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerTradeInCardsRequestEvent.class;
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsResponseRequestEvent ()
  {
    tradeIn = null;
  }
}
