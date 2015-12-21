package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.tools.common.id.Id;

public interface BattleActor
{
  Id getPlayerId ();

  Id getCountryId ();

  int getDieCount ();
}
