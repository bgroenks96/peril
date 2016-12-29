package com.forerunnergames.peril.common.net.events.server.denied;

import com.forerunnergames.peril.common.net.events.server.defaults.AbstractPlayerDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerRetreatDeniedEvent.Reason;

/**
 * Note: This event is currently never sent by Core, since retreats are always allowed while the inform event is valid.
 */
public class PlayerRetreatDeniedEvent extends AbstractPlayerDeniedEvent <Reason>
{
  public enum Reason
  {
    NO_COWARDS_ALLOWED
  }
}
