/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

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
