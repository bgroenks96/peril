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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

public final class NullCountryPrimaryImages implements CountryImages <CountryPrimaryImageState, CountryPrimaryImage>
{
  private final ImmutableMap <CountryPrimaryImageState, CountryPrimaryImage> imageStatesToImages;

  public NullCountryPrimaryImages (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final ImmutableMap.Builder <CountryPrimaryImageState, CountryPrimaryImage> builder = ImmutableMap.builder ();

    for (final CountryPrimaryImageState state : CountryPrimaryImageState.values ())
    {
      builder.put (state, new CountryPrimaryImage (null, countryName, state));
    }

    imageStatesToImages = builder.build ();
  }

  @Override
  public int getAtlasIndex ()
  {
    return 0;
  }

  @Override
  public void hide (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void show (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public boolean has (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.containsKey (state);
  }

  @Override
  public boolean doesNotHave (final CountryPrimaryImageState state)
  {
    return !has (state);
  }

  @Override
  public CountryPrimaryImage get (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.get (state);
  }

  @Override
  public ImmutableCollection <CountryPrimaryImage> getAll ()
  {
    return imageStatesToImages.values ();
  }
}
