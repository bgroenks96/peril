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

package com.forerunnergames.peril.ai.processors;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEvent;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

import java.util.Collection;

/**
 * API for various event-based AI behavior processing.
 */
public interface AiProcessor
{
  /**
   * Causes this processor to start receiving server events.
   */
  void activate ();

  /**
   * Causes this processor to stop receiving server events, but doesn't shut down, only pauses. May call
   * {@link #activate()} again after.
   */
  void deactivate ();

  /**
   * First calls {@link #deactivate()}, and then permanently shuts down this processor and any resources it may be using
   * (thread pools, etc).
   */
  void shutDown ();

  /**
   * Sends the specified request to the server. It could be a request initiated solely by the AI client before joining
   * as a player (ClientRequestEvent), or after joining as a player (PlayerRequestEvent), or a response to a request
   * initiated by the server (ResponseRequestEvent}.
   *
   * @see ClientRequestEvent
   * @see com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent
   * @see com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent
   */
  void send (final ClientRequestEvent event);

  /**
   * Gets whether the player in the specified {@link PlayerEvent} is this AI player.
   */
  boolean isSelf (final PlayerEvent event);

  /**
   * Get this AI player's name.
   */
  String getPlayerName ();

  /**
   * Get this AI's player name without any clan tag.
   */
  String getPlayerNameDeTagged ();

  /**
   * Get the clan acronym from this AI player's name. <br/>
   * Note that AI players always have an [AI] clan tag, with AI clan acronym.
   *
   * @return AI
   */
  String getPlayerClan ();

  /**
   * Gets whether this AI player's name contains a clan tag. <br/>
   * Note that AI players always have an [AI] clan tag.
   *
   * @return true
   */
  boolean hasClan ();

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

  /**
   * Gets the game & server configuration for the game server this AI has joined as a player.
   */
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

  /**
   * Choose one of the specified choices at random, with equal probability.
   * 
   * @param choices
   *          Must not be null, must not contain any null elements.
   */
  <T> T chooseRandomly (final T... choices);

  /**
   * Choose one of the specified choices at random, with equal probability.
   *
   * @param choices
   *          Must not be null, must not contain any null elements.
   */
  <T> T chooseRandomly (final Collection <T> choices);

  /**
   * Choose a random integer in the range [lowerInclusiveBound, upperInclusiveBound], with equal probability.
   *
   * @param inclusiveLowerBound
   *          The inclusive lower bound, must be >= 0 and <= inclusiveUpperBound and < Integer.MAX_VALUE.
   * @param inclusiveUpperBound
   *          The inclusive upper bound, must be >= 0 and >= inclusiveLowerBound and < Integer.MAX_VALUE.
   */
  int chooseRandomly (final int inclusiveLowerBound, final int inclusiveUpperBound);
}
