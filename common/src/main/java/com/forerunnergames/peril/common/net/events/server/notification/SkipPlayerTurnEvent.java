package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.game.PlayerColor;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerNotificationEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

public final class SkipPlayerTurnEvent implements PlayerNotificationEvent
{
  private final PlayerPacket player;

  public SkipPlayerTurnEvent (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    this.player = player;
  }

  @Override
  public PlayerPacket getPlayer ()
  {
    return player;
  }

  @Override
  public String getPlayerName ()
  {
    return player.getName ();
  }

  @Override
  public PlayerColor getPlayerColor ()
  {
    return player.getColor ();
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Player: [{}]", this.getClass ().getSimpleName (), this.player);
  }
}
