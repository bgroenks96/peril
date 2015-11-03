package com.forerunnergames.peril.client.ui.screens;

public interface ScreenChanger
{
  void toPreviousScreenOrSkipping (final ScreenId defaultScreenId, final ScreenId... skipScreenIds);

  void toScreen (final ScreenId id);
}
