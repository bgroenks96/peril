package com.forerunnergames.peril.server.dispatchers;

import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerAnswerEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerOriginatedRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.tools.net.server.remote.RemoteClient;

public interface ClientRequestEventDispatchListener
{
  void handleEvent (final HumanJoinGameServerRequestEvent event, final RemoteClient client);

  void handleEvent (final AiJoinGameServerRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerJoinGameRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerQuitGameRequestEvent event, final RemoteClient client);

  void handleEvent (final SpectatorJoinGameRequestEvent event, final RemoteClient client);

  void handleEvent (final ChatMessageRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerOriginatedRequestEvent event, final RemoteClient client);

  void handleEvent (final PlayerAnswerEvent <?> event, final RemoteClient client);
}
