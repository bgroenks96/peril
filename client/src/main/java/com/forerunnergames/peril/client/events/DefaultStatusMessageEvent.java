package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

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
    return Strings.format ("{}: Message: {} | Recipients: {}", getClass ().getSimpleName (), message, recipients);
  }
}
