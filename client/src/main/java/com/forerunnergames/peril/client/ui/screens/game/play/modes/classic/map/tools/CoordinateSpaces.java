package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools;

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
  public static Point2D actualInputSpaceToActualScreenSpace (final Point2D actualInputCoordinate,
                                                             final Size2D screenSize)
  {
    Arguments.checkIsNotNull (actualInputCoordinate, "actualInputCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    return Geometry.translate (actualInputCoordinate, InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION);
  }

  public static Point2D referencePlayMapSpaceToActualPlayMapSpace (final Point2D referencePlayMapCoordinate)
  {
    Arguments.checkIsNotNull (referencePlayMapCoordinate, "referencePlayMapCoordinate");

    return Geometry.scale (referencePlayMapCoordinate, PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);
  }

  public static Point2D referencePlayMapSpaceToReferenceScreenSpace (final Point2D referencePlayMapCoordinate)
  {
    Arguments.checkIsNotNull (referencePlayMapCoordinate, "referencePlayMapCoordinate");

    final Point2D actualPlayMapCoordinate = Geometry.scale (referencePlayMapCoordinate, PlayMapSettings.REFERENCE_PLAY_MAP_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_SCALING);

    return Geometry.translate (actualPlayMapCoordinate, PlayMapSettings.ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_SCREEN_SPACE_TRANSLATION);
  }

  public static Point2D actualScreenSpaceToReferencePlayMapSpace (final Point2D actualScreenCoordinate,
                                                                  final Size2D screenSize)
  {
    Arguments.checkIsNotNull (actualScreenCoordinate, "actualScreenCoordinate");
    Arguments.checkIsNotNull (screenSize, "screenSize");

    final Scaling2D actualToReferenceScreenScaling = Geometry.divide (GraphicsSettings.REFERENCE_SCREEN_SIZE, screenSize);
    final Point2D referenceScreenCoordinate = Geometry.scale (actualScreenCoordinate, actualToReferenceScreenScaling);

    return referenceScreenSpaceToReferencePlayMapSpace (referenceScreenCoordinate);
  }

  public static Point2D referenceScreenSpaceToReferencePlayMapSpace (final Point2D referenceScreenCoordinate)
  {
    Arguments.checkIsNotNull (referenceScreenCoordinate, "referenceScreenCoordinate");

    final Point2D actualPlayMapCoordinate = Geometry.translate (referenceScreenCoordinate, PlayMapSettings.REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION);

    return Geometry.scale (actualPlayMapCoordinate, PlayMapSettings.ACTUAL_PLAY_MAP_SPACE_TO_REFERENCE_PLAY_MAP_SPACE_SCALING);
  }

  public static Point2D toReferenceCountrySpace (final Point2D sourceSpaceCoordinate, final Point2D countryOriginSourceSpace)
  {
    Arguments.checkIsNotNull (sourceSpaceCoordinate, "sourceSpaceCoordinate");
    Arguments.checkIsNotNull (countryOriginSourceSpace, "countryOriginSourceSpace");

    return Geometry.absoluteValue (Geometry.translate (sourceSpaceCoordinate, new Translation2D (-countryOriginSourceSpace.getX (), -countryOriginSourceSpace.getY ())));
  }
  // @formatter:on

  private CoordinateSpaces ()
  {
    Classes.instantiationNotAllowed ();
  }
}
