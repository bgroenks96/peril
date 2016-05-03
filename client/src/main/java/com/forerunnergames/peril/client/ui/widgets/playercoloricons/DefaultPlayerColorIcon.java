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

package com.forerunnergames.peril.client.ui.widgets.playercoloricons;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.Map;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DefaultPlayerColorIcon implements PlayerColorIcon
{
  private static final Logger log = LoggerFactory.getLogger (DefaultPlayerColorIcon.class);
  private final ImmutableSet <String> lowercaseColors;
  private final PlayerColorIconWidgetFactory widgetFactory;
  private final ImmutableMap <String, Button> colorsToIcons;
  private final Table table = new Table ();
  private final Cell <Button> iconCell;
  private String activeColor;

  public DefaultPlayerColorIcon (final String activeColor,
                                 final ImmutableSet <String> lowercaseColors,
                                 final PlayerColorIconWidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (activeColor, "activeColor");
    Arguments.checkIsNotNull (lowercaseColors, "lowercaseColors");
    Arguments.checkHasNoNullOrEmptyOrBlankElements (lowercaseColors, "lowercaseColors");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.lowercaseColors = lowercaseColors;
    this.widgetFactory = widgetFactory;

    final ImmutableMap.Builder <String, Button> colorsToIconsBuilder = ImmutableMap.builder ();

    for (final String color : lowercaseColors)
    {
      colorsToIconsBuilder.put (color, widgetFactory.createPlayerColorIconButton (color));
    }

    colorsToIcons = colorsToIconsBuilder.build ();

    iconCell = table.add ((Button) null);

    setColor (activeColor);
  }

  @Override
  public void setColor (final String color)
  {
    Arguments.checkIsNotNull (color, "color");

    if (!lowercaseColors.contains (color.toLowerCase ()))
    {
      log.warn ("Not setting unrecognized color [{}]", color);
      return;
    }

    activeColor = color.toLowerCase ();

    final Button playerColorIcon = colorsToIcons.get (activeColor);

    iconCell.setActor (playerColorIcon).size (playerColorIcon.getPrefWidth (), playerColorIcon.getPrefHeight ());
    table.invalidateHierarchy ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    for (final Map.Entry <String, Button> entry : colorsToIcons.entrySet ())
    {
      entry.getValue ().setStyle (widgetFactory.createPlayerColorIconStyle (entry.getKey ()));
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
