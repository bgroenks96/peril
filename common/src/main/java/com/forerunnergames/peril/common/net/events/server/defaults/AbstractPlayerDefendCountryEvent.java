package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

public abstract class AbstractPlayerDefendCountryEvent extends AbstractPlayerEvent implements ServerEvent
{
  private final CountryPacket defendingCountry;
  private final BattleActorPacket attackerData;

  protected AbstractPlayerDefendCountryEvent (final PlayerPacket defendingPlayer,
                                              final CountryPacket defendingCountry,
                                              final BattleActorPacket attackerData)
  {
    super (defendingPlayer);

    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");
    Arguments.checkIsNotNull (attackerData, "attackerData");

    this.defendingCountry = defendingCountry;
    this.attackerData = attackerData;
  }

  public CountryPacket getCountry ()
  {
    return defendingCountry;
  }

  public BattleActorPacket getAttackerData ()
  {
    return attackerData;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: DefendingPlayer: [{}] | DefendingCountry: [{}] | AttackerData: [{}]", getPlayer (),
                           defendingCountry, attackerData);
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerDefendCountryEvent ()
  {
    defendingCountry = null;
    attackerData = null;
  }
}
