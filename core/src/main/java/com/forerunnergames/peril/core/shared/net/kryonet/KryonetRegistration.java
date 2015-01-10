package com.forerunnergames.peril.core.shared.net.kryonet;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayer;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.defaults.*;
import com.forerunnergames.peril.core.shared.net.events.denied.*;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.*;
import com.forerunnergames.peril.core.shared.net.events.success.*;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultCommandMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

public final class KryonetRegistration
{
  // TODO Java 8: Generalized target-type inference: Remove unnecessary explicit generic <Class <?>> type.
  public static final ImmutableSet <Class <?>> CLASSES = ImmutableSet.<Class <?>> of (
          // @formatter:off
          AbstractDeniedEvent.class,
          AbstractMessageEvent.class,
          ArrayList.class,
          AttackCountryDeniedEvent.class,
          AttackCountryRequestEvent.class,
          AttackCountrySuccessEvent.class,
          ChangePlayerColorDeniedEvent.class,
          ChangePlayerColorRequestEvent.class,
          ChangePlayerColorSuccessEvent.class,
          ChangePlayerLimitDeniedEvent.class,
          ChangePlayerLimitRequestEvent.class,
          ChangePlayerLimitSuccessEvent.class,
          ChatMessageDeniedEvent.class,
          ChatMessageRequestEvent.class,
          ChatMessageSuccessEvent.class,
          CloseMultiplayerServerSuccessEvent.class,
          DefaultChatMessage.class,
          DefaultChatMessageEvent.class,
          DefaultCommandMessage.class,
          DefaultCommandMessageEvent.class,
          DefaultDeniedEvent.class,
          DefaultJoinServerEvent.class,
          DefaultKickEvent.class,
          DefaultOpenServerEvent.class,
          DefaultPlayerColorEvent.class,
          DefaultPlayerDeniedEvent.class,
          DefaultPlayerEvent.class,
          DefaultPlayerTurnOrderEvent.class,
          DefaultMessage.class,
          DefaultStatusMessage.class,
          DefaultPlayer.class,
          DefaultStatusMessageEvent.class,
          DefendCountryDeniedEvent.class,
          DefendCountryRequestEvent.class,
          DefendCountrySuccessEvent.class,
          DeterminePlayerTurnOrderCompleteEvent.class,
          DistributeInitialArmiesCompleteEvent.class,
          HashMap.class,
          Id.class,
          ImmutableSet.class,
          InetSocketAddress.class,
          JoinMultiplayerServerDeniedEvent.class,
          JoinMultiplayerServerRequestEvent.class,
          JoinMultiplayerServerSuccessEvent.class,
          KickPlayerFromGameDeniedEvent.class,
          KickPlayerFromGameRequestEvent.class,
          KickPlayerFromGameSuccessEvent.class,
          KryonetRemote.class,
          OpenMultiplayerServerDeniedEvent.class,
          OpenMultiplayerServerRequestEvent.class,
          OpenMultiplayerServerSuccessEvent.class,
          PersonIdentity.class,
          PlayerColor.class,
          PlayerTurnOrder.class,
          PlayerJoinGameDeniedEvent.class,
          PlayerJoinGameRequestEvent.class,
          PlayerJoinGameSuccessEvent.class,
          PlayerLeaveGameSuccessEvent.class,
          QuitMultiplayerServerRequestEvent.class,
          StatusMessageSuccessEvent.class);

  private KryonetRegistration()
  {
    Classes.instantiationNotAllowed();
  }
}
