package com.forerunnergames.peril.core.shared.net.events.denied;

import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.CommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.CommandMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

public final class CommandMessageDeniedEvent implements CommandMessageEvent, DeniedEvent
{
  private final CommandMessageEvent commandMessageEvent;
  private final DeniedEvent deniedEvent;

  public CommandMessageDeniedEvent (final CommandMessage message, final String reasonForDenial)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (reasonForDenial, "reasonForDenial");

    commandMessageEvent = new DefaultCommandMessageEvent (message);
    deniedEvent = new DefaultDeniedEvent (reasonForDenial);
  }

  @Override
  public CommandMessage getMessage()
  {
    return commandMessageEvent.getMessage();
  }

  @Override
  public String getMessageText()
  {
    return commandMessageEvent.getMessageText();
  }

  @Override
  public String getReasonForDenial()
  {
    return deniedEvent.getReasonForDenial();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s | %3$s", getClass().getSimpleName(), commandMessageEvent, deniedEvent);
  }

  // Required for network serialization
  private CommandMessageDeniedEvent()
  {
    commandMessageEvent = null;
    deniedEvent = null;
  }
}
