/*
 * Copyright © 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.core.model.playmap.continent;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.io.AbstractTerritoryGraphDataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.graph.Graph;

public final class ContinentGraphDataLoader extends AbstractTerritoryGraphDataLoader <Continent, ContinentGraphModel>
{
  private final CountryGraphModel countryGraphModel;

  public ContinentGraphDataLoader (final StreamParserFactory streamParserFactory,
                                   final ContinentFactory data,
                                   final CountryGraphModel countryGraphModel)
  {
    super (streamParserFactory, data.getContinents ());

    Arguments.checkIsNotNull (countryGraphModel, "countryGraphModel");

    this.countryGraphModel = countryGraphModel;
  }

  @Override
  protected ContinentGraphModel createGraphModel (final Graph <Continent> graph)
  {
    Arguments.checkIsNotNull (graph, "graph");

    return new ContinentGraphModel (graph, countryGraphModel);
  }
}
