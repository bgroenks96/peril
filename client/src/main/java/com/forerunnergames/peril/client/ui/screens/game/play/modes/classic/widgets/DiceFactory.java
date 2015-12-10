package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.forerunnergames.peril.common.game.rules.GameRules;

public interface DiceFactory
{
  Dice createAttackerDice (final GameRules rules);

  Dice createDefenderDice (final GameRules rules);
}
