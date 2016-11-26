package com.forerunnergames.peril.core.model.game.phase.turn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.net.events.client.request.PlayerOrderFortifyRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerSelectFortifyVectorRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOrderFortifyDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerSelectFortifyVectorDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginFortificationEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderFortifySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerSelectFortifyVectorSuccessEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandlerTest;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.playmap.PlayMapStateBuilder;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import org.junit.Test;

public class FortifyPhaseHandlerTest extends AbstractGamePhaseHandlerTest
{
  private FortifyPhaseHandler fortifyPhase;

  @Override
  protected void setupTest ()
  {
    initializeGameModelWith (createPlayMapModelWithTestTerritoryGraphs (defaultTestCountries));
    addMaxPlayers ();
    fortifyPhase = new DefaultFortifyPhaseHandler (gameModelConfig);
  }

  @Test
  public void testBeginFortifyPhase ()
  {
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

    // begin fortification phase (part I)
    fortifyPhase.begin ();

    assertTrue (eventHandler.wasFiredExactlyOnce (BeginFortifyPhaseEvent.class));
    assertEquals (playerModel.playerPacketWith (player1),
                  eventHandler.lastEventOfType (BeginFortifyPhaseEvent.class).getPerson ());

    // begin fortification phase (part II)
    fortifyPhase.waitForPlayerToSelectFortifyVector ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerBeginFortificationEvent.class));
    assertEquals (playerModel.playerPacketWith (player1),
                  eventHandler.lastEventOfType (PlayerBeginFortificationEvent.class).getPerson ());
    final ImmutableMultimap <CountryPacket, CountryPacket> expectedFortifyVectors;
    expectedFortifyVectors = buildCountryMultimapFromIndices (defaultTestCountries, adj (0, 1, 3), adj (1, 0),
                                                              adj (3, 0));
    assertEquals (expectedFortifyVectors,
                  eventHandler.lastEventOfType (PlayerBeginFortificationEvent.class).getValidVectors ());
  }

  @Test
  public void testBeginFortifyPhaseSkipsPhaseWhenNoValidVectorsExist ()
  {
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

    fortifyPhase.begin ();

    assertTrue (eventHandler.wasFiredExactlyOnce (SkipFortifyPhaseEvent.class));
    assertTrue (eventHandler.wasNeverFired (BeginFortifyPhaseEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerBeginFortificationEvent.class));
  }

  @Test
  public void testVerifyValidPlayerFortifyCountryRequest ()
  {
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

    assertTrue (fortifyPhase.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (3))));
    assertTrue (fortifyPhase.verifyPlayerFortifyOrder (new PlayerOrderFortifyRequestEvent (countryArmyCount - 1)));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerOrderFortifySuccessEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorDeniedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerOrderFortifyDeniedEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryRequestSourceCountryNotOwned ()
  {
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

    assertFalse (fortifyPhase.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (2), defaultTestCountries.get (0))));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorDeniedEvent.class));
    assertEquals (PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_SOURCE_COUNTRY,
                  eventHandler.lastEventOfType (PlayerSelectFortifyVectorDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryRequestTargetCountryNotOwned ()
  {
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

    assertFalse (fortifyPhase.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (2))));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorDeniedEvent.class));
    assertEquals (PlayerSelectFortifyVectorDeniedEvent.Reason.NOT_OWNER_OF_TARGET_COUNTRY,
                  eventHandler.lastEventOfType (PlayerSelectFortifyVectorDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryRequestCountriesNotAdjacent ()
  {
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

    assertFalse (fortifyPhase.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (1), defaultTestCountries.get (3))));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorDeniedEvent.class));
    assertEquals (PlayerSelectFortifyVectorDeniedEvent.Reason.COUNTRIES_NOT_ADJACENT,
                  eventHandler.lastEventOfType (PlayerSelectFortifyVectorDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerSelectFortifyVectorSuccessEvent.class));
  }

  @Test
  public void testVerifyInvalidPlayerFortifyCountryRequestTooManyArmies ()
  {
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

    assertTrue (fortifyPhase.verifyPlayerFortifyVectorSelection (new PlayerSelectFortifyVectorRequestEvent (
            defaultTestCountries.get (0), defaultTestCountries.get (1))));
    assertFalse (fortifyPhase.verifyPlayerFortifyOrder (new PlayerOrderFortifyRequestEvent (countryArmyCount)));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerSelectFortifyVectorSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerOrderFortifyDeniedEvent.class));
    assertEquals (PlayerOrderFortifyDeniedEvent.Reason.FORTIFY_DELTA_ARMY_COUNT_OVERFLOW,
                  eventHandler.lastEventOfType (PlayerOrderFortifyDeniedEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (PlayerOrderFortifySuccessEvent.class));
  }
}
