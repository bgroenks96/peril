package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.core.model.Packets;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public final class StatusMessageEventFactory
{
  public static StatusMessageEvent create (final String messageText, final Player messageRecipient)
  {
    return create (messageText, ImmutableSet.of (messageRecipient));
  }

  public static StatusMessageEvent create (final String messageText, final ImmutableSet <Player> messageRecipients)
  {
    Arguments.checkIsNotNull (messageText, "messageText");
    Arguments.checkIsNotNull (messageRecipients, "messageRecipients");
    Arguments.checkHasNoNullElements (messageRecipients, "messageRecipients");

    return new DefaultStatusMessageEvent (new DefaultStatusMessage (messageText),
            Packets.fromPlayers (messageRecipients));
  }
}
