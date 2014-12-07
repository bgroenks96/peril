package com.forerunnergames.peril.core.shared.net.events.denied;

import static com.forerunnergames.peril.core.shared.net.events.EventInterpreter.*;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.tools.common.Arguments;

public final class ChangePlayerColorDeniedEvent extends AbstractDeniedEvent <ChangePlayerColorDeniedEvent.REASON>
        implements PlayerColorEvent
{
  public enum REASON
  {
    REQUESTED_COLOR_EQUALS_EXISTING_COLOR,
    COLOR_ALREADY_TAKEN
  }

  private final PlayerColorEvent playerColorEvent;

  public ChangePlayerColorDeniedEvent (final ChangePlayerColorRequestEvent event, final REASON reason)
  {
    this (playerFrom (event), currentColorFrom (event), previousColorFrom (event), reason);
  }

  public ChangePlayerColorDeniedEvent (final Player player,
                                       final PlayerColor currentColor,
                                       final PlayerColor previousColor,
                                       final REASON reason)
  {
    super (reason);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentColor, "color");
    Arguments.checkIsNotNull (previousColor, "previousColor");

    playerColorEvent = new DefaultPlayerColorEvent (player, currentColor, previousColor);
  }
  @Override
  public Player getPlayer()
  {
    return playerColorEvent.getPlayer();
  }

  @Override
  public String getPlayerName()
  {
    return playerColorEvent.getPlayerName();
  }

  @Override
  public PlayerColor getCurrentColor()
  {
    return playerColorEvent.getCurrentColor();
  }

  @Override
  public PlayerColor getPreviousColor()
  {
    return playerColorEvent.getPreviousColor();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s", getClass().getSimpleName(), playerColorEvent, super.toString());
  }

  @RequiredForNetworkSerialization
  private ChangePlayerColorDeniedEvent()
  {
    playerColorEvent = null;
  }
}
