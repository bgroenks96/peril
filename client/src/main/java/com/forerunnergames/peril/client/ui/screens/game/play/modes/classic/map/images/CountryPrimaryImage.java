package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.core.model.map.country.CountryName;

public final class CountryPrimaryImage extends AbstractCountryImage <CountryPrimaryImageState>
{
  public CountryPrimaryImage (final Drawable drawable,
                              final CountryName countryName,
                              final CountryImageState <CountryPrimaryImageState> state)
  {
    super (drawable, countryName, state);
  }
}
