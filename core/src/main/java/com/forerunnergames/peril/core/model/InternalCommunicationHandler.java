package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.events.player.InternalPlayerLeaveGameEvent;
import com.forerunnergames.peril.common.events.player.UpdatePlayerDataRequestEvent;
import com.forerunnergames.peril.common.events.player.UpdatePlayerDataResponseEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Handler class for internal communication events from server
final class InternalCommunicationHandler
{
  private static final Logger log = LoggerFactory.getLogger (InternalCommunicationHandler.class);
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final PlayerTurnModel playerTurnModel;
  private final MBassador <Event> eventBus;

  InternalCommunicationHandler (final PlayerModel playerModel,
                                final PlayMapModel playMapModel,
                                final PlayerTurnModel playerTurnModel,
                                final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.playerTurnModel = playerTurnModel;
    this.eventBus = eventBus;
  }

  @Handler
  void onEvent (final UpdatePlayerDataRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final ImmutableSet <PlayerPacket> players = playerModel.getPlayerPackets ();
    eventBus.publish (new UpdatePlayerDataResponseEvent (players, event.getEventId ()));
  }

  @Handler
  void onEvent (final InternalPlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    if (!playerModel.existsPlayerWith (event.getPlayerName ())) return;

    final Id player = playerModel.idOf (event.getPlayerName ());
    final CountryOwnerModel countryOwnerModel = playMapModel.getCountryOwnerModel ();

    countryOwnerModel.unassignAllCountriesOwnedBy (player);
    playerModel.remove (player);
    playerTurnModel.setTurnCount (playerModel.getPlayerLimit ());

    eventBus.publish (new PlayerLeaveGameEvent (event.getPlayer (), playerModel.getPlayerPackets ()));
  }
}
