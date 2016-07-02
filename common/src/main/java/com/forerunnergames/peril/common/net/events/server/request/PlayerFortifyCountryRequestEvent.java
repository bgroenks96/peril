package com.forerunnergames.peril.common.net.events.server.request;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerFortifyCountryRequestEvent extends AbstractPlayerEvent implements PlayerInputRequestEvent
{
  private final CountryPacket sourceCountry;
  private final CountryPacket targetCountry;
  private final int maxDeltaArmyCount;

  public PlayerFortifyCountryRequestEvent (final PlayerPacket player,
                                           final CountryPacket sourceCountry,
                                           final CountryPacket targetCountry,
                                           final int maxDeltaArmyCount)
  {
    super (player);

    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (maxDeltaArmyCount, "maxDeltaArmyCount");

    this.sourceCountry = sourceCountry;
    this.targetCountry = targetCountry;
    this.maxDeltaArmyCount = maxDeltaArmyCount;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getTargetCountry ()
  {
    return targetCountry;
  }

  public int getMaxDeltaArmyCount ()
  {
    return maxDeltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | TargetCountry: [{}] | MaxDeltaArmyCount: {}", super.toString (),
                           sourceCountry, targetCountry, maxDeltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryRequestEvent ()
  {
    sourceCountry = null;
    targetCountry = null;
    maxDeltaArmyCount = 0;
  }
}
