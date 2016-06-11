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
import com.badlogic.gdx.scenes.scene2d.Group;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImageState;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

final class NullPlayMap implements PlayMap
{
  private final Group group = new Group ();

  @Override
  public boolean mouseMoved (final Vector2 mouseCoordinate)
  {
    Arguments.checkIsNotNull (mouseCoordinate, "mouseCoordinate");

    return false;
  }

  @Override
  public boolean touchDown (final Vector2 touchDownCoordinate, final int button)
  {
    Arguments.checkIsNotNull (touchDownCoordinate, "touchDownCoordinate");

    return false;
  }

  @Override
  public boolean touchUp (final Vector2 touchUpCoordinate)
  {
    Arguments.checkIsNotNull (touchUpCoordinate, "touchUpCoordinate");

    return false;
  }

  @Override
  public void setCountriesTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void randomizeCountryStates ()
  {
  }

  @Override
  public void randomizeCountryStatesUsingNRandomStates (final int n)
  {
    Arguments.checkLowerInclusiveBound (n, 1, "n");
    Arguments.checkUpperInclusiveBound (n, CountryPrimaryImageState.values ().length, "n");
  }

  @Override
  public void randomizeCountryStatesUsingOnly (final CountryPrimaryImageState... states)
  {
    Arguments.checkIsNotNull (states, "states");
    Arguments.checkHasNoNullElements (states, "states");
  }

  @Override
  public void reset ()
  {
  }

  @Override
  public void resetCountryStates ()
  {
  }

  @Override
  public void resetArmies ()
  {
  }

  @Override
  public void setArmies (final int armies, final String countryName)
  {
    Arguments.checkIsNotNegative (armies, "armies");
    Arguments.checkIsNotNull (countryName, "countryName");
  }

  @Override
  public void changeArmiesBy (final int deltaArmies, final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
  }

  @Override
  public void setCountryState (final String countryName, final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void setCountryState (final String countryName, final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public boolean existsCountryWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return false;
  }

  @Override
  public Country getCountryWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return Country.NULL_COUNTRY;
  }

  @Override
  public ImmutableSet <String> getAllCountryNames ()
  {
    return ImmutableSet.of ();
  }

  @Override
  public boolean primaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return false;
  }

  @Override
  public boolean secondaryImageStateOfCountryIs (final CountrySecondaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return false;
  }

  @Nullable
  @Override
  public CountryPrimaryImageState getPrimaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return null;
  }

  @Nullable
  @Override
  public CountrySecondaryImageState getSecondaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return null;
  }

  @Override
  public void disable ()
  {
  }

  @Override
  public void enable (final Vector2 currentMouseLocation)
  {
    Arguments.checkIsNotNull (currentMouseLocation, "currentMouseLocation");
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return MapMetadata.NULL_MAP_METADATA;
  }

  @Override
  public Actor asActor ()
  {
    return group;
  }
}
