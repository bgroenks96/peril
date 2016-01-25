/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
