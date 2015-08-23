package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public class DefaultCardSetPacket implements CardSetPacket
{
  private final ImmutableSet <CardPacket> cards;

  public DefaultCardSetPacket (final ImmutableSet <CardPacket> cards)
  {
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkHasNoNullElements (cards, "cards");

    this.cards = cards;
  }

  @Override
  public ImmutableSet <CardPacket> getCards ()
  {
    return cards;
  }
}
