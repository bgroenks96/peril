package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Translation2D;

public final class InputSettings
{
  public static final Translation2D REFERENCE_INPUT_SPACE_TO_SCREEN_SPACE_TRANSLATION = new Translation2D (-1, -2);
  public static final Translation2D REFERENCE_SCREEN_SPACE_TO_INPUT_SPACE_TRANSLATION = new Translation2D (
                  -REFERENCE_INPUT_SPACE_TO_SCREEN_SPACE_TRANSLATION.getX (),
                  -REFERENCE_INPUT_SPACE_TO_SCREEN_SPACE_TRANSLATION.getY ());

  private InputSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
