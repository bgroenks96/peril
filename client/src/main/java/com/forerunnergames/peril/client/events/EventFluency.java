package com.forerunnergames.peril.client.events;

import com.forerunnergames.peril.core.shared.net.GameServerConfiguration;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.net.client.ClientConfiguration;

import com.google.common.collect.ImmutableSet;

public final class EventFluency
{
  public static ImmutableSet <PlayerPacket> playersFrom (final JoinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getPlayers ();
  }

  public static GameServerConfiguration gameServerConfigurationFrom (final JoinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getGameServerConfiguration ();
  }

  public static ClientConfiguration clientConfigurationFrom (final JoinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getClientConfiguration ();
  }

  public static String selectedCountryNameFrom (final SelectCountryEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    return event.getSelectedCountryName ();
  }

  private EventFluency ()
  {
    Classes.instantiationNotAllowed ();
  }
}
