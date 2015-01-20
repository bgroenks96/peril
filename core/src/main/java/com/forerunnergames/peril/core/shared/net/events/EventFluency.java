package com.forerunnergames.peril.core.shared.net.events;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.*;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.JoinGameServerSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.net.ServerConfiguration;
import com.forerunnergames.tools.common.net.events.DeniedEvent;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public final class EventFluency
{
  public static PlayerColor currentColorFrom (final PlayerColorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getCurrentColor ();
  }

  public static PlayerTurnOrder currentTurnOrderFrom (final PlayerTurnOrderEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getCurrentTurnOrder ();
  }

  public static boolean hasAuthorFrom (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.hasAuthor ();
  }

  public static <T extends Message> T messageFrom (final MessageEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getMessage ();
  }

  public static Player playerFrom (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayer ();
  }

  public static String playerNameFrom (final PlayerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static String playerNameFrom (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static String playerNameFrom (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static ImmutableSet <Player> playersInGameFrom (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayersInGame ();
  }

  public static PlayerColor previousColorFrom (final PlayerColorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPreviousColor ();
  }

  public static PlayerTurnOrder previousTurnOrderFrom (final PlayerTurnOrderEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPreviousTurnOrder ();
  }

  public static String reasonForKickFrom (final KickEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getReasonForKick ();
  }

  public static <T> T reasonFrom (final DeniedEvent <T> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getReason ();
  }

  @Nullable
  public static Author withAuthorFrom (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getAuthor ();
  }

  public static <T extends Message> T withMessageFrom (final MessageEvent <T> event)
  {
    return messageFrom (event);
  }

  public static String withMessageTextFrom (final MessageEvent <? extends Message> event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getMessageText ();
  }

  public static String withPlayerNameFrom (final PlayerJoinGameRequestEvent event)
  {
    return playerNameFrom (event);
  }

  public static GameServerConfiguration withGameServerConfigurationFrom (final CreateGameServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getConfiguration ();
  }

  public static ServerConfiguration withServerConfigurationFrom (final JoinGameServerEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getConfiguration ();
  }

  private EventFluency ()
  {
    Classes.instantiationNotAllowed ();
  }
}
