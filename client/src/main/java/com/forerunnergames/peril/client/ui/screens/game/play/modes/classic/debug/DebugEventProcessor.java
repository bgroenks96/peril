package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withMessageTextFrom;

import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.shared.net.events.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Event;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public class DebugEventProcessor
{
  private final MBassador <Event> eventBus;

  public DebugEventProcessor (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;

    eventBus.subscribe (this);
  }

  @Handler
  public void onChatMessageRequestEvent (final ChatMessageRequestEvent event)
  {
    final Author author = PlayerFactory.create ("Author");

    eventBus.publish (new ChatMessageSuccessEvent (new DefaultChatMessage (author, withMessageTextFrom (event))));
  }
}
