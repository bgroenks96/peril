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

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
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

import net.engio.mbassy.bus.MBassador;

public final class DefaultPlayMap implements PlayMap
{
  private final Group group = new Group ();
  private final ImmutableMap <String, Country> countryNamesToCountries;
  private final PlayMapInputDetection inputDetection;
  private final HoveredTerritoryText hoveredTerritoryText;
  private final MapMetadata mapMetadata;
  private final MBassador <Event> eventBus;
  @Nullable
  private Country hoveredCountry = null;
  @Nullable
  private Country touchedCountry = null;
  private boolean isEnabled = true;

  public DefaultPlayMap (final ImmutableMap <String, Country> countryNamesToCountries,
                         final PlayMapInputDetection inputDetection,
                         final HoveredTerritoryText hoveredTerritoryText,
                         final Image backgroundImage,
                         final MapMetadata mapMetadata,
                         final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (countryNamesToCountries, "countryNamesToCountries");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToCountries, "countryNamesToCountries");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");
    Arguments.checkIsNotNull (hoveredTerritoryText, "hoveredTerritoryText");
    Arguments.checkIsNotNull (backgroundImage, "backgroundImage");
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.countryNamesToCountries = countryNamesToCountries;
    this.inputDetection = inputDetection;
    this.hoveredTerritoryText = hoveredTerritoryText;
    this.mapMetadata = mapMetadata;
    this.eventBus = eventBus;

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
  public boolean mouseMoved (final Vector2 mouseCoordinate)
  {
    Arguments.checkIsNotNull (mouseCoordinate, "mouseCoordinate");

    if (!isEnabled) return false;

    if (!existsCountryAt (mouseCoordinate))
    {
      if (touchedCountry != null)
      {
        touchedCountry.onTouchUp ();
        touchedCountry = null;
      }

      if (this.hoveredCountry != null)
      {
        this.hoveredCountry.onHoverEnd ();
        this.hoveredCountry = null;
      }

      return false;
    }

    final Country hoveredCountry = getCountryAt (mouseCoordinate);
    hoveredCountry.onHoverStart ();

    if (this.hoveredCountry != null
            && !this.hoveredCountry.asActor ().getName ().equals (hoveredCountry.asActor ().getName ()))
    {
      this.hoveredCountry.onHoverEnd ();
    }

    this.hoveredCountry = hoveredCountry;

    return true;
  }

  @Override
  public boolean touchDown (final Vector2 touchDownCoordinate, final int button)
  {
    Arguments.checkIsNotNull (touchDownCoordinate, "touchDownCoordinate");

    if (!isEnabled) return false;

    if (!existsCountryAt (touchDownCoordinate))
    {
      if (touchedCountry != null)
      {
        touchedCountry.onTouchUp ();
        touchedCountry = null;
      }

      return false;
    }

    final Country touchedDownCountry = getCountryAt (touchDownCoordinate);

    switch (button)
    {
      case Input.Buttons.LEFT:
      {
        touchedDownCountry.onTouchDown ();

        if (touchedCountry != null
                && !touchedCountry.asActor ().getName ().equals (touchedDownCountry.asActor ().getName ()))
        {
          touchedCountry.onTouchUp ();
        }

        touchedCountry = touchedDownCountry;

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public boolean touchUp (final Vector2 touchUpCoordinate)
  {
    Arguments.checkIsNotNull (touchUpCoordinate, "touchUpCoordinate");

    if (!isEnabled) return false;

    if (countryAtPointIsNot (touchUpCoordinate, touchedCountry))
    {
      if (touchedCountry != null)
      {
        touchedCountry.onTouchUp ();
        touchedCountry = null;
      }

      if (hoveredCountry != null)
      {
        hoveredCountry.onHoverEnd ();
        hoveredCountry = null;
      }
    }

    if (!existsCountryAt (touchUpCoordinate)) return false;

    final Country touchedUpCountry = getCountryAt (touchUpCoordinate);
    touchedUpCountry.onTouchUp ();

    hoveredCountry = touchedUpCountry;
    hoveredCountry.onHoverStart ();

    if (touchedCountry == null) return true;

    if (!touchedCountry.asActor ().getName ().equals (touchedUpCountry.asActor ().getName ()))
    {
      touchedCountry.onTouchUp ();
    }
    else
    {
      eventBus.publish (new SelectCountryEvent (touchedUpCountry.asActor ().getName ()));
    }

    touchedCountry = null;

    return true;
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
  public void setCountryState (final String countryName, final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    if (!existsCountryWithName (countryName)) return;

    getCountryWithName (countryName).changePrimaryStateTo (state);
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

    if (country == null)
    {
      throw new IllegalStateException (
              "Cannot find " + DefaultCountry.class.getSimpleName () + " with name [" + countryName + "].");
    }

    return country;
  }

  @Override
  public ImmutableSet <String> getAllCountryNames ()
  {
    return countryNamesToCountries.keySet ();
  }

  @Override
  public boolean currentPrimaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryWithName (countryName) && getCurrentPrimaryImageStateOf (countryName) == state;
  }

  @Override
  public boolean currentPrimaryImageStateOfCountryIsNot (final CountryPrimaryImageState state, final String countryName)
  {
    return !currentPrimaryImageStateOfCountryIs (state, countryName);
  }

  @Override
  public boolean currentSecondaryImageStateOfCountryIs (final CountrySecondaryImageState state,
                                                        final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryWithName (countryName) && getCurrentSecondaryImageStateOf (countryName) == state;
  }

  @Override
  public boolean currentSecondaryImageStateOfCountryIsNot (final CountrySecondaryImageState state,
                                                           final String countryName)
  {
    return !currentSecondaryImageStateOfCountryIs (state, countryName);
  }

  @Override
  @Nullable
  public CountryPrimaryImageState getCurrentPrimaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryWithName (countryName)) return null;

    return getCountryWithName (countryName).getCurrentPrimaryImageState ();
  }

  @Override
  @Nullable
  public CountrySecondaryImageState getCurrentSecondaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryWithName (countryName)) return null;

    return getCountryWithName (countryName).getCurrentSecondaryImageState ();
  }

  @Override
  public void disable ()
  {
    hoveredTerritoryText.setVisible (false);

    if (hoveredCountry != null) hoveredCountry.onHoverEnd ();
    if (touchedCountry != null) touchedCountry.onTouchUp ();

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

    mouseMoved (currentMouseLocation);
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
      throw new IllegalStateException (
              "Cannot find " + DefaultCountry.class.getSimpleName () + " at " + inputCoordinate + ".");

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
            && getCountryAt (inputCoordinate).asActor ().getName ().equals (country.asActor ().getName ());
  }

  private boolean countryAtPointIsNot (final Vector2 inputCoordinate, final Country country)
  {
    return !countryAtPointIs (inputCoordinate, country);
  }

  private ImmutableCollection <Country> getCountries ()
  {
    return countryNamesToCountries.values ();
  }
}
