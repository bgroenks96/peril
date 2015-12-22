package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOccupyCountryRequestEvent implements PlayerInputRequestEvent
{
  private final PlayerPacket player;
  private final CountryPacket sourceCountry;
  private final CountryPacket destinationCountry;

  public PlayerOccupyCountryRequestEvent (final PlayerPacket player,
                                          final CountryPacket sourceCountry,
                                          final CountryPacket destinationCountry)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (destinationCountry, "destinationCountry");

    this.player = player;
    this.sourceCountry = sourceCountry;
    this.destinationCountry = destinationCountry;
  }

  @Override
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

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryRequestEvent ()
  {
    player = null;
    sourceCountry = null;
    destinationCountry = null;
  }
}
