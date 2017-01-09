/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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

package com.forerunnergames.peril.core.model.game.phase.turn;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.forerunnergames.peril.common.game.CardType;
import com.forerunnergames.peril.common.game.GamePhase;
import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerTradeInCardsDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerReinforceCountryEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.common.net.packets.defaults.DefaultCardSetPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.model.card.CardModelTest;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandlerTest;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.playmap.PlayMapStateBuilder;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.Iterator;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReinforcementPhaseHandlerTest extends AbstractGamePhaseHandlerTest
{
  private static final Logger log = LoggerFactory.getLogger (ReinforcementPhaseHandlerTest.class);
  private ReinforcementPhaseHandler reinforcementPhase;
  private TurnPhaseHandler turnPhaseHandler;

  @Override
  protected void setupTest ()
  {
    turnPhaseHandler = new DefaultTurnPhaseHandler (gameModelConfig, eventFactory);
    reinforcementPhase = new DefaultReinforcementPhaseHandler (gameModelConfig, turnPhaseHandler);
    phaseHandlerBase = reinforcementPhase;
  }

  @Test
  public void testBeginReinforcementPhase ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    reinforcementPhase.begin ();

    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    assertTrue (eventHandler.wasFiredExactlyOnce (BeginReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (BeginReinforcementPhaseEvent.class).getPerson ().is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (testPlayerPacket.getArmiesInHand () > 0);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCardTradeInAvailableEvent.class));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testBeginReinforcementPhaseSkipsForCountryArmyOverflow ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final PlayMapStateBuilder builder = new PlayMapStateBuilder (playMapModel);
    builder.forCountries (countryGraphModel.getCountryIds ()).setOwner (testPlayer)
            .addArmies (gameRules.getMaxArmiesOnCountry ());

    reinforcementPhase.begin ();

    assertTrue (eventHandler.wasFiredExactlyOnce (SkipReinforcementPhaseEvent.class));
    assertEquals (SkipReinforcementPhaseEvent.Reason.COUNTRY_ARMY_OVERFLOW,
                  eventHandler.lastEventOfType (SkipReinforcementPhaseEvent.class).getReason ());
    assertTrue (eventHandler.wasNeverFired (BeginReinforcementPhaseEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerArmiesChangedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerCardTradeInAvailableEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountryEvent.class));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testBeginReinforcementPhaseWithTradeInAvailable ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    reinforcementPhase.begin ();

    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    assertTrue (eventHandler.wasFiredExactlyOnce (BeginReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (BeginReinforcementPhaseEvent.class).getPerson ().is (testPlayerPacket));

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerArmiesChangedEvent.class));
    assertTrue (testPlayerPacket.getArmiesInHand () > 0);

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerCardTradeInAvailableEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerCardTradeInAvailableEvent.class).getPerson ()
            .is (testPlayerPacket));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testWaitForPlayerToPlaceReinforcements ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final PlayMapStateBuilder builder = new PlayMapStateBuilder (playMapModel);
    builder.forCountries (countryGraphModel.getCountryIds ()).setOwner (testPlayer);
    final int reinforcementCount = gameRules.getMinReinforcementsPlacedPerCountry ();
    playerModel.addArmiesToHandOf (testPlayer, reinforcementCount);

    reinforcementPhase.waitForPlayerToPlaceReinforcements ();

    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountryEvent.class).getPerson ()
            .is (playerModel.playerPacketWith (testPlayer)));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountryEvent.class).getReinforceableCountries ()
            .equals (countryGraphModel.getCountryPackets ()));
  }

  @Test
  public void testWaitForPlayerToPlaceReinforcementsEndsReinforcementPhaseWhenNoArmiesInHand ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final PlayMapStateBuilder builder = new PlayMapStateBuilder (playMapModel);
    builder.forCountries (countryGraphModel.getCountryIds ()).setOwner (testPlayer);

    assertEquals (0, playerModel.getArmiesInHand (testPlayer));

    reinforcementPhase.waitForPlayerToPlaceReinforcements ();

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountryEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (EndReinforcementPhaseEvent.class).getPlayerOwnedCountries ()
            .equals (countryGraphModel.getCountryPackets ()));
  }

  @Test
  public void testWaitForPlayerToPlaceReinforcementsEndsReinforcementPhaseMaxCountryArmyOverflow ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final PlayMapStateBuilder builder = new PlayMapStateBuilder (playMapModel);
    builder.forCountries (countryGraphModel.getCountryIds ()).setOwner (testPlayer)
            .addArmies (gameRules.getMaxArmiesOnCountry ());
    playerModel.addArmiesToHandOf (testPlayer, gameRules.getMinReinforcementsPlacedPerCountry ());

    reinforcementPhase.waitForPlayerToPlaceReinforcements ();

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountryEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndReinforcementPhaseEvent.class));
    assertTrue (eventHandler.lastEventOfType (EndReinforcementPhaseEvent.class).getPlayerOwnedCountries ()
            .equals (countryGraphModel.getCountryPackets ()));
  }

  @Test
  public void testVerifyPlayerCountryReinforcementNoTradeIns ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    reinforcementPhase.begin ();

    final Iterator <CountryPacket> countries = countryOwnerModel.getCountryPacketsOwnedBy (testPlayer).iterator ();
    final PlayerPacket testPlayerPacket = playerModel.playerPacketWith (testPlayer);
    final int armiesInHand = testPlayerPacket.getArmiesInHand ();

    final PlayerTradeInCardsRequestEvent tradeInRequest = new PlayerTradeInCardsRequestEvent (
            new DefaultCardSetPacket (ImmutableSet. <CardPacket>of ()));
    turnPhaseHandler.verifyPlayerCardTradeIn (tradeInRequest);

    final int count = armiesInHand;
    for (int i = 0; i < count; i++)
    {
      final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
              countries.next ().getName (), 1);
      reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);
      assertTrue (eventHandler.lastEventWasType (PlayerReinforceCountrySuccessEvent.class));
    }

    assertLastEventWasNotDeniedEvent ();
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerReinforceCountrySuccessEvent.class, count));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, count + 2));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);

    final Iterator <CountryPacket> updatedCountries = countryOwnerModel.getCountryPacketsOwnedBy (testPlayer)
            .iterator ();
    for (int i = 0; i < count; i++)
    {
      final String countryName = updatedCountries.next ().getName ();
      assertEquals (1, countryArmyModel.getArmyCountFor (countryGraphModel.countryWith (countryName)));
    }
  }

  @Test
  public void testVerifyPlayerCountryReinforcementWithRequiredTradeIn ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    reinforcementPhase.begin ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountryPacketsOwnedBy (testPlayer));

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerCardTradeInAvailableEvent.class).getMatches ()
            .asList ().get (0);
    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (match);
    turnPhaseHandler.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse;
    reinforceResponse = new PlayerReinforceCountryRequestEvent (randomCountry.getName (), 1);
    reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 3));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);
    assertEquals (1, countryArmyModel.getArmyCountFor (countryGraphModel.countryWith (randomCountry.getName ())));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWhenRequiredTradeInNotReceived ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE);

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    reinforcementPhase.begin ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountryPacketsOwnedBy (testPlayer));

    final PlayerReinforceCountryRequestEvent reinforceResponse;
    reinforceResponse = new PlayerReinforceCountryRequestEvent (randomCountry.getName (), 1);
    final boolean check = reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);

    log.debug ("{}", eventHandler.lastEvent ());
    assertFalse (check);
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryDeniedEvent.class));
    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountrySuccessEvent.class));
    assertEquals (PlayerReinforceCountryDeniedEvent.Reason.TRADE_IN_REQUIRED,
                  eventHandler.lastEventOfType (PlayerReinforceCountryDeniedEvent.class).getReason ());
    assertEquals (numCardsInHand, cardModel.countCardsInHand (testPlayer));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  public void testVerifyPlayerCountryReinforcementWithOptionalTradeIn ()
  {
    // min required less one; this will work as long as the required count is > 3
    final int numCardsInHand = gameRules.getMinCardsInHandToRequireTradeIn (TurnPhase.REINFORCE) - 1;

    cardDeck = CardModelTest.generateCards (CardType.TYPE1, numCardsInHand + 1);

    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer);
    }

    for (int i = 0; i < numCardsInHand; i++)
    {
      cardModel.giveCard (testPlayer, TurnPhase.REINFORCE);
    }

    reinforcementPhase.begin ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountryPacketsOwnedBy (testPlayer));

    final CardSetPacket match = eventHandler.lastEventOfType (PlayerCardTradeInAvailableEvent.class).getMatches ()
            .asList ().get (0);

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (match);
    turnPhaseHandler.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            randomCountry.getName (), 1);
    reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);

    log.debug ("{}", eventHandler.lastEvent ());
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyNTimes (PlayerArmiesChangedEvent.class, 2));
    assertTrue (cardModel.countCardsInHand (testPlayer) < numCardsInHand);
    assertEquals (1, countryArmyModel.getArmyCountFor (countryGraphModel.countryWith (randomCountry.getName ())));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInvalidTradeIn ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
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

    reinforcementPhase.begin ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountryPacketsOwnedBy (testPlayer));

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (
            CardPackets.fromCardMatchSet (ImmutableSet.of (testTradeIn)).asList ().get (0));
    turnPhaseHandler.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            randomCountry.getName (), 1);
    reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);

    assertTrue (eventHandler.wasNeverFired (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsDeniedEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerTradeInCardsDeniedEvent.class).getReason ()
            .equals (PlayerTradeInCardsDeniedEvent.Reason.CARDS_NOT_IN_HAND));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInvalidCountry ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    final Id notOwnedCountry = countryGraphModel.getCountryIds ().asList ().get (0);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      if (nextCountry.is (notOwnedCountry)) continue;
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    reinforcementPhase.begin ();

    final String notOwnedCountryName = countryGraphModel.nameOf (notOwnedCountry);
    final int armyCount = playerModel.getArmiesInHand (testPlayer);

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (
            new DefaultCardSetPacket (ImmutableSet. <CardPacket>of ()));
    turnPhaseHandler.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            notOwnedCountryName, armyCount);
    reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountryDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountryDeniedEvent.Reason.NOT_OWNER_OF_COUNTRY));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }

  @Test
  public void testVerifyPlayerCountryReinforcementFailsWithInsufficientArmyCount ()
  {
    addMaxPlayers ();

    final Id testPlayer = playerModel.playerWith (PlayerTurnOrder.FIRST);
    for (final Id nextCountry : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.requestToAssignCountryOwner (nextCountry, testPlayer).commitIfSuccessful ());
    }

    reinforcementPhase.begin ();

    final CountryPacket randomCountry = Randomness
            .getRandomElementFrom (countryOwnerModel.getCountryPacketsOwnedBy (testPlayer));
    final int reinforcementCount = playerModel.getArmiesInHand (testPlayer) + 1;

    final PlayerTradeInCardsRequestEvent tradeInResponse = new PlayerTradeInCardsRequestEvent (
            new DefaultCardSetPacket (ImmutableSet. <CardPacket>of ()));
    turnPhaseHandler.verifyPlayerCardTradeIn (tradeInResponse);

    final PlayerReinforceCountryRequestEvent reinforceResponse = new PlayerReinforceCountryRequestEvent (
            randomCountry.getName (), reinforcementCount);
    reinforcementPhase.verifyPlayerReinforceCountry (reinforceResponse);

    assertTrue (eventHandler.wasNeverFired (PlayerReinforceCountrySuccessEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerReinforceCountryDeniedEvent.class));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerTradeInCardsResponseSuccessEvent.class));
    assertTrue (eventHandler.lastEventOfType (PlayerReinforceCountryDeniedEvent.class).getReason ()
            .equals (PlayerReinforceCountryDeniedEvent.Reason.INSUFFICIENT_ARMIES_IN_HAND));
    assertGamePhaseIs (GamePhase.REINFORCEMENT);
  }
}
