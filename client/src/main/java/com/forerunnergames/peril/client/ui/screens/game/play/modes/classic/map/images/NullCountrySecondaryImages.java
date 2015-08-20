package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

public final class NullCountrySecondaryImages
        implements CountryImages <CountrySecondaryImageState, CountrySecondaryImage>
{
  private final ImmutableMap <CountrySecondaryImageState, CountrySecondaryImage> imageStatesToImages;

  public NullCountrySecondaryImages (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final ImmutableMap.Builder <CountrySecondaryImageState, CountrySecondaryImage> builder = ImmutableMap.builder ();

    for (final CountrySecondaryImageState state : CountrySecondaryImageState.values ())
    {
      builder.put (state, new CountrySecondaryImage (null, countryName, state));
    }

    imageStatesToImages = builder.build ();
  }

  @Override
  public int getAtlasIndex ()
  {
    return 0;
  }

  @Override
  public void hide (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void show (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public CountrySecondaryImage get (final CountrySecondaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.get (state);
  }

  @Override
  public ImmutableCollection <CountrySecondaryImage> getAll ()
  {
    return imageStatesToImages.values ();
  }
}
