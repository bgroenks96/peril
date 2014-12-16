package com.forerunnergames.peril.client.ui.screens;

import com.forerunnergames.tools.common.Arguments;

public abstract class AbstractScreenChanger implements ScreenChanger
{
  private final ScreenController screenController;

  protected AbstractScreenChanger (final ScreenController screenController)
  {
    Arguments.checkIsNotNull (screenController, "screenController");

    this.screenController = screenController;
  }

  protected void setScreenTo (final ScreenId id)
  {
    Arguments.checkIsNotNull (id, "id");

    screenController.setScreenTo (id);
  }

  @Override
  public void next()
  {
  }

  @Override
  public void previous()
  {
  }
}
