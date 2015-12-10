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
