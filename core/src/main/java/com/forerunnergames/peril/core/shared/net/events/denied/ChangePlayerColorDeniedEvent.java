package com.forerunnergames.peril.core.shared.net.events.denied;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.colorFrom;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

public final class ChangePlayerColorDeniedEvent extends AbstractDeniedEvent <ChangePlayerColorDeniedEvent.Reason>
        implements PlayerColorEvent
{
  private final PlayerColorEvent playerColorEvent;

  public enum Reason
  {
    REQUESTED_COLOR_EQUALS_EXISTING_COLOR,
    COLOR_ALREADY_TAKEN,
    REQUESTED_COLOR_INALID,
  }

  public ChangePlayerColorDeniedEvent (final ChangePlayerColorRequestEvent event, final Reason reason)
  {
    this (colorFrom (event), reason);
  }

  public ChangePlayerColorDeniedEvent (final PlayerColor color, final Reason reason)
  {
    super (reason);

    Arguments.checkIsNotNull (color, "color");

    playerColorEvent = new DefaultPlayerColorEvent (color);
  }

  @Override
  public PlayerColor getRequestedColor ()
  {
    return playerColorEvent.getRequestedColor ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s | %3$s", ((Object) this).getClass ().getSimpleName (), playerColorEvent,
                          super.toString ());
  }

  @RequiredForNetworkSerialization
  private ChangePlayerColorDeniedEvent ()
  {
    playerColorEvent = null;
  }
}
