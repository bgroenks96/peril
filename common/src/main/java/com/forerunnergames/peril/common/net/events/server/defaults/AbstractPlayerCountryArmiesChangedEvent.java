package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public abstract class AbstractPlayerCountryArmiesChangedEvent
        implements PlayerArmiesChangedEvent, CountryArmiesChangedEvent
{
  private final PlayerPacket player;
  private final CountryPacket country;
  private final int playerDeltaArmyCount;
  private final int countryDeltaArmyCount;

  protected AbstractPlayerCountryArmiesChangedEvent (final PlayerPacket player,
                                                     final CountryPacket country,
                                                     final int playerDeltaArmyCount,
                                                     final int countryDeltaArmyCount)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNegative (playerDeltaArmyCount, "playerDeltaArmyCount");
    Arguments.checkIsNotNegative (countryDeltaArmyCount, "countryDeltaArmyCount");

    this.player = player;
    this.country = country;
    this.playerDeltaArmyCount = playerDeltaArmyCount;
    this.countryDeltaArmyCount = countryDeltaArmyCount;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public CountryPacket getCountry ()
  {
    return country;
  }

  @Override
  public String getCountryName ()
  {
    return country.getName ();
  }

  @Override
  public int getCountryDeltaArmyCount ()
  {
    return countryDeltaArmyCount;
  }

  @Override
  public int getPlayerDeltaArmyCount ()
  {
    return playerDeltaArmyCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: Player: [{}] | Country: [{}] | PlayerDeltaArmyCount: [{}] | CountryDeltaArmyCount: [{}]",
                           player, country, playerDeltaArmyCount, countryDeltaArmyCount);
  }

  @RequiredForNetworkSerialization
  private AbstractPlayerCountryArmiesChangedEvent ()
  {
    player = null;
    country = null;
    playerDeltaArmyCount = 0;
    countryDeltaArmyCount = 0;
  }
}
