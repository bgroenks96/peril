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

// @formatter:off
public final class CoordinateSpaces
{
  public static Point2D actualInputSpaceToActualScreenSpace (final Point2D actualInputCoordinate,
                                                             final Size2D screenSize)
  {
    Arguments.checkIsNotNull (actualInputCoordinate, "actualInputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return Geometry.translate (actualInputCoordinate, InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION);
  }

  public static Point2D actualScreenSpaceToActualInputSpace (final Point2D actualScreenCoordinate,
                                                             final Size2D screenSize)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return Geometry.translate (actualScreenCoordinate, InputSettings.ACTUAL_SCREEN_SPACE_TO_ACTUAL_INPUT_SPACE_TRANSLATION);
  }

  public static Point2D referencePlayMapSpaceToActualScreenSpace (final Point2D referencePlayMapCoordinate,
                                                                  final Size2D screenSize)
  {
    Arguments.checkIsNotNull (referencePlayMapCoordinate, "referencePlayMapCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final Point2D referenceScreenCoordinate = referencePlayMapSpaceToReferenceScreenSpace (referencePlayMapCoordinate);
    final Scaling2D referenceToActualScreenScaling = Geometry.divide (screenSize, GraphicsSettings.REFERENCE_SCREEN_SIZE);
    final Point2D actualScreenCoordinate = Geometry.scale (referenceScreenCoordinate, referenceToActualScreenScaling);

    return actualScreenCoordinate;
  }

  public static Point2D referencePlayMapSpaceToReferenceScreenSpace (final Point2D referencePlayMapCoordinate)
  {
    Arguments.checkIsNotNull (referencePlayMapCoordinate, "referencePlayMapCoordinate");

    final Point2D actualPlayMapCoordinate = Geometry.scale (referencePlayMapCoordinate, PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
    final Point2D referenceScreenCoordinate = Geometry.translate (actualPlayMapCoordinate, PlayMapSettings.ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_SCREEN_SPACE_TRANSLATION);

    return referenceScreenCoordinate;
  }

  public static Point2D actualScreenSpaceToReferencePlayMapSpace (final Point2D actualScreenCoordinate,
                                                                  final Size2D screenSize)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final Scaling2D actualToReferenceScreenScaling = Geometry.divide (GraphicsSettings.REFERENCE_SCREEN_SIZE, screenSize);
    final Point2D referenceScreenCoordinate = Geometry.scale (actualScreenCoordinate, actualToReferenceScreenScaling);
    final Point2D referencePlayMapCoordinate = referenceScreenSpaceToReferencePlayMapSpace (referenceScreenCoordinate);

    return referencePlayMapCoordinate;
  }

  public static Point2D referenceScreenSpaceToReferencePlayMapSpace (final Point2D referenceScreenCoordinate)
  {
    Arguments.checkIsNotNull (referenceScreenCoordinate, "referenceScreenCoordinate");

    final Point2D actualPlayMapCoordinate = Geometry.translate (referenceScreenCoordinate, PlayMapSettings.REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION);
    final Point2D referencePlayMapCoordinate = Geometry.scale (actualPlayMapCoordinate, PlayMapSettings. ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_PLAY_MAP_SPACE_SCALING);

    return referencePlayMapCoordinate;
  }

  private CoordinateSpaces ()
  {
    Classes.instantiationNotAllowed ();
  }
}
