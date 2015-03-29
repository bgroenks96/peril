package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

public interface PlayerPacket
{
  String getName ();

  PlayerColor getColor ();

  PlayerTurnOrder getTurnOrder ();

  int getArmiesInHand ();

  boolean has (final PlayerColor color);

  boolean has (final PlayerTurnOrder turnOrder);

  boolean hasArmiesInHand (final int armies);

  boolean doesNotHave (final PlayerColor color);

  boolean doesNotHave (final PlayerTurnOrder turnOrder);
}
