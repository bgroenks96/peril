package com.forerunnergames.peril.common.net.events.server.interfaces;

public interface PlayerArmiesChangedEvent extends PlayerNotificationEvent
{
  int getPlayerDeltaArmyCount ();
}
