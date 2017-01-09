/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.data.CountryImageData;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CountryArmyTextFactory
{
  private static final Vector2 tempPosition = new Vector2 ();

  public static CountryArmyText create (final CountryImageData countryImageData,
                                        final BitmapFont font,
                                        final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (countryImageData, "countryImageData");
    Arguments.checkIsNotNull (font, "font");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    final CountryArmyText countryArmyText = new DefaultCountryArmyText (font);

    tempPosition.set (countryImageData.getReferenceTextUpperLeft ());
    tempPosition.y = playMapReferenceSize.y - tempPosition.y;
    tempPosition.scl (PlayMapSettings.referenceToActualPlayMapScaling (playMapReferenceSize));

    countryArmyText.setCircleTopLeft (tempPosition);
    countryArmyText.setCircleSize (PlayMapSettings.countryArmyCircleSizeActualPlayMapSpace (playMapReferenceSize));
    countryArmyText.setName (countryImageData.getCountryName ());

    return countryArmyText;
  }

  private CountryArmyTextFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
