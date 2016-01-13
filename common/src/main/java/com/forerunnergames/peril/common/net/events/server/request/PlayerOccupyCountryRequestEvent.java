package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerOccupyCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final CountryPacket sourceCountry;
  private final CountryPacket destinationCountry;

  public PlayerOccupyCountryRequestEvent (final PlayerPacket player,
                                          final CountryPacket sourceCountry,
                                          final CountryPacket destinationCountry)
  {
    super (player);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (destinationCountry, "destinationCountry");

    this.sourceCountry = sourceCountry;
    this.destinationCountry = destinationCountry;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getDestinationCountry ()
  {
    return destinationCountry;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | DestinationCountry: [{}]", super.toString (), sourceCountry,
                           destinationCountry);
  }

  @RequiredForNetworkSerialization
  private PlayerOccupyCountryRequestEvent ()
  {
    sourceCountry = null;
    destinationCountry = null;
  }
}
