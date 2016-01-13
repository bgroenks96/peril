package com.forerunnergames.peril.common.net.events.server.interfaces;

public interface CountryArmiesChangedEvent extends CountryNotificationEvent
{
  int getCountryDeltaArmyCount ();
}
