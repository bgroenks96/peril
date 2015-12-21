package com.forerunnergames.peril.common.net.packets.defaults;

import com.forerunnergames.peril.common.net.packets.battle.BattleActorPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class DefaultBattleActorPacket implements BattleActorPacket
{
  private final PlayerPacket player;
  private final CountryPacket country;
  private final int dieCount;

  public DefaultBattleActorPacket (final PlayerPacket player, final CountryPacket country, final int dieCount)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (country, "country");
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    this.player = player;
    this.country = country;
    this.dieCount = dieCount;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public CountryPacket getCountry ()
  {
    return country;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Country: [{}] | Die count: {}", player, country, dieCount);
  }

  @RequiredForNetworkSerialization
  private DefaultBattleActorPacket ()
  {
    player = null;
    country = null;
    dieCount = 0;
  }
}
