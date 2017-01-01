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

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.NonDelayingTimer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.EmptyTimerTask;
import com.forerunnergames.peril.client.ui.music.MusicWrapper;
import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.Dice;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DiceArrows;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.CountryArmyText;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.OkDialog;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.DieRange;
import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.battle.PendingBattleActorPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.annotations.AllowNegative;

import com.google.common.collect.ImmutableList;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractBattleDialog extends OkDialog implements BattleDialog
{
  private static final boolean DEBUG = false;
  private static final float COUNTRY_NAME_BOX_WIDTH = 400;
  private static final float COUNTRY_NAME_BOX_HEIGHT = 28;
  private static final float PLAYER_NAME_BOX_WIDTH = 400;
  private static final float PLAYER_NAME_BOX_HEIGHT = 28;
  private static final float COUNTRY_BOX_INNER_PADDING = 3;
  private static final float COUNTRY_BOX_WIDTH = 400 - COUNTRY_BOX_INNER_PADDING * 2;
  private static final float COUNTRY_BOX_HEIGHT = 200 - COUNTRY_BOX_INNER_PADDING - 3;
  private static final float BATTLING_ARROW_LABEL_TEXT_VERTICAL_INNER_PADDING = 4;
  protected final Logger log = LoggerFactory.getLogger (getClass ());
  private final BattleDialogWidgetFactory widgetFactory;
  private final ScreenShaker screenShaker;
  private final BattleDialogListener listener;
  private final Vector2 tempPosition = new Vector2 ();
  private final Vector2 tempScaling = new Vector2 ();
  private final Vector2 tempSize = new Vector2 ();
  private final CountryArmyText attackingCountryArmyText;
  private final CountryArmyText attackingCountryArmyTextEffects;
  private final CountryArmyText defendingCountryArmyText;
  private final CountryArmyText defendingCountryArmyTextEffects;
  private final Label attackingPlayerNameLabel;
  private final Label defendingPlayerNameLabel;
  private final Label attackingCountryNameLabel;
  private final Label defendingCountryNameLabel;
  private final Label battlingArrowLabel;
  private final Dice attackerDice;
  private final Dice defenderDice;
  private final DiceArrows diceArrows;
  private final Stack attackingCountryStack;
  private final Stack defendingCountryStack;
  private final AtomicBoolean isBattling = new AtomicBoolean (false);
  private final Timer timer = new NonDelayingTimer ();
  private Sound battleSingleExplosionSoundEffect;
  private MusicWrapper battleAmbienceSoundEffect;
  private Timer.Task battleTask = new EmptyTimerTask ();
  private Timer.Task playBattleEffectsTask = new EmptyTimerTask ();
  private Timer.Task showBattleResultCompleteTask = new EmptyTimerTask ();

  protected AbstractBattleDialog (final BattleDialogWidgetFactory widgetFactory,
                                  final String title,
                                  final Stage stage,
                                  final ScreenShaker screenShaker,
                                  final BattleDialogListener listener)
  {
    // @formatter:off
    super (widgetFactory,
            DialogStyle.builder ()
                    .windowStyle (StyleSettings.BATTLE_DIALOG_WINDOW_STYLE)
                    .modal (false)
                    .movable (true)
                    .size (990, 432)
                    .position (405, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 178)
                    .title (title)
                    .titleHeight (56)
                    .messageBox (false)
                    .border (28)
                    .buttonSpacing (16)
                    .buttonTextPaddingHorizontal (6)
                    .debug (DEBUG)
                    .build (),
            stage, listener);
    // @formatter:on

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (screenShaker, "screenShaker");
    Arguments.checkIsNotNull (listener, "listener");

    this.widgetFactory = widgetFactory;
    this.screenShaker = screenShaker;
    this.listener = listener;

    attackingPlayerNameLabel = widgetFactory.createBattleDialogPlayerNameLabel ();
    defendingPlayerNameLabel = widgetFactory.createBattleDialogPlayerNameLabel ();
    attackingCountryNameLabel = widgetFactory.createBattleDialogCountryNameLabel ();
    defendingCountryNameLabel = widgetFactory.createBattleDialogCountryNameLabel ();
    battlingArrowLabel = widgetFactory.createBattleDialogBattlingArrowLabel ();
    attackerDice = widgetFactory.createAttackerDice ();
    defenderDice = widgetFactory.createDefenderDice ();
    diceArrows = widgetFactory.createBattleDialogDiceArrows ();
    attackingCountryArmyText = widgetFactory.createCountryArmyText ();
    attackingCountryArmyTextEffects = widgetFactory.createAttackingCountryArmyTextEffects ();
    defendingCountryArmyText = widgetFactory.createCountryArmyText ();
    defendingCountryArmyTextEffects = widgetFactory.createDefendingCountryArmyTextEffects ();
    battleSingleExplosionSoundEffect = widgetFactory.createBattleSingleExplosionSoundEffect ();
    battleAmbienceSoundEffect = widgetFactory.createBattleAmbienceSoundEffect ();

    attackingCountryStack = new Stack ();
    defendingCountryStack = new Stack ();

    final Table attackingCountryTable = new Table ();
    attackingCountryTable.add (attackingCountryStack);
    attackingCountryTable.setClip (true);
    attackingCountryTable.setDebug (DEBUG, true);

    final Table defendingCountryTable = new Table ();
    defendingCountryTable.add (defendingCountryStack);
    defendingCountryTable.setClip (true);
    defendingCountryTable.setDebug (DEBUG, true);

    final Table diceTable = new Table ().pad (2);
    diceTable.add (attackerDice.asActor ()).spaceRight (22).top ();
    diceTable.add (defenderDice.asActor ()).spaceLeft (22).top ();
    diceTable.setDebug (DEBUG, true);

    final Table diceArrowsTable = new Table ().top ().padTop (10);
    diceArrowsTable.add (diceArrows.asActor ());
    diceArrowsTable.setDebug (DEBUG, true);

    final Stack diceTableStack = new Stack ();
    diceTableStack.add (diceTable);
    diceTableStack.add (diceArrowsTable);
    diceTableStack.setDebug (DEBUG, true);

    final Table leftTable = new Table ().top ().pad (2);
    leftTable.add (attackingPlayerNameLabel).size (PLAYER_NAME_BOX_WIDTH, PLAYER_NAME_BOX_HEIGHT).space (2);
    leftTable.row ();
    leftTable.add (attackingCountryTable).size (COUNTRY_BOX_WIDTH, COUNTRY_BOX_HEIGHT)
            .padBottom (COUNTRY_BOX_INNER_PADDING + 3).space (2);
    leftTable.row ();
    leftTable.add (attackingCountryNameLabel).size (COUNTRY_NAME_BOX_WIDTH, COUNTRY_NAME_BOX_HEIGHT).space (2);
    leftTable.setDebug (DEBUG, true);

    final Table centerTable = new Table ().top ().padTop (2).padBottom (2);
    centerTable.add (battlingArrowLabel).padRight (8).padTop (24 - BATTLING_ARROW_LABEL_TEXT_VERTICAL_INNER_PADDING)
            .padBottom (51 + BATTLING_ARROW_LABEL_TEXT_VERTICAL_INNER_PADDING);
    centerTable.row ();
    centerTable.add (diceTableStack).size (94, 116);
    centerTable.setDebug (DEBUG, true);

    final Table rightTable = new Table ().top ().pad (2);
    rightTable.add (defendingPlayerNameLabel).size (PLAYER_NAME_BOX_WIDTH, PLAYER_NAME_BOX_HEIGHT).space (2);
    rightTable.row ();
    rightTable.add (defendingCountryTable).size (COUNTRY_BOX_WIDTH, COUNTRY_BOX_HEIGHT)
            .padBottom (COUNTRY_BOX_INNER_PADDING + 3).space (2);
    rightTable.row ();
    rightTable.add (defendingCountryNameLabel).size (COUNTRY_NAME_BOX_WIDTH, COUNTRY_NAME_BOX_HEIGHT).space (2);
    rightTable.setDebug (DEBUG, true);

    getContentTable ().defaults ().space (0).pad (0);
    getContentTable ().top ();
    getContentTable ().add (leftTable).size (404, 264);
    getContentTable ().add (centerTable).size (126, 264);
    getContentTable ().add (rightTable).size (404, 264);
    getContentTable ().row ();
  }

  @Override
  @SuppressWarnings ("RefusedBequest")
  public final void hide ()
  {
    timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        reset ();
        AbstractBattleDialog.super.hide ();
      }
    }, getBattleResultCompleteDelaySeconds ());
  }

  @Override
  @SuppressWarnings ("RefusedBequest")
  public void hide (@Nullable final Action action)
  {
    timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        reset ();
        AbstractBattleDialog.super.hide (action);
      }
    }, getBattleResultCompleteDelaySeconds ());
  }

  @Override
  public void enableInput ()
  {
    super.enableInput ();
    setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE);
  }

  @Override
  public void disableInput ()
  {
    super.disableInput ();
    setDiceTouchable (false);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void update (final float delta)
  {
    super.update (delta);

    if (!isShown ()) return;

    attackerDice.update (delta);
    defenderDice.update (delta);
    screenShaker.update (delta);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    super.refreshAssets ();

    attackingPlayerNameLabel.setStyle (widgetFactory.createBattleDialogPlayerNameLabelStyle ());
    defendingPlayerNameLabel.setStyle (widgetFactory.createBattleDialogPlayerNameLabelStyle ());
    attackingCountryNameLabel.setStyle (widgetFactory.createBattleDialogCountryNameLabelStyle ());
    defendingCountryNameLabel.setStyle (widgetFactory.createBattleDialogCountryNameLabelStyle ());
    battlingArrowLabel.setStyle (widgetFactory.createBattleDialogBattlingArrowLabelStyle ());
    attackerDice.refreshAssets ();
    defenderDice.refreshAssets ();
    diceArrows.refreshAssets ();
    attackingCountryArmyText.setFont (widgetFactory.createCountryArmyTextFont ());
    defendingCountryArmyText.setFont (widgetFactory.createCountryArmyTextFont ());
    attackingCountryArmyTextEffects.setFont (widgetFactory.createCountryArmyTextEffectsFont ());
    defendingCountryArmyTextEffects.setFont (widgetFactory.createCountryArmyTextEffectsFont ());
    battleSingleExplosionSoundEffect = widgetFactory.createBattleSingleExplosionSoundEffect ();
    battleAmbienceSoundEffect = widgetFactory.createBattleAmbienceSoundEffect ();
  }

  @Override
  public final void startBattle (final PendingBattleActorPacket attacker,
                                 final PendingBattleActorPacket defender,
                                 final Country attackingCountry,
                                 final Country defendingCountry)
  {
    Arguments.checkIsNotNull (attacker, "attacker");
    Arguments.checkIsNotNull (defender, "defender");
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");

    isBattling.set (true);

    setCountries (attackingCountry, defendingCountry);
    initializeCountryArmies (attacker.getCountryArmyCount (), defender.getCountryArmyCount ());
    setPlayerNames (attacker.getPlayerName (), defender.getPlayerName ());
    initializeDice (attacker.getDieRange (), defender.getDieRange ());
    show ();
    playBattleAmbienceSoundEffect ();
    battle ();
  }

  @Override
  public void continueBattle (final DieRange attackerDieRange, final DieRange defenderDieRange)
  {
    Arguments.checkIsNotNull (attackerDieRange, "attackerDieRange");
    Arguments.checkIsNotNull (defenderDieRange, "defenderDieRange");

    isBattling.set (true);

    timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        clampDice (attackerDieRange, defenderDieRange);
        resetDieFaces ();
        resetDieOutcomes ();
        resetDiceArrows ();
        resetDiceSpinning ();
        setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE);
        battle ();
      }
    }, getBattleResultCompleteDelaySeconds ());
  }

  @Override
  public final void showBattleResult (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    // @formatter:off
    rollDice (result.getAttackerRolls (), result.getAttackerDieRange (), result.getDefenderRolls (), result.getDefenderDieRange ());
    playBattleEffects (result.getAttackingCountryArmyDelta (), result.getDefendingCountryArmyDelta ());
    changeCountryArmiesBy (result.getAttackingCountryArmyDelta (), result.getDefendingCountryArmyDelta ());
    setDiceTouchable (false);
    // @formatter:on

    synchronized (showBattleResultCompleteTask)
    {
      if (showBattleResultCompleteTask.isScheduled ())
      {
        log.warn ("Show battle result complete task already scheduled, not re-scheduling.");
        return;
      }
    }

    showBattleResultCompleteTask = timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        isBattling.set (false);
        if (!result.outcomeIs (BattleOutcome.CONTINUE)) battleAmbienceSoundEffect.stop ();
        if (result.outcomeIs (BattleOutcome.ATTACKER_VICTORIOUS)) listener.onResultAttackerVictorious (result);
        if (result.outcomeIs (BattleOutcome.ATTACKER_DEFEATED)) listener.onResultAttackerDefeated (result);
      }
    }, GameSettings.BATTLE_RESULT_VIEWING_TIME_SECONDS);
  }

  @Override
  public final void playBattleEffects (@AllowNegative final int attackingCountryDeltaArmies,
                                       @AllowNegative final int defendingCountryDeltaArmies)
  {
    if (attackingCountryDeltaArmies == 0 && defendingCountryDeltaArmies == 0) return;

    battleSingleExplosionSoundEffect.play ();
    screenShaker.shake ();

    if (attackingCountryDeltaArmies == -2 || defendingCountryDeltaArmies == -2)
    {
      playBattleEffectsTask = timer.scheduleTask (new Timer.Task ()
      {
        @Override
        public void run ()
        {
          battleSingleExplosionSoundEffect.play ();
          screenShaker.shake ();
        }

        @Override
        public synchronized void cancel ()
        {
          battleSingleExplosionSoundEffect.stop ();
          screenShaker.stop ();
        }
      }, 0.25f);
    }
  }

  @Override
  public void updateCountries (final Country attackingCountry, final Country defendingCountry)
  {
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");

    setCountries (attackingCountry, defendingCountry);
  }

  @Override
  public boolean isBattling ()
  {
    return isBattling.get ();
  }

  @Override
  public final int getActiveAttackerDieCount ()
  {
    return attackerDice.getActiveCount ();
  }

  @Override
  public final int getActiveDefenderDieCount ()
  {
    return defenderDice.getActiveCount ();
  }

  protected abstract void setDiceTouchable (final boolean areTouchable);

  protected final void setAttackerDiceTouchable (final boolean areTouchable)
  {
    attackerDice.setTouchable (areTouchable);
  }

  protected final void setDefenderDiceTouchable (final boolean areTouchable)
  {
    defenderDice.setTouchable (areTouchable);
  }

  private static Image asImage (final Country country)
  {
    return new Image (country.getPrimaryDrawable (), Scaling.none);
  }

  private void battle ()
  {
    synchronized (battleTask)
    {
      if (battleTask.isScheduled ())
      {
        log.warn ("Battle task already scheduled, not re-scheduling.");
        return;
      }
    }

    enableInput ();

    battleTask = timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        disableInput ();
        listener.onBattle ();
      }
    }, GameSettings.BATTLE_INTERACTION_TIME_SECONDS);
  }

  @SuppressWarnings ("IntegerDivisionInFloatingPointContext")
  private float getBattleResultCompleteDelaySeconds ()
  {
    synchronized (showBattleResultCompleteTask)
    {
      final float seconds = (showBattleResultCompleteTask.getExecuteTimeMillis () - (System.nanoTime () / 1000000L))
              / 1000.0f;
      return seconds > 0.0f ? seconds : 0.0f;
    }
  }

  private void reset ()
  {
    battleTask.cancel ();
    playBattleEffectsTask.cancel ();
    battleSingleExplosionSoundEffect.stop ();
    battleAmbienceSoundEffect.stop ();
    screenShaker.stop ();
    isBattling.set (false);
  }

  private void rollDice (final ImmutableList <DieRoll> attackerRolls,
                         final DieRange attackerDieRange,
                         final ImmutableList <DieRoll> defenderRolls,
                         final DieRange defenderDieRange)
  {
    attackerDice.clampToCount (attackerRolls.size (), attackerDieRange);
    defenderDice.clampToCount (defenderRolls.size (), defenderDieRange);

    attackerDice.roll (attackerRolls);
    defenderDice.roll (defenderRolls);

    attackerDice.setOutcomes (attackerRolls);
    defenderDice.setOutcomes (defenderRolls);

    diceArrows.setOutcomes (attackerRolls, defenderRolls);
  }

  private void playBattleAmbienceSoundEffect ()
  {
    battleAmbienceSoundEffect.setLooping (true);
    battleAmbienceSoundEffect.play ();
  }

  private void initializeDice (final DieRange attackerDieRange, final DieRange defenderDieRange)
  {
    resetDice ();
    clampDice (attackerDieRange, defenderDieRange);
    resetDiceArrows ();
    setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE);
  }

  private void resetDice ()
  {
    attackerDice.resetAll ();
    defenderDice.resetAll ();
  }

  private void clampDice (final DieRange attackerDieRange, final DieRange defenderDieRange)
  {
    attackerDice.clamp (attackerDieRange);
    defenderDice.clamp (defenderDieRange);
  }

  private void resetDieFaces ()
  {
    attackerDice.resetFaceValues ();
    defenderDice.resetFaceValues ();
  }

  private void resetDieOutcomes ()
  {
    attackerDice.resetOutcomes ();
    defenderDice.resetOutcomes ();
  }

  private void resetDiceArrows ()
  {
    diceArrows.reset ();
  }

  private void resetDiceSpinning ()
  {
    attackerDice.resetSpinning ();
    defenderDice.resetSpinning ();
  }

  private void changeCountryArmiesBy (final int attackingCountryArmiesDelta, final int defendingCountryArmiesDelta)
  {
    attackingCountryArmyText.changeArmiesBy (attackingCountryArmiesDelta);
    defendingCountryArmyText.changeArmiesBy (defendingCountryArmiesDelta);

    attackingCountryArmyTextEffects.changeArmiesBy (attackingCountryArmiesDelta);
    defendingCountryArmyTextEffects.changeArmiesBy (defendingCountryArmiesDelta);
  }

  private void setCountries (final Country attackingCountry, final Country defendingCountry)
  {
    setCountryNames (attackingCountry, defendingCountry);
    setCountryImages (attackingCountry, defendingCountry);
  }

  private void initializeCountryArmies (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    attackingCountryArmyTextEffects.setVisible (false);
    defendingCountryArmyTextEffects.setVisible (false);

    attackingCountryArmyText.changeArmiesTo (attackingCountryArmies);
    defendingCountryArmyText.changeArmiesTo (defendingCountryArmies);
  }

  private void setCountryNames (final Country attackingCountry, final Country defendingCountry)
  {
    setCountryNames (attackingCountry.getName (), defendingCountry.getName ());
  }

  private void setCountryImages (final Country attackingCountry, final Country defendingCountry)
  {
    setCountryImage (attackingCountry, attackingCountryArmyText, attackingCountryArmyTextEffects,
                     attackingCountryStack);
    setCountryImage (defendingCountry, defendingCountryArmyText, defendingCountryArmyTextEffects,
                     defendingCountryStack);
  }

  private void setCountryImage (final Country country,
                                final CountryArmyText countryArmyText,
                                final CountryArmyText countryArmyTextEffects,
                                final Stack countryStack)
  {
    final Image countryImage = asImage (country);

    countryStack.clear ();
    countryStack.add (countryImage);
    countryStack.add (countryArmyText.asActor ());
    countryStack.add (countryArmyTextEffects.asActor ());

    getContentTable ().layout ();

    updateCountryArmyCircle (countryArmyText, country, countryImage);
    updateCountryArmyCircle (countryArmyTextEffects, country, countryImage);
  }

  private void updateCountryArmyCircle (final CountryArmyText countryArmyText,
                                        final Country country,
                                        final Image countryImage)
  {
    setCountryArmyCircleSize (countryArmyText, country, countryImage);
    setCountryArmyCirclePosition (countryArmyText, country, countryImage);
  }

  private void setCountryArmyCircleSize (final CountryArmyText countryArmyText,
                                         final Country country,
                                         final Image countryImage)
  {
    countryArmyText.setCircleSize (calculateCountryArmyTextCircleSizeActualCountrySpace (country, countryImage));
  }

  private void setCountryArmyCirclePosition (final CountryArmyText countryArmyText,
                                             final Country country,
                                             final Image countryImage)
  {
    countryArmyText.setCircleTopLeft (calculateCountryArmyTextCircleTopLeftActualCountrySpace (country, countryImage));
  }

  private Vector2 calculateCountryArmyTextCircleTopLeftActualCountrySpace (final Country country,
                                                                           final Image countryImagePostLayout)
  {
    return tempPosition.set (country.getReferenceTextUpperLeft ()).sub (country.getReferenceDestination ())
            .set (Math.abs (tempPosition.x), Math.abs (tempPosition.y))
            .scl (calculateCountryImageScaling (country, countryImagePostLayout))
            .add (countryImagePostLayout.getImageX (), countryImagePostLayout.getImageY ());
  }

  private Vector2 calculateCountryArmyTextCircleSizeActualCountrySpace (final Country country,
                                                                        final Image countryImagePostLayout)
  {
    return tempSize.set (PlayMapSettings.COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE)
            .scl (calculateCountryImageScaling (country, countryImagePostLayout));
  }

  private Vector2 calculateCountryImageScaling (final Country country, final Image countryImagePostLayout)
  {
    return tempScaling.set (countryImagePostLayout.getImageWidth () / country.getReferenceWidth (),
                            countryImagePostLayout.getImageHeight () / country.getReferenceHeight ());
  }

  private void setCountryNames (final String attackingCountryName, final String defendingCountryName)
  {
    attackingCountryNameLabel.setText (attackingCountryName);
    defendingCountryNameLabel.setText (defendingCountryName);
  }

  private void setPlayerNames (final String attackingPlayerName, final String defendingPlayerName)
  {
    attackingPlayerNameLabel.setText (attackingPlayerName);
    defendingPlayerNameLabel.setText (defendingPlayerName);
  }
}
