/*
 * Copyright © 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.status;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.StringBuilder;

import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.SelectAttackSourceCountryRequestEvent;
import com.forerunnergames.peril.client.events.SelectAttackTargetCountryRequestEvent;
import com.forerunnergames.peril.client.events.SelectFortifySourceCountryRequestEvent;
import com.forerunnergames.peril.client.events.SelectFortifyTargetCountryRequestEvent;
import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.denied.EndPlayerTurnDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerClaimCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerOccupyCountryResponseDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerReinforceCountryDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginAttackWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginFortificationWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerBeginReinforcementWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerClaimCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerDefendCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerFortifyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerIssueAttackOrderWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.wait.PlayerOccupyCountryWaitEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerClaimCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.EndPlayerTurnSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.card.CardPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StatusMessageGenerator
{
  private static final Logger log = LoggerFactory.getLogger (StatusMessageGenerator.class);
  private static final int EVENT_HANDLER_PRIORITY_LAST = -1;
  private final MessageBox <StatusBoxRow> statusBox;
  private final WidgetFactory widgetFactory;
  @Nullable
  private PlayerPacket selfPlayer;

  public StatusMessageGenerator (final MessageBox <StatusBoxRow> statusBox, final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (statusBox, "statusBox");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.statusBox = statusBox;
    this.widgetFactory = widgetFactory;
  }

  @Handler
  void onEvent (final PlayGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    selfPlayer = event.getSelfPlayer ();

    final GameServerConfiguration config = event.getGameServerConfiguration ();
    final int nMorePlayers = config.getPlayerLimit () - event.getPlayerCount ();

    you (event.getSelfPlayer (), "Welcome, {}.", event.getSelfPlayerName ());

    youIf (event.isFirstPlayerInGame (), event.getSelfPlayer (), "It looks like you're the first one here.");

    youIf (nMorePlayers > 0, event.getSelfPlayer (),
           "This is a {} player {} Mode game. You must conquer {}% of the map to achieve victory.",
           config.getPlayerLimit (), Strings.toProperCase (config.getGameMode ().toString ()),
           config.getWinPercentage ());

    youIf (nMorePlayers > 0, event.getSelfPlayer (), "The game will begin when {}.",
           Strings.pluralize (nMorePlayers, "more player joins", "more players join"));
  }

  @Handler
  void onEvent (final BeginGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("The game has started!");
  }

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} joined the game.", event.getPlayerName ());

    everyoneElseIf (!event.gameIsFull (), event.getPlayer (), "The game will begin when {}.", Strings
            .pluralize (event.getPlayersNeededToMakeGameFull (), "more player joins", "more players join"));
  }

  @Handler
  void onEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("The order of turns has been decided.");

    for (final PlayerPacket player : event.getPlayersSortedByTurnOrder ())
    {
      final String turn = Strings.toMixedOrdinal (player.getTurnOrder ());
      everyone ("{} going {}.", nameifyVerbBe (player, LetterCase.PROPER), turn);
    }
  }

  @Handler
  void onEvent (final BeginPlayerCountryAssignmentEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} country assignment phase.", Strings.toProperCase (String.valueOf (event.getAssignmentMode ())));
  }

  @Handler
  void onEvent (final PlayerClaimCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, claim any unowned country.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerClaimCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is claiming a country...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} claimed {}.", nameify (event.getPlayer (), LetterCase.PROPER), event.getCountryName ());
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, something went wrong! We were unable to claim {}.", event.getCountryName ());
  }

  @Handler
  void onEvent (final BeginInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("Initial Reinforcement phase.");
  }

  @Handler
  void onEvent (final PlayerBeginReinforcementEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, choose a country to reinforce.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerBeginReinforcementWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is reinforcing...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerReinforceCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} reinforced {} with {}.", nameify (event.getPlayer (), LetterCase.PROPER), event.getCountryName (),
              Strings.pluralize (Math.abs (event.getPlayerDeltaArmyCount ()), "army", "armies"));
  }

  @Handler
  void onEvent (final PlayerReinforceCountryDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, something went wrong! We were unable to reinforce {}.",
         event.getOriginalRequest ().getCountryName ());

    you (event.getPlayer (), "General, choose a country to reinforce.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final EndInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("All initial reinforcements have been placed.");
  }

  @Handler
  void onEvent (final BeginRoundEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("Round {}.", event.getRound ());
  }

  @Handler
  void onEvent (final BeginPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("It is {} turn.", nameifyPossessiveYour (event.getPlayer (), LetterCase.LOWER));
  }

  @Handler
  void onEvent (final BeginReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "Reinforcement phase.");
    everyone ("{} {}.", nameifyVerbHave (event.getPlayer (), LetterCase.PROPER),
              Strings.pluralizeSZeroIsNo (event.getPlayerCardsInHand (), "card"));
  }

  @Handler
  void onEvent (final PlayerCardTradeInAvailableEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String reinforcementsPhrase = Strings.pluralizeS (event.getNextTradeInBonus (), "additional reinforcement");

    youIf (!event.isTradeInRequired () && event.getPlayerCardsInHand () == 3, event.getPlayer (),
           "Congratulations, General, you have just enough matching cards to purchase {}! If you so desire, that is, "
                   + "sir. Good things come to those who wait, General.",
           reinforcementsPhrase);

    youIf (!event.isTradeInRequired () && event.getPlayerCardsInHand () > 3, event.getPlayer (),
           "General, you may now use 3 of your {} matching cards to purchase {}! If you so desire, that is, sir. "
                   + "Fortune rewards the patient, General.",
           event.getPlayerCardsInHand (), reinforcementsPhrase);

    youIf (event.isTradeInRequired (), event.getPlayer (),
           "General, you now have so many matching cards that you must now use some of them to purchase {}!",
           reinforcementsPhrase);
  }

  @Handler
  void onEvent (final PlayerTradeInCardsResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} used 3 cards ({}) to purchase {}!", nameify (event.getPlayer (), LetterCase.PROPER),
              toString (event.getTradeInCards ()),
              Strings.pluralizeS (event.getTradeInBonus (), "additional reinforcement"));

    everyone ("{} now {} {} remaining.", nameify (event.getPlayer (), LetterCase.PROPER),
              verbifyHave (event.getPlayer ()), Strings.pluralizeSZeroIsNo (event.getPlayerCardsInHand (), "card"));

    everyone ("The next purchase will yield {}.", Strings.pluralizeS (event.getNextTradeInBonus (), "reinforcement"));
  }

  @Handler
  void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} has finished reinforcing.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BeginAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "Attack phase.");
  }

  @Handler
  void onEvent (final PlayerBeginAttackWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is deciding where to attack...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final SelectAttackSourceCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("General, choose a country from which to attack.");
  }

  @Handler
  void onEvent (final SelectAttackTargetCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("General, choose a country to attack from {}.", event.getSourceCountryName ());
  }

  @Handler
  void onEvent (final PlayerIssueAttackOrderWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("Waiting for {} to roll attacker dice...", nameify (event.getPlayer (), LetterCase.LOWER));
  }

  @Handler
  void onEvent (final PlayerDefendCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("Waiting for {} to roll defender dice...", nameify (event.getPlayer (), LetterCase.LOWER));
  }

  @Handler
  void onEvent (final PlayerOrderAttackSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final int defenderLoss = Math.abs (event.getDefendingCountryArmyDelta ());
    final int attackerLoss = Math.abs (event.getAttackingCountryArmyDelta ());
    final boolean defenderLostArmies = defenderLoss > 0;
    final boolean attackerLostArmies = attackerLoss > 0;
    final boolean defenderOnlyLostArmies = defenderLostArmies && !attackerLostArmies;
    final boolean attackerOnlyLostArmies = !defenderLostArmies && attackerLostArmies;
    final boolean bothLostArmies = attackerLostArmies && defenderLostArmies;
    final String attacker = nameify (event.getAttackingPlayer (), LetterCase.PROPER);
    final String defender = nameify (event.getDefendingPlayer (), LetterCase.LOWER);
    final String attackerCountry = event.getAttackingCountryName ();
    final String defenderCountry = event.getDefendingCountryName ();
    final String defenderLossInWords = Strings.pluralizeWord (defenderLoss, "no armies", "an army",
                                                              defenderLoss + " armies");
    final String attackerLossInWords = Strings.pluralizeWord (attackerLoss, "no armies", "an army",
                                                              defenderLoss + " armies");

    everyoneIf (bothLostArmies, "{} attacked {} in {} from {}, destroying {} & losing {}!", attacker, defender,
                defenderCountry, attackerCountry, defenderLossInWords, attackerLossInWords);

    everyoneIf (attackerOnlyLostArmies, "{} attacked {} in {} from {} & lost {}!", attacker, defender, defenderCountry,
                attackerCountry, attackerLossInWords);

    everyoneIf (defenderOnlyLostArmies, "{} attacked {} in {} from {} & destroyed {}!", attacker, defender,
                defenderCountry, attackerCountry, defenderLossInWords);

    everyoneIf (event.battleOutcomeIs (BattleOutcome.ATTACKER_VICTORIOUS), "{} conquered {}, defeating {} in battle.",
                attacker, defenderCountry, defender);

    everyoneIf (event.battleOutcomeIs (BattleOutcome.ATTACKER_DEFEATED),
                "{} failed to conquer {}, defeated by {} in battle.", attacker, defenderCountry, defender);
  }

  @Handler
  void onEvent (final PlayerOrderRetreatSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} retreated from attacking {} in {} from {}.", nameify (event.getAttackingPlayer (), LetterCase.PROPER),
              nameify (event.getDefendingPlayer (), LetterCase.LOWER), event.getDefendingCountryName (),
              event.getAttackingCountryName ());
  }

  @Handler
  void onEvent (final PlayerLoseGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, we have lost the war.");
    everyoneElse (event.getPlayer (), "{} was annihilated.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerWinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, you have won the war!");
    everyoneElse (event.getPlayer (), "{} won the war.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerOccupyCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is occupying {} with troops from {}...", event.getPlayerName (),
                  event.getTargetCountryName (), event.getSourceCountryName ());
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String country = event.getTargetCountryName ();
    final int armies = Math.abs (event.getDeltaArmyCount ());

    everyone ("{} occupied {} with {}.", nameify (event.getPlayer (), LetterCase.PROPER), country,
              Strings.pluralize (armies, "army", "armies"));

    everyoneIf (armies == 1, "Looks like an easy target...");
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    youIf (event.hasOriginalRequest (), event.getPlayer (),
           "General, something went wrong! We were unable to occupy {} from {}.", event.getTargetCountryName (),
           event.getSourceCountryName ());

    youIf (!event.hasOriginalRequest (), event.getPlayer (),
           "General, something went wrong! We were unable to occupy.");
  }

  @Handler
  void onEvent (final EndAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is finished attacking.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BeginFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "Post-Combat Maneuver phase.");
  }

  @Handler
  void onEvent (final PlayerBeginFortificationWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is considering a post-combat maneuver...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final SelectFortifySourceCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("General, choose a country from which to maneuver armies.");
  }

  @Handler
  void onEvent (final SelectFortifyTargetCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    status ("General, choose a country to maneuver armies into from {}.", event.getSourceCountryName ());
  }

  @Handler
  void onEvent (final PlayerFortifyCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is maneuvering armies into {} from {}...", event.getPlayerName (),
                  event.getTargetCountryName (), event.getSourceCountryName ());
  }

  @Handler
  void onEvent (final PlayerFortifyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} maneuvered {} into {} from {}.", nameify (event.getPlayer (), LetterCase.PROPER),
              Strings.pluralize (event.getDeltaArmyCount (), "army", "armies"), event.getTargetCountryName (),
              event.getSourceCountryName ());
  }

  @Handler
  void onEvent (final EndFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is finished maneuvering.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final EndPlayerTurnSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "You ended you turn.");
  }

  @Handler
  void onEvent (final EndPlayerTurnDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "General, something went wrong! You cannot end your turn at this time.");
  }

  @Handler
  void onEvent (final EndPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    youIf (event.wasCardReceived (), event.getPlayer (),
           "General, you earned a card ({}) as a reward for your military genius!", toString (event.getCard ()));

    youIf (!event.wasCardReceived (), event.getPlayer (),
           "General, we failed to earn you a card, sir. We will try harder next time, sir...");

    everyone ("{} turn is over.", nameifyPossessiveYour (event.getPlayer (), LetterCase.PROPER));
    everyoneElseIf (event.wasCardReceived (), event.getPlayer (), "{} earned a card.", event.getPlayerName ());
    everyoneElseIf (!event.wasCardReceived (), event.getPlayer (), "{} did not earn a card.", event.getPlayerName ());
    everyoneIf (event.wasCardReceived (), "{} now {} {}.", nameify (event.getPlayer (), LetterCase.PROPER),
                verbifyHave (event.getPlayer ()), Strings.pluralizeSZeroIsNo (event.getPlayerCardsInHand (), "card"));
  }

  @Handler
  void onEvent (final EndRoundEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("End of round {}.", event.getRound ());
  }

  @Handler
  void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} left the game.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("The game has ended.");
  }

  @Handler (priority = EVENT_HANDLER_PRIORITY_LAST)
  void onEvent (final CountryOwnerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    everyone ("{} now {} {}.", nameify (event.getNewOwner (), LetterCase.PROPER),
              verbifyS (event.getNewOwner (), "own"), event.getCountryName ());
  }

  @Handler (priority = EVENT_HANDLER_PRIORITY_LAST)
  void onEvent (final PlayerArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    if (event.getPlayerDeltaArmyCount () <= 0) return;

    everyone ("{} received {}.", nameify (event.getPlayer (), LetterCase.PROPER),
              Strings.pluralize (event.getPlayerDeltaArmyCount (), "army", "armies"));
  }

  private void status (final String statusMessageText)
  {
    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        statusBox.addRow (widgetFactory.createStatusMessageBoxRow (new DefaultStatusMessage (statusMessageText)));
        statusBox.showLastRow ();
      }
    });
  }

  private void status (final String statusMessageText, final Object... args)
  {
    status (Strings.format (statusMessageText, args));
  }

  private void statusIf (final boolean condition, final String statusMessage)
  {
    if (!condition) return;
    status (statusMessage);
  }

  private void statusIf (final boolean condition, final String statusMessage, final Object... args)
  {
    if (!condition) return;
    status (statusMessage, args);
  }

  private void everyone (final String statusMessageText)
  {
    status (statusMessageText);
  }

  private void everyone (final String statusMessageText, final Object... args)
  {
    status (statusMessageText, args);
  }

  private void everyoneIf (final boolean condition, final String statusMessage)
  {
    statusIf (condition, statusMessage);
  }

  private void everyoneIf (final boolean condition, final String statusMessage, final Object... args)
  {
    statusIf (condition, statusMessage, args);
  }

  private void everyoneElse (final PlayerPacket player, final String statusMessage, final Object... args)
  {
    statusIf (!isSelf (player), statusMessage, args);
  }

  private void everyoneElseIf (final boolean condition,
                               final PlayerPacket player,
                               final String statusMessage,
                               final Object... args)
  {
    statusIf (condition && !isSelf (player), statusMessage, args);
  }

  private void you (final PlayerPacket player, final String statusMessage, final Object... args)
  {
    statusIf (isSelf (player), statusMessage, args);
  }

  private void youIf (final boolean condition,
                      final PlayerPacket player,
                      final String statusMessage,
                      final Object... args)
  {
    statusIf (condition && isSelf (player), statusMessage, args);
  }

  private boolean isSelf (final PlayerPacket player)
  {
    return selfPlayer != null && player.is (selfPlayer);
  }

  private String nameify (final PlayerPacket player, final LetterCase letterCase)
  {
    checkLetterCase (letterCase);

    return isSelf (player) ? Strings.toCase ("you", letterCase) : player.getName ();
  }

  private String nameifyVerbBe (final PlayerPacket player, final LetterCase letterCase)
  {
    checkLetterCase (letterCase);

    return isSelf (player) ? Strings.toCase ("you", letterCase) + " are" : player.getName () + " is";
  }

  private String nameifyVerbHave (final PlayerPacket player, final LetterCase letterCase)
  {
    checkLetterCase (letterCase);

    return isSelf (player) ? Strings.toCase ("you", letterCase) + " have" : player.getName () + " has";
  }

  private String nameifyVerb (final PlayerPacket player,
                              final String secondPersonVerb,
                              final String thirdPersonVerb,
                              final LetterCase letterCase)
  {
    checkLetterCase (letterCase);

    return isSelf (player) ? Strings.toCase ("you", letterCase) + " " + secondPersonVerb
            : player.getName () + " " + thirdPersonVerb;
  }

  private String nameifyPossessiveYour (final PlayerPacket player, final LetterCase letterCase)
  {
    checkLetterCase (letterCase);

    return isSelf (player) ? Strings.toCase ("your", letterCase) : player.getName () + "'s";
  }

  private String verbify (final PlayerPacket player, final String secondPersonVerb, final String thirdPersonVerb)
  {
    return isSelf (player) ? secondPersonVerb : thirdPersonVerb;
  }

  private String verbifyHave (final PlayerPacket player)
  {
    return isSelf (player) ? "have" : "has";
  }

  private String verbifyS (final PlayerPacket player, final String baseVerb)
  {
    return isSelf (player) ? baseVerb : baseVerb + "s";
  }

  private String possessify (final PlayerPacket player, final LetterCase letterCase)
  {
    return Strings.toCase (isSelf (player) ? "your" : "their", letterCase);
  }

  private String toString (@Nullable final CardPacket card)
  {
    return card != null ? toString (ImmutableSet.of (card)) : "";
  }

  private String toString (final Iterable <CardPacket> cards)
  {
    final StringBuilder cardsDescription = new StringBuilder ();

    for (final CardPacket card : cards)
    {
      cardsDescription.append (card.getName ()).append (" (MSV: ").append (card.getType ()).append (")").append (", ");
    }

    cardsDescription.replace (cardsDescription.lastIndexOf (", "), cardsDescription.length, "");

    return cardsDescription.toString ();
  }

  private void checkLetterCase (final LetterCase letterCase)
  {
    if (letterCase == null || letterCase == LetterCase.NONE)
    {
      throw new IllegalStateException (
              Strings.format ("Invalid {}: [{}]", LetterCase.class.getSimpleName (), letterCase));
    }
  }
}
