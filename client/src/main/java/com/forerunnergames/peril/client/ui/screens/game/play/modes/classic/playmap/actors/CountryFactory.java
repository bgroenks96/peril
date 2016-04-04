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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageDataRepository;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryImagesRepository;
import com.forerunnergames.tools.common.Arguments;

public final class CountryFactory
{
  private final CountryImageDataRepository countryImageDataRepository;
  private final CountryImagesRepository countryImagesRepository;
  private final Vector2 playMapReferenceSize;

  public CountryFactory (final CountryImageDataRepository countryImageDataRepository,
                         final CountryImagesRepository countryImagesRepository,
                         final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (countryImageDataRepository, "countryImageDataRepository");
    Arguments.checkIsNotNull (countryImagesRepository, "countryImagesRepository");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    this.countryImageDataRepository = countryImageDataRepository;
    this.countryImagesRepository = countryImagesRepository;
    this.playMapReferenceSize = playMapReferenceSize;
  }

  public Country create (final String countryName, final BitmapFont countryArmyTextFont)
  {
    Arguments.checkIsNotNull (countryName, "countryName");
    Arguments.checkIsNotNull (countryArmyTextFont, "countryArmyTextFont");

    final CountryImageData countryImageData = countryImageDataRepository.get (countryName);

    return new DefaultCountry (countryImagesRepository.getPrimary (countryName),
            countryImagesRepository.getSecondary (countryName), countryImageData,
            CountryArmyTextFactory.create (countryImageData, countryArmyTextFont, playMapReferenceSize),
            playMapReferenceSize);
  }
}
