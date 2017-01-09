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

package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;

import com.forerunnergames.peril.client.ui.widgets.padding.CellPadding;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class Widgets
{
  public static void padCell (final Cell <?> cell, final float paddingAmount, final CellPadding paddingType)
  {
    Arguments.checkIsNotNull (cell, "cell");
    Arguments.checkIsNotNegative (paddingAmount, "paddingAmount");
    Arguments.checkIsNotNull (paddingType, "paddingType");

    switch (paddingType)
    {
      case LEFT:
      {
        cell.padLeft (paddingAmount);
        break;
      }
      case RIGHT:
      {
        cell.padRight (paddingAmount);
        break;
      }
      case TOP:
      {
        cell.padTop (paddingAmount);
        break;
      }
      case BOTTOM:
      {
        cell.padBottom (paddingAmount);
        break;
      }
      case ALL:
      {
        cell.pad (paddingAmount);
      }
      default:
      {
        throw new IllegalStateException (
                "Unknown " + CellPadding.class.getSimpleName () + " value [" + paddingType + "]");
      }
    }
  }

  private Widgets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
