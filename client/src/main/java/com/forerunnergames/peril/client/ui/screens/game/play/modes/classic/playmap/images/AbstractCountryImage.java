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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

public abstract class AbstractCountryImage <E extends Enum <E> & CountryImageState <E>> implements CountryImage <E>
{
  private final String countryName;
  private final CountryImageState <E> state;
  private final Image image;

  protected AbstractCountryImage (@Nullable final Drawable drawable,
                                  final String countryName,
                                  final CountryImageState <E> state)
  {
    image = new Image (drawable);

    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    this.countryName = countryName;
    this.state = state;

    image.setName (countryName + " " + Strings.toProperCase (state.getEnumName ()));
  }

  @Override
  public String getCountryName ()
  {
    return countryName;
  }

  @Override
  public CountryImageState <E> getState ()
  {
    return state;
  }

  @Nullable
  @Override
  public Drawable getDrawable ()
  {
    return image.getDrawable ();
  }

  @Override
  public void setVisible (final boolean isVisible)
  {
    image.setVisible (isVisible);
  }

  @Override
  public void setPosition (final Vector2 position)
  {
    Arguments.checkIsNotNull (position, "position");

    image.setPosition (position.x, position.y);
  }

  @Override
  public void setScale (final Vector2 scaling)
  {
    Arguments.checkIsNotNull (scaling, "scaling");

    image.setScale (scaling.x, scaling.y);
  }

  @Override
  public Actor asActor ()
  {
    return image;
  }

  @Override
  public final int hashCode ()
  {
    int result = countryName.hashCode ();
    result = 31 * result + state.hashCode ();
    return result;
  }

  @Override
  public final boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;
    final CountryImage <?> countryImage = (CountryImage <?>) obj;
    return countryName.equals (countryImage.getCountryName ()) && state == countryImage.getState ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Name: {} | State: {} | Image: {} | Drawable: {}", getClass ().getSimpleName (),
                           countryName, state, image, getDrawable ());
  }
}
