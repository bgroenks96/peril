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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;

public interface CountryArmyText
{
  CountryArmyText NULL_COUNTRY_ARMY_TEXT = new NullCountryArmyText ();

  void setCircleTopLeft (final Vector2 topLeft);

  void setCircleSize (final Vector2 size);

  void changeArmiesTo (final int armies);

  void incrementArmies ();

  void decrementArmies ();

  void changeArmiesBy (final int deltaArmies);

  void onPrimaryStateChange (final CountryPrimaryImageState state);

  int getArmies ();

  void setFont (final BitmapFont font);

  void setName (final String name);

  void setVisible (final boolean isVisible);

  Actor asActor ();
}
