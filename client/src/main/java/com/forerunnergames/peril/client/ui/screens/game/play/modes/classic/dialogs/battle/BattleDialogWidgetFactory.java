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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.ui.music.MusicWrapper;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.Dice;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DiceArrows;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.CountryArmyText;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.common.game.DieOutcome;

import javax.annotation.Nullable;

public interface BattleDialogWidgetFactory extends WidgetFactory
{
  Label createBattleDialogPlayerNameLabel ();

  Label.LabelStyle createBattleDialogPlayerNameLabelStyle ();

  Label createBattleDialogCountryNameLabel ();

  Label.LabelStyle createBattleDialogCountryNameLabelStyle ();

  Label createBattleDialogBattlingArrowLabel ();

  Label.LabelStyle createBattleDialogBattlingArrowLabelStyle ();

  CountryArmyText createCountryArmyText ();

  BitmapFont createCountryArmyTextFont ();

  Dice createAttackerDice ();

  Dice createDefenderDice ();

  DiceArrows createBattleDialogDiceArrows ();

  @Nullable
  Drawable createBattleDialogDiceArrowDrawable (final DieOutcome attackerDieOutcome);

  CountryArmyText createAttackingCountryArmyTextEffects ();

  CountryArmyText createDefendingCountryArmyTextEffects ();

  BitmapFont createCountryArmyTextEffectsFont ();

  MusicWrapper createBattleAmbienceSoundEffect ();

  Sound createBattleSingleExplosionSoundEffect ();
}
