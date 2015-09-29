package com.forerunnergames.peril.client.ui.screens;

public interface ScreenChanger
{
  void toPreviousScreenOr (final ScreenId defaultScreenId);

  void toPreviousScreenSkippingOr (final ScreenId skipScreenId, final ScreenId defaultScreenId);

  void toScreen (final ScreenId id);
}
