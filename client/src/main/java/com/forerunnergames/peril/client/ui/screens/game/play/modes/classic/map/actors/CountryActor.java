package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountrySecondaryImageState;

import javax.annotation.Nullable;

public interface CountryActor
{
  CountryActor NULL_COUNTRY_ACTOR = new NullCountryActor ();

  CountryPrimaryImageState getCurrentPrimaryImageState ();

  CountrySecondaryImageState getCurrentSecondaryImageState ();

  void changePrimaryStateRandomly ();

  void changePrimaryStateTo (CountryPrimaryImageState state);

  void changeSecondaryStateTo (CountrySecondaryImageState state);

  void nextPrimaryState ();

  void onHoverStart ();

  void onHoverEnd ();

  void onTouchDown ();

  void onTouchUp ();

  @Nullable
  Drawable getCurrentPrimaryDrawable ();

  Vector2 getReferenceDestination ();

  Vector2 getReferenceTextUpperLeft ();

  float getReferenceWidth ();

  float getReferenceHeight ();

  int getArmies ();

  void setArmies (int armies);

  void incrementArmies ();

  void decrementArmies ();

  void changeArmiesBy (int deltaArmies);

  void disable ();

  void enable ();

  int getAtlasIndex ();

  CountryArmyTextActor getArmyTextActor ();

  Actor asActor ();
}
