/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

public final class NullCountrySecondaryImages
        implements CountryImages <CountrySecondaryImageState, CountrySecondaryImage>
{
  private final ImmutableMap <CountrySecondaryImageState, CountrySecondaryImage> imageStatesToImages;

  public NullCountrySecondaryImages (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final ImmutableMap.Builder <CountrySecondaryImageState, CountrySecondaryImage> builder = ImmutableMap.builder ();

    for (final CountrySecondaryImageState state : CountrySecondaryImageState.values ())
    {
      builder.put (state, new CountrySecondaryImage (null, countryName, state));
    }

    imageStatesToImages = builder.build ();
  }

  @Override
  public int getAtlasIndex ()
  {
    return 0;
  }

  @Override
  public void hide (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void show (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public boolean has (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.containsKey (state);
  }

  @Override
  public boolean doesNotHave (final CountrySecondaryImageState state)
  {
    return ! has (state);
  }

  @Override
  public CountrySecondaryImage get (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.get (state);
  }

  @Override
  public ImmutableCollection <CountrySecondaryImage> getAll ()
  {
    return imageStatesToImages.values ();
  }
}
