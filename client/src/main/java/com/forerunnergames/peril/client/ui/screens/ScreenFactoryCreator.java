package com.forerunnergames.peril.client.ui.screens;

import com.badlogic.gdx.Gdx;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.assets.AssetUpdater;
import com.forerunnergames.peril.client.input.LibGdxMouseInput;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.SpriteBatchFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;

public final class ScreenFactoryCreator
{
  private final AssetManager assetManager;
  private final AssetUpdater assetUpdater;
  private final MBassador <Event> eventBus;

  public ScreenFactoryCreator (final AssetManager assetManager,
                               final AssetUpdater assetUpdater,
                               final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (assetUpdater, "assetUpdater");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.assetManager = assetManager;
    this.assetUpdater = assetUpdater;
    this.eventBus = eventBus;
  }

  public ScreenFactory create (final ScreenChanger screenChanger)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");

    return new ScreenFactory (screenChanger,
            new LibGdxScreenSize (Gdx.graphics, ScreenSettings.REFERENCE_SCREEN_WIDTH,
                    ScreenSettings.REFERENCE_SCREEN_HEIGHT),
            new LibGdxMouseInput (Gdx.input), SpriteBatchFactory.create (assetManager), assetManager, assetUpdater,
            eventBus);
  }
}
