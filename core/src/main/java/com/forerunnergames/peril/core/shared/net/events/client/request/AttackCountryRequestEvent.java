package com.forerunnergames.peril.core.shared.net.events.client.request;

import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ClientRequestEvent;

public final class AttackCountryRequestEvent implements ClientRequestEvent
{
  @RequiredForNetworkSerialization
  public AttackCountryRequestEvent ()
  {
  }
}
