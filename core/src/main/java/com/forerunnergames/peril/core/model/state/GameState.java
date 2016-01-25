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

package com.forerunnergames.peril.core.model.state;

// TODO This is a work in progress and should be modified as needed.
// @formatter:off
public enum GameState
{
  WAITING_FOR_PLAYERS_TO_JOIN_GAME,
  DETERMINE_PLAYER_TURN_ORDER,
  DISTRIBUTE_INITIAL_ARMIES,
  PLAYER_INIT_PHASE,
  // manual country assignment
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY,
  END_PLAYER_INIT_PHASE,
  ADD_INITIAL_ARMIES_TO_OWN_COUNTRY,
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY_TO_PLACE_INITIAL_ARMIES,

  // Begin looping states
  BEGIN_ROUND,
  BEGIN_REINFORCEMENT_PHASE,
  BEGIN_TRADE_IN,
  CHOOSE_CARDS_TO_TRADE_IN,
  CANCEL_TRADE_IN,
  TRADE_IN_CARDS,
  RECEIVE_BONUS_ARMIES_ON_OWN_TRADE_IN_COUNTRIES,
  END_TRADE_IN,
  RECEIVE_ARMIES_IN_HAND,
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY_TO_REINFORCE,
  ADD_ARMIES_TO_OWN_COUNTRY,
  END_REINFORCEMENT_PHASE,

  BEGIN_ATTACK_PHASE,
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY_TO_ATTACK_FROM,
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY_TO_ATTACK_TO,
  CHOOSE_NUMBER_OF_ATTACKING_DICE_TO_ROLL,
  CHOOSE_NUMBER_OF_DEFENDING_DICE_TO_ROLL,
  WAITING_FOR_PLAYER_TO_ROLL_ATTACKING_DICE,
  ROLL_ATTACKING_DICE,
  WAITING_FOR_PLAYER_TO_ROLL_DEFENDING_DICE,
  ROLL_DEFENDING_DICE,
  REMOVE_ARMIES_FROM_ATTACKING_COUNTRY,
  REMOVE_ARMIES_FROM_DEFENDING_COUNTRY,
  CANCEL_ATTACK,
  CONQUER_DEFENDING_COUNTRY,
  PLAYER_DEFEATED,
  RECEIVE_CARDS_OF_DEFEATED_PLAYER,
  WAITING_FOR_PLAYER_TO_MOVE_ARMIES_INTO_CONQUERED_COUNTRY,
  MOVE_ARMIES_INTO_CONQUERED_COUNTRY,
  END_ATTACK_PHASE,

  BEGIN_FORTIFICATION_PHASE,
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY_TO_FORTIFY_FROM,
  WAITING_FOR_PLAYER_TO_CHOOSE_COUNTRY_TO_FORTIFY_TO,
  WAITING_FOR_PLAYER_TO_CHOOSE_NUMBER_OF_ARMIES_TO_MOVE,
  MOVE_ARMIES_INTO_FORTIFIED_COUNTRY,
  CANCEL_FORTIFICATION,
  END_FORTIFICATION_PHASE,

  RECEIVE_CARD,
  END_ROUND,
  // End looping states

  FORFEIT_GAME,
  SAVE_GAME,
  SAVE_AND_QUIT_GAME,
  QUIT_WITHOUT_SAVING_GAME,
  GAME_OVER
}
