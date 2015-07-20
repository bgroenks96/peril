package com.forerunnergames.peril.core.shared.net.events.server.interfaces;

import com.forerunnergames.peril.core.shared.net.events.interfaces.MessageEvent;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableSet;

public interface StatusMessageEvent extends MessageEvent <StatusMessage>, ServerNotificationEvent
{
  ImmutableSet <PlayerPacket> getRecipients ();
}
