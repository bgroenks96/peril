package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerAttackCountryResponseSuccessEvent implements PlayerResponseSuccessEvent
{
  private final BattleResultPacket result;

  public PlayerAttackCountryResponseSuccessEvent (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    this.result = result;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return getAttackingPlayer ();
  }

  @Override
  public String getPlayerName ()
  {
    return getPlayer ().getName ();
  }

  public BattleResultPacket getBattleResult ()
  {
    return result;
  }

  public PlayerPacket getAttackingPlayer ()
  {
    return result.getAttacker ().getPlayer ();
  }

  public PlayerPacket getDefendingPlayer ()
  {
    return result.getDefender ().getPlayer ();
  }

  public CountryPacket getAttackingCountry ()
  {
    return result.getAttacker ().getCountry ();
  }

  public CountryPacket getDefendingCountry ()
  {
    return result.getDefender ().getCountry ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Result: [{}]", getClass ().getSimpleName (), result);
  }

  @RequiredForNetworkSerialization
  private PlayerAttackCountryResponseSuccessEvent ()
  {
    result = null;
  }
}
