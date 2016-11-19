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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

public abstract class AbstractCountryImages <E extends Enum <E> & CountryImageState <E>, T extends CountryImage <E>>
        implements CountryImages <E, T>
{
  private final ImmutableMap <E, T> imageStatesToImages;
  private final int atlasIndex;

  protected AbstractCountryImages (final ImmutableMap <E, T> imageStatesToImages, final int atlasIndex)
  {
    Arguments.checkIsNotNull (imageStatesToImages, "imageStatesToImages");
    Arguments.checkHasNoNullKeysOrValues (imageStatesToImages, "imageStatesToImages");

    this.imageStatesToImages = imageStatesToImages;
    this.atlasIndex = atlasIndex;
  }

  @Override
  public final int getAtlasIndex ()
  {
    return atlasIndex;
  }

  @Override
  public final void hide (final E state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (imageStatesToImages.containsKey (state),
                               "Cannot find " + CountryImage.class.getSimpleName () + " for "
                                       + CountryImageState.class.getSimpleName () + " [" + state + "].");

    imageStatesToImages.get (state).setVisible (false);
  }

  @Override
  public final void show (final E state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (imageStatesToImages.containsKey (state),
                               "Cannot find " + CountryImage.class.getSimpleName () + " for "
                                       + state.getClass ().getSimpleName () + " [" + state + "].");

    imageStatesToImages.get (state).setVisible (true);
  }

  @Override
  public boolean has (final E state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.containsKey (state);
  }

  @Override
  public boolean doesNotHave (final E state)
  {
    Arguments.checkIsNotNull (state, "state");

    return !imageStatesToImages.containsKey (state);
  }

  @Override
  public final T get (final E state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (imageStatesToImages.containsKey (state),
                               "Cannot find " + CountryImage.class.getSimpleName () + " for "
                                       + CountryImageState.class.getSimpleName () + " [" + state + "].");

    return imageStatesToImages.get (state);
  }

  @Override
  public final ImmutableCollection <T> getAll ()
  {
    return imageStatesToImages.values ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Country Image States => Country Images: {} | Atlas Index: {}", getClass ()
            .getSimpleName (), Strings.toString (imageStatesToImages), atlasIndex);
  }
}
