package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.tools.common.id.Id;

public interface AttackOrder
{
  Id getId ();

  Id getPlayerId ();

  Id getSourceCountry ();

  Id getTargetCountry ();

  int getDieCount ();
}
