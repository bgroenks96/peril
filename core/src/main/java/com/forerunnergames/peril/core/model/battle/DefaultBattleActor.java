package com.forerunnergames.peril.core.model.battle;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultBattleActor implements BattleActor
{
  private final Id playerId;
  private final Id countryId;
  private final int dieCount;

  public DefaultBattleActor (final Id playerId, final Id countryId, final int dieCount)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNegative (dieCount, "dieCount");

    this.playerId = playerId;
    this.countryId = countryId;
    this.dieCount = dieCount;
  }

  @Override
  public Id getPlayerId ()
  {
    return playerId;
  }

  @Override
  public Id getCountryId ()
  {
    return countryId;
  }

  @Override
  public int getDieCount ()
  {
    return dieCount;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}] | Country: [{}] | Die count: {}", getClass ().getSimpleName (), playerId,
                           countryId, dieCount);
  }
}
