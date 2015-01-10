package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class DefendCountryDeniedEvent implements DeniedEvent <String>
{
  private final DeniedEvent <String> deniedEvent;

  public DefendCountryDeniedEvent (final String reason)
  {
    Arguments.checkIsNotNull (reason, "reason");

    deniedEvent = new DefaultDeniedEvent (reason);
  }

  @Override
  public String getReason()
  {
    return deniedEvent.getReason();
  }

  @RequiredForNetworkSerialization
  private DefendCountryDeniedEvent()
  {
    deniedEvent = null;
  }
}
