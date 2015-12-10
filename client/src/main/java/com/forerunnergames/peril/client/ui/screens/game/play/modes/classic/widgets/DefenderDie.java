package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;

abstract class DefenderDie extends AbstractDie
{
  private final GameRules rules;

  protected DefenderDie (final int index, final ImageButton button, final GameRules rules)
  {
    super (index, GameSettings.DEFAULT_DIE_FACE_VALUE, button);

    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  protected DieOutcome determineOutcome (final DieFaceValue thisFaceValue, final DieFaceValue thatFaceValue)
  {
    Arguments.checkIsNotNull (thisFaceValue, "thisFaceValue");
    Arguments.checkIsNotNull (thatFaceValue, "thatFaceValue");

    return rules.determineDefenderOutcome (thisFaceValue, thatFaceValue);
  }
}
