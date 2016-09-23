/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.common.net.events.EventFluency.playerNameFrom;
import static com.forerunnergames.peril.common.net.events.EventFluency.reasonFrom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.EventBusHandler;
import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerFortifyCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.EndPlayerTurnDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerFortifyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectFortifyVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginFortificationEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.EndPlayerTurnSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectFortifyVectorSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.events.internal.player.DefaultInboundPlayerResponseRequestEvent;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardModelTest;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.map.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapStateBuilder;
import com.forerunnergames.peril.core.model.map.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModel;
import com.forerunnergames.peril.core.model.map.continent.ContinentMapGraphModelTest;
import com.forerunnergames.peril.core.model.map.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.map.country.CountryFactory;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModel;
import com.forerunnergames.peril.core.model.map.country.CountryMapGraphModelTest;
import com.forerunnergames.peril.core.model.map.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import net.engio.mbassy.bus.MBassador;

import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.ArrayUtils;

public class GameModelTest
{
  private static final Logger log = LoggerFactory.getLogger (GameModelTest.class);
  private final int defaultTestCountryCount = 30;
  private final ImmutableList <String> defaultTestCountries = generateTestCountryNames (defaultTestCountryCount);
  private MBassador <Event> eventBus;
  private EventBusHandler eventHandler;
  private int playerLimit;
  private int initialArmies;
  private int maxPlayers;
  private GameModel gameModel;
  private PlayerModel playerModel;
  private PlayMapModel playMapModel;
  private CountryOwnerModel countryOwnerModel;
  private CountryArmyModel countryArmyModel;
  private CountryMapGraphModel countryMapGraphModel;
  private BattleModel battleModel;
  private CardModel cardModel;
  private InternalCommunicationHandler mockCommHandler;
  private ImmutableSet <Card> cardDeck = CardModelTest.generateTestCards ();
  private GameRules gameRules;

  public static CountryMapGraphModel createDefaultTestCountryMapGraph (final ImmutableList <String> countryNames)
  {
    final DefaultGraphModel.Builder <String> countryNameGraphBuilder = DefaultGraphModel.builder ();
    // set every node adjacent to country 0
    for (int i = 1; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (0), countryNames.get (i));
    }
    // set each country 1-4 adjacent to its sequential neighbors
    for (int i = 2; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (i - 1), countryNames.get (i));
    }
    // complete the cycle by setting country 1 adjacent to last country
    countryNameGraphBuilder.setAdjacent (countryNames.get (countryNames.size () - 1), countryNames.get (1));
    final GraphModel <String> countryNameGraph = countryNameGraphBuilder.build ();
    return CountryMapGraphModelTest.createCountryMapGraphModelFrom (countryNameGraph);
  }

  @Before
  public void setup ()
  {
    eventBus = EventBusFactory.create (ImmutableSet.of (EventBusHandler.createEventBusFailureHandler ()));
    eventHandler = new EventBusHandler ();
    eventHandler.subscribe (eventBus);
    // crate default play map + game model
    gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (defaultTestCountryCount).build ();
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    mockCommHandler = mock (InternalCommunicationHandler.class);
    initializeGameModelWith (playMapModel);
    assert gameModel != null;
  }

  @Test
  public void testDeterminePlayerTurnOrderMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderOnePlayer ()
  {
    addSinglePlayer ();

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesMaxPlayers ()
  {
    addMaxPlayers ();

    gameModel.distributeInitialArmies ();

    final ImmutableSet <PlayerPacket> players = eventHandler
            .lastEventOfType (DistributeInitialArmiesCompleteEvent.class).getPlayers ();

    for (final PlayerPacket player : players)
    {
      assertTrue (player.hasArmiesInHand (initialArmies));
    }

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, players.size ()));
    for (final PlayerArmiesChangedEvent event : eventHandler.allEventsOfType (PlayerArmiesChangedEvent.class))
    {
      assertEquals (initialArmies, event.getPlayerDeltaArmyCount ());
    }
  }

  @Test
  public void testDistributeInitialArmiesZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    gameModel.distributeInitialArmies ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testWaitForCountryAssignmentToBeginRandom ()
  {
    gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (defaultTestCountryCount).initialCountryAssignment (InitialCountryAssignment.RANDOM)
            .build ();
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    initializeGameModelWith (playMapModel);

    addMaxPlayers ();

    gameModel.waitForCountryAssignmentToBegin ();

    assertTrue (eventHandler.wasFiredExactlyOnce (BeginPlayerCountryAssignmentEvent.class));
    assertEquals (InitialCountryAssignment.RANDOM,
                  eventHandler.lastEventOfType (BeginPlayerCountryAssignmentEvent.class).getAssignmentMode ());
  }

  @Test
  public void testWaitForCountryAssignmentToBeginManual ()
  {
    gameRules = new ClassicGameRules.Builder ().playerLimit (ClassicGameRules.MAX_PLAYERS)
            .totalCountryCount (defaultTestCountryCount).initialCountryAssignment (InitialCountryAssignment.MANUAL)
            .build ();
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    initializeGameModelWith (playMapModel);

    addMaxPlayers ();

    gameModel.waitForCountryAssignmentToBegin ();

    assertTrue (eventHandler.wasFiredExactlyOnce (BeginPlayerCountryAssignmentEvent.class));
    assertEquals (InitialCountryAssignment.MANUAL,
                  eventHandler.lastEventOfType (BeginPlayerCountryAssignmentEvent.class).getAssignmentMode ());
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayers ()
  {
    addMaxPlayers ();

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, initialArmies);
    }

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (countryOwnerModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesTenPlayersTenCountries ()
  {
    // test case in honor of Aaron on PR 27 ;)
    // can't use 5, though, because 5 < ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT

    gameRules = new ClassicGameRules.Builder ().totalCountryCount (10).playerLimit (10).build ();
    initializeGameModelWith (createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (10)));
    for (int i = 0; i < 10; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer" + i));
    }
    assertTrue (gameModel.playerCountIs (10));
    assertTrue (countryMapGraphModel.countryCountIs (10));

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, countryMapGraphModel.getCountryCount () / gameModel.getPlayerCount ());
    }

    gameModel.randomlyAssignPlayerCountries ();

    assertFalse (countryOwnerModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayersMaxCountries ()
  {
    final int countryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    gameRules = new ClassicGameRules.Builder ().totalCountryCount (countryCount)
            .playerLimit (ClassicGameRules.MAX_PLAYER_LIMIT).build ();
    initializeGameModelWith (createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (countryCount)));

    addMaxPlayers ();

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, countryMapGraphModel.getCountryCount () / gameModel.getPlayerCount ());
    }

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (countryOwnerModel.allCountriesAreOwned ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesZeroPlayers ()
  {
    assertTrue (playerModel.isEmpty ());

    gameModel.randomlyAssignPlayerCountries ();

    assertTrue (countryOwnerModel.allCountriesAreUnowned ());
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testWaitForPlayersToClaimInitialCountriesAllUnowned ()
  {
    addMaxPlayers ();

    // add army to first player's hand
    playerModel.addArmyToHandOf (playerModel.playerWith (PlayerTurnOrder.FIRST));

    assertTrue (countryOwnerModel.allCountriesAreUnowned ());

    gameModel.waitForPlayersToClaimInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerClaimCountryRequestEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    assertTrue (eventHandler.lastEventOfType (PlayerClaimCountryRequestEvent.class).getPlayer ().is (expectedPlayer));
    assertTrue (eventHandler.lastEventOfType (ActivePlayerChangedEvent.class).getPlayer ().is (expectedPlayer));
  }

  @Test
  public void testWaitForPlayersToClaimInitialCountriesSkipsPlayerWithEmptyHand ()
  {
    addMaxPlayers ();

    assertTrue (countryOwnerModel.allCountriesAreUnowned ());

    gameModel.waitForPlayersToClaimInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (SkipPlayerTurnEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerClaimCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    assertTrue (eventHandler.lastEvent (SkipPlayerTurnEvent.class).getPlayer ().is (expectedPlayer));
  }

  @Test
  public void testWaitForPlayersToClaimInitialCountriesAllOwned ()
  {
    addMaxPlayers ();

    final Id testPlayerOwner = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayerOwner).commitIfSuccessful ());
    }

    assertTrue (countryOwnerModel.allCountriesAreOwned ());

    gameModel.waitForPlayersToClaimInitialCountries ();

    assertTrue (eventHandler.wasNeverFired (PlayerClaimCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));

    verifyPlayerCountryAssignmentCompleteEvent ();
  }

  @Test
  public void testVerifyPlayerClaimCountryResponseRequestWhenValid ()
  {
    addMaxPlayers ();

    // add armies to player hands
    playerModel.addArmyToHandOf (playerModel.playerWith (PlayerTurnOrder.FIRST));

    final Id randomCountry = randomCountry ();
    final String randomCountryName = countryMapGraphModel.nameOf (randomCountry);

    final PlayerClaimCountryResponseRequestEvent responseRequest = new PlayerClaimCountryResponseRequestEvent (
            randomCountryName);
    gameModel.verifyPlayerClaimCountryResponseRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerClaimCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testVerifyPlayerClaimCountryResponseRequestInvalidCountryDoesNotExist ()
  {
    addMaxPlayers ();

    final PlayerClaimCountryResponseRequestEvent responseRequest = new PlayerClaimCountryResponseRequestEvent (
            "Transylvania");
    when (mockCommHandler.requestFor (responseRequest)).thenReturn (Optional.of (mock (PlayerInputRequestEvent.class)));
    publishInternalResponseRequestEvent (responseRequest);
    gameModel.verifyPlayerClaimCountryResponseRequest (responseRequest);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerClaimCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerInputRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerClaimCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testVerifyPlayerClaimCountryResponseRequestInvalidCountryAlreadyOwned ()
  {
    addMaxPlayers ();

    // add armies to player hands
    playerModel.addArmyToHandOf (playerModel.playerWith (PlayerTurnOrder.FIRST));
    playerModel.addArmyToHandOf (playerModel.playerWith (PlayerTurnOrder.SECOND));

    final Id country = randomCountry ();
    final PlayerClaimCountryResponseRequestEvent responseRequest = new PlayerClaimCountryResponseRequestEvent (
            countryMapGraphModel.nameOf (country));
    publishInternalResponseRequestEvent (responseRequest);
    gameModel.verifyPlayerClaimCountryResponseRequest (responseRequest);
    // should be successful for first player
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerClaimCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));

    gameModel.advancePlayerTurn (); // state machine does this as state exit action

    when (mockCommHandler.requestFor (responseRequest)).thenReturn (Optional.of (mock (PlayerInputRequestEvent.class)));
    publishInternalResponseRequestEvent (responseRequest);
    gameModel.verifyPlayerClaimCountryResponseRequest (responseRequest);
    // unsuccessful for second player
    assertTrue (eventHandler.secondToLastEventWasType (PlayerClaimCountryResponseDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerInputRequestEvent.class));
    // should not have received any more PlayerArmiesChangedEvents
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testWaitForInitialReinforcementSkipsPlayersWithZeroArmies ()
  {
    addMaxPlayers ();

    // add armies to player hands
    playerModel.addArmyToHandOf (playerModel.playerWith (PlayerTurnOrder.FIRST));
    playerModel.addArmyToHandOf (playerModel.playerWith (PlayerTurnOrder.THIRD));

    gameModel.waitForPlayersToReinforceInitialCountries ();
    assertTrue (eventHandler.thirdToLastEventWasType (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.secondToLastEventWasType (PlayerBeginReinforcementWaitEvent.class));
    assertTrue (eventHandler.lastEventWasType (ActivePlayerChangedEvent.class));

    eventHandler.clearEvents ();
    gameModel.advancePlayerTurn ();

    gameModel.waitForPlayersToReinforceInitialCountries ();
    assertTrue (eventHandler.wasNeverFired (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerBeginReinforcementWaitEvent.class));
    assertTrue (eventHandler.wasNeverFired (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.lastEventWasType (SkipPlayerTurnEvent.class));

    eventHandler.clearEvents ();
    gameModel.advancePlayerTurn ();
    gameModel.waitForPlayersToReinforceInitialCountries ();
    assertTrue (eventHandler.thirdToLastEventWasType (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.secondToLastEventWasType (PlayerBeginReinforcementWaitEvent.class));
    assertTrue (eventHandler.lastEventWasType (ActivePlayerChangedEvent.class));

  }

  @Test
  public void testBeginReinforcementPhase ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    gameModel.beginReinforcementPhase ();

    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    assertTrue (eventHandler.wasFiredExactlyOnce (BeginReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (BeginReinforcementPhaseEvent.class).getPlayer ().is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (testPlayerPacket.getArmiesInHand () > 0);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCardTradeInAvailableEvent.class));
  }

  @Test
  public void testBeginReinforcementPhaseWithTradeInAvailable ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    gameModel.beginReinforcementPhase ();

    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    assertTrue (eventHandler.wasFiredExactlyOnce (BeginReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (BeginReinforcementPhaseEvent.class).getPlayer ().is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (testPlayerPacket.getArmiesInHand () > 0);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCardTradeInAvailableEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerCardTradeInAvailableEvent.class).getPlayer ()
            .is (testPlayerPacket));
  }

  @Test
  public void testWaitForPlayerToPlaceReinforcements ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final PlayMapStateBuilder builder = new PlayMapStateBuilder (playMapModel);
    builder.forCountries (countryMapGraphModel.getCountryIds ()).setOwner (testPlayer);
    final int reinforcementCount = gameRules.getInitialReinforcementArmyCount ();
    playerModel.addArmiesToHandOf (testPlayer, reinforcementCount);

    gameModel.waitForPlayerToPlaceReinforcements ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerBeginReinforcementEvent.class).getPlayer ()
            .is (playerModel.playerPacketWith (testPlayer)));
    assertTrue (eventHandler.lastEventOfType (PlayerBeginReinforcementEvent.class).getPlayerOwnedCountries ()
            .equals (countryMapGraphModel.getCountryPackets ()));
  }

  @Test
  public void testWaitForPlayerToPlaceReinforcementsEndsReinforcementPhase ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final PlayMapStateBuilder builder = new PlayMapStateBuilder (playMapModel);
    builder.forCountries (countryMapGraphModel.getCountryIds ()).setOwner (testPlayer);

    assertEquals (0, playerModel.getArmiesInHand (testPlayer));

    gameModel.waitForPlayerToPlaceReinforcements ();

    assertTrue (eventHandler.wasNeverFired (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (EndReinforcementPhaseEvent.class).getPlayerOwnedCountries ()
            .equals (countryMapGraphModel.getCountryPackets ()));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementNoTradeIns ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    gameModel.beginReinforcementPhase ();

    final Iterator <CountryPacket> countries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    final int armiesInHand = testPlayerPacket.getArmiesInHand ();

    final PlayerTradeInCardsRequestEvent tradeInRequest = new PlayerTradeInCardsRequestEvent (
            new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.verifyPlayerCardTradeIn (tradeInRequest);

    final int count = armiesInHand;
    for (int i = 0; i < count; i++)
    {
      final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
              countries.next ().getName (), 1);
      gameModel.handlePlayerReinforceCountry (reinforceResponse);
      assertTrue (eventHandler.lastEventWasType (PlayerReinforceCountrySuccessEvent.class));
    }

    assertLastEventWasNotDeniedEvent ();
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerReinforceCountrySuccessEvent.class, count));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, count + 2));

    final Iterator <CountryPacket> updatedCountries = countryOwnerModel.getCountriesOwnedBy (testPlayer).iterator ();
    for (int i = 0; i < count; i++)
    {
      final String countryName = updatedCountries.next ().getName ();
      assertEquals (1, countryArmyModel.getArmyCountFor (countryMapGraphModel.countryWith (countryName)));
    }
  }

  @Test
  public void testVerifyPlayerCountryReinforcementWithRequiredTradeIn ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    gameModel.beginReinforcementPhase ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountriesOwnedBy (testPlayer));

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerCardTradeInAvailableEvent.class).getMatches ()
            .asList ().get (0);
    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (match);
    gameModel.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse;
    reinforceResponse = new PlayerReinforceCountryRequestEvent (randomCountry.getName (), 1);
    gameModel.handlePlayerReinforceCountry (reinforceResponse);

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 3));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);

    assertEquals (1, countryArmyModel.getArmyCountFor (countryMapGraphModel.countryWith (randomCountry.getName ())));
  }

  public void testVerifyPlayerCountryReinforcementWithOptionalTradeIn ()
  {
    // min required less one; this will work as long as the required count is > 3
    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE) - 1;

    cardDeck = CardModelTest.generateCards (CardType.TYPE1, numCardsInHand + 1);

    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    gameModel.beginReinforcementPhase ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountriesOwnedBy (testPlayer));

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerCardTradeInAvailableEvent.class).getMatches ()
            .asList ().get (0);

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (match);
    gameModel.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            randomCountry.getName (), 1);
    gameModel.handlePlayerReinforceCountry (reinforceResponse);

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 2));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);

    assertEquals (1, countryArmyModel.getArmyCountFor (countryMapGraphModel.countryWith (randomCountry.getName ())));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInvalidTradeIn ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    // pre-trade in match set so the cards are no longer in the player's hand
    final ImmutableList <CardSet.Match> matches = cardModel.computeMatchesFor (testPlayer).asList ();
    assertFalse (matches.isEmpty ());
    final CardSet.Match testTradeIn = matches.get (0);
    assertTrue (cardModel.requestTradeInCards (testPlayer, testTradeIn, TurnPhase.REINFORCE).isSuccessful ());

    gameModel.beginReinforcementPhase ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountriesOwnedBy (testPlayer));

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (
            CardPackets.fromCardMatchSet (ImmutableSet.of (testTradeIn)).asList ().get (0));
    gameModel.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            randomCountry.getName (), 1);
    gameModel.handlePlayerReinforceCountry (reinforceResponse);

    assertTrue (eventHandler.wasNeverFired (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseDeniedEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerTradeInCardsResponseDeniedEvent.class).getReason ()
            .equals (PlayerTradeInCardsResponseDeniedEvent.Reason.CARDS_NOT_IN_HAND));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInvalidCountry ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id notOwnedCountry = countryMapGraphModel.getCountryIds ().asList ().get (0);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      if (nextCountry.is (notOwnedCountry)) continue;
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    gameModel.beginReinforcementPhase ();

    final String notOwnedCountryName = countryMapGraphModel.nameOf (notOwnedCountry);
    final int armyCount = playerModel.getArmiesInHand (testPlayer);

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (
            new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            notOwnedCountryName, armyCount);
    gameModel.handlePlayerReinforceCountry (reinforceResponse);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountryDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountryDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInsufficientArmyCount ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    gameModel.beginReinforcementPhase ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountriesOwnedBy (testPlayer));
    final int reinforcementCount = playerModel.getArmiesInHand (testPlayer) + 1;

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (
            new DefaultCardSetPacket (ImmutableSet.<CardPacket> of ()));
    gameModel.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            randomCountry.getName (), reinforcementCount);
    gameModel.handlePlayerReinforceCountry (reinforceResponse);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountryDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND));
  }

  @Test
  public void testBeginFortifyPhase ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.beginFortifyPhase ();

    assertTrue (eventHandler.wasFiredExactlyOnce (BeginFortifyPhaseEvent.class));
    assertEquals (playerModel.playerPacketWith (player1),
                  eventHandler.lastEventOfType (BeginFortifyPhaseEvent.class).getPlayer ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerBeginFortificationEvent.class));
    assertEquals (playerModel.playerPacketWith (player1),
                  eventHandler.lastEventOfType (PlayerBeginFortificationEvent.class).getPlayer ());
    final ImmutableMultimap <CountryPacket, CountryPacket> expectedFortifyVectors;
    expectedFortifyVectors = buildCountryMultimapFromIndices (defaultTestCountries, adj (0, 1, 3), adj (1, 0),
                                                              adj (3, 0));
    assertEquals (expectedFortifyVectors,
                  eventHandler.lastEventOfType (PlayerBeginFortificationEvent.class).getValidVectors ());
  }

  @Test
  public void testBeginFortifyPhaseSkipsPhaseWhenNoValidVectorsExist ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    // make sure country army count is below threshold for fortification
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () - 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    gameModel.beginFortifyPhase ();

    assertTrue (eventHandler.wasFiredExactlyOnce (SkipFortifyPhaseEvent.class));
    assertTrue (eventHandler.wasNeverFired (BeginFortifyPhaseEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerBeginFortificationEvent.class));
  }

  @Test
  public void testVerifyValidPlayerFortifyCountryResponseRequest ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    assertTrue (gameModel.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (3))));
    assertTrue (gameModel
            .verifyPlayerFortifyCountryResponse (new PlayerFortifyCountryResponseRequestEvent (countryArmyCount - 1)));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorDeniedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseDeniedEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestSourceCountryNotOwned ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    assertFalse (gameModel.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (2), defaultTestCountries.get (0))));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorDeniedEvent.class));
    assertEquals (PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_SOURCE_COUNTRY,
                  eventHandler.lastEventOfType (PlayerSelectFortifyVectorDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestTargetCountryNotOwned ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    assertFalse (gameModel.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (2))));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorDeniedEvent.class));
    assertEquals (PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_TARGET_COUNTRY,
                  eventHandler.lastEventOfType (PlayerSelectFortifyVectorDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestCountriesNotAdjacent ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    assertFalse (gameModel.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (1), defaultTestCountries.get (3))));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorDeniedEvent.class));
    assertEquals (PlayerSelectFortifyVectorDeniedEvent.Reason.COUNTRIES_NOT_ADJACENT,
                  eventHandler.lastEventOfType (PlayerSelectFortifyVectorDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryResponseRequestTooManyArmies ()
  {
    initializeGameModelWith (createPlayMapModelWithTestMapGraph (defaultTestCountries));

    addMaxPlayers ();

    // sanity checks
    assertTrue (gameModel.isFirstTurn ());
    assertTrue (gameModel.getCurrentPlayerId ().is (playerModel.playerWith (PlayerTurnOrder.FIRST)));

    final Id player1 = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id player2 = playerModel.playerWith (PlayerTurnOrder.SECOND);
    final int countryArmyCount = gameRules.getMinArmiesOnSourceCountryForFortify () + 1;
    final ImmutableList <Integer> ownedCountryIndicesPlayer1 = ImmutableList.of (0, 1, 3);
    final ImmutableList <Integer> ownedCountryIndicesPlayer2 = ImmutableList.of (2, 4, 5);
    final ImmutableList <Id> countryIdsPlayer1 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer1);
    final ImmutableList <Id> countryIdsPlayer2 = countryIdsFor (defaultTestCountries, ownedCountryIndicesPlayer2);
    final PlayMapStateBuilder playMapStateBuilder = new PlayMapStateBuilder (playMapModel);
    playMapStateBuilder.forCountries (countryIdsPlayer1).setOwner (player1).addArmies (countryArmyCount);
    playMapStateBuilder.forCountries (countryIdsPlayer2).setOwner (player2).addArmies (countryArmyCount);

    assertTrue (gameModel.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (1))));
    assertFalse (gameModel
            .verifyPlayerFortifyCountryResponse (new PlayerFortifyCountryResponseRequestEvent (countryArmyCount)));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerFortifyCountryResponseDeniedEvent.class));
    assertEquals (PlayerFortifyCountryResponseDeniedEvent.Reason.FORTIFY_DELTA_ARMY_COUNT_OVERFLOW,
                  eventHandler.lastEventOfType (PlayerFortifyCountryResponseDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerFortifyCountryResponseSuccessEvent.class));
  }

  @Test
  public void testVerifyPlayerEndTurnRequest ()
  {
    addMaxPlayers ();

    final PlayerPacket player = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    final EndPlayerTurnRequestEvent endTurnRequest = new EndPlayerTurnRequestEvent ();
    when (mockCommHandler.senderOf (endTurnRequest)).thenReturn (Optional.of (player));

    assertTrue (gameModel.verifyPlayerEndTurnRequest (endTurnRequest));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndPlayerTurnSuccessEvent.class));
  }

  @Test
  public void testVerifyPlayerEndTurnRequestFailsWithInvalidPlayer ()
  {
    addMaxPlayers ();

    final PlayerPacket player = playerModel.playerPacketWith (PlayerTurnOrder.SECOND);
    final EndPlayerTurnRequestEvent endTurnRequest = new EndPlayerTurnRequestEvent ();
    when (mockCommHandler.senderOf (endTurnRequest)).thenReturn (Optional.of (player));

    assertFalse (gameModel.verifyPlayerEndTurnRequest (endTurnRequest));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndPlayerTurnDeniedEvent.class));
    assertTrue (eventHandler.wasNeverFired (EndPlayerTurnSuccessEvent.class));
  }

  @Test
  public void testHandlePlayerJoinGameRequestFailed ()
  {
    addMaxPlayers ();

    final String name = "TestPlayerX";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameDeniedEvent.class));
    assertThat (playerNameFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)), is (name));
    assertThat (reasonFrom (eventHandler.lastEventOfType (PlayerJoinGameDeniedEvent.class)),
                is (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL));
  }

  @Test
  public void testHandlePlayerJoinGameRequestSucceeded ()
  {
    final String name = "TestPlayer";

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent (name));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
    assertEquals (eventHandler.lastEventOfType (PlayerJoinGameSuccessEvent.class).getPlayerName (), name);
  }

  @Test
  public void testIsEmpty ()
  {
    assertTrue (gameModel.isEmpty ());

    addSinglePlayer ();

    assertFalse (gameModel.isEmpty ());
  }

  // --- private test utility methods --- //

  @Test
  public void testIsFull ()
  {
    addMaxPlayers ();

    assertTrue (gameModel.isFull ());
  }

  private static ImmutableList <String> generateTestCountryNames (final int totalCountryCount)
  {
    final ImmutableList.Builder <String> countryNames = ImmutableList.builder ();
    for (int i = 0; i < totalCountryCount; i++)
    {
      countryNames.add ("TestCountry-" + i);
    }
    return countryNames.build ();
  }

  private void verifyPlayerCountryAssignmentCompleteEvent ()
  {
    for (final Id country : countryMapGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.isCountryOwned (country));
      final PlayerCountryAssignmentCompleteEvent event = eventHandler
              .lastEventOfType (PlayerCountryAssignmentCompleteEvent.class);
      final CountryPacket countryPacket = countryMapGraphModel.countryPacketWith (country);
      final Id player = countryOwnerModel.ownerOf (country);
      assertEquals (playerModel.playerPacketWith (player), event.getOwner (countryPacket));
    }
  }

  private void addMaxPlayers ()
  {
    assertTrue (gameModel.playerLimitIs (maxPlayers));

    for (int i = 1; i <= playerLimit; i++)
    {
      gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer" + i));
    }

    assertTrue (gameModel.isFull ());
  }

  private void addSinglePlayer ()
  {
    assertTrue (gameModel.isEmpty ());
    assertTrue (gameModel.isNotFull ());

    gameModel.handlePlayerJoinGameRequest (new PlayerJoinGameRequestEvent ("TestPlayer"));

    assertTrue (gameModel.playerCountIs (1));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
  }

  private void assertLastEventWasNotDeniedEvent ()
  {
    if (eventHandler.lastEventWasType (DeniedEvent.class))
    {
      final DeniedEvent <?> event = eventHandler.lastEvent (DeniedEvent.class);
      fail (event.getReason ().toString ());
    }
  }

  /**
   * Publish the internal response request event so that it gets registered with InternalCommunicationHandler.
   */
  private <T extends ResponseRequestEvent> void publishInternalResponseRequestEvent (final T event)
  {
    // this is so nasty... but it works for some reason O_o
    try
    {
      final Class <? extends ServerRequestEvent> requestType = event.getRequestType ();
      final Constructor <? extends ServerRequestEvent> ctor = requestType.getDeclaredConstructor ();
      ctor.setAccessible (true);
      eventBus.publish (new DefaultInboundPlayerResponseRequestEvent <T, PlayerInputRequestEvent> (
              mock (PlayerPacket.class), event, (PlayerInputRequestEvent) ctor.newInstance ()));
    }
    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
            | IllegalArgumentException | InvocationTargetException e)
    {
      fail (e.toString ());
    }
  }

  private Id randomCountry ()
  {
    return Randomness.getRandomElementFrom (countryMapGraphModel.getCountryIds ());
  }

  private ImmutableList <Id> countryIdsFor (final ImmutableList <String> countryNames,
                                            final ImmutableList <Integer> indices)
  {
    final ImmutableList.Builder <Id> countryIds = ImmutableList.builder ();
    for (final int i : indices)
    {
      countryIds.add (countryMapGraphModel.countryWith (countryNames.get (i)));
    }
    return countryIds.build ();
  }

  private CountryAdjacencyIndices adj (final int... adjArr)
  {
    assert adjArr != null;
    assert adjArr.length >= 1;

    return new CountryAdjacencyIndices (adjArr [0], ArrayUtils.subarray (adjArr, 1, adjArr.length));
  }

  private ImmutableMultimap <CountryPacket, CountryPacket> buildCountryMultimapFromIndices (final ImmutableList <String> countryNameList,
                                                                                            final CountryAdjacencyIndices... adjacencyIndices)
  {
    assert countryNameList != null;
    assert adjacencyIndices != null;

    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> expectedFortifyVectors = ImmutableSetMultimap
            .builder ();
    for (final CountryAdjacencyIndices adjInd : adjacencyIndices)
    {
      final CountryPacket cp0 = countryMapGraphModel.countryPacketWith (countryNameList.get (adjInd.c0));
      for (final int adj : adjInd.adj)
      {
        final CountryPacket cpAdj = countryMapGraphModel.countryPacketWith (countryNameList.get (adj));
        expectedFortifyVectors.put (cp0, cpAdj);
      }
    }
    return expectedFortifyVectors.build ();
  }

  private void initializeGameModelWith (final PlayMapModel playMapModel)
  {
    gameRules = playMapModel.getRules ();
    playerModel = new DefaultPlayerModel (gameRules);
    cardModel = new DefaultCardModel (gameRules, playerModel, cardDeck);
    battleModel = new DefaultBattleModel (playMapModel);
    countryMapGraphModel = playMapModel.getCountryMapGraphModel ();
    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    this.playMapModel = playMapModel;

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxPlayers ();
    gameModel = GameModel.builder (gameRules).eventBus (eventBus).playMapModel (playMapModel).battleModel (battleModel)
            .playerModel (playerModel).cardModel (cardModel).internalComms (mockCommHandler).build ();
  }

  private PlayMapModel createPlayMapModelWithDisjointMapGraph (final ImmutableList <String> countryNames)
  {
    final CountryFactory factory = new CountryFactory ();
    for (final String name : countryNames)
    {
      factory.newCountryWith (name);
    }
    final CountryMapGraphModel countryMapGraphModel = CountryMapGraphModelTest
            .createDisjointCountryMapGraphModelWith (factory);

    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentMapGraphModel continentMapGraphModel = ContinentMapGraphModelTest
            .createContinentMapGraphModelWith (continentFactory, countryMapGraphModel);
    return new DefaultPlayMapModelFactory (gameRules).create (countryMapGraphModel, continentMapGraphModel);
  }

  private PlayMapModel createPlayMapModelWithTestMapGraph (final ImmutableList <String> countryNames)
  {
    final CountryMapGraphModel countryMapGraphModel = createDefaultTestCountryMapGraph (countryNames);
    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentMapGraphModel continentMapGraphModel = ContinentMapGraphModelTest
            .createContinentMapGraphModelWith (continentFactory, countryMapGraphModel);
    playMapModel = new DefaultPlayMapModelFactory (gameRules).create (countryMapGraphModel, continentMapGraphModel);
    return playMapModel;
  }

  private class CountryAdjacencyIndices
  {
    final int c0;
    final int[] adj;

    CountryAdjacencyIndices (final int c0, final int... adj)
    {
      this.c0 = c0;
      this.adj = adj;
    }
  }
}
