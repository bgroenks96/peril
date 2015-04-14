package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

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
                       final PlayMapInputDetection inputDetection,
                       final HoveredTerritoryTextActor hoveredTerritoryTextActor)
  {
    Arguments.checkIsNotNull (countryNamesToActors, "countryNamesToActors");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToActors, "countryNamesToActors");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");
    Arguments.checkIsNotNull (hoveredTerritoryTextActor, "hoveredTerritoryTextActor");

    this.countryNamesToActors = countryNamesToActors;
    this.inputDetection = inputDetection;

    final Image backgroundImage = new Image (Assets.playMapBackground);
    backgroundImage.setSize (PlayMapSettings.ACTUAL_WIDTH, PlayMapSettings.ACTUAL_HEIGHT);

    addActor (backgroundImage);

    for (final CountryActor countryActor : countryNamesToActors.values ())
    {
      addActor (countryActor);
    }

    addActor (hoveredTerritoryTextActor);

    hoveredTerritoryTextActor.setPlayMapActor (this);
  }

  public boolean mouseMoved (final Vector2 mouseCoordinate, final Vector2 screenSize)
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

  public boolean touchDown (final Vector2 touchDownCoordinate, final int button, final Vector2 screenSize)
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

  public boolean touchUp (final Vector2 touchUpCoordinate, final int button, final Vector2 screenSize)
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

  public void setCountriesTo (final CountryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changeStateTo (state);
    }
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

  public void randomizeCountryStatesUsingOnly (final CountryImageState... states)
  {
    randomizeCountryStatesUsingOnly (Arrays.asList (states));
  }

  public void resetArmies ()
  {
    setAllArmiesTo (0);
  }

  public void changeArmiesBy (final int deltaArmies, final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    getCountryActorWithName (countryName).changeArmiesBy (deltaArmies);
  }

  public void setCountryState (final String countryName, final CountryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    final CountryName name = new CountryName (countryName);

    if (countryNamesToActors.containsKey (name)) countryNamesToActors.get (name).changeStateTo (state);
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

  @Nullable
  public CountryImageState getCurrentImageStateOf (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToActors.containsKey (countryName)) return null;

    return countryNamesToActors.get (countryName).getCurrentImageState ();
  }

  private CountryActor getCountryActorAt (final Vector2 inputCoordinate, final Vector2 screenSize)
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

  private void randomizeCountryStatesUsingOnly (final Collection <CountryImageState> states)
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

  private void setAllArmiesTo (final int armies)
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.setArmies (armies);
    }
  }

  private boolean existsCountryActorAt (final Vector2 inputCoordinate, final Vector2 screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return countryNamesToActors.containsKey (inputDetection.getCountryNameAt (inputCoordinate, screenSize));
  }

  private boolean countryActorAtPointIs (final Vector2 inputCoordinate,
                                         final Vector2 screenSize,
                                         @Nullable final CountryActor countryActor)
  {
    return countryActor != null && existsCountryActorAt (inputCoordinate, screenSize)
            && getCountryActorAt (inputCoordinate, screenSize).getName ().equals (countryActor.getName ());
  }

  private boolean countryActorAtPointIsNot (final Vector2 inputCoordinate,
                                            final Vector2 screenSize,
                                            final CountryActor countryActor)
  {
    return !countryActorAtPointIs (inputCoordinate, screenSize, countryActor);
  }

  private ImmutableCollection <CountryActor> getCountryActors ()
  {
    return countryNamesToActors.values ();
  }
}
