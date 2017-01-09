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

package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.client.configuration.ClientConfiguration;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.collect.ImmutableSet;

public final class PlayGameEvent implements LocalEvent
{
  private final GameServerConfiguration gameServerConfig;
  private final ClientConfiguration clientConfig;
  private final PlayerPacket selfPlayer;
  private final ImmutableSet <PlayerPacket> allPlayers;
  private final PlayMap playMap;

  public PlayGameEvent (final GameServerConfiguration gameServerConfig,
                        final ClientConfiguration clientConfig,
                        final PlayerPacket selfPlayer,
                        final ImmutableSet <PlayerPacket> allPlayers,
                        final PlayMap playMap)

  {
    Arguments.checkIsNotNull (gameServerConfig, "gameServerConfig");
    Arguments.checkIsNotNull (clientConfig, "clientConfig");
    Arguments.checkIsNotNull (selfPlayer, "selfPlayer");
    Arguments.checkIsNotNull (allPlayers, "allPlayers");
    Arguments.checkHasNoNullElements (allPlayers, "allPlayers");
    Arguments.checkIsNotNull (playMap, "playMap");

    this.gameServerConfig = gameServerConfig;
    this.clientConfig = clientConfig;
    this.selfPlayer = selfPlayer;
    this.allPlayers = allPlayers;
    this.playMap = playMap;
  }

  public GameServerConfiguration getGameServerConfiguration ()
  {
    return gameServerConfig;
  }

  public GameRules getGameRules ()
  {
    return gameServerConfig.getGameRules ();
  }

  public ClientConfiguration getClientConfiguration ()
  {
    return clientConfig;
  }

  public PlayerPacket getSelfPlayer ()
  {
    return selfPlayer;
  }

  public String getSelfPlayerName ()
  {
    return selfPlayer.getName ();
  }

  public ImmutableSet <PlayerPacket> getAllPlayers ()
  {
    return allPlayers;
  }

  public int getPlayerCount ()
  {
    return allPlayers.size ();
  }

  public boolean isFirstPlayerInGame ()
  {
    return getPlayerCount () == 1;
  }

  public PlayMapMetadata getPlayMapMetadata ()
  {
    return gameServerConfig.getPlayMapMetadata ();
  }

  public PlayMap getPlayMap ()
  {
    return playMap;
  }

  public int getWinningCountryCount ()
  {
    return gameServerConfig.getWinningCountryCount ();
  }

  @Override
  public String toString ()
  {
    return Strings.format (
                           "{}: GameServerConfig: [{}] | ClientConfig: [{}] | SelfPlayer: [{}] | "
                                   + "AllPlayers: [{}] | PlayMap: [{}]",
                           getClass ().getSimpleName (), gameServerConfig, clientConfig, selfPlayer, allPlayers,
                           playMap);
  }
}
