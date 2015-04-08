package com.forerunnergames.peril.client.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;

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
        throw new IllegalStateException ("Unknown " + CellPadding.class.getSimpleName () + " value [" + paddingType
                + "]");
      }
    }
  }

  private Widgets ()
  {
    Classes.instantiationNotAllowed ();
  }
}
