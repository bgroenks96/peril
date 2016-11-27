package com.forerunnergames.peril.core.model.game.phase.init;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.response.PlayerClaimCountryResponseRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandlerTest;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

import org.junit.Test;

public class InitialPhaseHandlerTest extends AbstractGamePhaseHandlerTest
{
  private InitialPhaseHandler initialPhase;

  @Override
  protected void setupTest ()
  {
    initialPhase = new DefaultInitialPhaseHandler (gameModelConfig, eventFactory);
  }

  @Test
  public void testDeterminePlayerTurnOrderMaxPlayers ()
  {
    addMaxPlayers ();

    initialPhase.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerTurnOrderChangedEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderOnePlayer ()
  {
    addSinglePlayer ();

    initialPhase.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerTurnOrderChangedEvent.class));
  }

  @Test
  public void testDeterminePlayerTurnOrderZeroPlayers ()
  {
    assertTrue (gameModel.isEmpty ());

    initialPhase.determinePlayerTurnOrder ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DeterminePlayerTurnOrderCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerTurnOrderChangedEvent.class));
  }

  @Test
  public void testDistributeInitialArmiesMaxPlayers ()
  {
    addMaxPlayers ();

    initialPhase.distributeInitialArmies ();

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

    initialPhase.distributeInitialArmies ();

    assertTrue (eventHandler.wasFiredExactlyOnce (DistributeInitialArmiesCompleteEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
  }

  @Test
  public void testWaitForCountryAssignmentToBeginRandom ()
  {
    gameRules = ClassicGameRules.builder ().maxHumanPlayers ().totalCountryCount (defaultTestCountryCount)
            .initialCountryAssignment (InitialCountryAssignment.RANDOM).build ();
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    initializeGameModelWith (playMapModel);

    addMaxPlayers ();

    initialPhase.waitForCountryAssignmentToBegin ();

    assertTrue (eventHandler.wasFiredExactlyOnce (BeginPlayerCountryAssignmentEvent.class));
    assertEquals (InitialCountryAssignment.RANDOM,
                  eventHandler.lastEventOfType (BeginPlayerCountryAssignmentEvent.class).getAssignmentMode ());
  }

  @Test
  public void testWaitForCountryAssignmentToBeginManual ()
  {
    gameRules = ClassicGameRules.builder ().maxHumanPlayers ().totalCountryCount (defaultTestCountryCount)
            .initialCountryAssignment (InitialCountryAssignment.MANUAL).build ();
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    initializeGameModelWith (playMapModel);
    setupTest (); // re-initialize test with new model configuration

    addMaxPlayers ();

    initialPhase.waitForCountryAssignmentToBegin ();

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

    initialPhase.randomlyAssignPlayerCountries ();

    assertFalse (countryOwnerModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesTenPlayersTenCountries ()
  {
    // test case in honor of Aaron on PR 27 ;)
    // can't use 5, though, because 5 < ClassicGameRules.MIN_TOTAL_COUNTRY_COUNT

    gameRules = ClassicGameRules.builder ().totalCountryCount (10).humanPlayerLimit (10).build ();
    initializeGameModelWith (createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (10)));
    setupTest (); // re-initialize test with new model configuration
    for (int i = 0; i < 10; ++i)
    {
      gameModel.handlePlayerJoinGameRequest (new HumanPlayerJoinGameRequestEvent ("TestPlayer" + i));
    }
    assertTrue (gameModel.playerCountIs (10));
    assertTrue (countryGraphModel.countryCountIs (10));

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, countryGraphModel.getCountryCount () / gameModel.getPlayerCount ());
    }

    initialPhase.randomlyAssignPlayerCountries ();

    assertFalse (countryOwnerModel.hasAnyUnownedCountries ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesMaxPlayersMaxCountries ()
  {
    final int countryCount = ClassicGameRules.MAX_TOTAL_COUNTRY_COUNT;
    gameRules = ClassicGameRules.builder ().totalCountryCount (countryCount).maxHumanPlayers ().build ();
    initializeGameModelWith (createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (countryCount)));
    setupTest (); // re-initialize test with new model configuration

    addMaxPlayers ();

    for (final Id player : playerModel.getPlayerIds ())
    {
      playerModel.addArmiesToHandOf (player, countryGraphModel.getCountryCount () / gameModel.getPlayerCount ());
    }

    initialPhase.randomlyAssignPlayerCountries ();

    assertTrue (countryOwnerModel.allCountriesAreOwned ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCountryAssignmentCompleteEvent.class));
    verifyPlayerCountryAssignmentCompleteEvent ();
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, playerModel.getPlayerCount ()));
  }

  @Test
  public void testRandomlyAssignPlayerCountriesZeroPlayers ()
  {
    assertTrue (playerModel.isEmpty ());

    initialPhase.randomlyAssignPlayerCountries ();

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

    initialPhase.waitForPlayersToClaimInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerClaimCountryRequestEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    assertTrue (eventHandler.lastEventOfType (PlayerClaimCountryRequestEvent.class).getPerson ().is (expectedPlayer));
    assertTrue (eventHandler.lastEventOfType (ActivePlayerChangedEvent.class).getPerson ().is (expectedPlayer));
  }

  @Test
  public void testWaitForPlayersToClaimInitialCountriesSkipsPlayerWithEmptyHand ()
  {
    addMaxPlayers ();

    assertTrue (countryOwnerModel.allCountriesAreUnowned ());

    initialPhase.waitForPlayersToClaimInitialCountries ();

    assertTrue (eventHandler.wasFiredExactlyOnce (SkipPlayerTurnEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerClaimCountryRequestEvent.class));
    assertTrue (eventHandler.wasNeverFired (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCountryAssignmentCompleteEvent.class));

    final PlayerPacket expectedPlayer = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    assertTrue (eventHandler.lastEvent (SkipPlayerTurnEvent.class).getPerson ().is (expectedPlayer));
  }

  @Test
  public void testWaitForPlayersToClaimInitialCountriesAllOwned ()
  {
    addMaxPlayers ();

    final Id testPlayerOwner = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayerOwner).commitIfSuccessful ());
    }

    assertTrue (countryOwnerModel.allCountriesAreOwned ());

    initialPhase.waitForPlayersToClaimInitialCountries ();

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
    final String randomCountryName = countryGraphModel.nameOf (randomCountry);

    final PlayerClaimCountryResponseRequestEvent responseRequest = new PlayerClaimCountryResponseRequestEvent (
            randomCountryName);
    initialPhase.verifyPlayerClaimCountryResponseRequest (responseRequest);

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
    when (mockCommHandler.inputRequestFor (responseRequest))
            .thenReturn (Optional.of (mock (PlayerInputRequestEvent.class)));
    publishInternalResponseRequestEvent (responseRequest);
    initialPhase.verifyPlayerClaimCountryResponseRequest (responseRequest);

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
            countryGraphModel.nameOf (country));
    publishInternalResponseRequestEvent (responseRequest);
    initialPhase.verifyPlayerClaimCountryResponseRequest (responseRequest);
    // should be successful for first player
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerClaimCountryResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));

    advancePlayerTurn (); // state machine does this as state exit action

    when (mockCommHandler.inputRequestFor (responseRequest))
            .thenReturn (Optional.of (mock (PlayerInputRequestEvent.class)));
    publishInternalResponseRequestEvent (responseRequest);
    initialPhase.verifyPlayerClaimCountryResponseRequest (responseRequest);
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

    initialPhase.waitForPlayersToReinforceInitialCountries ();
    assertTrue (eventHandler.thirdToLastEventWasType (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.secondToLastEventWasType (PlayerBeginReinforcementWaitEvent.class));
    assertTrue (eventHandler.lastEventWasType (ActivePlayerChangedEvent.class));

    eventHandler.clearEvents ();
    advancePlayerTurn ();

    initialPhase.waitForPlayersToReinforceInitialCountries ();
    assertTrue (eventHandler.wasNeverFired (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerBeginReinforcementWaitEvent.class));
    assertTrue (eventHandler.wasNeverFired (ActivePlayerChangedEvent.class));
    assertTrue (eventHandler.lastEventWasType (SkipPlayerTurnEvent.class));

    eventHandler.clearEvents ();
    advancePlayerTurn ();
    initialPhase.waitForPlayersToReinforceInitialCountries ();
    assertTrue (eventHandler.thirdToLastEventWasType (PlayerBeginReinforcementEvent.class));
    assertTrue (eventHandler.secondToLastEventWasType (PlayerBeginReinforcementWaitEvent.class));
    assertTrue (eventHandler.lastEventWasType (ActivePlayerChangedEvent.class));

  }
}
