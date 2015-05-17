package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.forerunnergames.peril.client.input.LibGdxMouseInput;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ScreenFactoryCreator
{
  public static ScreenFactory create (final ScreenChanger screenChanger, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new ScreenFactory (Assets.skin, screenChanger, new LibGdxScreenSize (Gdx.graphics), new LibGdxMouseInput (
            Gdx.input), new SpriteBatch (), eventBus);
  }
}
