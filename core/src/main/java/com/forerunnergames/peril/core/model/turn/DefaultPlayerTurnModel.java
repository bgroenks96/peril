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

package com.forerunnergames.peril.core.model.turn;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

/**
 * Model class for tracking player turn.
 */
public final class DefaultPlayerTurnModel implements PlayerTurnModel
{
  private final GameRules rules;
  private PlayerTurnOrder currentTurn = PlayerTurnOrder.FIRST;
  private PlayerTurnOrder lastTurn;

  public DefaultPlayerTurnModel (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
    resetTurnCount ();
  }

  @Override
  public void advance ()
  {
    currentTurn = currentTurn.is (lastTurn) ? PlayerTurnOrder.FIRST : currentTurn.nextValid ();
  }

  @Override
  public PlayerTurnOrder getCurrentTurn ()
  {
    return currentTurn;
  }

  @Override
  public PlayerTurnOrder getLastTurn ()
  {
    return lastTurn;
  }

  @Override
  public boolean isFirstTurn ()
  {
    return currentTurn.is (PlayerTurnOrder.FIRST);
  }

  @Override
  public boolean isLastTurn ()
  {
    return currentTurn.is (lastTurn);
  }

  @Override
  public void decrementTurnCount ()
  {
    lastTurn = lastTurn.hasPreviousValid () ? lastTurn.previousValid () : PlayerTurnOrder.FIRST;

    // currentTurn can at most ever become 1 position beyond lastTurn due to decrement-only.
    // When this occurs, it means the currentTurn was the old lastTurn, so we want to update
    // currentTurn to the new lastTurn.
    if (isCurrentTurnInvalid ()) fixCurrentTurn ();
  }

  @Override
  public void resetCurrentTurn ()
  {
    currentTurn = PlayerTurnOrder.FIRST;
  }

  @Override
  public void resetTurnCount ()
  {
    lastTurn = PlayerTurnOrder.getNthValidTurnOrder (rules.getTotalPlayerLimit ());

    // currentTurn can never be invalidated here because lastTurn can only ever be decremented from this max value,
    // implying by logical deduction that this method can only ever increase lastTurn's position, or at worst leave it
    // unchanged, therefore currentTurn's position could never become greater than lastTurn's position.
    assert !isCurrentTurnInvalid ();
  }

  private boolean isCurrentTurnInvalid ()
  {
    return currentTurn.getPosition () > lastTurn.getPosition ();
  }

  private void fixCurrentTurn ()
  {
    currentTurn = lastTurn;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: CurrentTurn: [{}] | LastTurn: [{}] | Rules: [{}]", getClass ().getSimpleName (),
                           currentTurn, lastTurn, rules);
  }
}
