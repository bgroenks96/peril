package com.forerunnergames.peril.core.shared.net.events.denied;

import static com.forerunnergames.peril.core.shared.net.events.EventInterpreter.currentColorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventInterpreter.playerFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventInterpreter.previousColorFrom;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class ChangePlayerColorDeniedEvent implements PlayerColorEvent, DeniedEvent
{
  private final PlayerColorEvent playerColorEvent;
  private final DeniedEvent deniedEvent;

  public ChangePlayerColorDeniedEvent (final ChangePlayerColorRequestEvent event, final String reasonForDenial)
  {
    this (playerFrom (event), currentColorFrom (event), previousColorFrom (event), reasonForDenial);
  }

  public ChangePlayerColorDeniedEvent (final Player player,
                                       final PlayerColor currentColor,
                                       final PlayerColor previousColor,
                                       final String reasonForDenial)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (currentColor, "color");
    Arguments.checkIsNotNull (previousColor, "previousColor");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    playerColorEvent = new DefaultPlayerColorEvent (player, currentColor, previousColor);
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
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
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s",
            getClass().getSimpleName(), playerColorEvent.toString(), deniedEvent.toString());
  }

  // Required for network serialization
  private ChangePlayerColorDeniedEvent()
  {
    deniedEvent = null;
    playerColorEvent = null;
  }
}
