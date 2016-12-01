package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerSourceTargetCountryEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOrderFortifySuccessEvent extends AbstractPlayerSourceTargetCountryEvent
        implements PlayerSuccessEvent
{
  private final int deltaArmyCount;

  public PlayerOrderFortifySuccessEvent (final PlayerPacket player,
                                         final CountryPacket sourceCountry,
                                         final CountryPacket targetCountry,
                                         final int deltaArmyCount)
  {
    super (player, sourceCountry, targetCountry);

    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.deltaArmyCount = deltaArmyCount;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | DeltaArmyCount: {}", super.toString (), deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerOrderFortifySuccessEvent ()
  {
    deltaArmyCount = 0;
  }
}
