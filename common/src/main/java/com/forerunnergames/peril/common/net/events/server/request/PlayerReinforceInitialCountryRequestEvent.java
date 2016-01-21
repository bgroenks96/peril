package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class PlayerReinforceInitialCountryRequestEvent extends AbstractPlayerEvent
        implements PlayerInputRequestEvent
{
  private final ImmutableSet <CountryPacket> playerOwnedCountries;
  private final int preSetReinforcementCount;

  public PlayerReinforceInitialCountryRequestEvent (final PlayerPacket player,
                                                    final ImmutableSet <CountryPacket> playerOwnedCountries,
                                                    final int preSetReinforcementCount)
  {
    super (player);

    Arguments.checkIsNotNull (playerOwnedCountries, "playerOwnedCountries");
    Arguments.checkIsNotNegative (preSetReinforcementCount, "preSetReinforcementCount");

    this.playerOwnedCountries = playerOwnedCountries;
    this.preSetReinforcementCount = preSetReinforcementCount;
  }

  public ImmutableSet <CountryPacket> getPlayerOwnedCountries ()
  {
    return playerOwnedCountries;
  }

  public int getPreSetReinforcementCount ()
  {
    return preSetReinforcementCount;
  }

  public int getTotalReinforcements ()
  {
    return getPlayer ().getArmiesInHand ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | OwnedCountries: [{}]", playerOwnedCountries);
  }

  @RequiredForNetworkSerialization
  private PlayerReinforceInitialCountryRequestEvent ()
  {
    playerOwnedCountries = null;
    preSetReinforcementCount = 0;
  }
}
