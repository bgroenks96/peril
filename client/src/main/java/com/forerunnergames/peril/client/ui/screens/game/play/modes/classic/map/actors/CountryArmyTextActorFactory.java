package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.data.CountryImageData;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class CountryArmyTextActorFactory
{
  private static final Vector2 tempPosition = new Vector2 ();

  public static CountryArmyTextActor create (final CountryImageData countryImageData,
                                             final BitmapFont font,
                                             final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (countryImageData, "countryImageData");
    Arguments.checkIsNotNull (font, "font");
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    final CountryArmyTextActor countryArmyTextActor = new DefaultCountryArmyTextActor (font);

    tempPosition.set (countryImageData.getReferenceTextUpperLeft ());
    tempPosition.y = playMapReferenceSize.y - tempPosition.y;
    tempPosition.scl (PlayMapSettings.referenceToActualPlayMapScaling (playMapReferenceSize));

    countryArmyTextActor.setCircleTopLeft (tempPosition);
    countryArmyTextActor.setCircleSize (PlayMapSettings.countryArmyCircleSizeActualPlayMapSpace (playMapReferenceSize));
    countryArmyTextActor.asActor ().setName (countryImageData.getCountryName ());

    return countryArmyTextActor;
  }

  private CountryArmyTextActorFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
