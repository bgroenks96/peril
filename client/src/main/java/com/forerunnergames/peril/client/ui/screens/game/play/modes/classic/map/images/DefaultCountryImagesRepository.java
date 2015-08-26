package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.images;

import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultCountryImagesRepository implements CountryImagesRepository
{
  private static final Logger log = LoggerFactory.getLogger (DefaultCountryImagesRepository.class);
  private final ImmutableMap <String, CountryPrimaryImages> countryNamesToPrimaryImages;
  private final ImmutableMap <String, CountrySecondaryImages> countryNamesToSecondaryImages;

  public DefaultCountryImagesRepository (final ImmutableMap <String, CountryPrimaryImages> countryNamesToPrimaryImages,
                                         final ImmutableMap <String, CountrySecondaryImages> countryNamesToSecondaryImages)
  {
    Arguments.checkHasNoNullKeysOrValues (countryNamesToPrimaryImages, "countryNamesToPrimaryImages");
    Arguments.checkIsNotNull (countryNamesToSecondaryImages, "countryNamesToSecondaryImages");

    this.countryNamesToPrimaryImages = countryNamesToPrimaryImages;
    this.countryNamesToSecondaryImages = countryNamesToSecondaryImages;
  }

  @Override
  public CountryImages <CountryPrimaryImageState, CountryPrimaryImage> getPrimary (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToPrimaryImages.containsKey (countryName))
    {
      log.warn ("Cannot find any {}'s for [{}].", CountryPrimaryImage.class.getSimpleName (), countryName);
      return new NullCountryPrimaryImages (countryName);
    }

    return countryNamesToPrimaryImages.get (countryName);
  }

  @Override
  public CountryImages <CountrySecondaryImageState, CountrySecondaryImage> getSecondary (final String countryName)
  {
    Arguments.checkIsNotNull (countryName, "countryName");

    if (!countryNamesToSecondaryImages.containsKey (countryName))
    {
      log.warn ("Cannot find any {}'s for [{}].", CountrySecondaryImage.class.getSimpleName (), countryName);
      return new NullCountrySecondaryImages (countryName);
    }

    return countryNamesToSecondaryImages.get (countryName);
  }
}
