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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import com.forerunnergames.peril.client.events.BattleDialogResetCompleteEvent;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.peril.client.ui.NonPausingTimer;
import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.Dice;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DiceArrows;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dice.DiceFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.CountryArmyText;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogStyle;
import com.forerunnergames.peril.client.ui.widgets.dialogs.OkDialog;
import com.forerunnergames.peril.common.game.DieRoll;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableList;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

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
  private final MBassador <Event> eventBus;
  private final BattleDialogListener listener;
  private final GameRules gameRules;
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
  private final Timer timer = new NonPausingTimer ();
  private final AtomicBoolean isBattleInProgress = new AtomicBoolean (false);
  private final AtomicBoolean isResettingBattle = new AtomicBoolean (false);
  private final AtomicBoolean shouldStartBattle = new AtomicBoolean (false);
  private final Object lock = new Object ();
  private Sound battleSingleExplosionSoundEffect;
  private Music battleAmbienceSoundEffect;
  private Timer.Task battleTask = new Timer.Task ()
  {
    @Override
    public void run ()
    {
    }
  };
  private Timer.Task resetBattleTask = new Timer.Task ()
  {
    @Override
    public void run ()
    {
    }
  };
  private Timer.Task playBattleEffectsTask = new Timer.Task ()
  {
    @Override
    public void run ()
    {
    }
  };

  protected AbstractBattleDialog (final BattleDialogWidgetFactory widgetFactory,
                                  final DiceFactory diceFactory,
                                  final String title,
                                  final Stage stage,
                                  final ScreenShaker screenShaker,
                                  final MBassador <Event> eventBus,
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
    Arguments.checkIsNotNull (diceFactory, "diceFactory");
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (screenShaker, "screenShaker");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    this.widgetFactory = widgetFactory;
    this.screenShaker = screenShaker;
    this.eventBus = eventBus;
    this.listener = listener;

    gameRules = new ClassicGameRules.Builder ().build ();

    attackingPlayerNameLabel = widgetFactory.createBattleDialogPlayerNameLabel ();
    defendingPlayerNameLabel = widgetFactory.createBattleDialogPlayerNameLabel ();
    attackingCountryNameLabel = widgetFactory.createBattleDialogCountryNameLabel ();
    defendingCountryNameLabel = widgetFactory.createBattleDialogCountryNameLabel ();
    battlingArrowLabel = widgetFactory.createBattleDialogBattlingArrowLabel ();
    attackerDice = diceFactory.createAttackerDice (gameRules);
    defenderDice = diceFactory.createDefenderDice (gameRules);
    diceArrows = widgetFactory.createBattleDialogDiceArrows (gameRules);
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
  public final void hide ()
  {
    battleTask.cancel ();
    resetBattleTask.cancel ();
    playBattleEffectsTask.cancel ();
    battleSingleExplosionSoundEffect.stop ();
    battleAmbienceSoundEffect.stop ();
    screenShaker.stop ();
    isBattleInProgress.set (false);
    isResettingBattle.set (false);
    shouldStartBattle.set (false);

    super.hide ();
  }

  @Override
  public void hide (@Nullable final Action action)
  {
    battleTask.cancel ();
    resetBattleTask.cancel ();
    playBattleEffectsTask.cancel ();
    battleSingleExplosionSoundEffect.stop ();
    battleAmbienceSoundEffect.stop ();
    screenShaker.stop ();
    isBattleInProgress.set (false);
    isResettingBattle.set (false);
    shouldStartBattle.set (false);

    super.hide (action);
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
  public final void show (final Country attackingCountry,
                          final Country defendingCountry,
                          final String attackingPlayerName,
                          final String defendingPlayerName)
  {
    Arguments.checkIsNotNull (attackingCountry, "attackingCountry");
    Arguments.checkIsNotNull (defendingCountry, "defendingCountry");
    Arguments.checkIsNotNull (attackingPlayerName, "attackingPlayerName");
    Arguments.checkIsNotNull (defendingPlayerName, "defendingPlayerName");

    if (isShown ()) return;

    final int attackingCountryArmies = attackingCountry.getArmies ();
    final int defendingCountryArmies = defendingCountry.getArmies ();

    setCountries (attackingCountry, defendingCountry);
    initializeCountryArmies (attackingCountryArmies, defendingCountryArmies);
    setPlayerNames (attackingPlayerName, defendingPlayerName);
    initializeDice (attackingCountryArmies, defendingCountryArmies);
    show ();
    playBattleAmbienceSoundEffect ();
  }

  @Override
  public void battle ()
  {
    synchronized (battleTask)
    {
      if (battleTask.isScheduled ())
      {
        log.warn ("Battle task already scheduled, not starting battle.");
        return;
      }
    }

    synchronized (lock)
    {
      if (shouldStartBattle.get ())
      {
        log.warn ("Not starting battle (already waiting for battle to start after reset is complete).");
        return;
      }

      if (isResettingBattle.get ())
      {
        shouldStartBattle.set (true);
        log.debug ("Battle reset in progress; battle will start when reset is complete.");
        return;
      }

      shouldStartBattle.set (false);
      isBattleInProgress.set (true);
    }

    enableInput ();
    setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE);

    battleTask = timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        disableInput ();
        listener.onBattle ();
      }

      @Override
      public synchronized void cancel ()
      {
        shouldStartBattle.set (false);
        isBattleInProgress.set (false);
      }
    }, GameSettings.BATTLE_INTERACTION_TIME_SECONDS);
  }

  @Override
  public boolean isBattleInProgress ()
  {
    return isBattleInProgress.get ();
  }

  @Override
  public boolean isResetting ()
  {
    return isResettingBattle.get ();
  }

  @Override
  public final void showBattleResult (final BattleResultPacket result)
  {
    Arguments.checkIsNotNull (result, "result");

    rollDice (result.getAttackerRolls (), result.getDefenderRolls ());
    playBattleEffects (result.getAttackingCountryArmyDelta (), result.getDefendingCountryArmyDelta ());
    changeCountryArmiesBy (result.getAttackingCountryArmyDelta (), result.getDefendingCountryArmyDelta ());
    setDiceTouchable (false);
    resetBattle ();
  }

  @Override
  public final void playBattleEffects (final int attackingCountryDeltaArmies, final int defendingCountryDeltaArmies)
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

        public synchronized void cancel ()
        {
          battleSingleExplosionSoundEffect.stop ();
          screenShaker.stop ();
        }
      }, 0.25f);
    }
  }

  @Override
  public final String getAttackingCountryName ()
  {
    return attackingCountryNameLabel.getText ().toString ();
  }

  @Override
  public final String getDefendingCountryName ()
  {
    return defendingCountryNameLabel.getText ().toString ();
  }

  @Override
  public final String getAttackingPlayerName ()
  {
    return attackingPlayerNameLabel.getText ().toString ();
  }

  @Override
  public final String getDefendingPlayerName ()
  {
    return defendingPlayerNameLabel.getText ().toString ();
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

  @Override
  public final int getAttackingCountryArmyCount ()
  {
    return attackingCountryArmyText.getArmies ();
  }

  @Override
  public final int getDefendingCountryArmyCount ()
  {
    return defendingCountryArmyText.getArmies ();
  }

  @Override
  public final void stopBattle ()
  {
    battleTask.cancel ();
    isBattleInProgress.set (false);
    shouldStartBattle.set (false);
    setDiceTouchable (false);
    log.debug ("Stopped battle.");
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

  private void rollDice (final ImmutableList <DieRoll> attackerRolls, final ImmutableList <DieRoll> defenderRolls)
  {
    clampAttackerDiceToCount (attackerRolls.size ());
    clampDefenderDiceToCount (defenderRolls.size ());

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

  private void resetBattle ()
  {
    synchronized (resetBattleTask)
    {
      if (resetBattleTask.isScheduled ()) return;
    }

    synchronized (lock)
    {
      if (isResettingBattle.get ())
      {
        log.warn ("Not resetting battle (reset is already in progress).");
        return;
      }
    }

    isResettingBattle.set (true);

    resetBattleTask = timer.scheduleTask (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        final int attackingCountryArmies = getAttackingCountryArmyCount ();
        final int defendingCountryArmies = getDefendingCountryArmyCount ();

        if (gameRules.canBattle (attackingCountryArmies, defendingCountryArmies))
        {
          resetDieFaces ();
          resetDieOutcomes ();
          resetDiceArrows ();
          resetDiceSpinning ();
          clampDice (attackingCountryArmies, defendingCountryArmies);
        }
        else if (!gameRules.attackerCanBattle (attackingCountryArmies))
        {
          stopBattle ();
          listener.onAttackerLoseFinal ();
        }
        else if (!gameRules.defenderCanBattle (defendingCountryArmies))
        {
          stopBattle ();
          listener.onAttackerWinFinal ();
        }

        updateDiceTouchability (attackingCountryArmies, defendingCountryArmies);

        isResettingBattle.set (false);

        eventBus.publish (new BattleDialogResetCompleteEvent ());

        if (shouldStartBattle.getAndSet (false)) battle ();
      }

      @Override
      public synchronized void cancel ()
      {
        super.cancel ();
        isResettingBattle.set (false);
      }
    }, GameSettings.BATTLE_OUTCOME_VIEWING_TIME_SECONDS);
  }

  private void initializeDice (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    resetDice ();
    clampDice (attackingCountryArmies, defendingCountryArmies);
    resetDiceArrows ();
    updateDiceTouchability (attackingCountryArmies, defendingCountryArmies);
  }

  private void resetDice ()
  {
    attackerDice.resetAll ();
    defenderDice.resetAll ();
  }

  private void clampDice (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    attackerDice.clamp (gameRules.getMinAttackerDieCount (attackingCountryArmies),
                        gameRules.getMaxAttackerDieCount (attackingCountryArmies));

    defenderDice.clamp (gameRules.getMinDefenderDieCount (defendingCountryArmies),
                        gameRules.getMaxDefenderDieCount (defendingCountryArmies));
  }

  private void clampAttackerDiceToCount (final int count)
  {
    if (attackerDice.getActiveCount () == count) return;

    final int attackingCountryArmies = getAttackingCountryArmyCount ();

    attackerDice.clampToCount (count, gameRules.getMinAttackerDieCount (attackingCountryArmies),
                               gameRules.getMaxAttackerDieCount (attackingCountryArmies));
  }

  private void clampDefenderDiceToCount (final int count)
  {
    if (defenderDice.getActiveCount () == count) return;

    final int defendingCountryArmies = getDefendingCountryArmyCount ();

    defenderDice.clampToCount (count, gameRules.getMinDefenderDieCount (defendingCountryArmies),
                               gameRules.getMaxDefenderDieCount (defendingCountryArmies));
  }

  private void updateDiceTouchability (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE
            && gameRules.canBattle (attackingCountryArmies, defendingCountryArmies));
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
