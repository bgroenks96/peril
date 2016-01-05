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

public final class DefaultPlayMapActor implements PlayMapActor
{
  private final Group group = new Group ();
  private final ImmutableMap <String, CountryActor> countryNamesToActors;
  private final PlayMapInputDetection inputDetection;
  private final HoveredTerritoryTextActor hoveredTerritoryTextActor;
  private final MapMetadata mapMetadata;
  private final MBassador <Event> eventBus;
  @Nullable
  private CountryActor hoveredCountryActor = null;
  @Nullable
  private CountryActor touchedCountryActor = null;
  private boolean isEnabled = true;

  public DefaultPlayMapActor (final ImmutableMap <String, CountryActor> countryNamesToActors,
                              final PlayMapInputDetection inputDetection,
                              final HoveredTerritoryTextActor hoveredTerritoryTextActor,
                              final Image backgroundImage,
                              final MapMetadata mapMetadata,
                              final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (countryNamesToActors, "countryNamesToActors");
    Arguments.checkHasNoNullKeysOrValues (countryNamesToActors, "countryNamesToActors");
    Arguments.checkIsNotNull (inputDetection, "inputDetection");
    Arguments.checkIsNotNull (hoveredTerritoryTextActor, "hoveredTerritoryTextActor");
    Arguments.checkIsNotNull (backgroundImage, "backgroundImage");
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.countryNamesToActors = countryNamesToActors;
    this.inputDetection = inputDetection;
    this.hoveredTerritoryTextActor = hoveredTerritoryTextActor;
    this.mapMetadata = mapMetadata;
    this.eventBus = eventBus;

    group.setTransform (false);

    backgroundImage.setSize (PlayMapSettings.ACTUAL_WIDTH, PlayMapSettings.ACTUAL_HEIGHT);

    group.addActor (backgroundImage);

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
      group.addActor (countryActor.asActor ());
    }

    for (final CountryActor countryActor : countryActorsSortedByAtlasIndex)
    {
      group.addActor (countryActor.getArmyTextActor ().asActor ());
    }

    group.addActor (hoveredTerritoryTextActor);
  }

  @Override
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
            && !this.hoveredCountryActor.asActor ().getName ().equals (hoveredCountryActor.asActor ().getName ()))
    {
      this.hoveredCountryActor.onHoverEnd ();
    }

    this.hoveredCountryActor = hoveredCountryActor;

    return true;
  }

  @Override
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

        if (touchedCountryActor != null
                && !touchedCountryActor.asActor ().getName ().equals (touchedDownCountryActor.asActor ().getName ()))
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

  @Override
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

    if (!touchedCountryActor.asActor ().getName ().equals (touchedUpCountryActor.asActor ().getName ()))
    {
      touchedCountryActor.onTouchUp ();
    }
    else
    {
      eventBus.publish (new SelectCountryEvent (touchedUpCountryActor.asActor ().getName ()));
    }

    touchedCountryActor = null;

    return true;
  }

  @Override
  public void setCountriesTo (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changePrimaryStateTo (state);
    }
  }

  @Override
  public void randomizeCountryStates ()
  {
    for (final CountryActor countryActor : getCountryActors ())
    {
      countryActor.changePrimaryStateRandomly ();
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

    if (!existsCountryActorWithName (countryName)) return;

    getCountryActorWithName (countryName).setArmies (armies);
  }

  @Override
  public void changeArmiesBy (final int deltaArmies, final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryActorWithName (countryName)) return;

    getCountryActorWithName (countryName).changeArmiesBy (deltaArmies);
  }

  @Override
  public void setCountryState (final String countryName, final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (state, "state");

    if (!existsCountryActorWithName (countryName)) return;

    getCountryActorWithName (countryName).changePrimaryStateTo (state);
  }

  @Override
  public boolean existsCountryActorWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return countryNamesToActors.containsKey (countryName);
  }

  @Override
  public CountryActor getCountryActorWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final CountryActor countryActor = countryNamesToActors.get (countryName);

    if (countryActor == null)
    {
      throw new IllegalStateException (
              "Cannot find " + DefaultCountryActor.class.getSimpleName () + " with name [" + countryName + "].");
    }

    return countryActor;
  }

  @Override
  public ImmutableSet <String> getAllCountryNames ()
  {
    return countryNamesToActors.keySet ();
  }

  @Override
  public boolean currentPrimaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryActorWithName (countryName) && getCurrentPrimaryImageStateOf (countryName) == state;
  }

  @Override
  public boolean currentSecondaryImageStateOfCountryIs (final CountrySecondaryImageState state,
                                                        final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return existsCountryActorWithName (countryName) && getCurrentSecondaryImageStateOf (countryName) == state;
  }

  @Override
  @Nullable
  public CountryPrimaryImageState getCurrentPrimaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryActorWithName (countryName)) return null;

    return getCountryActorWithName (countryName).getCurrentPrimaryImageState ();
  }

  @Override
  @Nullable
  public CountrySecondaryImageState getCurrentSecondaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!existsCountryActorWithName (countryName)) return null;

    return getCountryActorWithName (countryName).getCurrentSecondaryImageState ();
  }

  @Override
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

  @Override
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

  private CountryActor getCountryActorAt (final Vector2 inputCoordinate)
  {
    final CountryActor countryActor = countryNamesToActors.get (inputDetection.getCountryNameAt (inputCoordinate));

    if (countryActor == null)
    {
      throw new IllegalStateException (
              "Cannot find " + DefaultCountryActor.class.getSimpleName () + " at " + inputCoordinate + ".");

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
            && getCountryActorAt (inputCoordinate).asActor ().getName ().equals (countryActor.asActor ().getName ());
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
