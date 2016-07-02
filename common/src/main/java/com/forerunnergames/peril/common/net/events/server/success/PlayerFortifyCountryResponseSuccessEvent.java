package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerFortifyCountryResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent
{
  private final CountryPacket sourceCountry;
  private final CountryPacket targetCountry;
  private final int deltaArmyCount;

  public PlayerFortifyCountryResponseSuccessEvent (final PlayerPacket player,
                                                   final CountryPacket sourceCountry,
                                                   final CountryPacket targetCountry,
                                                   final int deltaArmyCount)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (deltaArmyCount, "deltaArmyCount");

    this.sourceCountry = sourceCountry;
    this.targetCountry = targetCountry;
    this.deltaArmyCount = deltaArmyCount;
  }

  public CountryPacket getSourceCountry ()
  {
    return sourceCountry;
  }

  public CountryPacket getTargetCountry ()
  {
    return targetCountry;
  }

  public String getSourceCountryName ()
  {
    return sourceCountry.getName ();
  }

  public String getTargetCountryName ()
  {
    return targetCountry.getName ();
  }

  public int getDeltaArmyCount ()
  {
    return deltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | SourceCountry: [{}] | TargetCountry: [{}] | DeltaArmyCount: {}", super.toString (),
                           sourceCountry, targetCountry, deltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryResponseSuccessEvent ()
  {
    sourceCountry = null;
    targetCountry = null;
    deltaArmyCount = 0;
  }
}
