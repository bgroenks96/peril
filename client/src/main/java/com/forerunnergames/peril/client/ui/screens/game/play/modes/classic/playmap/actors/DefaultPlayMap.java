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
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountrySecondaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.listeners.PlayMapInputListener;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public final class DefaultPlayMap implements PlayMap
{
  private final Group group = new Group ();
  private final ImmutableMap <String, Country> countryNamesToCountries;
  private final PlayMapInputDetection inputDetection;
  private final HoveredTerritoryText hoveredTerritoryText;
  private final MapMetadata mapMetadata;
  private List <PlayMapInputListener> listeners = new ArrayList <> ();
  @Nullable
  private Country hoveredCountry = null;
  @Nullable
  private Country leftClickedCountry = null;
  @Nullable
  private Country rightClickedCountry = null;
  private boolean isEnabled = true;

  public DefaultPlayMap (final ImmutableMap <String, Country> countryNamesToCountries,
                         final PlayMapInputDetection inputDetection,
                         final HoveredTerritoryText hoveredTerritoryText,
                         final Image backgroundImage,
                         final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (countryNamesToCountries, "countryNamesToCountries");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToCountries, "countryNamesToCountries");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");
    Arguments.checkIsNotNull (hoveredTerritoryText, "hoveredTerritoryText");
    Arguments.checkIsNotNull (backgroundImage, "backgroundImage");
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    this.countryNamesToCountries = countryNamesToCountries;
    this.inputDetection = inputDetection;
    this.hoveredTerritoryText = hoveredTerritoryText;
    this.mapMetadata = mapMetadata;

    group.setTransform (false);

    backgroundImage.setSize (PlayMapSettings.ACTUAL_WIDTH, PlayMapSettings.ACTUAL_HEIGHT);

    group.addActor (backgroundImage);

    final List <Country> countriesSortedByAtlasIndex = new ArrayList <> (countryNamesToCountries.values ());

    Collections.sort (countriesSortedByAtlasIndex, new Comparator <Country> ()
    {
      @Override
      public int compare (final Country o1, final Country o2)
      {
        Arguments.checkIsNotNull (o1, "o1");
        Arguments.checkIsNotNull (o2, "o2");

        return Integer.compare (o1.getAtlasIndex (), o2.getAtlasIndex ());
      }
    });

    for (final Country country : countriesSortedByAtlasIndex)
    {
      group.addActor (country.asActor ());
    }

    for (final Country country : countriesSortedByAtlasIndex)
    {
      group.addActor (country.getArmyText ().asActor ());
    }

    group.addActor (hoveredTerritoryText);
  }

  @Override
  public boolean onMouseMoved (final Vector2 coordinate)
  {
    Arguments.checkIsNotNull (coordinate, "coordinate");

    if (!isEnabled) return false;

    if (!existsCountryAt (coordinate))
    {
      if (leftClickedCountry != null)
      {
        leftClickedCountry.onLeftButtonUp ();
        leftClickedCountry = null;
      }

      if (rightClickedCountry != null)
      {
        rightClickedCountry.onRightButtonUp ();
        rightClickedCountry = null;
      }

      if (hoveredCountry != null)
      {
        hoveredCountry.onHoverEnd ();
        hoveredCountry = null;
      }

      return false;
    }

    final Country country = getCountryAt (coordinate);
    country.onHoverStart ();

    if (hoveredCountry != null && !hoveredCountry.hasName (country.getName ())) hoveredCountry.onHoverEnd ();

    hoveredCountry = country;

    return true;
  }

  @Override
  public boolean onLeftButtonDown (final Vector2 coordinate)
  {
    Arguments.checkIsNotNull (coordinate, "coordinate");

    if (!isEnabled) return false;

    if (!existsCountryAt (coordinate))
    {
      if (leftClickedCountry != null)
      {
        leftClickedCountry.onLeftButtonUp ();
        leftClickedCountry = null;
      }

      return false;
    }

    final Country country = getCountryAt (coordinate);

    country.onLeftButtonDown ();

    if (leftClickedCountry != null && !leftClickedCountry.hasName (country.getName ()))
    {
      leftClickedCountry.onLeftButtonUp ();
    }

    leftClickedCountry = country;

    return true;
  }

  @Override
  public boolean onLeftButtonUp (final Vector2 coordinate)
  {
    Arguments.checkIsNotNull (coordinate, "touchUpCoordinate");

    if (!isEnabled) return false;

    if (countryAtPointIsNot (coordinate, leftClickedCountry))
    {
      if (leftClickedCountry != null)
      {
        leftClickedCountry.onLeftButtonUp ();
        leftClickedCountry = null;
      }

      if (hoveredCountry != null)
      {
        hoveredCountry.onHoverEnd ();
        hoveredCountry = null;
      }
    }

    if (!existsCountryAt (coordinate))
    {
      for (final PlayMapInputListener listener : listeners)
      {
        listener.onNonCountryLeftClicked (coordinate.x, coordinate.y);
      }

      return false;
    }

    final Country country = getCountryAt (coordinate);
    country.onLeftButtonUp ();

    hoveredCountry = country;
    hoveredCountry.onHoverStart ();

    if (leftClickedCountry == null) return true;

    if (leftClickedCountry.hasName (country.getName ()))
    {
      for (final PlayMapInputListener listener : listeners)
      {
        listener.onCountryLeftClicked (country.getName (), coordinate.x, coordinate.y);
      }
    }
    else
    {
      leftClickedCountry.onLeftButtonUp ();
    }

    leftClickedCountry = null;

    return true;
  }

  @Override
  public boolean onRightButtonDown (final Vector2 coordinate)
  {
    Arguments.checkIsNotNull (coordinate, "coordinate");

    if (!isEnabled) return false;

    if (!existsCountryAt (coordinate))
    {
      if (rightClickedCountry != null)
      {
        rightClickedCountry.onRightButtonUp ();
        rightClickedCountry = null;
      }

      return false;
    }

    final Country country = getCountryAt (coordinate);

    country.onRightButtonDown ();

    if (rightClickedCountry != null && !rightClickedCountry.hasName (country.getName ()))
    {
      rightClickedCountry.onRightButtonUp ();
    }

    rightClickedCountry = country;

    return true;
  }

  @Override
  public boolean onRightButtonUp (final Vector2 coordinate)
  {
    Arguments.checkIsNotNull (coordinate, "coordinate");

    if (!isEnabled) return false;

    if (countryAtPointIsNot (coordinate, rightClickedCountry))
    {
      if (rightClickedCountry != null)
      {
        rightClickedCountry.onRightButtonUp ();
        rightClickedCountry = null;
      }

      if (hoveredCountry != null)
      {
        hoveredCountry.onHoverEnd ();
        hoveredCountry = null;
      }
    }

    if (!existsCountryAt (coordinate))
    {
      for (final PlayMapInputListener listener : listeners)
      {
        listener.onNonCountryRightClicked (coordinate.x, coordinate.y);
      }

      return false;
    }

    final Country country = getCountryAt (coordinate);
    country.onRightButtonUp ();

    hoveredCountry = country;
    hoveredCountry.onHoverStart ();

    if (rightClickedCountry == null) return true;

    if (rightClickedCountry.hasName (country.getName ()))
    {
      for (final PlayMapInputListener listener : listeners)
      {
        listener.onCountryRightClicked (country.getName (), coordinate.x, coordinate.y);
      }
    }
    else
    {
      rightClickedCountry.onRightButtonUp ();
    }

    rightClickedCountry = null;

    return true;
  }

  @Override
  public void addListener (final PlayMapInputListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final List <PlayMapInputListener> listenersCopy = new ArrayList <> (listeners);
    listenersCopy.add (listener);
    listeners = listenersCopy;
  }

  @Override
  public void removeListener (final PlayMapInputListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    final List <PlayMapInputListener> listenersCopy = new ArrayList <> (listeners);
    listenersCopy.remove (listener);
    listeners = listenersCopy;
  }

  @Override
  public void setCountriesTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    for (final Country country : getCountries ())
    {
      country.changePrimaryStateTo (state);
    }
  }

  @Override
  public void randomizeCountryStates ()
  {
    for (final Country country : getCountries ())
    {
      country.changePrimaryStateRandomly ();
    }
  }

  @Override
  public void randomizeCountryStatesUsingNRandomStates (final int n)
  {
    Arguments.checkLowerInclusiveBound (n, 1, "n");
    Arguments.checkUpperInclusiveBound (n, CountryPrimaryImageState.values ().length, "n");

    final ImmutableSet.Builder <CountryPrimaryImageState> nStatesBuilder = ImmutableSet.builder ();
    final Set <CountryPrimaryImageState> states = EnumSet.allOf (CountryPrimaryImageState.class);

    for (int i = 0; i < n; ++i)
    {
      final CountryPrimaryImageState randomState = Randomness.getRandomElementFrom (states);
      nStatesBuilder.add (randomState);
      states.remove (randomState);
    }

    randomizeCountryStatesUsingOnly (nStatesBuilder.build ());
  }

  @Override
  public void randomizeCountryStatesUsingOnly (final CountryPrimaryImageState... states)
  {
    Arguments.checkIsNotNull (states, "states");
    Arguments.checkHasNoNullElements (states, "states");

    randomizeCountryStatesUsingOnly (Arrays.asList (states));
  }

  @Override
  public void reset ()
  {
    resetCountryStates ();
    resetArmies ();
  }

  @Override
  public void resetCountryStates ()
  {
    setCountriesTo (CountryPrimaryImageState.UNOWNED);
  }

  @Override
  public void resetArmies ()
  {
    setAllArmiesTo (0);
  }

  @Override
  public void setArmies (final int armies, final String countryName)
  {
    Arguments.checkIsNotNegative (armies, "armies");
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryWithName (countryName)) return;

    getCountryWithName (countryName).setArmies (armies);
  }

  @Override
  public void changeArmiesBy (final int deltaArmies, final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryWithName (countryName)) return;

    getCountryWithName (countryName).changeArmiesBy (deltaArmies);
  }

  @Override
  public boolean countryArmyCountIs (final int armies, final String countryName)
  {
    Arguments.checkIsNotNegative (armies, "armies");
    Arguments.checkIsNotNull (countryName, "countryName");

    return getCountryWithName (countryName).armyCountIs (armies);
  }

  @Override
  public void setCountryState (final String countryName, final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    if (!existsCountryWithName (countryName)) return;

    getCountryWithName (countryName).changePrimaryStateTo (state);
  }

  @Override
  public void setCountryState (final String countryName, final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    if (!existsCountryWithName (countryName)) return;

    getCountryWithName (countryName).changeSecondaryStateTo (state);
  }

  @Override
  public boolean existsCountryWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToCountries.containsKey (countryName);
  }

  @Override
  public Country getCountryWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final Country country = countryNamesToCountries.get (countryName);

    return country == null ? Country.NULL : country;
  }

  @Override
  public ImmutableSet <String> getAllCountryNames ()
  {
    return countryNamesToCountries.keySet ();
  }

  @Override
  public boolean primaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryWithName (countryName) && getPrimaryImageStateOf (countryName) == state;
  }

  @Override
  public boolean secondaryImageStateOfCountryIs (final CountrySecondaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryWithName (countryName) && getSecondaryImageStateOf (countryName) == state;
  }

  @Override
  @Nullable
  public CountryPrimaryImageState getPrimaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryWithName (countryName)) return null;

    return getCountryWithName (countryName).getPrimaryImageState ();
  }

  @Override
  @Nullable
  public CountrySecondaryImageState getSecondaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryWithName (countryName)) return null;

    return getCountryWithName (countryName).getSecondaryImageState ();
  }

  @Override
  public void disable ()
  {
    hoveredTerritoryText.setVisible (false);

    if (hoveredCountry != null) hoveredCountry.onHoverEnd ();
    if (leftClickedCountry != null) leftClickedCountry.onLeftButtonUp ();
    if (rightClickedCountry != null) rightClickedCountry.onRightButtonUp ();

    for (final Country country : getCountries ())
    {
      country.disable ();
    }

    isEnabled = false;
  }

  @Override
  public void enable (final Vector2 currentMouseLocation)
  {
    Arguments.checkIsNotNull (currentMouseLocation, "currentMouseLocation");

    hoveredTerritoryText.setVisible (true);

    for (final Country country : getCountries ())
    {
      country.enable ();
    }

    isEnabled = true;

    onMouseMoved (currentMouseLocation);
  }

  @Override
  public MapMetadata getMapMetadata ()
  {
    return mapMetadata;
  }

  @Override
  public Actor asActor ()
  {
    return group;
  }

  private Country getCountryAt (final Vector2 inputCoordinate)
  {
    final Country country = countryNamesToCountries.get (inputDetection.getCountryNameAt (inputCoordinate));

    if (country == null)
    {
      throw new IllegalStateException ("Cannot find " + DefaultCountry.class.getSimpleName () + " at "
              + inputCoordinate + ".");

    }

    return country;
  }

  private void randomizeCountryStatesUsingOnly (final Collection <CountryPrimaryImageState> states)
  {
    CountryPrimaryImageState randomState;

    for (final Country country : getCountries ())
    {
      randomState = Randomness.getRandomElementFrom (states);

      country.changePrimaryStateTo (randomState);
    }
  }

  private void setAllArmiesTo (final int armies)
  {
    for (final Country country : getCountries ())
    {
      country.setArmies (armies);
    }
  }

  private boolean existsCountryAt (final Vector2 inputCoordinate)
  {
    return countryNamesToCountries.containsKey (inputDetection.getCountryNameAt (inputCoordinate));
  }

  private boolean countryAtPointIs (final Vector2 inputCoordinate, @Nullable final Country country)
  {
    return country != null && existsCountryAt (inputCoordinate)
            && getCountryAt (inputCoordinate).hasName (country.getName ());
  }

  private boolean countryAtPointIsNot (final Vector2 inputCoordinate, @Nullable final Country country)
  {
    return !countryAtPointIs (inputCoordinate, country);
  }

  private ImmutableCollection <Country> getCountries ()
  {
    return countryNamesToCountries.values ();
  }
}
