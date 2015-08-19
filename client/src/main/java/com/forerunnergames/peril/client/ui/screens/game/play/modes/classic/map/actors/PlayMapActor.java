package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.map.MapMetadata;

import javax.annotation.Nullable;

public interface PlayMapActor
{
  PlayMapActor NULL_PLAY_MAP_ACTOR = new NullPlayMapActor ();

  boolean mouseMoved (Vector2 mouseCoordinate);

  boolean touchDown (Vector2 touchDownCoordinate, int button);

  boolean touchUp (Vector2 touchUpCoordinate);

  void setCountriesTo (CountryPrimaryImageState state);

  void randomizeCountryStates ();

  void randomizeCountryStatesUsingNRandomStates (int n);

  void randomizeCountryStatesUsingOnly (CountryPrimaryImageState... states);

  void reset ();

  void resetCountryStates ();

  void resetArmies ();

  void changeArmiesBy (int deltaArmies, CountryName countryName);

  void setCountryState (String countryName, CountryPrimaryImageState state);

  CountryActor getCountryActorWithName (CountryName countryName);

  boolean currentPrimaryImageStateOfCountryIs (CountryPrimaryImageState state, CountryName countryName);

  boolean currentSecondaryImageStateOfCountryIs (CountrySecondaryImageState state, CountryName countryName);

  @Nullable
  CountryPrimaryImageState getCurrentPrimaryImageStateOf (CountryName countryName);

  @Nullable
  CountrySecondaryImageState getCurrentSecondaryImageStateOf (CountryName countryName);

  void disable ();

  void enable (Vector2 currentMouseLocation);

  MapMetadata getMapMetadata ();

  Actor asActor ();
}
