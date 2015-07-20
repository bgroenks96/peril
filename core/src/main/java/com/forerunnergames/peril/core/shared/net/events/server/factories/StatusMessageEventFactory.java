package com.forerunnergames.peril.core.shared.net.events.server.factories;

import com.forerunnergames.peril.core.model.Packets;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.shared.net.events.server.defaults.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

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

  @RequiredForNetworkSerialization
  private StatusMessageEventFactory ()
  {
  }
}
