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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dice;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractDiceFactory implements DiceFactory
{
  private final WidgetFactory widgetFactory;

  protected AbstractDiceFactory (final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;
  }

  @Override
  public final Dice createAttackerDice (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    final int absoluteMinDieCount = rules.getMinTotalAttackerDieCount ();
    final int absoluteMaxDieCount = rules.getMaxTotalAttackerDieCount ();
    final ImmutableSet.Builder <Die> dieBuilder = ImmutableSet.builder ();

    for (int dieIndex = 0; dieIndex < absoluteMaxDieCount; ++dieIndex)
    {
      dieBuilder.add (new AttackerDie (dieIndex, createAttackerDieImageButton (Die.DEFAULT_STATE,
                                                                               GameSettings.DEFAULT_DIE_FACE_VALUE,
                                                                               Die.DEFAULT_OUTCOME), rules)
      {
        @Override
        protected ImageButton.ImageButtonStyle createDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
        {
          return createAttackerDieImageButtonStyle (state, faceValue, outcome);
        }
      });
    }

    final Dice dice = new AttackerDice (dieBuilder.build (), absoluteMinDieCount, absoluteMaxDieCount);
    dice.setTouchable (areAttackerDiceTouchable ());

    return dice;
  }

  @Override
  public final Dice createDefenderDice (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    final int absoluteMinDieCount = rules.getMinTotalDefenderDieCount ();
    final int absoluteMaxDieCount = rules.getMaxTotalDefenderDieCount ();
    final ImmutableSet.Builder <Die> dieBuilder = ImmutableSet.builder ();

    for (int dieIndex = 0; dieIndex < absoluteMaxDieCount; ++dieIndex)
    {
      dieBuilder.add (new DefenderDie (dieIndex, createDefenderDieImageButton (Die.DEFAULT_STATE,
                                                                               GameSettings.DEFAULT_DIE_FACE_VALUE,
                                                                               Die.DEFAULT_OUTCOME), rules)
      {
        @Override
        protected ImageButton.ImageButtonStyle createDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
        {
          return createDefenderDieImageButtonStyle (state, faceValue, outcome);
        }
      });
    }

    final Dice dice = new DefenderDice (dieBuilder.build (), absoluteMinDieCount, absoluteMaxDieCount);
    dice.setTouchable (areDefenderDiceTouchable ());

    return dice;
  }

  protected abstract boolean areAttackerDiceTouchable ();

  protected abstract boolean areDefenderDiceTouchable ();

  private static String createDieStyleName (final String dieTypeStyleNameSection,
                                            final DieState state,
                                            final DieFaceValue faceValue,
                                            final DieOutcome outcome)
  {
    // @formatter:off
    return StyleSettings.BATTLE_DIALOG_DIE_STYLE_PREFIX + dieTypeStyleNameSection + (state == DieState.ENABLED ?
            faceValue.lowerCaseName () + StyleSettings.BATTLE_DIALOG_DIE_OUTCOME_STYLE_SEGMENT + outcome.lowerCaseName () :
            state.lowerCaseName ());
    // @formatter:on
  }

  private ImageButton createAttackerDieImageButton (final DieState state,
                                                    final DieFaceValue faceValue,
                                                    final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return widgetFactory.createImageButton (createAttackerDieImageButtonStyle (state, faceValue, outcome));
  }

  private ImageButton createDefenderDieImageButton (final DieState state,
                                                    final DieFaceValue faceValue,
                                                    final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return widgetFactory.createImageButton (createDefenderDieImageButtonStyle (state, faceValue, outcome));
  }

  private ImageButton.ImageButtonStyle createAttackerDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return widgetFactory
            .createImageButtonStyle (createDieStyleName (StyleSettings.BATTLE_DIALOG_DIE_ATTACK_STYLE_SEGMENT, state,
                                                         faceValue, outcome));
  }

  private ImageButton.ImageButtonStyle createDefenderDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return widgetFactory
            .createImageButtonStyle (createDieStyleName (StyleSettings.BATTLE_DIALOG_DIE_DEFEND_STYLE_SEGMENT, state,
                                                         faceValue, outcome));
  }
}
