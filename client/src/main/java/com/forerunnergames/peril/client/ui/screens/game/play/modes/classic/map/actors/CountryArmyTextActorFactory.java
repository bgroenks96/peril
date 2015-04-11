package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.tools.CoordinateSpaces;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Geometry;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Translation2D;

public final class CountryArmyTextActorFactory
{
  // @formatter:off
  public static CountryArmyTextActor create (final CountryImageData countryImageData)
  {
    Arguments.checkIsNotNull (countryImageData, "countryImageData");

    final Point2D textUpperLeftPlayMapReferenceSpace = countryImageData.getTextUpperLeftPlayMapReferenceSpace ();

    final Point2D textUpperLeftPlayMapReferenceSpaceFlippedY =
            Geometry.absoluteValue (Geometry.translate (textUpperLeftPlayMapReferenceSpace, new Translation2D (0, -PlayMapSettings.REFERENCE_HEIGHT)));

    final Point2D textUpperLeftActualPlayMapSpace = CoordinateSpaces.referencePlayMapSpaceToActualPlayMapSpace (textUpperLeftPlayMapReferenceSpaceFlippedY);

    final CountryArmyTextActor countryArmyTextActor = new CountryArmyTextActor ();

    countryArmyTextActor.setName (countryImageData.getName ());
    countryArmyTextActor.setCircleTopLeft (new Vector2 (textUpperLeftActualPlayMapSpace.getX (), textUpperLeftActualPlayMapSpace.getY ()));
    countryArmyTextActor.setCircleSize (PlayMapSettings.COUNTRY_ARMY_CIRCLE_SIZE_ACTUAL_PLAY_MAP_SPACE);

    return countryArmyTextActor;
  }
  // @formatter:on

  private CountryArmyTextActorFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
