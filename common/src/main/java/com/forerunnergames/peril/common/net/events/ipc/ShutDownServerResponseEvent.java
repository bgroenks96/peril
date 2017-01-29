package com.forerunnergames.peril.common.net.events.ipc;

import com.forerunnergames.peril.common.net.events.ipc.interfaces.ServerInterProcessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public class ShutDownServerResponseEvent extends AbstractInterProcessEvent implements ServerInterProcessEvent
{
  public enum ResponseCode
  {
    OK,
    FAILURE
  }

  private final ResponseCode responseCode;

  public ShutDownServerResponseEvent (final ResponseCode responseCode)
  {
    Arguments.checkIsNotNull (responseCode, "responseCode");

    this.responseCode = responseCode;
  }

  public ResponseCode getResponseCode ()
  {
    return responseCode;
  }

  public boolean success ()
  {
    return responseCode == ResponseCode.OK;
  }

  public boolean failures ()
  {
    return responseCode == ResponseCode.FAILURE;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | ResponseCode: {}", super.toString (), responseCode);
  }

  @RequiredForNetworkSerialization
  private ShutDownServerResponseEvent ()
  {
    responseCode = null;
  }
}
