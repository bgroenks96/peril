package com.forerunnergames.peril.core.shared.net.events.server.request;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.core.shared.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public class PlayerTradeInCardsRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;
  private final ImmutableSet <CardSetPacket> matches;
  private final boolean required;

  public PlayerTradeInCardsRequestEvent (final PlayerPacket player,
                                         final ImmutableSet <CardSetPacket> matches,
                                         final boolean required)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (matches, "matches");

    this.player = player;
    this.matches = matches;
    this.required = required;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public ImmutableSet <CardSetPacket> getMatches ()
  {
    return matches;
  }

  public boolean isTradeInRequired ()
  {
    return required;
  }

  @RequiredForNetworkSerialization
  private PlayerTradeInCardsRequestEvent ()
  {
    player = null;
    matches = null;
    required = false;
  }
}
