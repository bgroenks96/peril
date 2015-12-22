package com.forerunnergames.peril.common.net.events.server.interfaces;

import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangeDeniedEvent.Reason;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;

public interface CountryOwnerChangeDeniedEvent extends DeniedEvent <Reason>
{
  enum Reason
  {
    COUNTRY_ALREADY_OWNED,
    COUNTRY_DOES_NOT_EXIST,
    COUNTRY_DISABLED,
    NOT_OWNER_OF_COUNTRY,
    INSUFFICIENT_ARMIES,
    COUNTRY_NOT_ADJACENT
  }
}
