package com.forerunnergames.peril.core.shared.net.events.request;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.RequestEvent;

public final class ChangePlayerColorRequestEvent implements PlayerColorEvent, RequestEvent
{
  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorRequestEvent (final Player player,
                                        final PlayerColor currentColor,
                                        final PlayerColor previousColor)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentColor, "currentColor");
    Arguments.checkIsNotNull (previousColor, "previousColor");

    playerColorEvent = new DefaultPlayerColorEvent (currentColor);
  }

  @Override
  public PlayerColor getRequestedColor ()
  {
    return playerColorEvent.getRequestedColor ();
  }


  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), playerColorEvent);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerColorRequestEvent ()
  {
    playerColorEvent = null;
  }
}
