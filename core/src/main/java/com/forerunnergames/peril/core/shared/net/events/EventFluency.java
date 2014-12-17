package com.forerunnergames.peril.core.shared.net.events;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.*;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerLimitSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.JoinMultiplayerServerSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public final class EventFluency
{
  public static Player playerFrom (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayer();
  }

  public static String playerNameFrom (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName();
  }

  public static String playerNameFrom (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName();
  }

  public static String withPlayerNameFrom (final PlayerJoinGameRequestEvent event)
  {
    return playerNameFrom (event);
  }

  public static String playerNameFrom (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName();
  }

  public static PlayerColor currentColorFrom (final PlayerColorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getCurrentColor();
  }

  public static PlayerColor previousColorFrom (final PlayerColorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPreviousColor();
  }

  public static PlayerTurnOrder currentTurnOrderFrom (final PlayerTurnOrderEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getCurrentTurnOrder();
  }

  public static PlayerTurnOrder previousTurnOrderFrom (final PlayerTurnOrderEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPreviousTurnOrder();
  }

  public static <T> T reasonFrom (final DeniedEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getReason();
  }

  public static String reasonForKickFrom (final KickEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getReasonForKick();
  }

  public static int newPlayerLimitFrom (final ChangePlayerLimitSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getNewPlayerLimit();
  }

  public static int deltaFrom (final PlayerLimitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerLimitDelta();
  }

  public static String serverNameFrom (final OpenServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerName();
  }

  public static String withNameFrom (final OpenServerEvent event)
  {
    return serverNameFrom (event);
  }

  public static String serverAddressFrom (final ServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerAddress();
  }

  public static String withAddressFrom (final ServerRequestEvent event)
  {
    return serverAddressFrom (event);
  }

  public static int serverTcpPortFrom (final OpenServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerTcpPort();
  }

  public static int withTcpPortFrom (final OpenServerEvent event)
  {
    return serverTcpPortFrom (event);
  }

  public static String serverAddressFrom (final JoinServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerAddress();
  }

  public static String withAddressFrom (final JoinServerEvent event)
  {
    return serverAddressFrom (event);
  }

  public static int serverTcpPortFrom (final JoinServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerTcpPort();
  }

  public static int withTcpPortFrom (final JoinServerEvent event)
  {
    return serverTcpPortFrom (event);
  }

  public static String serverNameFrom (final JoinMultiplayerServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerName();
  }

  public static ImmutableSet <Player> playersInGameFrom (final JoinMultiplayerServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayersInGame();
  }

  public static int additionalPlayersAllowedFrom (final JoinMultiplayerServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getAdditionalPlayersAllowed();
  }

  public static <T extends Message> T messageFrom (final MessageEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getMessage();
  }

  public static <T extends Message> T withMessageFrom (final MessageEvent <T> event)
  {
    return messageFrom (event);
  }

  public static String withMessageTextFrom (final MessageEvent <? extends Message> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getMessageText();
  }

  public static boolean hasAuthorFrom (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.hasAuthor();
  }

  @Nullable
  public static Author withAuthorFrom (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getAuthor();
  }

  private EventFluency()
  {
    Classes.instantiationNotAllowed();
  }
}
