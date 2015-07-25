package com.forerunnergames.peril.core.shared.net.events.server.defaults;

import com.forerunnergames.peril.core.shared.net.events.server.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.collect.ImmutableSet;

public final class DefaultStatusMessageEvent implements StatusMessageEvent
{
  private final StatusMessage message;
  private final ImmutableSet <PlayerPacket> recipients;

  public DefaultStatusMessageEvent (final StatusMessage message, final ImmutableSet <PlayerPacket> recipients)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (recipients, "recipients");

    this.message = message;
    this.recipients = recipients;
  }

  @Override
  public StatusMessage getMessage ()
  {
    return message;
  }

  @Override
  public String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public ImmutableSet <PlayerPacket> getRecipients ()
  {
    return recipients;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("%{}: Message: {} | Recipients: {}", getClass ().getSimpleName (), message, recipients);
  }

  @RequiredForNetworkSerialization
  private DefaultStatusMessageEvent ()
  {
    message = null;
    recipients = null;
  }
}
