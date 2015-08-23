package com.forerunnergames.peril.common.eventbus;

import com.forerunnergames.tools.common.Arguments;

import net.engio.mbassy.bus.common.DeadMessage;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Listener (references = References.Strong)
final class DeadEventHandler
{
  private static final Logger log = LoggerFactory.getLogger (DeadEventHandler.class);

  @Handler
  public void onDeadMessage (final DeadMessage deadMessage)
  {
    Arguments.checkIsNotNull (deadMessage, "deadMessage");

    log.warn ("Dead event detected, no handlers are registered to receive it. Event [{}]", deadMessage.getMessage ());
  }
}
