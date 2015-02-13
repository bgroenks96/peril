package com.forerunnergames.peril.client.settings;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.geometry.Translation2D;

public final class InputSettings
{
  public static final Translation2D ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION = new Translation2D (-1, -2);
  public static final Translation2D ACTUAL_SCREEN_SPACE_TO_ACTUAL_INPUT_SPACE_TRANSLATION = new Translation2D (1, 2);

  private InputSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
