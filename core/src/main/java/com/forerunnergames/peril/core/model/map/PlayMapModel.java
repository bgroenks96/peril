package com.forerunnergames.peril.core.model.map;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.map.continent.ContinentArmyModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentOwnerModel;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;

public interface PlayMapModel
{
  CountryOwnerModel getCountryOwnerModel ();

  CountryMapGraphModel getCountryMapGraphModel ();

  CountryArmyModel getCountryArmyModel ();

  ContinentOwnerModel getContinentOwnerModel ();

  ContinentMapGraphModel getContinentMapGraphModel ();

  ContinentArmyModel getContinentArmyModel ();

  GameRules getRules ();

  int getCountryCount ();

  int getContinentCount ();
}
