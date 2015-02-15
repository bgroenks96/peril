package com.forerunnergames.peril.core.shared.net.packets;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

public interface PlayerPacket
{
  public String getName ();

  public PlayerColor getColor ();

  public PlayerTurnOrder getTurnOrder ();

  public int getArmiesInHand ();

  public boolean has (final PlayerColor color);

  public boolean has (final PlayerTurnOrder turnOrder);

  public boolean hasArmiesInHand (final int armies);

  public boolean doesNotHave (final PlayerColor color);

  public boolean doesNotHave (final PlayerTurnOrder turnOrder);
}
