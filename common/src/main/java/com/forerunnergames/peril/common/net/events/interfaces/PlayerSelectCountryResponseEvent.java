package com.forerunnergames.peril.common.net.events.interfaces;

import com.forerunnergames.tools.net.events.remote.ResponseEvent;

public interface PlayerSelectCountryResponseEvent extends ResponseEvent
{
  String getSelectedCountryName ();
}
