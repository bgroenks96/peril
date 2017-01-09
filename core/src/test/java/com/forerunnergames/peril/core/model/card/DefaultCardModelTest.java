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

package com.forerunnergames.peril.core.model.card;

import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public class DefaultCardModelTest extends CardModelTest
{
  @Override
  protected CardModel createCardModel (final GameRules rules,
                                       final PlayerModel playerModel,
                                       final ImmutableSet <Card> cards)
  {
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkHasNoNullElements (cards, "cards");

    return new DefaultCardModel (rules, new DefaultPlayerCardHandler (playerModel, rules),
            new DefaultCardDealer (cards));
  }

  @Override
  protected CardModel createCardModel (final GameRules rules,
                                       final PlayerModel playerModel,
                                       final CardDealer dealer,
                                       final ImmutableSet <Card> cards)
  {
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (dealer, "dealer");
    Arguments.checkIsNotNull (cards, "cards");
    Arguments.checkHasNoNullElements (cards, "cards");

    return new DefaultCardModel (rules, new DefaultPlayerCardHandler (playerModel, rules), dealer);
  }

  @Override
  protected CardDealer createCardDealer (final ImmutableSet <Card> cardDeck)
  {
    return new DefaultCardDealer (cardDeck);
  }
}
