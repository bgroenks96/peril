package com.forerunnergames.peril.core.shared.net.events.success;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.currentColorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.previousColorFrom;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.SuccessEvent;

public final class ChangePlayerColorSuccessEvent implements PlayerColorEvent, SuccessEvent
{
  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorSuccessEvent (final ChangePlayerColorRequestEvent event)
  {
    this (playerFrom (event), currentColorFrom (event), previousColorFrom (event));
  }

  public ChangePlayerColorSuccessEvent (final Player player,
                                        final PlayerColor currentColor,
                                        final PlayerColor previousColor)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentColor, "color");
    Arguments.checkIsNotNull (previousColor, "previousColor");

    playerColorEvent = new DefaultPlayerColorEvent (player, currentColor, previousColor);
  }

  @Override
  public PlayerColor getCurrentColor ()
  {
    return playerColorEvent.getCurrentColor ();
  }

  @Override
  public PlayerColor getPreviousColor ()
  {
    return playerColorEvent.getPreviousColor ();
  }

  @Override
  public Player getPlayer ()
  {
    return playerColorEvent.getPlayer ();
  }

  @Override
  public String getPlayerName ()
  {
    return playerColorEvent.getPlayerName ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", ((Object) this).getClass ().getSimpleName (), playerColorEvent);
  }

  @RequiredForNetworkSerialization
  private ChangePlayerColorSuccessEvent ()
  {
    playerColorEvent = null;
  }
}
