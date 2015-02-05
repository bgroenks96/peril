package com.forerunnergames.peril.core.shared.net.kryonet;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayer;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.DefaultGameConfiguration;
import com.forerunnergames.peril.core.shared.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.defaults.*;
import com.forerunnergames.peril.core.shared.net.events.denied.*;
import com.forerunnergames.peril.core.shared.net.events.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DestroyGameServerEvent;
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
import com.forerunnergames.tools.net.DefaultServerConfiguration;

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
          ChatMessageDeniedEvent.class,
          ChatMessageRequestEvent.class,
          ChatMessageSuccessEvent.class,
          PlayerCountryAssignmentCompleteEvent.class,
          CreateGameServerDeniedEvent.class,
          CreateGameServerRequestEvent.class,
          CreateGameServerSuccessEvent.class,
          DefaultChatMessage.class,
          DefaultChatMessageEvent.class,
          DefaultCommandMessage.class,
          DefaultCommandMessageEvent.class,
          DefaultDeniedEvent.class,
          DefaultGameConfiguration.class,
          DefaultGameServerConfiguration.class,
          DefaultJoinGameServerEvent.class,
          DefaultKickEvent.class,
          DefaultPlayerColorEvent.class,
          DefaultPlayerDeniedEvent.class,
          DefaultPlayerTurnOrderEvent.class,
          DefaultMessage.class,
          DefaultStatusMessage.class,
          DefaultPlayer.class,
          DefaultServerConfiguration.class,
          DefaultStatusMessageEvent.class,
          DefendCountryDeniedEvent.class,
          DefendCountryRequestEvent.class,
          DefendCountrySuccessEvent.class,
          DestroyGameServerEvent.class,
          DeterminePlayerTurnOrderCompleteEvent.class,
          DistributeInitialArmiesCompleteEvent.class,
          HashMap.class,
          Id.class,
          ImmutableSet.class,
          InetSocketAddress.class,
          JoinGameServerDeniedEvent.class,
          JoinGameServerRequestEvent.class,
          JoinGameServerSuccessEvent.class,
          KickPlayerFromGameDeniedEvent.class,
          KickPlayerFromGameRequestEvent.class,
          KickPlayerFromGameSuccessEvent.class,
          KryonetRemote.class,
          PersonIdentity.class,
          PlayerColor.class,
          PlayerTurnOrder.class,
          PlayerJoinGameDeniedEvent.class,
          PlayerJoinGameRequestEvent.class,
          PlayerJoinGameSuccessEvent.class,
          PlayerLeaveGameSuccessEvent.class);
          // @formatter:on

  private KryonetRegistration ()
  {
    Classes.instantiationNotAllowed ();
  }
}
