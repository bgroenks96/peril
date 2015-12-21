package com.forerunnergames.peril.common.net.packets.battle;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;

public interface BattleActorPacket
{
  PlayerPacket getPlayer ();

  CountryPacket getCountry ();

  int getDieCount ();
}
