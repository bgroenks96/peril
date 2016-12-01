/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.widgets.personicons.players;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DefaultPlayerIcon implements PlayerIcon
{
  private static final Logger log = LoggerFactory.getLogger (DefaultPlayerIcon.class);
  private final PlayerIconWidgetFactory widgetFactory;
  private final ImmutableMap <PlayerColor, Button> colorsToIcons;
  private final Table table = new Table ();
  private final Cell <Button> iconCell;
  private PlayerColor activeColor;

  DefaultPlayerIcon (final PlayerColor activeColor, final PlayerIconWidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (activeColor, "activeColor");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;

    final ImmutableMap.Builder <PlayerColor, Button> colorsToIconsBuilder = ImmutableMap.builder ();

    for (final PlayerColor color : PlayerColor.VALID_VALUES)
    {
      colorsToIconsBuilder.put (color, widgetFactory.createPlayerIconButton (color));
    }

    colorsToIcons = colorsToIconsBuilder.build ();

    iconCell = table.add ((Button) null);

    setColor (activeColor);
  }

  @Override
  public void setColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    if (color == PlayerColor.UNKNOWN)
    {
      log.warn ("Not setting invalid color [{}]", PlayerColor.UNKNOWN);
      return;
    }

    activeColor = color;

    final Button playerIcon = colorsToIcons.get (activeColor);

    iconCell.setActor (playerIcon).size (playerIcon.getPrefWidth (), playerIcon.getPrefHeight ());
    table.invalidateHierarchy ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    for (final Map.Entry <PlayerColor, Button> entry : colorsToIcons.entrySet ())
    {
      entry.getValue ().setStyle (widgetFactory.createPlayerIconStyle (entry.getKey ()));
    }
  }

  @Override
  public Actor asActor ()
  {
    return table;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Active color: {} | Icon Cell: {} | Colors to Icons: {} | Table: {}",
                           getClass ().getSimpleName (), activeColor, iconCell, colorsToIcons, table);
  }
}
