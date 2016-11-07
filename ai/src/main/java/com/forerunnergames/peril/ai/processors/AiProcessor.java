/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.ai.processors;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public interface AiProcessor
{
  void activate ();

  void deactivate ();

  void send (final ClientRequestEvent event, final String playerName);

  boolean isSelf (final PlayerEvent event);

  /**
   * Get this AI's player name.
   */
  String getPlayerName ();

  /**
   * Get this AI's player name without any clan tag.
   */
  String getPlayerNameDeTagged ();

  /**
   * Get the specified player name without any clan tag.
   */
  String deTag (final String playerName);

  /**
   * Gets whether the specified player name contains a clan tag.
   */
  boolean hasClan (final String playerName);

  /**
   * Get the clan acronym from the specified player name, or empty string if there is no clan tag.
   */
  String clanFrom (final String playerName);

  GameServerConfiguration getConfig ();

  /**
   * Check whether an action should be performed by an AI actor, based on the specified probability.
   *
   * @param probability
   *          The specified probability, in the range [0.0, 1.0] of how often this method should return true, where 0.0
   *          will always return false, 1.0 will always return true, and 0.5, for example, will (on average) return true
   *          50% of the time and false 50% of the time.
   *
   * @return Whether the action should be carried out based on performing a random true/false choice, taking into
   *         account the specified probability for a 'true' choice.
   */
  boolean shouldAct (final double probability);
}
