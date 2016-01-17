package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

public final class PlayerClaimCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final String claimedCountryName;

  public PlayerClaimCountryResponseRequestEvent (final String claimedCountryName)
  {
    Arguments.checkIsNotNull (claimedCountryName, "claimedCountryName");

    this.claimedCountryName = claimedCountryName;
  }

  public String getClaimedCountryName ()
  {
    return claimedCountryName;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerClaimCountryRequestEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Claimed Country Name: {}", getClass ().getSimpleName (), claimedCountryName);
  }

  @RequiredForNetworkSerialization
  private PlayerClaimCountryResponseRequestEvent ()
  {
    claimedCountryName = null;
  }
}
