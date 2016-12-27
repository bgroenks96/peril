package com.forerunnergames.peril.core.model.game.phase.turn;

import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.core.model.game.phase.GamePhaseHandler;

public interface ReinforcementPhaseHandler extends GamePhaseHandler
{
  void waitForPlayerToPlaceReinforcements ();

  boolean verifyPlayerReinforceCountry (PlayerReinforceCountryRequestEvent event);

  void endReinforcementPhase ();
}
