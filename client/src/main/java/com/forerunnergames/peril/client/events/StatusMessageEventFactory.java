package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class StatusMessageEventFactory
{
  public static StatusMessageEvent create (final String messageText, final PlayerPacket messageRecipient)
  {
    return create (messageText, ImmutableSet.of (messageRecipient));
  }

  public static StatusMessageEvent create (final String messageText,
                                           final ImmutableSet <PlayerPacket> messageRecipients)
  {
    Arguments.checkIsNotNull (messageText, "messageText");
    Arguments.checkIsNotNull (messageRecipients, "messageRecipients");
    Arguments.checkHasNoNullElements (messageRecipients, "messageRecipients");

    return new DefaultStatusMessageEvent (new DefaultStatusMessage (messageText), messageRecipients);
  }

  public static StatusMessageEvent create (final String messageText)
  {
    return create (messageText, ImmutableSet.<PlayerPacket> of ());
  }
}
