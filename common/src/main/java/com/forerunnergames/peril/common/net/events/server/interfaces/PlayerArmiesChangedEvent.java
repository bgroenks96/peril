package com.forerunnergames.peril.common.net.events.server.interfaces;

/**
 * All implementations of PlayerArmiesChangedEvent should be constructed with an updated PlayerPacket (accessible via
 * {@link #getPlayer()} that contains the number of armies in the player's hand <b>after</b> the delta is applied.
 */
public interface PlayerArmiesChangedEvent extends PlayerNotificationEvent
{
  int getPlayerDeltaArmyCount ();
}
