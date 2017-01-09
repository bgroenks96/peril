/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.widgets.personicons.players;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerIconWidgetFactoryCreator
{
  private static final Logger log = LoggerFactory.getLogger (PlayerIconWidgetFactoryCreator.class);

  public static PlayerIconWidgetFactory create (final PlayerPacket player,
                                                final AssetManager assetManager,
                                                final AssetDescriptor <Skin> skinAssetDescriptor)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (skinAssetDescriptor, "skinAssetDescriptor");
    Arguments.checkIsNotNull (assetManager, "assetManager");

    switch (player.getSentience ())
    {
      case HUMAN:
      {
        return new AbstractHumanPlayerIconWidgetFactory (assetManager)
        {
          @Override
          protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
          {
            return skinAssetDescriptor;
          }
        };
      }
      case AI:
      {
        return new AbstractAiPlayerIconWidgetFactory (assetManager)
        {
          @Override
          protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
          {
            return skinAssetDescriptor;
          }
        };
      }
      default:
      {
        log.warn ("Unrecognized sentience for player [{}].", player);

        return new AbstractNullPlayerIconWidgetFactory (assetManager)
        {
          @Override
          protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
          {
            return skinAssetDescriptor;
          }
        };
      }
    }
  }

  private PlayerIconWidgetFactoryCreator ()
  {
    Classes.instantiationNotAllowed ();
  }
}
