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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.CountryArmyText;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.DefaultCountryArmyText;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.popups.battle.CountryArmyTextEffects.HorizontalMoveDirection;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dice.DiceArrow;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dice.DiceArrows;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.ImmutableSet;

import javax.annotation.Nullable;

public class DefaultBattlePopupWidgetFactory extends AbstractWidgetFactory implements BattlePopupWidgetFactory
{
  public DefaultBattlePopupWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  public Label createBattlePopupPlayerNameLabel ()
  {
    return createLabel ("", Align.center, createBattlePopupPlayerNameLabelStyle ());
  }

  @Override
  public Label.LabelStyle createBattlePopupPlayerNameLabelStyle ()
  {
    return createLabelStyle ("battle-popup-player-name");
  }

  @Override
  public Label createBattlePopupCountryNameLabel ()
  {
    return createLabel ("", Align.center, createBattlePopupCountryNameLabelStyle ());
  }

  @Override
  public Label.LabelStyle createBattlePopupCountryNameLabelStyle ()
  {
    return createLabelStyle ("battle-popup-country-name");
  }

  @Override
  public Label createBattlePopupBattlingArrowLabel ()
  {
    return createLabel ("Attacking", Align.left, createBattlePopupBattlingArrowLabelStyle ());
  }

  @Override
  public Label.LabelStyle createBattlePopupBattlingArrowLabelStyle ()
  {
    return createLabelStyle ("battle-popup-arrow");
  }

  @Override
  public DiceArrows createBattlePopupDiceArrows (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    final ImmutableSet.Builder <DiceArrow> arrowsBuilder = ImmutableSet.builder ();
    final int arrowCount = Math.min (rules.getMaxTotalAttackerDieCount (), rules.getMaxTotalDefenderDieCount ());

    for (int index = 0; index < arrowCount; ++index)
    {
      arrowsBuilder.add (createBattlePopupDiceArrow (index));
    }

    return new DiceArrows (arrowsBuilder.build ());
  }

  @Nullable
  @Override
  public Drawable createBattlePopupDiceArrowDrawable (final DieOutcome attackerDieOutcome)
  {
    Arguments.checkIsNotNull (attackerDieOutcome, "attackerDieOutcome");

    if (attackerDieOutcome == DieOutcome.NONE) return null;

    return new TextureRegionDrawable (
            createTextureRegion ("dice-arrow-outcome-attacker-" + attackerDieOutcome.lowerCaseName ()));
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
    return createBitmapFont ("dejaVu-22");
  }

  @Override
  protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.CLASSIC_MODE_PLAY_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  private DiceArrow createBattlePopupDiceArrow (final int index)
  {
    return new DiceArrow (index, createBattlePopupDiceArrowImage (DiceArrow.DEFAULT_ATTACKER_OUTCOME), this);
  }

  private Image createBattlePopupDiceArrowImage (final DieOutcome attackerOutcome)
  {
    return new Image (createBattlePopupDiceArrowDrawable (attackerOutcome));
  }
}
