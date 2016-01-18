package com.forerunnergames.peril.core.model.map.country;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.defaults.AbstractCountryStateChangeDeniedEvent.Reason;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.MutatorResult;
import com.forerunnergames.tools.common.MutatorResult.MutatorCallback;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

public final class DefaultCountryArmyModel implements CountryArmyModel
{
  private final CountryMapGraphModel countryMapGraphModel;
  private final GameRules rules;

  public DefaultCountryArmyModel (final CountryMapGraphModel countryMapGraphModel, final GameRules rules)
  {
    Arguments.checkIsNotNull (countryMapGraphModel, "countryMapGraphModel");
    Arguments.checkIsNotNull (rules, "rules");

    this.countryMapGraphModel = countryMapGraphModel;
    this.rules = rules;
  }

  @Override
  public MutatorResult <Reason> requestToAddArmiesToCountry (final Id countryId, final int armyCount)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    final Country country = countryMapGraphModel.modelCountryWith (countryId);
    if (country.getArmyCount () + armyCount > rules.getMaxArmiesOnCountry ())
    {
      return MutatorResult.failure (Reason.COUNTRY_ARMY_COUNT_OVERFLOW);
    }

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        country.addArmies (armyCount);
      }
    });
  }

  @Override
  public MutatorResult <Reason> requestToRemoveArmiesFromCountry (final Id countryId, final int armyCount)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Arguments.checkIsNotNegative (armyCount, "armyCount");

    final Country country = countryMapGraphModel.modelCountryWith (countryId);
    if (country.getArmyCount () - armyCount < rules.getMinArmiesOnCountry ())
    {
      return MutatorResult.failure (Reason.COUNTRY_ARMY_COUNT_UNDERFLOW);
    }

    country.removeArmies (armyCount);

    return MutatorResult.success (new MutatorCallback ()
    {
      @Override
      public void commitChanges ()
      {
        country.removeArmies (armyCount);
      }
    });
  }

  @Override
  public int getArmyCountFor (final Id countryId)
  {
    Arguments.checkIsNotNull (countryId, "countryId");
    Preconditions.checkIsTrue (countryMapGraphModel.existsCountryWith (countryId),
                               Strings.format ("No country with id [{}] exists.", countryId));

    return countryMapGraphModel.modelCountryWith (countryId).getArmyCount ();
  }

  @Override
  public boolean armyCountIs (final int armyCount, final Id countryId)
  {
    Arguments.checkIsNotNegative (armyCount, "minArmyCount");
    Arguments.checkIsNotNull (countryId, "countryId");

    return getArmyCountFor (countryId) == armyCount;
  }

  @Override
  public boolean armyCountIsAtLeast (final int minArmyCount, final Id countryId)
  {
    Arguments.checkIsNotNegative (minArmyCount, "minArmyCount");
    Arguments.checkIsNotNull (countryId, "countryId");

    return getArmyCountFor (countryId) >= minArmyCount;
  }
}
