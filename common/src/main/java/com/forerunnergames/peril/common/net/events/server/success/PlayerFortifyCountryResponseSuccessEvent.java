package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Optional;

public final class PlayerFortifyCountryResponseSuccessEvent extends AbstractPlayerEvent
        implements PlayerResponseSuccessEvent
{
  private final PlayerPacket player;
  private final Optional <CountryPacket> sourceCountry;
  private final Optional <CountryPacket> targetCountry;
  private final int fortifyArmyCount;

  public PlayerFortifyCountryResponseSuccessEvent (final PlayerPacket player,
                                                   final CountryPacket sourceCountry,
                                                   final CountryPacket targetCountry,
                                                   final int fortifyArmyCount)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkIsNotNegative (fortifyArmyCount, "fortifyArmyCount");

    this.player = player;
    this.sourceCountry = Optional.of (sourceCountry);
    this.targetCountry = Optional.of (targetCountry);
    this.fortifyArmyCount = fortifyArmyCount;
  }

  public PlayerFortifyCountryResponseSuccessEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;

    sourceCountry = Optional.absent ();
    targetCountry = Optional.absent ();
    fortifyArmyCount = 0;
  }

  public boolean isCountryDataPresent ()
  {
    return sourceCountry.isPresent () && targetCountry.isPresent ();
  }

  public Optional <CountryPacket> getSourceCountry ()
  {
    return sourceCountry;
  }

  public Optional <CountryPacket> getTargetCountry ()
  {
    return targetCountry;
  }

  public int fortifyArmyCount ()
  {
    return fortifyArmyCount;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: SourceCountry: {} | TargetCountry: {} | FortifyArmyCount: {}", sourceCountry,
                           targetCountry, fortifyArmyCount);
  }

  @RequiredForNetworkSerialization
  private PlayerFortifyCountryResponseSuccessEvent ()
  {
    player = null;
    sourceCountry = null;
    targetCountry = null;
    fortifyArmyCount = 0;
  }
}
