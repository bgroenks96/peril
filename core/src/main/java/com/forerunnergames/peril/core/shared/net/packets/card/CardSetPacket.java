package com.forerunnergames.peril.core.shared.net.packets.card;

import com.google.common.collect.ImmutableSet;

public interface CardSetPacket
{
  ImmutableSet <CardPacket> getCards ();
}
