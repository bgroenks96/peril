package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerEvent;

public interface CountryEvent extends ServerEvent
{
  CountryPacket getCountry ();

  String getCountryName ();
}
