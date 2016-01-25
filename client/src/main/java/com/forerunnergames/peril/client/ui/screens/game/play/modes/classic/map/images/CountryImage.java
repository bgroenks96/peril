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

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import javax.annotation.Nullable;

public interface CountryImage <E extends Enum <E> & CountryImageState <E>>
{
  String getCountryName ();

  CountryImageState <E> getState ();

  @Nullable
  Drawable getDrawable ();

  void setVisible (final boolean isVisible);

  void setPosition (final Vector2 position);

  void setScale (final Vector2 scaling);

  Actor asActor ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object o);

  @Override
  String toString ();
}
