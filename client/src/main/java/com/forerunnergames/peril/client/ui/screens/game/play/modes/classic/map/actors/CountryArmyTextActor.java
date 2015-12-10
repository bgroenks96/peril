package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images.CountryPrimaryImageState;

public interface CountryArmyTextActor
{
  CountryArmyTextActor NULL_COUNTRY_ARMY_TEXT_ACTOR = new NullCountryArmyTextActor ();

  void setCircleTopLeft (final Vector2 topLeft);

  void setCircleSize (final Vector2 size);

  void changeArmiesTo (final int armies);

  void incrementArmies ();

  void decrementArmies ();

  void changeArmiesBy (final int deltaArmies);

  void onPrimaryStateChange (final CountryPrimaryImageState state);

  int getArmies ();

  void setFont (final BitmapFont font);

  Actor asActor ();
}
