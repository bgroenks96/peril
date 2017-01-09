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

package com.forerunnergames.peril.common.net.events.defaults;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.packets.person.PersonSentience;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import java.util.UUID;

import javax.annotation.Nullable;

public abstract class AbstractPlayerJoinGameRequestEvent implements PlayerJoinGameRequestEvent
{
  private final String playerName;
  private final PersonSentience playerSentience;
  @Nullable
  private final UUID playerSecretId;

  protected AbstractPlayerJoinGameRequestEvent (final String playerName, final PersonSentience playerSentience)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (playerSentience, "playerSentience");

    this.playerName = playerName;
    this.playerSentience = playerSentience;
    playerSecretId = null;
  }

  protected AbstractPlayerJoinGameRequestEvent (final String playerName,
                                                final PersonSentience playerSentience,
                                                final UUID playerSecretId)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (playerSentience, "playerSentience");
    Arguments.checkIsNotNull (playerSecretId, "playerSecretId");

    this.playerName = playerName;
    this.playerSentience = playerSentience;
    this.playerSecretId = playerSecretId;
  }

  @RequiredForNetworkSerialization
  protected AbstractPlayerJoinGameRequestEvent ()
  {
    playerName = null;
    playerSentience = null;
    playerSecretId = null;
  }

  @Override
  public String getPlayerName ()
  {
    return playerName;
  }

  @Override
  public PersonSentience getPlayerSentience ()
  {
    return playerSentience;
  }

  @Override
  @Nullable
  public UUID getPlayerSecretId ()
  {
    return playerSecretId;
  }

  @Override
  public boolean hasPlayerSecretId ()
  {
    return playerSecretId != null;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: PlayerName: [{}] | PlayerSentience: [{}] | PlayerSecretId: [{}]",
                           getClass ().getSimpleName (), playerName, playerSentience, playerSecretId);
  }
}
