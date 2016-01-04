package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

public final class NullCountryPrimaryImages implements CountryImages <CountryPrimaryImageState, CountryPrimaryImage>
{
  private final ImmutableMap <CountryPrimaryImageState, CountryPrimaryImage> imageStatesToImages;

  public NullCountryPrimaryImages (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    final ImmutableMap.Builder <CountryPrimaryImageState, CountryPrimaryImage> builder = ImmutableMap.builder ();

    for (final CountryPrimaryImageState state : CountryPrimaryImageState.values ())
    {
      builder.put (state, new CountryPrimaryImage (null, countryName, state));
    }

    imageStatesToImages = builder.build ();
  }

  @Override
  public int getAtlasIndex ()
  {
    return 0;
  }

  @Override
  public void hide (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public void show (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");
  }

  @Override
  public boolean has (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.containsKey (state);
  }

  @Override
  public boolean doesNotHave (final CountryPrimaryImageState state)
  {
    return ! has (state);
  }

  @Override
  public CountryPrimaryImage get (final CountryPrimaryImageState state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.get (state);
  }

  @Override
  public ImmutableCollection <CountryPrimaryImage> getAll ()
  {
    return imageStatesToImages.values ();
  }
}
