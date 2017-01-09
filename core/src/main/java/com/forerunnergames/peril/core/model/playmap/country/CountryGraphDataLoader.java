/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.playmap.country;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.peril.core.model.playmap.io.AbstractTerritoryGraphDataLoader;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.graph.Graph;

public final class CountryGraphDataLoader extends AbstractTerritoryGraphDataLoader <Country, CountryGraphModel>
{
  public CountryGraphDataLoader (final StreamParserFactory streamParserFactory, final CountryFactory factory)
  {
    super (streamParserFactory, factory.getCountries ());
  }

  @Override
  protected CountryGraphModel createGraphModel (final Graph <Country> graph)
  {
    Arguments.checkIsNotNull (graph, "graph");

    return new CountryGraphModel (graph);
  }
}
