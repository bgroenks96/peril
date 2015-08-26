package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import javax.annotation.Nullable;

public interface CountryImage <E extends Enum <E> & CountryImageState <E>>
{
  String getCountryName ();

  CountryImageState <E> getState ();

  @Nullable
  Drawable getDrawable ();

  void setVisible (final boolean isVisible);

  void setPosition (final Vector2 position);

  void setScale (final Vector2 scaling);

  Actor asActor ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object o);

  @Override
  String toString ();
}
