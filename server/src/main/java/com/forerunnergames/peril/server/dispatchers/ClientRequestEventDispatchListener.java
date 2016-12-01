package com.forerunnergames.peril.server.dispatchers;

import com.forerunnergames.peril.common.net.events.client.interfaces.InformRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.interfaces.PlayerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.AiJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.HumanJoinGameServerRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerRejoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.SpectatorJoinGameRequestEvent;
import com.forerunnergames.tools.net.Remote;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;

public interface ClientRequestEventDispatchListener
{
  void handleEvent (final HumanJoinGameServerRequestEvent event, final Remote client);

  void handleEvent (final AiJoinGameServerRequestEvent event, final Remote client);

  void handleEvent (final PlayerJoinGameRequestEvent event, final Remote client);

  void handleEvent (final PlayerRejoinGameRequestEvent event, final Remote client);

  void handleEvent (final SpectatorJoinGameRequestEvent event, final Remote client);

  void handleEvent (final ChatMessageRequestEvent event, final Remote client);

  void handleEvent (final PlayerRequestEvent event, final Remote client);

  void handleEvent (final ResponseRequestEvent event, final Remote client);

  void handleEvent (final InformRequestEvent event, final Remote client);
}
