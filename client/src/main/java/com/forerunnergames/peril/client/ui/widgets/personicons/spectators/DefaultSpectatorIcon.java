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

package com.forerunnergames.peril.client.ui.widgets.personicons.spectators;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.OverridingMethodsMustInvokeSuper;

final class DefaultSpectatorIcon implements SpectatorIcon
{
  private final WidgetFactory widgetFactory;
  private final Table table = new Table ();
  private final Cell <Button> iconCell;
  private final Button icon;

  DefaultSpectatorIcon (final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;
    icon = widgetFactory.createButton (StyleSettings.SPECTATOR_ICON_STYLE);
    iconCell = table.add (icon);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    icon.setStyle (widgetFactory.createButtonStyle (StyleSettings.SPECTATOR_ICON_STYLE));
  }

  @Override
  public Actor asActor ()
  {
    return table;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Icon: [{}] Icon Cell: [{}] | Table: [{}] | Widget Factory: [{}]", getClass ()
            .getSimpleName (), icon, iconCell, table, widgetFactory);
  }
}
