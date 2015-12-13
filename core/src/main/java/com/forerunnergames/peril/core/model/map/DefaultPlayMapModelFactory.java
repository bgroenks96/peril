package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.map.continent.ContinentArmyModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.map.continent.DefaultContinentArmyModel;
import com.forerunnergames.peril.core.model.map.continent.DefaultContinentOwnerModel;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.map.country.DefaultCountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.DefaultCountryOwnerModel;
import com.forerunnergames.tools.common.Arguments;

public class DefaultPlayMapModelFactory implements PlayMapModelFactory
{
  private final GameRules rules;

  public DefaultPlayMapModelFactory (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  public PlayMapModel create (final CountryFactory countryFactory,
                              final CountryMapGraphModel countryMapGraphModel,
                              final ContinentFactory continentFactory,
                              final ContinentMapGraphModel continentMapGraphModel)
  {
    final CountryOwnerModel countryOwnerModel = new DefaultCountryOwnerModel (countryMapGraphModel, rules);
    final ContinentOwnerModel continentOwnerModel = new DefaultContinentOwnerModel (continentMapGraphModel,
            countryOwnerModel);
    final CountryArmyModel countryArmyModel = new DefaultCountryArmyModel (countryMapGraphModel, rules);
    final ContinentArmyModel continentArmyModel = new DefaultContinentArmyModel (continentMapGraphModel);
    return new DefaultPlayMapModel (countryMapGraphModel, countryOwnerModel, countryArmyModel, continentMapGraphModel,
            continentOwnerModel, continentArmyModel, rules);
  }
}
