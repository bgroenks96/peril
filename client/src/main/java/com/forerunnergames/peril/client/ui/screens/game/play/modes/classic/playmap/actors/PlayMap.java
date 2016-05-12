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

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImageState;
import com.forerunnergames.peril.common.map.MapMetadata;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public interface PlayMap
{
  PlayMap NULL_PLAY_MAP = new NullPlayMap ();

  boolean mouseMoved (final Vector2 mouseCoordinate);

  boolean touchDown (final Vector2 touchDownCoordinate, final int button);

  boolean touchUp (final Vector2 touchUpCoordinate);

  void setCountriesTo (final CountryPrimaryImageState state);

  void randomizeCountryStates ();

  void randomizeCountryStatesUsingNRandomStates (final int n);

  void randomizeCountryStatesUsingOnly (final CountryPrimaryImageState... states);

  void reset ();

  void resetCountryStates ();

  void resetArmies ();

  void setArmies (final int armies, final String countryName);

  void changeArmiesBy (final int deltaArmies, final String countryName);

  void setCountryState (final String countryName, final CountryPrimaryImageState state);

  void setCountryState (final String countryName, final CountrySecondaryImageState state);

  boolean existsCountryWithName (final String countryName);

  Country getCountryWithName (final String countryName);

  ImmutableSet <String> getAllCountryNames ();

  boolean primaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName);

  boolean secondaryImageStateOfCountryIs (final CountrySecondaryImageState state, final String countryName);

  @Nullable
  CountryPrimaryImageState getPrimaryImageStateOf (final String countryName);

  @Nullable
  CountrySecondaryImageState getSecondaryImageStateOf (final String countryName);

  void disable ();

  void enable (final Vector2 currentMouseLocation);

  MapMetadata getMapMetadata ();

  Actor asActor ();
}
