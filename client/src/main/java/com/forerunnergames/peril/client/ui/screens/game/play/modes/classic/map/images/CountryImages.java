package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.google.common.collect.ImmutableCollection;

public interface CountryImages <E extends Enum <E> & CountryImageState <E>, T extends CountryImage <E>>
{
  int getAtlasIndex ();

  void hide (E state);

  void show (E state);

  T get (E state);

  ImmutableCollection <T> getAll ();

  @Override
  String toString ();
}
