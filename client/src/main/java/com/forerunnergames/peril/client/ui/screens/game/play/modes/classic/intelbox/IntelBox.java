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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.person.SpectatorPacket;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;

import javax.annotation.Nullable;

public interface IntelBox
{
  void setSelf (final PlayerPacket player);

  void setSelf (final SpectatorPacket spectator);

  void setGameServerConfiguration (final GameServerConfiguration config);

  void setClientConfiguration (final ClientConfiguration config);

  void setPlayMapMetadata (final PlayMapMetadata playMapMetadata);

  void setGamePhase (final GamePhase phase);

  void setGameRound (final int round);

  void setOwnedCountriesForSelf (final int countries, @Nullable final PersonPacket person);

  void addOwnedCountryForSelf (@Nullable final PersonPacket person);

  void removeOwnedCountryForSelf (@Nullable final PersonPacket person);

  void clear ();

  Actor asActor ();

  void refreshAssets ();
}
