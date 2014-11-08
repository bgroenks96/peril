package com.forerunnergames.peril.server.controllers;

import static com.forerunnergames.peril.core.model.player.PlayerInterpreter.idOf;
import static com.forerunnergames.peril.core.shared.net.events.EventInterpreter.*;
import static com.forerunnergames.tools.common.net.RemoteInterpreter.addressOf;
import static com.forerunnergames.tools.common.net.events.EventInterpreter.clientFrom;
import static com.forerunnergames.tools.common.net.events.EventInterpreter.questionFrom;

import static com.google.common.collect.Maps.newHashMap;

import com.forerunnergames.peril.core.model.person.PersonIdentity;
import com.forerunnergames.peril.core.model.player.Player;
import com.forerunnergames.peril.core.model.player.PlayerColor;
import com.forerunnergames.peril.core.model.player.PlayerFactory;
import com.forerunnergames.peril.core.model.player.PlayerModel;
import com.forerunnergames.peril.core.model.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
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
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.peril.core.shared.net.settings.NetworkSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Controller;
import com.forerunnergames.tools.common.Id;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.net.ClientCommunicator;
import com.forerunnergames.tools.common.net.ClientConnector;
import com.forerunnergames.tools.common.net.Remote;
import com.forerunnergames.tools.common.net.events.ClientCommunicationEvent;
import com.forerunnergames.tools.common.net.events.ClientConnectionEvent;
import com.forerunnergames.tools.common.net.events.ClientDisconnectionEvent;

import java.util.Map;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MultiplayerController implements Controller
{
  private static final Logger log = LoggerFactory.getLogger (MultiplayerController.class);
  private final Map <Id, Remote> playerIdsToClients = newHashMap();
  private final Map <Remote, Id> clientsToPlayerIds = newHashMap();
  private final ClientConnector clientConnector;
  private final ClientCommunicator clientCommunicator;
  private final PlayerModel playerModel;
  private String serverName;
  private int serverTcpPort;
  private Remote host;
  private boolean shouldShutDown = false;

  public MultiplayerController (final PlayerModel playerModel,
                                final String serverName,
                                final int serverTcpPort,
                                final ClientConnector clientConnector,
                                final ClientCommunicator clientCommunicator)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (serverName, "serverName");
    Arguments.checkIsNotNegative (serverTcpPort, "serverTcpPort");
    Arguments.checkIsNotNull (clientConnector, "clientConnector");
    Arguments.checkIsNotNull (clientCommunicator, "clientCommunicator");

    this.playerModel = playerModel;
    this.serverName = serverName;
    this.serverTcpPort = serverTcpPort;
    this.clientConnector = clientConnector;
    this.clientCommunicator = clientCommunicator;
  }

  @Override
  public void initialize()
  {
    AnnotationProcessor.process (this);
  }

  @Override
  public boolean shouldShutDown()
  {
    return shouldShutDown;
  }

  @Override
  public void shutDown()
  {
    AnnotationProcessor.unprocess (this);
  }

  @EventSubscriber (eventClass = ClientConnectionEvent.class)
  public void onClientConnectionEvent (ClientConnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.info ("Client [{}] connected.", clientFrom (event));
  }

  @EventSubscriber (eventClass = ClientDisconnectionEvent.class)
  public void onClientDisconnectionEvent (ClientDisconnectionEvent disconnectionEvent)
  {
    Arguments.checkIsNotNull (disconnectionEvent, "disconnectionEvent");

    if (! existsPlayerWith (clientFrom (disconnectionEvent)))
    {
      log.warn ("Client [{}] disconnected, but did not exist as a player.", clientFrom (disconnectionEvent));
      return;
    }

    log.info ("Player [{}] disconnected.", playerWith (clientFrom (disconnectionEvent)));

    final PlayerLeaveGameSuccessEvent playerLeaveGameSuccessEvent =
            new PlayerLeaveGameSuccessEvent (playerWith (clientFrom (disconnectionEvent)));

    removePlayerWith (clientFrom (disconnectionEvent));
    sendToAll (playerLeaveGameSuccessEvent);
    sendToAll (statusMessage (playerNameFrom (playerLeaveGameSuccessEvent) + " left the game."));
  }

  private boolean existsPlayerWith (final Remote client)
  {
    return clientsToPlayerIds.containsKey (client) && playerIdsToClients.containsValue (client);
  }

  private void removePlayerWith (final Remote client)
  {
    remove (playerWith (client));
  }

  private Player playerWith (final Remote client)
  {
    assert clientsToPlayerIds.containsKey (client);
    assert playerIdsToClients.containsValue (client);

    return playerModel.playerWith (clientsToPlayerIds.get (client));
  }

  private void remove (final Player player)
  {
    playerModel.remove (player);
    clientsToPlayerIds.remove (playerIdsToClients.remove (idOf (player)));
  }

  private Remote clientOf (final Player player)
  {
    assert playerIdsToClients.containsKey (idOf (player));
    assert clientsToPlayerIds.containsValue (idOf (player));

    return playerIdsToClients.get (idOf (player));
  }

  private void sendToAll (final Object object)
  {
    clientCommunicator.sendToAll (object);
  }

  @EventSubscriber (eventClass = ClientCommunicationEvent.class)
  public void onClientCommunicationEvent (final ClientCommunicationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event [{}] received.", event);

    if (questionFrom (event) instanceof ChatMessageRequestEvent)
    {
      onEvent ((ChatMessageRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof PlayerJoinGameRequestEvent)
    {
      onEvent ((PlayerJoinGameRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof ChangePlayerColorRequestEvent)
    {
      onEvent ((ChangePlayerColorRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof ChangePlayerLimitRequestEvent)
    {
      onEvent ((ChangePlayerLimitRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof ChangePlayerTurnOrderRequestEvent)
    {
      onEvent ((ChangePlayerTurnOrderRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof JoinMultiplayerServerRequestEvent)
    {
      onEvent ((JoinMultiplayerServerRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof OpenMultiplayerServerRequestEvent)
    {
      onEvent ((OpenMultiplayerServerRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof QuitMultiplayerServerRequestEvent)
    {
      onEvent ((QuitMultiplayerServerRequestEvent) questionFrom (event), clientFrom (event));
    }
    else if (questionFrom (event) instanceof KickPlayerFromGameRequestEvent)
    {
      onEvent ((KickPlayerFromGameRequestEvent) questionFrom (event), clientFrom (event));
    }
    else
    {
      log.warn ("Received unrecognized message [{}] from client [{}].", questionFrom (event), clientFrom (event));
    }
  }

  private void onEvent (final PlayerJoinGameRequestEvent requestEvent, final Remote sender)
  {
    Result result = addClientToGame (sender);

    if (result.isFailure())
    {
      playerJoinGameDenied (playerNameFrom (requestEvent), result.getMessage(), sender);
      return;
    }

    Player player = createPlayer (withNameFrom (requestEvent));
    result = addPlayerToGame (player);

    if (result.isFailure())
    {
      playerJoinGameDenied (playerNameFrom (requestEvent), result.getMessage(), sender);
      return;
    }

    playerJoinGameSuccess (player, sender);
  }

  private Result addClientToGame (final Remote requestingClient)
  {
    if (shouldWaitForHostToJoinFirst (requestingClient))
    {
      return Result.failure ("Please wait for the host to join the game first!");
    }

    return Result.success();
  }

  private boolean shouldWaitForHostToJoinFirst (final Remote joiningClient)
  {
    return host != null && ! existsPlayerWith (host) && joiningClient.isNot (host);
  }

  private void playerJoinGameDenied (final String playerName, final String reasonForDenial, final Remote sender)
  {
    PlayerJoinGameDeniedEvent deniedEvent = new PlayerJoinGameDeniedEvent (playerName, reasonForDenial);
    sendTo (sender, deniedEvent);
    if (isHost (sender)) sendTo (sender, new CloseMultiplayerServerSuccessEvent());
    disconnect (sender);
  }

  private Player createPlayer (final String name)
  {
    return PlayerFactory.create (name, playerModel.nextAvailableId());
  }

  private Result addPlayerToGame (final Player player)
  {
    return playerModel.add (player);
  }

  private void playerJoinGameSuccess (final Player player, final Remote client)
  {
    register (idOf (player), client);
    PlayerJoinGameSuccessEvent successEvent = new PlayerJoinGameSuccessEvent (player);
    playerFrom(successEvent).setIdentity (PersonIdentity.NON_SELF);
    sendToAllExcept (client, successEvent);
    sendToAllExcept (client, statusMessage (player.getName() + " joined the game as a player."));
    playerFrom(successEvent).setIdentity (PersonIdentity.SELF);
    sendTo (client, successEvent);
    sendTo (client, statusMessage ("You successfully joined \"" + serverName + "\" multiplayer game."));
  }

  private void sendTo (final Remote client, final Object object)
  {
    clientCommunicator.sendTo (client, object);
  }

  private void sendToAllExcept (final Remote client, final Object object)
  {
    clientCommunicator.sendToAllExcept (client, object);
  }

  private void register (final Id playerId, final Remote playerClient)
  {
    playerIdsToClients.put (playerId, playerClient);
    clientsToPlayerIds.put (playerClient, playerId);
  }

  private void onEvent (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    if (! isHost (sender))
    {
      kickPlayerNonSelfFromGameDenied (requestEvent, sender);
    }
    else if (! isHost (clientOf (playerFrom (requestEvent))))
    {
      kickPlayerNonSelfFromGameSuccess (requestEvent, sender);
    }
    else
    {
      kickPlayerSelfFromGameDenied (requestEvent, sender);
    }
  }

  private boolean isHost (final Remote remote)
  {
    return host != null && remote.equals (host);
  }

  private void kickPlayerNonSelfFromGameSuccess (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    disconnect (clientOf (playerFrom (requestEvent)));
    KickPlayerFromGameSuccessEvent successEvent =
            new KickPlayerFromGameSuccessEvent (playerFrom (requestEvent), reasonForKickFrom (requestEvent));
    playerFrom(requestEvent).setIdentity (PersonIdentity.NON_SELF);
    sendToAll (successEvent);
    sendTo (sender, statusMessage ("You kicked " + playerNameFrom (requestEvent) + " from the game. Reason: " +
            reasonForKickFrom (requestEvent)));
    sendToAllExcept (sender, statusMessage (playerNameFrom (requestEvent) + " was kicked from the game. Reason: " +
            reasonForKickFrom (requestEvent)));
  }

  private void kickPlayerSelfFromGameDenied (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    KickPlayerFromGameDeniedEvent deniedEvent =
            new KickPlayerFromGameDeniedEvent (
                    playerFrom (requestEvent), reasonForKickFrom (requestEvent), "You can't kick yourself!");
    playerFrom(deniedEvent).setIdentity (PersonIdentity.SELF);
    sendTo (sender, deniedEvent);
  }

  private void kickPlayerNonSelfFromGameDenied (final KickPlayerFromGameRequestEvent requestEvent, final Remote sender)
  {
    KickPlayerFromGameDeniedEvent deniedEvent =
            new KickPlayerFromGameDeniedEvent (
                    playerFrom (requestEvent), reasonForKickFrom (requestEvent), "Only the host can kick players!");
    playerFrom(deniedEvent).setIdentity (PersonIdentity.NON_SELF);
    sendTo (sender, deniedEvent);
  }

  private void disconnect (final Remote client)
  {
    clientConnector.disconnect (client);
  }

  private void onEvent (final ChatMessageRequestEvent requestEvent, final Remote sender)
  {
    sendToAll (new ChatMessageSuccessEvent (withMessageFrom (requestEvent)));
  }

  private Author withAuthorOf (final Remote sender)
  {
    return existsPlayerWith (sender) ? playerWith (sender) : null;
  }

  private void onEvent (final ChangePlayerColorRequestEvent requestEvent, final Remote sender)
  {
    if (! clientOf (playerFrom (requestEvent)).equals (sender))
    {
      changePlayerColorDenied (requestEvent, sender, "You can only change your own color!");
      return;
    }

    Result result =
            playerModel.changeColorOf (
                    idOf (playerFrom (requestEvent)),
                    currentColorFrom (requestEvent),
                    previousColorFrom (requestEvent));

    if (result.isFailure())
    {
      changePlayerColorDenied (requestEvent, sender, result.getMessage());
      return;
    }

    changePlayerColorSuccess (currentColorFrom (requestEvent), previousColorFrom (requestEvent), sender);
  }

  private void changePlayerColorDenied (final ChangePlayerColorRequestEvent requestEvent,
                                        final Remote sender,
                                        final String reasonForDenial)
  {
    ChangePlayerColorDeniedEvent deniedEvent = new ChangePlayerColorDeniedEvent (requestEvent, reasonForDenial);
    sendTo (sender, deniedEvent);
    sendTo (sender, statusMessage (reasonForDenial));
  }

  private void changePlayerColorSuccess (final PlayerColor currentColor,
                                         final PlayerColor previousColor,
                                         final Remote sender)
  {
    final ChangePlayerColorSuccessEvent successEvent =
            new ChangePlayerColorSuccessEvent (playerWith (sender), currentColor, previousColor);

    sendToAllExcept (sender, successEvent);
    sendToAllExcept (sender, statusMessage (playerNameFrom (successEvent) + " changed colors from " +
            previousColorFrom(successEvent).toLowerCase() + " to " + currentColorFrom(successEvent).toLowerCase() + "."));
  }

  private StatusMessageSuccessEvent statusMessage (final String text)
  {
    return new StatusMessageSuccessEvent (new DefaultStatusMessage (text));
  }

  private void onEvent (final ChangePlayerLimitRequestEvent requestEvent, final Remote sender)
  {
    if (! isHost (sender))
    {
      changePlayerLimitDenied (deltaFrom (requestEvent), sender, "Only the host can change the player limit.");
      return;
    }

    playerModel.changePlayerLimitBy (deltaFrom (requestEvent));

    changePlayerLimitSuccess (deltaFrom (requestEvent), sender);
  }

  private void changePlayerLimitDenied (final int playerLimitDelta, final Remote sender, final String reasonForDenial)
  {
    ChangePlayerLimitDeniedEvent deniedEvent = new ChangePlayerLimitDeniedEvent (playerLimitDelta, reasonForDenial);
    sendTo (sender, deniedEvent);
  }

  private void changePlayerLimitSuccess (final int playerLimitDelta, final Remote sender)
  {
    ChangePlayerLimitSuccessEvent successEvent = new ChangePlayerLimitSuccessEvent (playerLimitDelta);
    sendToAllExcept (sender, statusMessage ("The maximum number of players was changed from " +
            (playerModel.getPlayerLimit() - successEvent.getPlayerLimitDelta()) + " to " +
            playerModel.getPlayerLimit() + "."));
    sendToAll (successEvent);
  }

  private void onEvent (final ChangePlayerTurnOrderRequestEvent requestEvent, final Remote sender)
  {
    // TODO The Model should check if the turn order change is valid.
    // TODO If not, then send PlayerTurnOrderChangeDeniedEvent to the sender only.

    changeTurnOrderOf (playerFrom (requestEvent), currentTurnOrderFrom (requestEvent));
    ChangePlayerTurnOrderSuccessEvent successEvent = new ChangePlayerTurnOrderSuccessEvent (requestEvent);
    sendTo (sender, statusMessage ("You are now the " + currentTurnOrderFrom(successEvent).toMixedOrdinal() +
            " player."));
    sendToAllExcept (sender, statusMessage (playerNameFrom (successEvent) + " is now the " +
            currentTurnOrderFrom(successEvent).toMixedOrdinal() + " player."));
    sendToAll (successEvent);
  }

  private void changeTurnOrderOf (final Player player, final PlayerTurnOrder turnOrder)
  {
    player.setTurnOrder (turnOrder);
  }

  private void onEvent (final OpenMultiplayerServerRequestEvent requestEvent, Remote sender)
  {
    Result result = checkServerIsEmpty();

    if (result.isFailure())
    {
      openMultiplayerServerDenied (requestEvent, sender, result.getMessage());
      return;
    }

    result = checkIsValid (serverNameFrom (requestEvent), serverTcpPortFrom (requestEvent), addressOf (sender));

    if (result.isFailure())
    {
      openMultiplayerServerDenied (requestEvent, sender, result.getMessage());
      return;
    }

    openMultiplayerServerSuccess (sender);
  }

  private Result checkServerIsEmpty()
  {
    return playerModel.isEmpty() ? Result.success() : Result.failure ("There are already players in the server.");
  }

  private Result checkIsValid (final String name, final int tcpPort, final String address)
  {
    if (! serverNameMatches (name))
    {
      return Result.failure ("Requested server name doesn't match the actual server name.");
    }
    else if (! serverTcpPortMatches (tcpPort))
    {
      return Result.failure ("Requested server port doesn't match the actual server port.");
    }
    else if (! isLocal (address))
    {
      return Result.failure ("You can only host & play on the same machine as the server!");
    }

    return Result.success();
  }

  private boolean serverNameMatches (final String serverName)
  {
    return this.serverName.equals (serverName);
  }

  private boolean serverTcpPortMatches (final int tcpPort)
  {
    return serverTcpPort == tcpPort;
  }

  private boolean isLocal (final String address)
  {
    return address.equals (NetworkSettings.LOCALHOST_ADDRESS);
  }

  private void openMultiplayerServerDenied (final OpenMultiplayerServerRequestEvent event,
                                            final Remote sender,
                                            final String reasonForDenial)
  {
    sendTo (sender, new OpenMultiplayerServerDeniedEvent (event, reasonForDenial));
  }

  private void openMultiplayerServerSuccess (final Remote sender)
  {
    host = sender;
    sendTo (sender, new OpenMultiplayerServerSuccessEvent (serverName, serverTcpPort));
  }

  private void onEvent (final JoinMultiplayerServerRequestEvent requestEvent, Remote sender)
  {
    joinMultiplayerServerSuccess (serverAddressFrom (requestEvent), sender);
  }

  private void joinMultiplayerServerSuccess (final String serverAddress, final Remote joiningClient)
  {
    sendTo (joiningClient,
            new JoinMultiplayerServerSuccessEvent (
                    serverName, serverAddress, serverTcpPort, playerModel.getPlayers(), playerModel.getPlayerLimit()));
  }

  private void onEvent (final QuitMultiplayerServerRequestEvent event, final Remote sender)
  {
    if (isHost (sender))
    {
      closeServer();
    }
    else if (existsPlayerWith (sender))
    {
      disconnect (sender);
    }
    else
    {
      log.warn ("Client [{}] sent event [{}], but does not exist as a player.", sender, event);
    }
  }

  private void closeServer()
  {
    sendTo (host, new CloseMultiplayerServerSuccessEvent());
    sendToAllExcept (host, statusMessage ("The host is shutting down the server."));
    clientConnector.disconnectAll();
    shouldShutDown = true;
  }
}
