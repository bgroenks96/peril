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

import com.forerunnergames.peril.client.events.DefaultStatusMessageEvent;
import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.StatusMessageEvent;
import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
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
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StatusMessageEventGenerator
{
  private static final Logger log = LoggerFactory.getLogger (StatusMessageEventGenerator.class);
  private final MBassador <Event> eventBus;
  @Nullable
  private PlayerPacket selfPlayer;

  public StatusMessageEventGenerator (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
  }

  public static StatusMessageEvent create (final String messageText)
  {
    Arguments.checkIsNotNull (messageText, "messageText");

    return new DefaultStatusMessageEvent (new DefaultStatusMessage (messageText));
  }

  public static StatusMessageEvent create (final String messageText, final Object... args)
  {
    Arguments.checkIsNotNull (args, "args");

    return create (Strings.format (messageText, args));
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

    youIf (nMorePlayers > 0, event.getSelfPlayer (), "The game will begin when {}.",
           Strings.pluralize (nMorePlayers, "more player joins", "more players join"));

    youIf (nMorePlayers > 0, event.getSelfPlayer (),
           "This is a {} player {} Mode game. You must conquer {}% of the map to achieve victory.",
           config.getPlayerLimit (), Strings.toProperCase (config.getGameMode ().toString ()),
           config.getWinPercentage ());
  }

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} joined the game.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final CountryOwnerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    everyone ("{} now {} {}.", nameify (event.getNewOwner (), LetterCase.PROPER),
              verbifyS (event.getNewOwner (), "own"), event.getCountryName ());
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
      everyone ("{} going {}.", nameifyVerb (player, LetterCase.PROPER), turn);
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
  void onEvent (final PlayerClaimCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is claiming a country...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BeginInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "Initial Reinforcement phase.");
  }

  @Handler
  void onEvent (final PlayerBeginReinforcementWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is placing reinforcements...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerReinforceCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} placed {} on {}.", nameify (event.getPlayer (), LetterCase.PROPER),
              Strings.pluralize (Math.abs (event.getPlayerDeltaArmyCount ()), "army", "armies"),
              event.getCountryName ());
  }

  @Handler
  void onEvent (final BeginPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("It is {} turn.", nameifyPossessive (event.getPlayer (), LetterCase.LOWER));
  }

  @Handler
  void onEvent (final BeginReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    you (event.getPlayer (), "Reinforcement phase.");
  }

  @Handler
  void onEvent (final PlayerCardTradeInAvailableEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    // TODO Implement trade-in status messages.
  }

  @Handler
  void onEvent (final PlayerTradeInCardsResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyone ("{} purchased {} reinforcements!", nameify (event.getPlayer (), LetterCase.PROPER),
              event.getTradeInBonus ());
  }

  @Handler
  void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is finished reinforcing.", event.getPlayerName ());
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
    final String defenderLossInWords = Strings.pluralize (defenderLoss, "no armies", "an army",
                                                          defenderLoss + " armies");
    final String attackerLossInWords = Strings.pluralize (attackerLoss, "no armies", "an army",
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

    everyone ("{} stopped attacking {} in {} from {}.", nameify (event.getAttackingPlayer (), LetterCase.PROPER),
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

    you (event.getPlayer (), "General, we won the war!");
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
  void onEvent (final PlayerFortifyCountryWaitEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is maneuvering troops into {} from {}...", event.getPlayerName (),
                  event.getTargetCountryName (), event.getSourceCountryName ());
  }

  @Handler
  void onEvent (final PlayerFortifyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String sourceCountry = event.getSourceCountryName ();
    final String targetCountry = event.getTargetCountryName ();
    final String armies = Strings.pluralize (event.getDeltaArmyCount (), "army", "armies");

    everyone ("{} maneuvered {} into {} from {}.", nameify (event.getPlayer (), LetterCase.PROPER), armies,
              event.getTargetCountryName (), sourceCountry);
  }

  @Handler
  void onEvent (final EndFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} is finished maneuvering.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final EndPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    youIf (event.wasCardReceived (), event.getPlayer (),
           "General, you earned a card as a reward for your military genius!\n\nCard: [ Country: {}, MSV: {} ]\n",
           event.getCardName (), event.getCardType ());

    youIf (!event.wasCardReceived (), event.getPlayer (),
           "General, we failed to earn you a card, sir. We will try harder next time, sir.");

    everyone ("{} turn is over.", nameifyPossessive (event.getPlayer (), LetterCase.PROPER));
    everyoneElseIf (event.wasCardReceived (), event.getPlayer (), "{} earned a card.", event.getPlayerName ());
    everyoneElseIf (!event.wasCardReceived (), event.getPlayer (), "{} did not earn a card.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    everyoneElse (event.getPlayer (), "{} left the game.", event.getPlayerName ());
  }

  private void status (final String statusMessageText)
  {
    eventBus.publish (new DefaultStatusMessageEvent (new DefaultStatusMessage (statusMessageText)));
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
    if (letterCase == LetterCase.NONE)
    {
      throw new IllegalStateException (
              Strings.format ("Invalid {}: [{}]", LetterCase.class.getSimpleName (), LetterCase.NONE));
    }

    return isSelf (player) ? Strings.toCase ("you", letterCase) : player.getName ();
  }

  private String nameifyVerb (final PlayerPacket player, final LetterCase letterCase)
  {
    if (letterCase == LetterCase.NONE)
    {
      throw new IllegalStateException (
              Strings.format ("Invalid {}: [{}]", LetterCase.class.getSimpleName (), LetterCase.NONE));
    }

    return isSelf (player) ? Strings.toCase ("you", letterCase) + " are" : player.getName () + " is";
  }

  private String nameifyPossessive (final PlayerPacket player, final LetterCase letterCase)
  {
    if (letterCase == LetterCase.NONE)
    {
      throw new IllegalStateException (
              Strings.format ("Invalid {}: [{}]", LetterCase.class.getSimpleName (), LetterCase.NONE));
    }

    return isSelf (player) ? Strings.toCase ("your", letterCase) : player.getName () + "'s";
  }

  private String verbifyS (final PlayerPacket player, final String baseVerb)
  {
    return isSelf (player) ? baseVerb : baseVerb + "s";
  }
}
