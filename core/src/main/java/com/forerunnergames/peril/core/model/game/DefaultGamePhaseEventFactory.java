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

package com.forerunnergames.peril.core.model.game;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.packets.card.CardSetPacket;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardPackets;
import com.forerunnergames.peril.core.model.card.CardSet;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableSet;

public final class DefaultGamePhaseEventFactory implements GamePhaseEventFactory
{
  private final PlayerModel playerModel;
  private final CardModel cardModel;
  private final GameRules rules;

  public DefaultGamePhaseEventFactory (final PlayerModel playerModel,
                                       final PlayMapModel playMapModel,
                                       final CardModel cardModel,
                                       final GameRules rules)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (cardModel, "cardModel");
    Arguments.checkIsNotNull (rules, "rules");

    this.playerModel = playerModel;
    this.cardModel = cardModel;
    this.rules = rules;
  }

  @Override
  public PlayerCardTradeInAvailableEvent createCardTradeInEventFor (final Id playerId,
                                                                    final ImmutableSet <CardSet.Match> matches,
                                                                    final TurnPhase turnPhase)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (matches, "matches");
    Arguments.checkHasNoNullElements (matches, "matches");
    Arguments.checkIsNotNull (turnPhase, "turnPhase");

    final int cardCount = cardModel.countCardsInHand (playerId);
    final ImmutableSet <CardSetPacket> matchPackets = CardPackets.fromCardMatchSet (matches);

    return new PlayerCardTradeInAvailableEvent (playerModel.playerPacketWith (playerId),
            cardModel.getNextTradeInBonus (), matchPackets,
            cardCount >= rules.getMinCardsInHandToRequireTradeIn (turnPhase));
  }
}
