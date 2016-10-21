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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;

/**
 * Handles game logic for various game phases.
 */
public interface GamePhaseHandler
{
  GamePhaseHandler NULL = new NullGamePhaseHandler ();

  /**
   * Enable the handler. Call before performing game phase logic via {@link #execute()}, such as when the necessary game
   * phase becomes active, which will allow the handler to begin listening for events, for example. GamePhaseHandler's
   * can be re-used after activation by calling {@link #reset()}. Or {@link #reset()} can be called from within this
   * method.
   */
  void activate ();

  /**
   * Enable the handler only if the specified {@link PlayerPacket} matches the player previously set by
   * {@link #setSelfPlayer(PlayerPacket)}.
   *
   * @see #activate() ()
   */
  void activateForSelf (final PlayerPacket player);

  /**
   * Enable the handler only if the specified {@link PlayerPacket} does *not* match the player previously set by
   * {@link #setSelfPlayer(PlayerPacket)}.
   *
   * @see #activate()
   */
  void activateForEveryoneElse (final PlayerPacket player);

  /**
   * Perform the game phase logic here, after calling {@link #activate}.
   */
  void execute ();

  /**
   * Cancels the game phase logic that would have been executed by {@link #execute()}. Does not affect activation. It is
   * also a good place to call {@link #reset()}.
   */
  void cancel ();

  /**
   * Disable the handler. Call when finished performing game phase logic via {@link #execute()}, such as when the
   * necessary game phase is no longer active, which will prevent the handler from listening for events when it
   * shouldn't, for example. GamePhaseHandler's can be re-used after deactivation by calling {@link #reset()}, then one
   * of the {@link #activate()} methods again.
   */
  void deactivate ();

  /**
   * Disable the handler only if the specified {@link PlayerPacket} matches the player previously set by
   * {@link #setSelfPlayer(PlayerPacket)}.
   *
   * @see #deactivate()
   */
  void deactivateForSelf (final PlayerPacket player);

  /**
   * Disable the handler only if the specified {@link PlayerPacket} does *not* match the player previously set by
   * {@link #setSelfPlayer(PlayerPacket)}.
   *
   * @see #deactivate()
   */
  void deactivateForEveryoneElse (final PlayerPacket player);

  void setPlayMap (final PlayMap playMap);

  /**
   * Set the player identity of this handler. Allows for conditional activation / deactivation based on a
   * {@link PlayerPacket} received from the server.
   *
   * @param player
   *          The player to whom this client / handler belongs.
   */
  void setSelfPlayer (final PlayerPacket player);

  /**
   * Updates the self player's attributes, if the specified player is the self player.
   *
   * @param player
   *          The updated player, used to update the self player's attributes when player identity matches.
   */
  void updatePlayerForSelf (final PlayerPacket player);

  /**
   * Return this handler to an initialized state, ready to call {@link #execute()} again. Does not affect activation
   * status.
   */
  void reset ();
}
