package com.forerunnergames.peril.core.shared.net.kryonet;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayer;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.defaults.*;
import com.forerunnergames.peril.core.shared.net.events.denied.*;
import com.forerunnergames.peril.core.shared.net.events.request.*;
import com.forerunnergames.peril.core.shared.net.events.success.*;
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
          AbstractDeniedEvent.class,
          AbstractMessageEvent.class,
          AttackCountryDeniedEvent.class,
          AttackCountryRequestEvent.class,
          AttackCountrySuccessEvent.class,
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
          DefaultOpenServerEvent.class,
          DefaultPlayerColorEvent.class,
          DefaultPlayerDeniedEvent.class,
          DefaultPlayerEvent.class,
          DefaultPlayerTurnOrderEvent.class,
          DefaultStatusMessageEvent.class,
          DefendCountryDeniedEvent.class,
          DefendCountryRequestEvent.class,
          DefendCountrySuccessEvent.class,
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
