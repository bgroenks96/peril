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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImageState;

import javax.annotation.Nullable;

public interface Country
{
  Country NULL_COUNTRY = new NullCountry ();

  CountryPrimaryImageState getCurrentPrimaryImageState ();

  CountrySecondaryImageState getCurrentSecondaryImageState ();

  void changePrimaryStateRandomly ();

  void changePrimaryStateTo (final CountryPrimaryImageState state);

  void changeSecondaryStateTo (final CountrySecondaryImageState state);

  void nextPrimaryState ();

  void onHoverStart ();

  void onHoverEnd ();

  void onTouchDown ();

  void onTouchUp ();

  @Nullable
  Drawable getCurrentPrimaryDrawable ();

  Vector2 getReferenceDestination ();

  Vector2 getReferenceTextUpperLeft ();

  float getReferenceWidth ();

  float getReferenceHeight ();

  int getArmies ();

  void setArmies (final int armies);

  void incrementArmies ();

  void decrementArmies ();

  void changeArmiesBy (final int deltaArmies);

  void disable ();

  void enable ();

  int getAtlasIndex ();

  CountryArmyText getArmyText ();

  String getName ();

  void setName (final String name);

  boolean hasName (final String name);

  Actor asActor ();
}
