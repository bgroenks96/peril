package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.core.shared.net.events.interfaces.MessageEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.collect.ImmutableSet;

public interface StatusMessageEvent extends MessageEvent <StatusMessage>, LocalEvent
{
  ImmutableSet <PlayerPacket> getRecipients ();
}
