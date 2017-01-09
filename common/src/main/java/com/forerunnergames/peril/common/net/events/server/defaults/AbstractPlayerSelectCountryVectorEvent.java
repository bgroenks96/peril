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

package com.forerunnergames.peril.common.net.events.server.defaults;

import com.forerunnergames.peril.common.net.events.interfaces.PlayerSelectCountryVectorEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMultimap;

import java.util.Map;

public abstract class AbstractPlayerSelectCountryVectorEvent extends AbstractPlayerEvent
        implements PlayerSelectCountryVectorEvent
{
  private final ImmutableMultimap <CountryPacket, CountryPacket> validVectors;

  public AbstractPlayerSelectCountryVectorEvent (final PlayerPacket currentPlayer,
                                                 final ImmutableMultimap <CountryPacket, CountryPacket> validVectors)
  {
    super (currentPlayer);

    Arguments.checkIsNotNull (validVectors, "validVectors");

    this.validVectors = validVectors;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerSelectCountryVectorEvent ()
  {
    validVectors = null;
  }

  @Override
  public ImmutableMultimap <CountryPacket, CountryPacket> getValidVectors ()
  {
    return validVectors;
  }

  @Override
  public boolean isValidSourceCountry (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return FluentIterable.from (validVectors.keySet ()).firstMatch (new Predicate <CountryPacket> ()
    {
      @Override
      public boolean apply (final CountryPacket input)
      {
        return input.hasName (countryName);
      }
    }).isPresent ();
  }

  @Override
  public boolean isValidVector (final String fromCountryName, final String toCountryName)
  {
    Arguments.checkIsNotNull (fromCountryName, "fromCountryName");
    Arguments.checkIsNotNull (toCountryName, "toCountryName");

    return FluentIterable.from (validVectors.entries ())
            .firstMatch (new Predicate <Map.Entry <CountryPacket, CountryPacket>> ()
            {
              @Override
              public boolean apply (final Map.Entry <CountryPacket, CountryPacket> input)
              {
                return input.getKey ().hasName (fromCountryName) && input.getValue ().hasName (toCountryName);
              }
            }).isPresent ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ValidVectors: [{}]", super.toString (), validVectors);
  }
}
