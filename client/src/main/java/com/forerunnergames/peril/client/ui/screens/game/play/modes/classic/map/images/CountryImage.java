package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public interface CountryImage <E extends Enum <E> & CountryImageState <E>>
{
  String getCountryName ();

  CountryImageState <E> getState ();

  Drawable getDrawable ();

  void setVisible (final boolean isVisible);

  void setPosition (final Vector2 position);

  void setScale (final Vector2 scaling);

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object o);

  @Override
  String toString ();
}
