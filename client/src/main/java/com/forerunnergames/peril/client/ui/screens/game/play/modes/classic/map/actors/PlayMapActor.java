package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.forerunnergames.peril.client.events.SelectCountryEvent;
import com.forerunnergames.peril.client.settings.ClassicPlayMapSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.input.PlayMapInputDetection;
import com.forerunnergames.peril.core.model.map.country.CountryName;
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

public final class PlayMapActor extends Group
{
  private final ImmutableMap <CountryName, CountryActor> countryNamesToActors;
  private final PlayMapInputDetection inputDetection;
  private final HoveredTerritoryTextActor hoveredTerritoryTextActor;
  private final MBassador <Event> eventBus;
  private CountryActor hoveredCountryActor;
  private CountryActor touchedCountryActor;
  private boolean isEnabled = true;

  public PlayMapActor (final ImmutableMap <CountryName, CountryActor> countryNamesToActors,
                       final PlayMapInputDetection inputDetection,
                       final HoveredTerritoryTextActor hoveredTerritoryTextActor,
                       final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (countryNamesToActors, "countryNamesToActors");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToActors, "countryNamesToActors");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");
    Arguments.checkIsNotNull (hoveredTerritoryTextActor, "hoveredTerritoryTextActor");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.countryNamesToActors = countryNamesToActors;
    this.inputDetection = inputDetection;
    this.hoveredTerritoryTextActor = hoveredTerritoryTextActor;
    this.eventBus = eventBus;

    setTransform (false);

    final Image backgroundImage = new Image (Assets.playMapBackground);
    backgroundImage.setSize (ClassicPlayMapSettings.ACTUAL_WIDTH, ClassicPlayMapSettings.ACTUAL_HEIGHT);

    addActor (backgroundImage);

    final List <CountryActor> countryActorsSortedByAtlasIndex = new ArrayList <> (countryNamesToActors.values ());

    Collections.sort (countryActorsSortedByAtlasIndex, new Comparator <CountryActor> ()
    {
      @Override
      public int compare (final CountryActor o1, final CountryActor o2)
      {
        Arguments.checkIsNotNull (o1, "o1");
        Arguments.checkIsNotNull (o2, "o2");

        return Integer.compare (o1.getAtlasIndex (), o2.getAtlasIndex ());
      }
    });

    for (final CountryActor countryActor : countryActorsSortedByAtlasIndex)
    {
      addActor (countryActor);
    }

    for (final CountryActor countryActor : countryActorsSortedByAtlasIndex)
    {
      addActor (countryActor.getArmyTextActor ());
    }

    addActor (hoveredTerritoryTextActor);
  }

  public boolean mouseMoved (final Vector2 mouseCoordinate)
  {
    Arguments.checkIsNotNull (mouseCoordinate, "mouseCoordinate");

    if (!isEnabled) return false;

    if (!existsCountryActorAt (mouseCoordinate))
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

    final CountryActor hoveredCountryActor = getCountryActorAt (mouseCoordinate);
    hoveredCountryActor.onHoverStart ();

    if (this.hoveredCountryActor != null
            && !this.hoveredCountryActor.getName ().equals (hoveredCountryActor.getName ()))
    {
      this.hoveredCountryActor.onHoverEnd ();
    }

    this.hoveredCountryActor = hoveredCountryActor;

    return true;
  }

  public boolean touchDown (final Vector2 touchDownCoordinate, final int button)
  {
    Arguments.checkIsNotNull (touchDownCoordinate, "touchDownCoordinate");

    if (!isEnabled) return false;

    if (!existsCountryActorAt (touchDownCoordinate))
    {
      if (touchedCountryActor != null)
      {
        touchedCountryActor.onTouchUp ();
        touchedCountryActor = null;
      }

      return false;
    }

    final CountryActor touchedDownCountryActor = getCountryActorAt (touchDownCoordinate);

    switch (button)
    {
      case Input.Buttons.LEFT:
      {
        touchedDownCountryActor.onTouchDown ();

        if (touchedCountryActor != null && !touchedCountryActor.getName ().equals (touchedDownCountryActor.getName ()))
        {
          touchedCountryActor.onTouchUp ();
        }

        touchedCountryActor = touchedDownCountryActor;

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  public boolean touchUp (final Vector2 touchUpCoordinate)
  {
    Arguments.checkIsNotNull (touchUpCoordinate, "touchUpCoordinate");

    if (!isEnabled) return false;

    if (countryActorAtPointIsNot (touchUpCoordinate, touchedCountryActor))
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

    if (!existsCountryActorAt (touchUpCoordinate)) return false;

    final CountryActor touchedUpCountryActor = getCountryActorAt (touchUpCoordinate);
    touchedUpCountryActor.onTouchUp ();

    hoveredCountryActor = touchedUpCountryActor;
    hoveredCountryActor.onHoverStart ();

    if (touchedCountryActor == null) return true;

    if (!touchedCountryActor.getName ().equals (touchedUpCountryActor.getName ()))
    {
      touchedCountryActor.onTouchUp ();
    }
    else
    {
      eventBus.publish (new SelectCountryEvent (touchedUpCountryActor.getName ()));
    }

    touchedCountryActor = null;

    return true;
  }

  public void setCountriesTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changePrimaryStateTo (state);
    }
  }

  public void randomizeCountryStates ()
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changePrimaryStateRandomly ();
    }
  }

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

  public void randomizeCountryStatesUsingOnly (final CountryPrimaryImageState... states)
  {
    randomizeCountryStatesUsingOnly (Arrays.asList (states));
  }

  public void reset ()
  {
    resetCountryStates ();
    resetArmies ();
  }

  public void resetCountryStates ()
  {
    setCountriesTo (CountryPrimaryImageState.UNOWNED);
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

  public void setCountryState (final String countryName, final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    final CountryName name = new CountryName (countryName);

    if (countryNamesToActors.containsKey (name)) countryNamesToActors.get (name).changePrimaryStateTo (state);
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

  public boolean currentPrimaryImageStateOfCountryIs (final CountryPrimaryImageState state,
                                                      final CountryName countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return getCurrentPrimaryImageStateOf (countryName) == state;
  }

  public boolean currentSecondaryImageStateOfCountryIs (final CountrySecondaryImageState state,
                                                        final CountryName countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return getCurrentSecondaryImageStateOf (countryName) == state;
  }

  @Nullable
  public CountryPrimaryImageState getCurrentPrimaryImageStateOf (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToActors.containsKey (countryName)) return null;

    return countryNamesToActors.get (countryName).getCurrentPrimaryImageState ();
  }

  @Nullable
  public CountrySecondaryImageState getCurrentSecondaryImageStateOf (final CountryName countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToActors.containsKey (countryName)) return null;

    return countryNamesToActors.get (countryName).getCurrentSecondaryImageState ();
  }

  public void disable ()
  {
    hoveredTerritoryTextActor.setVisible (false);

    if (hoveredCountryActor != null) hoveredCountryActor.onHoverEnd ();
    if (touchedCountryActor != null) touchedCountryActor.onTouchUp ();

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.disable ();
    }

    isEnabled = false;
  }

  public void enable (final Vector2 currentMouseLocation)
  {
    Arguments.checkIsNotNull (currentMouseLocation, "currentMouseLocation");

    hoveredTerritoryTextActor.setVisible (true);

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.enable ();
    }

    isEnabled = true;

    mouseMoved (currentMouseLocation);
  }

  private CountryActor getCountryActorAt (final Vector2 inputCoordinate)
  {
    final CountryActor countryActor = countryNamesToActors.get (inputDetection.getCountryNameAt (inputCoordinate));

    if (countryActor == null)
    {
      throw new IllegalStateException ("Cannot find " + CountryActor.class.getSimpleName () + " at " + inputCoordinate
              + ".");

    }

    return countryActor;
  }

  private void randomizeCountryStatesUsingOnly (final Collection <CountryPrimaryImageState> states)
  {
    CountryPrimaryImageState randomState;

    for (final CountryActor countryActor : getCountryActors ())
    {
      randomState = Randomness.getRandomElementFrom (states);

      countryActor.changePrimaryStateTo (randomState);
    }
  }

  private void setAllArmiesTo (final int armies)
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.setArmies (armies);
    }
  }

  private boolean existsCountryActorAt (final Vector2 inputCoordinate)
  {
    return countryNamesToActors.containsKey (inputDetection.getCountryNameAt (inputCoordinate));
  }

  private boolean countryActorAtPointIs (final Vector2 inputCoordinate, @Nullable final CountryActor countryActor)
  {
    return countryActor != null && existsCountryActorAt (inputCoordinate)
            && getCountryActorAt (inputCoordinate).getName ().equals (countryActor.getName ());
  }

  private boolean countryActorAtPointIsNot (final Vector2 inputCoordinate, final CountryActor countryActor)
  {
    return !countryActorAtPointIs (inputCoordinate, countryActor);
  }

  private ImmutableCollection <CountryActor> getCountryActors ()
  {
    return countryNamesToActors.values ();
  }
}
