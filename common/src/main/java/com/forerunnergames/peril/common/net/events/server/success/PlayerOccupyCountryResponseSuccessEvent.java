package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ResponseSuccessEvent;

public class PlayerOccupyCountryResponseSuccessEvent implements ResponseSuccessEvent
{
  private final PlayerPacket player;
  private final CountryPacket sourceCountry;
  private final CountryPacket destinationCountry;
  private final int deltaArmyCount;

  public PlayerOccupyCountryResponseSuccessEvent (final PlayerPacket player,
                                                  final CountryPacket sourceCountry,
                                                  final CountryPacket destinationCountry,
                                                  final int deltaArmyCount)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (destinationCountry, "destinationCountry");
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.player = player;
    this.sourceCountry = sourceCountry;
    this.destinationCountry = destinationCountry;
    this.deltaArmyCount = deltaArmyCount;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getDestinationCountry ()
  {
    return destinationCountry;
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryResponseSuccessEvent ()
  {
    player = null;
    sourceCountry = null;
    destinationCountry = null;
    deltaArmyCount = 0;
  }
}
