package com.forerunnergames.peril.core.shared.net.kryonet;

import com.forerunnergames.peril.core.model.player.DefaultPlayer;
import com.forerunnergames.peril.core.model.person.PersonIdentity;
import com.forerunnergames.peril.core.model.player.PlayerColor;
import com.forerunnergames.peril.core.model.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.defaults.AbstractDefaultMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultCommandMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultJoinServerEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultKickEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultOpenServerEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultPlayerTurnOrderEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerTurnOrderDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChatMessageDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.JoinMultiplayerServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.KickPlayerFromGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.OpenMultiplayerServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerTurnOrderRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChatMessageRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.JoinMultiplayerServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.KickPlayerFromGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.OpenMultiplayerServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.QuitMultiplayerServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerColorSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerLimitSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerTurnOrderSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.CloseMultiplayerServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.JoinMultiplayerServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.KickPlayerFromGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.OpenMultiplayerServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerLeaveGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.StatusMessageSuccessEvent;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultCommandMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Id;

import com.google.common.collect.ImmutableSet;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public final class KryonetRegistration
{
  public static final ImmutableSet <Class <?>> CLASSES = ImmutableSet.of (
          AbstractDefaultMessageEvent.class,
          ChangePlayerColorDeniedEvent.class,
          ChangePlayerColorRequestEvent.class,
          ChangePlayerColorSuccessEvent.class,
          ChangePlayerLimitDeniedEvent.class,
          ChangePlayerLimitRequestEvent.class,
          ChangePlayerLimitSuccessEvent.class,
          ChangePlayerTurnOrderDeniedEvent.class,
          ChangePlayerTurnOrderRequestEvent.class,
          ChangePlayerTurnOrderSuccessEvent.class,
          ChatMessageDeniedEvent.class,
          ChatMessageRequestEvent.class,
          ChatMessageSuccessEvent.class,
          CloseMultiplayerServerSuccessEvent.class,
          DefaultChatMessageEvent.class,
          DefaultCommandMessageEvent.class,
          DefaultDeniedEvent.class,
          DefaultJoinServerEvent.class,
          DefaultKickEvent.class,
          DefaultStatusMessageEvent.class,
          DefaultOpenServerEvent.class,
          DefaultPlayerColorEvent.class,
          DefaultPlayerDeniedEvent.class,
          DefaultPlayerEvent.class,
          DefaultPlayerTurnOrderEvent.class,
          JoinMultiplayerServerDeniedEvent.class,
          JoinMultiplayerServerRequestEvent.class,
          JoinMultiplayerServerSuccessEvent.class,
          KickPlayerFromGameDeniedEvent.class,
          KickPlayerFromGameRequestEvent.class,
          KickPlayerFromGameSuccessEvent.class,
          OpenMultiplayerServerDeniedEvent.class,
          OpenMultiplayerServerRequestEvent.class,
          OpenMultiplayerServerSuccessEvent.class,
          PlayerJoinGameDeniedEvent.class,
          PlayerJoinGameRequestEvent.class,
          PlayerJoinGameSuccessEvent.class,
          PlayerLeaveGameSuccessEvent.class,
          QuitMultiplayerServerRequestEvent.class,
          StatusMessageSuccessEvent.class,
          ArrayList.class,
          DefaultChatMessage.class,
          DefaultCommandMessage.class,
          DefaultMessage.class,
          DefaultStatusMessage.class,
          DefaultPlayer.class,
          HashMap.class,
          Id.class,
          ImmutableSet.class,
          InetSocketAddress.class,
          KryonetRemote.class,
          PersonIdentity.class,
          PlayerColor.class,
          PlayerTurnOrder.class);

  private KryonetRegistration()
  {
    Classes.instantiationNotAllowed();
  }
}
