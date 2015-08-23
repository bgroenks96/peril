package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableSet;

public final class PlayerLeaveGameEvent implements ServerNotificationEvent
{
  private final PlayerPacket player;
  private final ImmutableSet <PlayerPacket> playersLeftInGame;

  public PlayerLeaveGameEvent (final PlayerPacket player, final ImmutableSet <PlayerPacket> playersLeftInGame)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
    this.playersLeftInGame = playersLeftInGame;
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public String getPlayerName ()
  {
    return player.getName ();
  }

  public ImmutableSet <PlayerPacket> getPlayersLeftInGame ()
  {
    return playersLeftInGame;
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: Player: %2$s", getClass ().getSimpleName (), player);
  }

  @RequiredForNetworkSerialization
  private PlayerLeaveGameEvent ()
  {
    player = null;
    playersLeftInGame = null;
  }
}
