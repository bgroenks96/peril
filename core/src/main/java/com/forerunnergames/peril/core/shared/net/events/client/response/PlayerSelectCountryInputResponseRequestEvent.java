package com.forerunnergames.peril.core.shared.net.events.client.response;

import com.forerunnergames.peril.core.shared.net.events.client.interfaces.InputResponseRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class PlayerSelectCountryInputResponseRequestEvent implements InputResponseRequestEvent
{
  private final String selectedCountryName;

  public PlayerSelectCountryInputResponseRequestEvent (final String selectedCountryName)
  {
    Arguments.checkIsNotNull (selectedCountryName, "selectedCountryName");
    this.selectedCountryName = selectedCountryName;
  }

  public String getSelectedCountryName ()
  {
    return selectedCountryName;
  }

  @RequiredForNetworkSerialization
  private PlayerSelectCountryInputResponseRequestEvent ()
  {
    selectedCountryName = null;
  }
}
