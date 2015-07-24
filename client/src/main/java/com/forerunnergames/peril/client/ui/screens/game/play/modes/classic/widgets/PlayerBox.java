package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
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
  private final Collection <PlayerPacket> turnOrderedPlayers = Collections.synchronizedSortedSet (new TreeSet <> (
          PlayerPacket.TURN_ORDER_COMPARATOR));

  public PlayerBox (final MessageBox <Message> messageBox)
  {
    Arguments.checkIsNotNull (messageBox, "messageBox");

    this.messageBox = messageBox;
  }

  public void setPlayers (final ImmutableSet <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    if (turnOrderedPlayers.isEmpty () && players.isEmpty ()) return;

    turnOrderedPlayers.clear ();
    turnOrderedPlayers.addAll (players);

    resetMessageBox ();
  }

  public void addPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (turnOrderedPlayers.add (player)) resetMessageBox ();
  }

  public void removePlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (turnOrderedPlayers.remove (player)) resetMessageBox ();
  }

  public void clear ()
  {
    messageBox.clear ();
  }

  public Actor asActor ()
  {
    return messageBox.asActor ();
  }

  private void resetMessageBox ()
  {
    messageBox.clear ();

    synchronized (turnOrderedPlayers)
    {
      for (final PlayerPacket player : turnOrderedPlayers)
      {
        messageBox.addMessage (new DefaultMessage (Strings.toMixedOrdinal (player.getTurnOrder ()) + ". "
                + player.getName ()));
      }
    }
  }
}
