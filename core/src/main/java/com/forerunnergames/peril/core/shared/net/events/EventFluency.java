package com.forerunnergames.peril.core.shared.net.events;

import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.events.client.request.JoinGameServerRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.KickEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.MessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerColorEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.PlayerSelectCountryResponseEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.JoinGameServerDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.CountryArmiesChangedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.JoinGameServerSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Author;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public final class EventFluency
{
  public static PlayerColor colorFrom (final PlayerColorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getRequestedColor ();
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

  public static PlayerPacket playerFrom (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayer ();
  }

  public static PlayerPacket playerFrom (final PlayerInputRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayer ();
  }

  public static String playerNameFrom (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static String playerNameFrom (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static String playerNameFrom (final PlayerJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static PlayerPacket playerFrom (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayer ();
  }

  public static String playerNameFrom (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerName ();
  }

  public static ImmutableSet <PlayerPacket> playersInGameFrom (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayersInGame ();
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

  public static String withAuthorNameFrom (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");
    Arguments.checkIsTrue (event.hasAuthor (),
                           "Cannot get author name for non-existent author in event [" + event + "].");

    return event.getAuthor ().getName ();
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

  public static GameServerConfiguration withGameServerConfigurationFrom (final JoinGameServerSuccessEvent event)
  {
    return gameServerConfigurationFrom (event);
  }

  public static GameServerConfiguration gameServerConfigurationFrom (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getGameServerConfiguration ();
  }

  public static ServerConfiguration withServerConfigurationFrom (final JoinGameServerRequestEvent event)
  {
    return serverConfigurationFrom (event);
  }

  public static ServerConfiguration withServerConfigurationFrom (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerConfiguration ();
  }

  public static ServerConfiguration serverConfigurationFrom (final JoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerConfiguration ();
  }

  public static ServerConfiguration serverConfigurationFrom (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerConfiguration ();
  }

  public static ClientConfiguration clientConfigurationFrom (final JoinGameServerDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getClientConfiguration ();
  }

  public static ClientConfiguration clientConfigurationFrom (final JoinGameServerSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getClientConfiguration ();
  }

  public static String serverAddressFrom (final JoinGameServerRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getServerAddress ();
  }

  public static String withCountryNameFrom (final CountryArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getCountryName ();
  }

  public static int deltaArmyCountFrom (final CountryArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getDeltaArmyCount ();
  }

  public static ImmutableSet <PlayerPacket> recipientsFrom (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getRecipients ();
  }

  public static String playerColorFrom (final PlayerSelectCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayerColor ();
  }

  public static String selectedCountryNameFrom (final PlayerSelectCountryResponseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getSelectedCountryName ();
  }

  public static ImmutableSet <CountryPacket> countriesFrom (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getCountries ();
  }

  private EventFluency ()
  {
    Classes.instantiationNotAllowed ();
  }
}
