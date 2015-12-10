package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.widgets.popup.OkPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableList;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;

abstract class AbstractBattlePopup extends OkPopup
{
  // @formatter:off
  private static final boolean DEBUG = false;
  private static final float COUNTRY_NAME_BOX_WIDTH = 400;
  private static final float COUNTRY_NAME_BOX_HEIGHT = 28;
  private static final float PLAYER_NAME_BOX_WIDTH = 400;
  private static final float PLAYER_NAME_BOX_HEIGHT = 28;
  private static final float COUNTRY_BOX_INNER_PADDING = 3;
  private static final float COUNTRY_BOX_WIDTH = 400 - COUNTRY_BOX_INNER_PADDING * 2;
  private static final float COUNTRY_BOX_HEIGHT = 200 - COUNTRY_BOX_INNER_PADDING - 3;
  private final BattlePopupWidgetFactory widgetFactory;
  private final BattlePopupListener listener;
  private final GameRules gameRules;
  private final Vector2 tempPosition = new Vector2 ();
  private final Vector2 tempScaling = new Vector2 ();
  private final Vector2 tempSize = new Vector2 ();
  private final CountryArmyTextActor attackingCountryArmyTextActor;
  private final CountryArmyTextActor attackingCountryArmyTextEffectsActor;
  private final CountryArmyTextActor defendingCountryArmyTextActor;
  private final CountryArmyTextActor defendingCountryArmyTextEffectsActor;
  private final BattleOutcome outcome = new BattleOutcome ();
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
  private CountryActor attackingCountryActor = CountryActor.NULL_COUNTRY_ACTOR;
  private CountryActor defendingCountryActor = CountryActor.NULL_COUNTRY_ACTOR;
  // @formatter:on
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

  protected AbstractBattlePopup (final BattlePopupWidgetFactory widgetFactory,
                                 final DiceFactory diceFactory,
                                 final String title,
                                 final Stage stage,
                                 final BattlePopupListener listener,
                                 final MBassador <Event> eventBus)
  {
    // @formatter:off
    super (widgetFactory,
           PopupStyle.builder ()
                   .windowStyle ("battle-popup")
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
    Arguments.checkIsNotNull (listener, "listener");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.widgetFactory = widgetFactory;
    this.listener = listener;

    gameRules = new ClassicGameRules.Builder ().build ();

    attackingPlayerNameLabel = widgetFactory.createBattlePopupPlayerNameLabel ();
    defendingPlayerNameLabel = widgetFactory.createBattlePopupPlayerNameLabel ();
    attackingCountryNameLabel = widgetFactory.createBattlePopupCountryNameLabel ();
    defendingCountryNameLabel = widgetFactory.createBattlePopupCountryNameLabel ();
    battlingArrowLabel = widgetFactory.createBattlePopupBattlingArrowLabel ();
    attackerDice = diceFactory.createAttackerDice (gameRules);
    defenderDice = diceFactory.createDefenderDice (gameRules);
    diceArrows = widgetFactory.createBattlePopupDiceArrows (gameRules);
    attackingCountryArmyTextActor = widgetFactory.createCountryArmyTextActor ();
    attackingCountryArmyTextEffectsActor = widgetFactory.createAttackingCountryArmyTextEffectsActor ();
    defendingCountryArmyTextActor = widgetFactory.createCountryArmyTextActor ();
    defendingCountryArmyTextEffectsActor = widgetFactory.createDefendingCountryArmyTextEffectsActor ();

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

    final Table diceArrowsTable = new Table ().top ().padTop (13);
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
    centerTable.add (battlingArrowLabel).padRight (8).padTop (15 - 4).padBottom (50 + 4);
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
    super.hide ();

    battleTask.cancel ();
    resetBattleTask.cancel ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void update (final float delta)
  {
    super.update (delta);

    if (!isShown ()) return;

    attackerDice.update (delta);
    defenderDice.update (delta);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void refreshAssets ()
  {
    super.refreshAssets ();

    attackingPlayerNameLabel.setStyle (widgetFactory.createBattlePopupPlayerNameLabelStyle ());
    defendingPlayerNameLabel.setStyle (widgetFactory.createBattlePopupPlayerNameLabelStyle ());
    attackingCountryNameLabel.setStyle (widgetFactory.createBattlePopupCountryNameLabelStyle ());
    defendingCountryNameLabel.setStyle (widgetFactory.createBattlePopupCountryNameLabelStyle ());
    battlingArrowLabel.setStyle (widgetFactory.createBattlePopupBattlingArrowLabelStyle ());
    attackerDice.refreshAssets ();
    defenderDice.refreshAssets ();
    diceArrows.refreshAssets ();
    attackingCountryArmyTextActor.setFont (widgetFactory.createCountryArmyTextActorFont ());
    defendingCountryArmyTextActor.setFont (widgetFactory.createCountryArmyTextActorFont ());
    attackingCountryArmyTextEffectsActor.setFont (widgetFactory.createCountryArmyTextEffectsActorFont ());
    defendingCountryArmyTextEffectsActor.setFont (widgetFactory.createCountryArmyTextEffectsActorFont ());
  }

  public final void show (final CountryActor attackingCountryActor,
                          final CountryActor defendingCountryActor,
                          final String attackingPlayerName,
                          final String defendingPlayerName)
  {
    Arguments.checkIsNotNull (attackingCountryActor, "attackingCountryActor");
    Arguments.checkIsNotNull (defendingCountryActor, "defendingCountryActor");
    Arguments.checkIsNotNull (attackingPlayerName, "attackingPlayerName");
    Arguments.checkIsNotNull (defendingPlayerName, "defendingPlayerName");

    if (isShown ()) return;

    final int attackingCountryArmies = attackingCountryActor.getArmies ();
    final int defendingCountryArmies = defendingCountryActor.getArmies ();

    setCountryActors (attackingCountryActor, defendingCountryActor);
    initializeCountryArmies (attackingCountryArmies, defendingCountryArmies);
    setPlayerNames (attackingPlayerName, defendingPlayerName);
    initializeDice (attackingCountryArmies, defendingCountryArmies);
    show ();
  }

  public final void rollAttackerDice (final ImmutableList <DieFaceValue> dieFaceValues)
  {
    Arguments.checkIsNotNull (dieFaceValues, "dieFaceValues");
    Arguments.checkHasNoNullElements (dieFaceValues, "dieFaceValues");

    attackerDice.roll (dieFaceValues);
  }

  public final void rollDefenderDice (final ImmutableList <DieFaceValue> dieFaceValues)
  {
    Arguments.checkIsNotNull (dieFaceValues, "dieFaceValues");
    Arguments.checkHasNoNullElements (dieFaceValues, "dieFaceValues");

    defenderDice.roll (dieFaceValues);
  }

  public final BattleOutcome determineOutcome (final ImmutableList <DieFaceValue> attackerDieFaceValues,
                                               final ImmutableList <DieFaceValue> defenderDieFaceValues)
  {
    Arguments.checkIsNotNull (attackerDieFaceValues, "attackerDieFaceValues");
    Arguments.checkIsNotNull (defenderDieFaceValues, "defenderDieFaceValues");
    Arguments.checkHasNoNullElements (attackerDieFaceValues, "attackerDieFaceValues");
    Arguments.checkHasNoNullElements (defenderDieFaceValues, "defenderDieFaceValues");

    attackerDice.setOutcomeAgainst (defenderDieFaceValues);
    defenderDice.setOutcomeAgainst (attackerDieFaceValues);
    diceArrows.setOutcomes (attackerDice.getOutcomes (), defenderDice.getOutcomes ());

    final int attackingCountryArmiesDelta = -Math.min (attackerDice.getLosingCount (), defenderDice.getWinningCount ());
    final int defendingCountryArmiesDelta = -Math.min (attackerDice.getWinningCount (), defenderDice.getLosingCount ());

    changeCountryArmiesBy (attackingCountryArmiesDelta, defendingCountryArmiesDelta);
    setDiceTouchable (false);
    resetBattle ();

    return outcome.set (getAttackingCountryName (), attackingCountryArmiesDelta, getAttackingPlayerName (),
                        getDefendingCountryName (), defendingCountryArmiesDelta, getDefendingPlayerName ());
  }

  public final String getAttackingCountryName ()
  {
    return attackingCountryNameLabel.getText ().toString ();
  }

  public final String getDefendingCountryName ()
  {
    return defendingCountryNameLabel.getText ().toString ();
  }

  public final String getAttackingPlayerName ()
  {
    return attackingPlayerNameLabel.getText ().toString ();
  }

  public final String getDefendingPlayerName ()
  {
    return defendingPlayerNameLabel.getText ().toString ();
  }

  public final int getActiveAttackerDieCount ()
  {
    return attackerDice.getActiveCount ();
  }

  public final int getActiveDefenderDieCount ()
  {
    return defenderDice.getActiveCount ();
  }

  public final int getAttackingCountryArmies ()
  {
    return attackingCountryArmyTextActor.getArmies ();
  }

  public final int getDefendingCountryArmies ()
  {
    return defendingCountryArmyTextActor.getArmies ();
  }

  public final CountryActor getAttackingCountryActor ()
  {
    return attackingCountryActor;
  }

  public final CountryActor getDefendingCountryActor ()
  {
    return defendingCountryActor;
  }

  public void startBattle ()
  {
    if (battleTask.isScheduled ()) return;

    setDiceTouchable (false);

    battleTask = Timer.schedule (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        listener.onBattle ();
      }
    }, GameSettings.INITIAL_BATTLE_DELAY_SECONDS, GameSettings.BATTLE_INTERVAL_SECONDS);
  }

  public void stopBattle ()
  {
    if (!battleTask.isScheduled ()) return;

    battleTask.cancel ();
    setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE);
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

  // TODO Production: Move to common::GameRules.
  private static boolean canBattle (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    return attackerCanBattle (attackingCountryArmies) && defenderCanBattle (defendingCountryArmies);
  }

  // TODO Production: Move to common::GameRules.
  private static boolean attackerCanBattle (final int attackingCountryArmies)
  {
    return attackingCountryArmies > 1;
  }

  // TODO Production: Move to common::GameRules.
  private static boolean defenderCanBattle (final int defendingCountryArmies)
  {
    return defendingCountryArmies > 0;
  }

  private static Image asImage (final CountryActor countryActor)
  {
    return new Image (countryActor.getCurrentPrimaryDrawable (), Scaling.none);
  }

  private void resetBattle ()
  {
    if (resetBattleTask.isScheduled ()) return;

    resetBattleTask = Timer.schedule (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        final int attackingCountryArmies = getAttackingCountryArmies ();
        final int defendingCountryArmies = getDefendingCountryArmies ();

        if (canBattle (attackingCountryArmies, defendingCountryArmies))
        {
          resetDieFaces ();
          resetDieOutcomes ();
          resetDiceArrows ();
          resetDiceSpinning ();
          clampDice (attackingCountryArmies, defendingCountryArmies);
        }
        else if (!attackerCanBattle (attackingCountryArmies))
        {
          listener.onAttackerLoseFinal ();
        }
        else if (!defenderCanBattle (defendingCountryArmies))
        {
          listener.onAttackerWinFinal ();
        }

        updateDiceTouchability (attackingCountryArmies, defendingCountryArmies);
      }
    }, GameSettings.BATTLE_OUTCOME_VIEWING_TIME_SECONDS);
  }

  private void initializeDice (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    resetDice ();
    clampDice (attackingCountryArmies, defendingCountryArmies);
    updateDiceTouchability (attackingCountryArmies, defendingCountryArmies);
    resetDiceArrows ();
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

  private void updateDiceTouchability (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    setDiceTouchable (GameSettings.CAN_ADD_REMOVE_DICE_IN_BATTLE
            && canBattle (attackingCountryArmies, defendingCountryArmies));
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
    attackingCountryArmyTextActor.changeArmiesBy (attackingCountryArmiesDelta);
    defendingCountryArmyTextActor.changeArmiesBy (defendingCountryArmiesDelta);

    attackingCountryArmyTextEffectsActor.changeArmiesBy (attackingCountryArmiesDelta);
    defendingCountryArmyTextEffectsActor.changeArmiesBy (defendingCountryArmiesDelta);
  }

  private void setCountryActors (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    this.attackingCountryActor = attackingCountryActor;
    this.defendingCountryActor = defendingCountryActor;

    setCountryNames (attackingCountryActor, defendingCountryActor);
    setCountryImages (attackingCountryActor, defendingCountryActor);
  }

  private void initializeCountryArmies (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    attackingCountryArmyTextEffectsActor.asActor ().setVisible (false);
    defendingCountryArmyTextEffectsActor.asActor ().setVisible (false);

    attackingCountryArmyTextActor.changeArmiesTo (attackingCountryArmies);
    defendingCountryArmyTextActor.changeArmiesTo (defendingCountryArmies);
  }

  private void setCountryNames (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryNames (attackingCountryActor.asActor ().getName (), defendingCountryActor.asActor ().getName ());
  }

  private void setCountryImages (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryImage (attackingCountryActor, attackingCountryArmyTextActor, attackingCountryArmyTextEffectsActor,
                     attackingCountryStack);
    setCountryImage (defendingCountryActor, defendingCountryArmyTextActor, defendingCountryArmyTextEffectsActor,
                     defendingCountryStack);
  }

  private void setCountryImage (final CountryActor countryActor,
                                final CountryArmyTextActor countryArmyTextActor,
                                final CountryArmyTextActor countryArmyTextEffectsActor,
                                final Stack countryStack)
  {
    final Image countryImage = asImage (countryActor);

    countryStack.clear ();
    countryStack.add (countryImage);
    countryStack.add (countryArmyTextActor.asActor ());
    countryStack.add (countryArmyTextEffectsActor.asActor ());

    getContentTable ().layout ();

    updateCountryArmyCircle (countryArmyTextActor, countryActor, countryImage);
    updateCountryArmyCircle (countryArmyTextEffectsActor, countryActor, countryImage);
  }

  private void updateCountryArmyCircle (final CountryArmyTextActor countryArmyTextActor,
                                        final CountryActor countryActor,
                                        final Image countryImage)
  {
    setCountryArmyCircleSize (countryArmyTextActor, countryActor, countryImage);
    setCountryArmyCirclePosition (countryArmyTextActor, countryActor, countryImage);
  }

  private void setCountryArmyCircleSize (final CountryArmyTextActor countryArmyTextActor,
                                         final CountryActor countryActor,
                                         final Image countryImage)
  {
    countryArmyTextActor
            .setCircleSize (calculateCountryArmyTextCircleSizeActualCountrySpace (countryActor, countryImage));
  }

  private void setCountryArmyCirclePosition (final CountryArmyTextActor countryArmyTextActor,
                                             final CountryActor countryActor,
                                             final Image countryImage)
  {
    countryArmyTextActor
            .setCircleTopLeft (calculateCountryArmyTextCircleTopLeftActualCountrySpace (countryActor, countryImage));
  }

  private Vector2 calculateCountryArmyTextCircleTopLeftActualCountrySpace (final CountryActor countryActor,
                                                                           final Image countryImagePostLayout)
  {
    return tempPosition.set (countryActor.getReferenceTextUpperLeft ()).sub (countryActor.getReferenceDestination ())
            .set (Math.abs (tempPosition.x), Math.abs (tempPosition.y))
            .scl (calculateCountryImageScaling (countryActor, countryImagePostLayout))
            .add (countryImagePostLayout.getImageX (), countryImagePostLayout.getImageY ());
  }

  private Vector2 calculateCountryArmyTextCircleSizeActualCountrySpace (final CountryActor countryActor,
                                                                        final Image countryImagePostLayout)
  {
    return tempSize.set (PlayMapSettings.COUNTRY_ARMY_CIRCLE_SIZE_REFERENCE_PLAY_MAP_SPACE)
            .scl (calculateCountryImageScaling (countryActor, countryImagePostLayout));
  }

  private Vector2 calculateCountryImageScaling (final CountryActor countryActor, final Image countryImagePostLayout)
  {
    return tempScaling.set (countryImagePostLayout.getImageWidth () / countryActor.getReferenceWidth (),
                            countryImagePostLayout.getImageHeight () / countryActor.getReferenceHeight ());
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
