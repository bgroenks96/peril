package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

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
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyTextActor;
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
  public CountryArmyTextActor createCountryArmyTextActor ()
  {
    return new DefaultCountryArmyTextActor (createCountryArmyTextActorFont ());
  }

  @Override
  public BitmapFont createCountryArmyTextActorFont ()
  {
    return new BitmapFont ();
  }

  @Override
  public CountryArmyTextActor createAttackingCountryArmyTextEffectsActor ()
  {
    return new CountryArmyTextEffectsActor (createCountryArmyTextActorFont (),
            CountryArmyTextEffectsActor.HorizontalMoveDirection.RIGHT);
  }

  @Override
  public CountryArmyTextActor createDefendingCountryArmyTextEffectsActor ()
  {
    return new CountryArmyTextEffectsActor (createCountryArmyTextActorFont (),
            CountryArmyTextEffectsActor.HorizontalMoveDirection.LEFT);
  }

  @Override
  public BitmapFont createCountryArmyTextEffectsActorFont ()
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
