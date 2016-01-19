package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

import com.google.common.collect.ImmutableMap;

public final class EndInitialReinforcementPhaseEvent implements ServerNotificationEvent
{
  private final ImmutableMap <CountryPacket, PlayerPacket> playMapView;

  public EndInitialReinforcementPhaseEvent (final ImmutableMap <CountryPacket, PlayerPacket> playMapView)
  {
    Arguments.checkIsNotNull (playMapView, "playMapView");

    this.playMapView = playMapView;
  }

  public ImmutableMap <CountryPacket, PlayerPacket> getPlayMapView ()
  {
    return playMapView;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: PlayMapView: [{}]", getClass ().getSimpleName (), playMapView);
  }

  @RequiredForNetworkSerialization
  private EndInitialReinforcementPhaseEvent ()
  {
    playMapView = null;
  }
}
