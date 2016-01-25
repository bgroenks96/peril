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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;

public final class PlayerBox
{
  private final MessageBox <Message> messageBox;
  private final Collection <PlayerPacket> turnOrderedPlayers = Collections
          .synchronizedSortedSet (new TreeSet<> (PlayerPacket.TURN_ORDER_COMPARATOR));

  public PlayerBox (final MessageBox <Message> messageBox)
  {
    Arguments.checkIsNotNull (messageBox, "messageBox");

    this.messageBox = messageBox;
  }

  public void setPlayers (final ImmutableSet <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    turnOrderedPlayers.clear ();
    if (turnOrderedPlayers.addAll (players)) updateMessageBox ();
  }

  public void addPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (turnOrderedPlayers.add (player)) updateMessageBox ();
  }

  public void removePlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (turnOrderedPlayers.remove (player)) updateMessageBox ();
  }

  public void clear ()
  {
    messageBox.clear ();
    turnOrderedPlayers.clear ();
  }

  public Actor asActor ()
  {
    return messageBox.asActor ();
  }

  public void refreshAssets ()
  {
    messageBox.refreshAssets ();
  }

  private void updateMessageBox ()
  {
    messageBox.clear ();

    synchronized (turnOrderedPlayers)
    {
      for (final PlayerPacket player : turnOrderedPlayers)
      {
        messageBox.addMessage (new DefaultMessage (
                Strings.toMixedOrdinal (player.getTurnOrder ()) + ". " + player.getName ()));
      }
    }
  }
}
