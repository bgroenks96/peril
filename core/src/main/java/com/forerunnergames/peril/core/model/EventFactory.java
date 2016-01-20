package com.forerunnergames.peril.core.model;

import com.forerunnergames.peril.common.game.TurnPhase;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceCountriesRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerReinforceInitialCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.tools.common.id.Id;

public interface EventFactory
{
  PlayerReinforceInitialCountryRequestEvent createInitialReinforcementRequestFor (final Id playerId);

  PlayerReinforceCountriesRequestEvent createReinforcementRequestFor (final Id playerId);

  PlayerTradeInCardsRequestEvent createTradeInCardsRequestFor (final Id playerId, final TurnPhase turnPhase);
}
