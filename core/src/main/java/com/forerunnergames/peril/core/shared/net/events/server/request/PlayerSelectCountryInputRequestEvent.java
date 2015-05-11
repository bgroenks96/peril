package com.forerunnergames.peril.core.shared.net.events.server.request;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.InputRequestEvent;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputRequestEvent implements InputRequestEvent
{
  @RequiredForNetworkSerialization
  private PlayerSelectCountryInputRequestEvent ()
  {
  }
}
