package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data;

import com.google.common.collect.ImmutableSet;

public interface CountryImageDataRepository
{
  boolean has (String countryName);

  CountryImageData get (String name);

  ImmutableSet<String> getCountryNames ();
}
