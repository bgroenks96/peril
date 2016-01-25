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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;

import javax.annotation.Nullable;

public interface BattlePopupWidgetFactory extends WidgetFactory
{
  Label createBattlePopupPlayerNameLabel ();

  Label.LabelStyle createBattlePopupPlayerNameLabelStyle ();

  Label createBattlePopupCountryNameLabel ();

  Label.LabelStyle createBattlePopupCountryNameLabelStyle ();

  Label createBattlePopupBattlingArrowLabel ();

  Label.LabelStyle createBattlePopupBattlingArrowLabelStyle ();

  DiceArrows createBattlePopupDiceArrows (final GameRules rules);

  @Nullable
  Drawable createBattlePopupDiceArrowDrawable (final DieOutcome attackerDieOutcome);

  CountryArmyTextActor createCountryArmyTextActor ();

  BitmapFont createCountryArmyTextActorFont ();

  CountryArmyTextActor createAttackingCountryArmyTextEffectsActor ();

  CountryArmyTextActor createDefendingCountryArmyTextEffectsActor ();

  BitmapFont createCountryArmyTextEffectsActorFont ();
}
