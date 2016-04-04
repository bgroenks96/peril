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

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.input.PlayMapInputDetection;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

public final class HoveredTerritoryText extends Actor
{
  private static final Vector2 TEXT_OFFSET = new Vector2 (35, -17);
  private final PlayMapInputDetection playMapInputDetection;
  private final MouseInput mouseInput;
  private final BitmapFont font;
  private final Vector2 mousePosition = new Vector2 ();
  private final Vector2 textPosition = new Vector2 ();
  private String text = "";
  private PlayMap playMap = PlayMap.NULL_PLAY_MAP;
  @Nullable
  private CountryPrimaryImageState countryPrimaryImageState;

  public HoveredTerritoryText (final PlayMapInputDetection playMapInputDetection,
                               final MouseInput mouseInput,
                               final BitmapFont font)
  {
    Arguments.checkIsNotNull (playMapInputDetection, "playMapInputDetection");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (font, "font");

    this.playMapInputDetection = playMapInputDetection;
    this.mouseInput = mouseInput;
    this.font = font;
  }

  @Override
  public void draw (final Batch batch, final float parentAlpha)
  {
    font.draw (batch, text, textPosition.x, textPosition.y);
  }

  @Override
  public void act (final float delta)
  {
    super.act (delta);

    mousePosition.set (mouseInput.position ());

    final String countryName = playMapInputDetection.getCountryNameAt (mousePosition);
    final String continentName = playMapInputDetection.getContinentNameAt (mousePosition);

    countryPrimaryImageState = playMap.getCurrentPrimaryImageStateOf (countryName);

    text = Strings.toStringList (", ", LetterCase.PROPER, false, countryName, continentName,
                                 countryPrimaryImageState != null ? countryPrimaryImageState.toString () : "");

    getStage ().screenToStageCoordinates (mousePosition);

    textPosition.set (mousePosition.x + TEXT_OFFSET.x, mousePosition.y + TEXT_OFFSET.y);
  }

  public void setPlayMap (final PlayMap playMap)
  {
    Arguments.checkIsNotNull (playMap, "playMap");

    this.playMap = playMap;
  }
}
