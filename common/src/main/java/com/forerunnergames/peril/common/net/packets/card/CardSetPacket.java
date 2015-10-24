package com.forerunnergames.peril.common.net.packets.card;

import com.google.common.collect.ImmutableSet;

public interface CardSetPacket
{
  ImmutableSet <CardPacket> getCards ();

  boolean matches (final CardSetPacket cardSet);
}
