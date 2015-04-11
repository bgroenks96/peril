package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

public final class PlayMapActor extends Group
{
  private final ImmutableMap <CountryName, CountryActor> countryNamesToActors;
  private final PlayMapInputDetection inputDetection;
  private CountryActor hoveredCountryActor;
  private CountryActor touchedCountryActor;

  public PlayMapActor (final ImmutableMap <CountryName, CountryActor> countryNamesToActors,
                       final PlayMapInputDetection inputDetection)
  {
    Arguments.checkIsNotNull (countryNamesToActors, "countryNamesToActors");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToActors, "countryNamesToActors");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");

    this.countryNamesToActors = countryNamesToActors;
    this.inputDetection = inputDetection;

    for (final CountryActor countryActor : countryNamesToActors.values ())
    {
      addActor (countryActor);
    }
  }

  public boolean existsCountryActorAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return countryNamesToActors.containsKey (inputDetection.getCountryNameAt (inputCoordinate, screenSize));
  }

  public boolean countryActorAtPointIs (final Point2D inputCoordinate,
                                        final Size2D screenSize,
                                        @Nullable final CountryActor countryActor)
  {
    return countryActor != null && existsCountryActorAt (inputCoordinate, screenSize)
            && getCountryActorAt (inputCoordinate, screenSize).getName ().equals (countryActor.getName ());
  }

  public boolean countryActorAtPointIsNot (final Point2D inputCoordinate,
                                           final Size2D screenSize,
                                           final CountryActor countryActor)
  {
    return !countryActorAtPointIs (inputCoordinate, screenSize, countryActor);
  }

  public CountryActor getCountryActorAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final CountryActor countryActor = countryNamesToActors.get (inputDetection.getCountryNameAt (inputCoordinate,
                                                                                                 screenSize));

    if (countryActor == null)
    {
      throw new IllegalStateException ("Cannot find " + CountryActor.class.getSimpleName () + " at " + inputCoordinate
              + ".");

    }

    return countryActor;
  }

  public CountryName getCountryNameAt (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (!existsCountryActorAt (inputCoordinate, screenSize)) return new CountryName ("");

    return new CountryName (getCountryActorAt (inputCoordinate, screenSize).getName ());
  }

  public CountryImageState getHoveredCountryState ()
  {
    return hoveredCountryActor != null ? hoveredCountryActor.getCurrentImageState () : CountryImageState.UNOWNED;
  }

  public boolean mouseMoved (final Point2D mouseCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (mouseCoordinate, "mouseCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (!existsCountryActorAt (mouseCoordinate, screenSize))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      if (hoveredCountryActor != null)
      {
        hoveredCountryActor.onHoverEnd ();
        hoveredCountryActor = null;
      }

      return false;
    }

    final CountryActor hoveredCountryActor = getCountryActorAt (mouseCoordinate, screenSize);
    hoveredCountryActor.onHoverStart ();

    if (this.hoveredCountryActor != null
            && !this.hoveredCountryActor.getName ().equals (hoveredCountryActor.getName ()))
    {
      this.hoveredCountryActor.onHoverEnd ();
    }

    this.hoveredCountryActor = hoveredCountryActor;

    return true;
  }

  public boolean touchDown (final Point2D touchDownCoordinate, final int button, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (touchDownCoordinate, "touchDownCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (!existsCountryActorAt (touchDownCoordinate, screenSize))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      return false;
    }

    final CountryActor touchedDownCountryActor = getCountryActorAt (touchDownCoordinate, screenSize);

    switch (button)
    {
      case Input.Buttons.LEFT:
      {
        touchedDownCountryActor.onTouchDown ();
        touchedDownCountryActor.changeStateRandomly ();
        touchedDownCountryActor.incrementArmies ();

        if (touchedCountryActor != null && !touchedCountryActor.getName ().equals (touchedDownCountryActor.getName ()))
        {
          touchedCountryActor.onTouchUp ();
        }

        touchedCountryActor = touchedDownCountryActor;

        return true;
      }
      case Input.Buttons.RIGHT:
      {
        touchedDownCountryActor.nextState ();
        touchedDownCountryActor.decrementArmies ();

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  public boolean touchUp (final Point2D touchUpCoordinate, final int button, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (touchUpCoordinate, "touchUpCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    if (countryActorAtPointIsNot (touchUpCoordinate, screenSize, touchedCountryActor))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      if (hoveredCountryActor != null)
      {
        hoveredCountryActor.onHoverEnd ();
        hoveredCountryActor = null;
      }
    }

    if (!existsCountryActorAt (touchUpCoordinate, screenSize)) return false;

    final CountryActor touchedUpCountryActor = getCountryActorAt (touchUpCoordinate, screenSize);
    touchedUpCountryActor.onTouchUp ();

    hoveredCountryActor = touchedUpCountryActor;
    hoveredCountryActor.onHoverStart ();

    if (touchedCountryActor != null && !touchedCountryActor.getName ().equals (touchedUpCountryActor.getName ()))
    {
      touchedCountryActor.onTouchUp ();
    }

    touchedCountryActor = null;

    return true;
  }

  public boolean existsCountryActorWithName (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToActors.containsKey (countryName);
  }

  public CountryActor getCountryActorWithName (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final CountryActor countryActor = countryNamesToActors.get (countryName);

    if (countryActor == null)
    {
      throw new IllegalStateException ("Cannot find " + CountryActor.class.getSimpleName () + " with name ["
              + countryName + "].");
    }

    return countryActor;
  }

  public void setClassicCountryStates ()
  {
    // North America
    setCountryState ("Alaska", CountryImageState.GOLD);
    setCountryState ("Northwest Territory", CountryImageState.GOLD);
    setCountryState ("Greenland", CountryImageState.GOLD);
    setCountryState ("Alberta", CountryImageState.GOLD);
    setCountryState ("Ontario", CountryImageState.GOLD);
    setCountryState ("Quebec", CountryImageState.GOLD);
    setCountryState ("Western United States", CountryImageState.GOLD);
    setCountryState ("Eastern United States", CountryImageState.GOLD);
    setCountryState ("Central America", CountryImageState.GOLD);

    // South America
    setCountryState ("Venezuela", CountryImageState.RED);
    setCountryState ("Peru", CountryImageState.RED);
    setCountryState ("Brazil", CountryImageState.RED);
    setCountryState ("Argentina", CountryImageState.RED);

    // Europe
    setCountryState ("Iceland", CountryImageState.BLUE);
    setCountryState ("Scandinavia", CountryImageState.BLUE);
    setCountryState ("Great Britain", CountryImageState.BLUE);
    setCountryState ("Northern Europe", CountryImageState.BLUE);
    setCountryState ("Ukraine", CountryImageState.BLUE);
    setCountryState ("Western Europe", CountryImageState.BLUE);
    setCountryState ("Southern Europe", CountryImageState.BLUE);

    // Asia
    setCountryState ("Ural", CountryImageState.GREEN);
    setCountryState ("Siberia", CountryImageState.GREEN);
    setCountryState ("Yakutsk", CountryImageState.GREEN);
    setCountryState ("Kamchatka", CountryImageState.GREEN);
    setCountryState ("Afghanistan", CountryImageState.GREEN);
    setCountryState ("Irkutsk", CountryImageState.GREEN);
    setCountryState ("Mongolia", CountryImageState.GREEN);
    setCountryState ("Japan", CountryImageState.GREEN);
    setCountryState ("Middle East", CountryImageState.GREEN);
    setCountryState ("India", CountryImageState.GREEN);
    setCountryState ("China", CountryImageState.GREEN);
    setCountryState ("Siam", CountryImageState.GREEN);

    // Africa
    setCountryState ("North Africa", CountryImageState.BROWN);
    setCountryState ("Egypt", CountryImageState.BROWN);
    setCountryState ("Congo", CountryImageState.BROWN);
    setCountryState ("East Africa", CountryImageState.BROWN);
    setCountryState ("South Africa", CountryImageState.BROWN);
    setCountryState ("Madagascar", CountryImageState.BROWN);

    // Australia
    setCountryState ("Indonesia", CountryImageState.PINK);
    setCountryState ("New Guinea", CountryImageState.PINK);
    setCountryState ("Western Australia", CountryImageState.PINK);
    setCountryState ("Eastern Australia", CountryImageState.PINK);

    // Not used in classic mode
    setCountryState ("Hawaii", CountryImageState.DISABLED);
    setCountryState ("Caribbean Islands", CountryImageState.DISABLED);
    setCountryState ("Falkland Islands", CountryImageState.DISABLED);
    setCountryState ("Svalbard", CountryImageState.DISABLED);
    setCountryState ("Philippines", CountryImageState.DISABLED);
    setCountryState ("New Zealand", CountryImageState.DISABLED);
    setCountryState ("Antarctica", CountryImageState.DISABLED);
  }

  public void setCountriesTo (final CountryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changeStateTo (state);
    }
  }

  public void setCountryState (final String countryName, final CountryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    final CountryName name = new CountryName (countryName);

    if (countryNamesToActors.containsKey (name)) countryNamesToActors.get (name).changeStateTo (state);
  }

  public void randomizeCountryStates ()
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changeStateRandomly ();
    }
  }

  public void randomizeCountryStatesUsingNRandomStates (final int n)
  {
    Arguments.checkLowerInclusiveBound (n, 1, "n");
    Arguments.checkUpperInclusiveBound (n, CountryImageState.values ().length, "n");

    final ImmutableSet.Builder <CountryImageState> nStatesBuilder = ImmutableSet.builder ();
    final Set <CountryImageState> states = new HashSet <> (Arrays.asList (CountryImageState.values ()));

    for (int i = 0; i < n; ++i)
    {
      final CountryImageState randomState = Randomness.getRandomElementFrom (states);
      nStatesBuilder.add (randomState);
      states.remove (randomState);
    }

    randomizeCountryStatesUsingOnly (nStatesBuilder.build ());
  }

  public void randomizeCountryStatesUsingOnly (final Collection <CountryImageState> states)
  {
    Arguments.checkIsNotNullOrEmpty (states, "states");
    Arguments.checkHasNoNullElements (states, "states");

    CountryImageState randomState;

    for (final CountryActor countryActor : getCountryActors ())
    {
      randomState = Randomness.getRandomElementFrom (states);

      countryActor.changeStateTo (randomState);
    }
  }

  public void randomizeCountryStatesUsingOnly (final CountryImageState... states)
  {
    randomizeCountryStatesUsingOnly (Arrays.asList (states));
  }

  public void resetArmies ()
  {
    setAllArmiesTo (0);
  }

  public void setAllArmiesTo (final int armies)
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.setArmies (armies);
    }
  }

  public void incrementArmies (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    getCountryActorWithName (countryName).incrementArmies ();
  }

  public void decrementArmies (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    getCountryActorWithName (countryName).decrementArmies ();
  }

  public void setArmiesTo (final int armies, final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNegative (armies, "armies");

    getCountryActorWithName (countryName).setArmies (armies);
  }

  public void changeArmiesBy (final int deltaArmies, final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    getCountryActorWithName (countryName).changeArmiesBy (deltaArmies);
  }

  private ImmutableCollection <CountryActor> getCountryActors ()
  {
    return countryNamesToActors.values ();
  }
}
