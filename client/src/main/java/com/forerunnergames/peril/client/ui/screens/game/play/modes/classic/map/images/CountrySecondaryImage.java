package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.core.model.map.country.CountryName;

public final class CountrySecondaryImage extends AbstractCountryImage <CountrySecondaryImageState>
{
  public CountrySecondaryImage (final Drawable drawable,
                                final CountryName countryName,
                                final CountryImageState <CountrySecondaryImageState> state)
  {
    super (drawable, countryName, state);
  }
}
