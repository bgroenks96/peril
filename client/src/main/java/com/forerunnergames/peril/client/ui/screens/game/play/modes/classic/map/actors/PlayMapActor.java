package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.common.map.MapMetadata;

import javax.annotation.Nullable;

public interface PlayMapActor
{
  PlayMapActor NULL_PLAY_MAP_ACTOR = new NullPlayMapActor ();

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

  void changeArmiesBy (final int deltaArmies, final String countryName);

  void setCountryState (final String countryName, final CountryPrimaryImageState state);

  boolean existsCountryActorWithName (final String countryName);

  CountryActor getCountryActorWithName (final String countryName);

  boolean currentPrimaryImageStateOfCountryIs (final CountryPrimaryImageState state, final String countryName);

  boolean currentSecondaryImageStateOfCountryIs (final CountrySecondaryImageState state, final String countryName);

  @Nullable
  CountryPrimaryImageState getCurrentPrimaryImageStateOf (final String countryName);

  @Nullable
  CountrySecondaryImageState getCurrentSecondaryImageStateOf (final String countryName);

  void disable ();

  void enable (final Vector2 currentMouseLocation);

  MapMetadata getMapMetadata ();

  Actor asActor ();
}
