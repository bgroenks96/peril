package com.forerunnergames.peril.client.settings;

import com.badlogic.gdx.math.Vector2;

import com.forerunnergames.tools.common.Classes;

public final class InputSettings
{
  public static final Vector2 ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION = new Vector2 (0, -3);
  public static final Vector2 MENU_NORMAL_MOUSE_CURSOR_HOTSPOT = new Vector2 (0, 0);
  public static final Vector2 PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT = new Vector2 (0, 0);

  private InputSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
