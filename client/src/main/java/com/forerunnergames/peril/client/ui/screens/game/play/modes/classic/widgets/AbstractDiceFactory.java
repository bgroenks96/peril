package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

abstract class AbstractDiceFactory implements DiceFactory
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
      dieBuilder.add (new AttackerDie (dieIndex,
              createAttackerDieImageButton (Die.DEFAULT_STATE, GameSettings.DEFAULT_DIE_FACE_VALUE,
                                            Die.DEFAULT_OUTCOME),
              rules)
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
      dieBuilder.add (new DefenderDie (dieIndex,
              createDefenderDieImageButton (Die.DEFAULT_STATE, GameSettings.DEFAULT_DIE_FACE_VALUE,
                                            Die.DEFAULT_OUTCOME),
              rules)
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
    return "die-" + dieTypeStyleNameSection + "-" + (state == DieState.ENABLED
            ? faceValue.lowerCaseName () + "-outcome-" + outcome.lowerCaseName () : state.lowerCaseName ());
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

    return widgetFactory.createImageButtonStyle (createDieStyleName ("attack", state, faceValue, outcome));
  }

  private ImageButton.ImageButtonStyle createDefenderDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return widgetFactory.createImageButtonStyle (createDieStyleName ("defend", state, faceValue, outcome));
  }
}
