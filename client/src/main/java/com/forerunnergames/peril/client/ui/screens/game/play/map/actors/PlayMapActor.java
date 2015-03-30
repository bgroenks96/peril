package com.forerunnergames.peril.client.ui.screens.game.play.map.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.map.sprites.CountrySpriteState;
import com.forerunnergames.peril.client.ui.screens.game.play.map.input.PlayMapInputDetection;
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

public final class PlayMapActor extends Actor
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
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    for (final CountryActor actor : countryNamesToActors.values ())
    {
      actor.draw (batch, parentAlpha);
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

  public CountrySpriteState getHoveredCountryState ()
  {
    return hoveredCountryActor != null ? hoveredCountryActor.getCurrentState () : CountrySpriteState.UNOWNED;
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

    if (button == Input.Buttons.RIGHT)
    {
      touchedDownCountryActor.nextState ();

      return true;
    }

    touchedDownCountryActor.onTouchDown ();
    touchedDownCountryActor.changeStateRandomly ();

    if (touchedCountryActor != null && !touchedCountryActor.getName ().equals (touchedDownCountryActor.getName ()))
    {
      touchedCountryActor.onTouchUp ();
    }

    touchedCountryActor = touchedDownCountryActor;

    return true;
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
    setCountryState ("Alaska", CountrySpriteState.GOLD);
    setCountryState ("Northwest Territory", CountrySpriteState.GOLD);
    setCountryState ("Greenland", CountrySpriteState.GOLD);
    setCountryState ("Alberta", CountrySpriteState.GOLD);
    setCountryState ("Ontario", CountrySpriteState.GOLD);
    setCountryState ("Quebec", CountrySpriteState.GOLD);
    setCountryState ("Western United States", CountrySpriteState.GOLD);
    setCountryState ("Eastern United States", CountrySpriteState.GOLD);
    setCountryState ("Central America", CountrySpriteState.GOLD);

    // South America
    setCountryState ("Venezuela", CountrySpriteState.RED);
    setCountryState ("Peru", CountrySpriteState.RED);
    setCountryState ("Brazil", CountrySpriteState.RED);
    setCountryState ("Argentina", CountrySpriteState.RED);

    // Europe
    setCountryState ("Iceland", CountrySpriteState.BLUE);
    setCountryState ("Scandinavia", CountrySpriteState.BLUE);
    setCountryState ("Great Britain", CountrySpriteState.BLUE);
    setCountryState ("Northern Europe", CountrySpriteState.BLUE);
    setCountryState ("Ukraine", CountrySpriteState.BLUE);
    setCountryState ("Western Europe", CountrySpriteState.BLUE);
    setCountryState ("Southern Europe", CountrySpriteState.BLUE);

    // Asia
    setCountryState ("Ural", CountrySpriteState.GREEN);
    setCountryState ("Siberia", CountrySpriteState.GREEN);
    setCountryState ("Yakutsk", CountrySpriteState.GREEN);
    setCountryState ("Kamchatka", CountrySpriteState.GREEN);
    setCountryState ("Afghanistan", CountrySpriteState.GREEN);
    setCountryState ("Irkutsk", CountrySpriteState.GREEN);
    setCountryState ("Mongolia", CountrySpriteState.GREEN);
    setCountryState ("Japan", CountrySpriteState.GREEN);
    setCountryState ("Middle East", CountrySpriteState.GREEN);
    setCountryState ("India", CountrySpriteState.GREEN);
    setCountryState ("China", CountrySpriteState.GREEN);
    setCountryState ("Siam", CountrySpriteState.GREEN);

    // Africa
    setCountryState ("North Africa", CountrySpriteState.BROWN);
    setCountryState ("Egypt", CountrySpriteState.BROWN);
    setCountryState ("Congo", CountrySpriteState.BROWN);
    setCountryState ("East Africa", CountrySpriteState.BROWN);
    setCountryState ("South Africa", CountrySpriteState.BROWN);
    setCountryState ("Madagascar", CountrySpriteState.BROWN);

    // Australia
    setCountryState ("Indonesia", CountrySpriteState.PINK);
    setCountryState ("New Guinea", CountrySpriteState.PINK);
    setCountryState ("Western Australia", CountrySpriteState.PINK);
    setCountryState ("Eastern Australia", CountrySpriteState.PINK);

    // Not used in classic mode
    setCountryState ("Hawaii", CountrySpriteState.DISABLED);
    setCountryState ("Caribbean Islands", CountrySpriteState.DISABLED);
    setCountryState ("Falkland Islands", CountrySpriteState.DISABLED);
    setCountryState ("Svalbard", CountrySpriteState.DISABLED);
    setCountryState ("Philippines", CountrySpriteState.DISABLED);
    setCountryState ("New Zealand", CountrySpriteState.DISABLED);
    setCountryState ("Antarctica", CountrySpriteState.DISABLED);
  }

  public void setCountriesTo (final CountrySpriteState state)
  {
    Arguments.checkIsNotNull (state, "state");

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changeStateTo (state);
    }
  }

  public void setCountryState (final String countryName, final CountrySpriteState state)
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
    Arguments.checkUpperInclusiveBound (n, CountrySpriteState.values ().length, "n");

    final ImmutableSet.Builder <CountrySpriteState> nStatesBuilder = ImmutableSet.builder ();
    final Set <CountrySpriteState> states = new HashSet <> (Arrays.asList (CountrySpriteState.values ()));

    for (int i = 0; i < n; ++i)
    {
      final CountrySpriteState randomState = Randomness.getRandomElementFrom (states);
      nStatesBuilder.add (randomState);
      states.remove (randomState);
    }

    randomizeCountryStatesUsingOnly (nStatesBuilder.build ());
  }

  public void randomizeCountryStatesUsingOnly (final Collection <CountrySpriteState> states)
  {
    Arguments.checkIsNotNullOrEmpty (states, "states");
    Arguments.checkHasNoNullElements (states, "states");

    CountrySpriteState randomState;

    for (final CountryActor countryActor : getCountryActors ())
    {
      randomState = Randomness.getRandomElementFrom (states);

      countryActor.changeStateTo (randomState);
    }
  }

  public void randomizeCountryStatesUsingOnly (final CountrySpriteState... states)
  {
    randomizeCountryStatesUsingOnly (Arrays.asList (states));
  }

  private ImmutableCollection <CountryActor> getCountryActors ()
  {
    return countryNamesToActors.values ();
  }
}
