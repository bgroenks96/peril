package com.forerunnergames.peril.common.net.events.server.success;

import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;

public final class PlayerAttackCountrySuccessEvent implements PlayerSuccessEvent
{
  private final BattleResultPacket result;

  public PlayerAttackCountrySuccessEvent (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    this.result = result;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return getAttackingPlayer ();
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
}
