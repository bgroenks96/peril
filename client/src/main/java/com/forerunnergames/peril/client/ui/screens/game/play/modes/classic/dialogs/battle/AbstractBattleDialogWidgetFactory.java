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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.music.NullMusic;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.CountryArmyTextEffects.HorizontalMoveDirection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.AttackerDice;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.AttackerDie;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DefenderDice;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DefenderDie;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.Dice;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DiceArrow;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DiceArrows;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.Die;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DieState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.CountryArmyText;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.DefaultCountryArmyText;
import com.forerunnergames.peril.client.ui.sound.NullSound;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public abstract class AbstractBattleDialogWidgetFactory extends AbstractWidgetFactory implements
        BattleDialogWidgetFactory
{
  private static final String BATTLING_ARROW_LABEL_TEXT = "ATTACKING";
  private static final String COUNTRY_ARMY_TEXT_EFFECTS_FONT_NAME = "dejaVu-22";
  private static final String DICE_ARROW_OUTCOME_TEXTURE_REGION_NAME_PREFIX = "dice-arrow-outcome-attacker-";
  private final DieRange absoluteAttackerDieRange;
  private final DieRange absoluteDefenderDieRange;

  protected AbstractBattleDialogWidgetFactory (final AssetManager assetManager,
                                               final DieRange absoluteAttackerDieRange,
                                               final DieRange absoluteDefenderDieRange)
  {
    super (assetManager);

    Arguments.checkIsNotNull (absoluteAttackerDieRange, "absoluteAttackerDieRange");
    Arguments.checkIsNotNull (absoluteDefenderDieRange, "absoluteDefenderDieRange");

    this.absoluteAttackerDieRange = absoluteAttackerDieRange;
    this.absoluteDefenderDieRange = absoluteDefenderDieRange;
  }

  @Override
  public Label createBattleDialogPlayerNameLabel ()
  {
    return createLabel ("", Align.center, createBattleDialogPlayerNameLabelStyle ());
  }

  @Override
  public Label.LabelStyle createBattleDialogPlayerNameLabelStyle ()
  {
    return createLabelStyle (StyleSettings.BATTLE_DIALOG_PLAYER_NAME_LABEL_STYLE);
  }

  @Override
  public Label createBattleDialogCountryNameLabel ()
  {
    return createLabel ("", Align.center, createBattleDialogCountryNameLabelStyle ());
  }

  @Override
  public Label.LabelStyle createBattleDialogCountryNameLabelStyle ()
  {
    return createLabelStyle (StyleSettings.BATTLE_DIALOG_COUNTRY_NAME_LABEL_STYLE);
  }

  @Override
  public Label createBattleDialogBattlingArrowLabel ()
  {
    return createLabel (BATTLING_ARROW_LABEL_TEXT, Align.left, createBattleDialogBattlingArrowLabelStyle ());
  }

  @Override
  public Label.LabelStyle createBattleDialogBattlingArrowLabelStyle ()
  {
    return createLabelStyle (StyleSettings.BATTLE_DIALOG_BATTLING_ARROW_LABEL_STYLE);
  }

  @Override
  public CountryArmyText createCountryArmyText ()
  {
    return new DefaultCountryArmyText (createCountryArmyTextFont ());
  }

  @Override
  public BitmapFont createCountryArmyTextFont ()
  {
    return new BitmapFont ();
  }

  @Override
  public Dice createAttackerDice ()
  {
    final ImmutableSet.Builder <Die> dieBuilder = ImmutableSet.builder ();

    for (int dieIndex = 0; dieIndex < absoluteAttackerDieRange.max (); ++dieIndex)
    {
      dieBuilder.add (new AttackerDie (dieIndex, createAttackerDieImageButton (Die.DEFAULT_STATE,
                                                                               GameSettings.DEFAULT_DIE_FACE_VALUE,
                                                                               Die.DEFAULT_OUTCOME))
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

    final Dice dice = new AttackerDice (dieBuilder.build (), absoluteAttackerDieRange);
    dice.setTouchable (areAttackerDiceTouchable ());

    return dice;
  }

  @Override
  public Dice createDefenderDice ()
  {
    final ImmutableSet.Builder <Die> dieBuilder = ImmutableSet.builder ();

    for (int dieIndex = 0; dieIndex < absoluteDefenderDieRange.max (); ++dieIndex)
    {
      dieBuilder.add (new DefenderDie (dieIndex, createDefenderDieImageButton (Die.DEFAULT_STATE,
                                                                               GameSettings.DEFAULT_DIE_FACE_VALUE,
                                                                               Die.DEFAULT_OUTCOME))
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

    final Dice dice = new DefenderDice (dieBuilder.build (), absoluteDefenderDieRange);
    dice.setTouchable (areDefenderDiceTouchable ());

    return dice;
  }

  @Override
  public DiceArrows createBattleDialogDiceArrows ()
  {
    final ImmutableSet.Builder <DiceArrow> arrowsBuilder = ImmutableSet.builder ();
    final int arrowCount = Math.min (absoluteAttackerDieRange.max (), absoluteDefenderDieRange.max ());

    for (int index = 0; index < arrowCount; ++index)
    {
      arrowsBuilder.add (createBattleDialogDiceArrow (index));
    }

    return new DiceArrows (arrowsBuilder.build ());
  }

  @Nullable
  @Override
  public Drawable createBattleDialogDiceArrowDrawable (final DieOutcome attackerDieOutcome)
  {
    Arguments.checkIsNotNull (attackerDieOutcome, "attackerDieOutcome");

    if (attackerDieOutcome == DieOutcome.NONE) return null;

    return new TextureRegionDrawable (createTextureRegion (DICE_ARROW_OUTCOME_TEXTURE_REGION_NAME_PREFIX
            + attackerDieOutcome.lowerCaseName ()));
  }

  @Override
  public CountryArmyText createAttackingCountryArmyTextEffects ()
  {
    return new CountryArmyTextEffects (createCountryArmyTextFont (), HorizontalMoveDirection.RIGHT);
  }

  @Override
  public CountryArmyText createDefendingCountryArmyTextEffects ()
  {
    return new CountryArmyTextEffects (createCountryArmyTextFont (), HorizontalMoveDirection.LEFT);
  }

  @Override
  public BitmapFont createCountryArmyTextEffectsFont ()
  {
    return createBitmapFont (COUNTRY_ARMY_TEXT_EFFECTS_FONT_NAME);
  }

  @Override
  public Music createBattleAmbienceSoundEffect ()
  {
    if (!isAssetLoaded (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BATTLE_AMBIENCE_SOUND_EFFECT_ASSET_DESCRIPTOR))
    {
      return new NullMusic ();
    }

    return getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BATTLE_AMBIENCE_SOUND_EFFECT_ASSET_DESCRIPTOR);
  }

  @Override
  public Sound createBattleSingleExplosionSoundEffect ()
  {
    if (!isAssetLoaded (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BATTLE_SINGLE_EXPLOSION_SOUND_ASSET_DESCRIPTOR))
    {
      return new NullSound ();
    }

    return getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BATTLE_SINGLE_EXPLOSION_SOUND_ASSET_DESCRIPTOR);
  }

  @Override
  protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR;
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

  private DiceArrow createBattleDialogDiceArrow (final int index)
  {
    return new DiceArrow (index, createBattleDialogDiceArrowImage (DiceArrow.DEFAULT_ATTACKER_OUTCOME), this);
  }

  private Image createBattleDialogDiceArrowImage (final DieOutcome attackerOutcome)
  {
    return new Image (createBattleDialogDiceArrowDrawable (attackerOutcome));
  }

  private ImageButton createAttackerDieImageButton (final DieState state,
                                                    final DieFaceValue faceValue,
                                                    final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return createImageButton (createAttackerDieImageButtonStyle (state, faceValue, outcome));
  }

  private ImageButton createDefenderDieImageButton (final DieState state,
                                                    final DieFaceValue faceValue,
                                                    final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return createImageButton (createDefenderDieImageButtonStyle (state, faceValue, outcome));
  }

  private ImageButton.ImageButtonStyle createAttackerDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return createImageButtonStyle (createDieStyleName (StyleSettings.BATTLE_DIALOG_DIE_ATTACK_STYLE_SEGMENT, state,
                                                       faceValue, outcome));
  }

  private ImageButton.ImageButtonStyle createDefenderDieImageButtonStyle (final DieState state,
                                                                          final DieFaceValue faceValue,
                                                                          final DieOutcome outcome)
  {
    Arguments.checkIsNotNull (state, "state");
    Arguments.checkIsNotNull (faceValue, "faceValue");
    Arguments.checkIsNotNull (outcome, "outcome");

    return createImageButtonStyle (createDieStyleName (StyleSettings.BATTLE_DIALOG_DIE_DEFEND_STYLE_SEGMENT, state,
                                                       faceValue, outcome));
  }
}
