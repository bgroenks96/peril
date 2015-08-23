package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.net.client.ClientConfiguration;
import com.forerunnergames.tools.net.server.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

public final class EventFluency
{
  public static ImmutableSet <PlayerPacket> playersFrom (final JoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayers ();
  }

  public static GameServerConfiguration gameServerConfigurationFrom (final JoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getGameServerConfiguration ();
  }

  public static GameServerConfiguration withGameServerConfigurationFrom (final CreateGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getGameServerConfiguration ();
  }

  public static ServerConfiguration withServerConfigurationFrom (final CreateGameRequestEvent event)
  {
    return withGameServerConfigurationFrom (event);
  }

  public static ClientConfiguration clientConfigurationFrom (final JoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getClientConfiguration ();
  }

  public static String selectedCountryNameFrom (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getSelectedCountryName ();
  }

  public static ImmutableSet <PlayerPacket> recipientsFrom (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getRecipients ();
  }

  private EventFluency ()
  {
    Classes.instantiationNotAllowed ();
  }
}
