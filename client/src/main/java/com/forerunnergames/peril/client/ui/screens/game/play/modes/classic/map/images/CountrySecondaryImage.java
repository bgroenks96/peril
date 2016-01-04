package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import javax.annotation.Nullable;

public final class CountrySecondaryImage extends AbstractCountryImage <CountrySecondaryImageState>
{
  public CountrySecondaryImage (@Nullable final Drawable drawable,
                                final String countryName,
                                final CountryImageState <CountrySecondaryImageState> state)
  {
    super (drawable, countryName, state);
  }
}
