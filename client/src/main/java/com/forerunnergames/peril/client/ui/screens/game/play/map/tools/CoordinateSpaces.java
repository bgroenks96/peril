package com.forerunnergames.peril.client.ui.screens.game.play.map.tools;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Scaling2D;
import com.forerunnergames.tools.common.geometry.Size2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

// @formatter:off
public final class CoordinateSpaces
{
  public static Point2D inputSpaceToScreenSpace (final Point2D inputCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (inputCoordinate, "inputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return Geometry.translate (inputCoordinate, InputSettings.REFERENCE_INPUT_SPACE_TO_SCREEN_SPACE_TRANSLATION);
  }

  public static Point2D screenSpaceToInputSpace (final Point2D screenCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (screenCoordinate, "screenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return Geometry.translate (screenCoordinate, InputSettings.REFERENCE_SCREEN_SPACE_TO_INPUT_SPACE_TRANSLATION);
  }

  public static Point2D playMapSpaceToScreenSpace (final Point2D playMapCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (playMapCoordinate, "playMapCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final Translation2D translation = PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_SCREEN_SPACE_TRANSLATION;
    final Scaling2D scaling = Geometry.divide (screenSize, GraphicsSettings.REFERENCE_SCREEN_SIZE);

    return Geometry.scale (Geometry.translate (playMapCoordinate, translation), scaling);
  }

  public static Point2D screenSpaceToPlayMapSpace (final Point2D screenCoordinate, final Size2D screenSize)
  {
    Arguments.checkIsNotNull (screenCoordinate, "screenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final Translation2D translation = PlayMapSettings.REFERENCE_SCREEN_SPACE_TO_PLAY_MAP_SPACE_TRANSLATION;
    final Scaling2D scaling = Geometry.divide (GraphicsSettings.REFERENCE_SCREEN_SIZE, screenSize);

    return Geometry.translate (Geometry.scale (screenCoordinate, scaling), translation);
  }

  private CoordinateSpaces ()
  {
    Classes.instantiationNotAllowed ();
  }
}
