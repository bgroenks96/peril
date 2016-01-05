package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

public final class PlayMapSettings
{
  public static final float ACTUAL_WIDTH = 1800;
  public static final float ACTUAL_HEIGHT = 788;
  public static final boolean ENABLE_HOVER_EFFECTS = true;
  public static final boolean ENABLE_CLICK_EFFECTS = false;
  public static final Vector2 REFERENCE_SCREEN_SPACE_TO_ACTUAL_PLAY_MAP_SPACE_TRANSLATION = new Vector2 (-12, -12);
  public static final Vector2 COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE = new Vector2 (32, 30);

  public static Vector2 referenceToActualPlayMapScaling (final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");
    Arguments.checkLowerExclusiveBound (playMapReferenceSize.x, 0, "playMapReferenceSize.x");
    Arguments.checkLowerExclusiveBound (playMapReferenceSize.y, 0, "playMapReferenceSize.y");

    return new Vector2 (ACTUAL_WIDTH / playMapReferenceSize.x, ACTUAL_HEIGHT / playMapReferenceSize.y);
  }

  public static Vector2 actualToReferencePlayMapScaling (final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    return new Vector2 (playMapReferenceSize.x / ACTUAL_WIDTH, playMapReferenceSize.y / ACTUAL_HEIGHT);
  }

  public static Vector2 countryArmyCircleSizeActualPlayMapSpace (final Vector2 playMapReferenceSize)
  {
    Arguments.checkIsNotNull (playMapReferenceSize, "playMapReferenceSize");

    return new Vector2 (COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE)
            .scl (referenceToActualPlayMapScaling (playMapReferenceSize));
  }

  private PlayMapSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
