package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;

public abstract class AbstractCountryImages <E extends Enum <E> & CountryImageState <E>, T extends CountryImage <E>>
        implements CountryImages <E, T>
{
  private final ImmutableMap <E, T> imageStatesToImages;
  private final int atlasIndex;

  protected AbstractCountryImages (final ImmutableMap <E, T> imageStatesToImages, final int atlasIndex)
  {
    Arguments.checkIsNotNull (imageStatesToImages, "imageStatesToImages");
    Arguments.checkHasNoNullKeysOrValues (imageStatesToImages, "imageStatesToImages");

    this.imageStatesToImages = imageStatesToImages;
    this.atlasIndex = atlasIndex;
  }

  @Override
  public final int getAtlasIndex ()
  {
    return atlasIndex;
  }

  @Override
  public final void hide (final E state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (imageStatesToImages.containsKey (state),
                               "Cannot find " + CountryImage.class.getSimpleName () + " for "
                                       + CountryImageState.class.getSimpleName () + " [" + state + "].");

    imageStatesToImages.get (state).setVisible (false);
  }

  @Override
  public final void show (final E state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (imageStatesToImages.containsKey (state), "Cannot find "
            + CountryImage.class.getSimpleName () + " for " + state.getClass ().getSimpleName () + " [" + state + "].");

    imageStatesToImages.get (state).setVisible (true);
  }

  @Override
  public boolean has (final E state)
  {
    Arguments.checkIsNotNull (state, "state");

    return imageStatesToImages.containsKey (state);
  }

  @Override
  public boolean doesNotHave (final E state)
  {
    Arguments.checkIsNotNull (state, "state");

    return !imageStatesToImages.containsKey (state);
  }

  @Override
  public final T get (final E state)
  {
    Arguments.checkIsNotNull (state, "state");
    Preconditions.checkIsTrue (imageStatesToImages.containsKey (state),
                               "Cannot find " + CountryImage.class.getSimpleName () + " for "
                                       + CountryImageState.class.getSimpleName () + " [" + state + "].");

    return imageStatesToImages.get (state);
  }

  @Override
  public final ImmutableCollection <T> getAll ()
  {
    return imageStatesToImages.values ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s | Country Image States => Country Images: %2$s | Atlas Index: %3$s",
                          getClass ().getSimpleName (), Strings.toString (imageStatesToImages), atlasIndex);
  }
}
