package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

final class NullPlayMapActor implements PlayMapActor
{
  private final Group group = new Group ();
  private final CountryActor nullCountryActor = CountryActor.NULL_COUNTRY_ACTOR;

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
  public boolean existsCountryActorWithName (final String countryName)
  {
    return true;
  }

  @Override
  public CountryActor getCountryActorWithName (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return nullCountryActor;
  }

  @Override
  public ImmutableSet <String> getAllCountryNames ()
  {
    return ImmutableSet.of ();
  }

  @Override
  public boolean currentPrimaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return false;
  }

  @Override
  public boolean currentSecondaryImageStateOfCountryIs (final CountrySecondaryImageState state,
                                                        final String countryName)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (countryName, "countryName");

    return false;
  }

  @Nullable
  @Override
  public CountryPrimaryImageState getCurrentPrimaryImageStateOf (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    return null;
  }

  @Nullable
  @Override
  public CountrySecondaryImageState getCurrentSecondaryImageStateOf (final String countryName)
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
