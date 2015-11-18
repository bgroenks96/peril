package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.settings.ScreenSettings;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.CountryArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.DefaultCountryArmyTextActor;
import com.forerunnergames.peril.client.ui.widgets.popup.KeyListener;
import com.forerunnergames.peril.client.ui.widgets.popup.OkPopup;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupStyle;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public final class BattlePopup extends OkPopup
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
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final BattlePopupListener listener;
  private final GameRules gameRules;
  private final Vector2 tempPosition = new Vector2 ();
  private final Vector2 tempScaling = new Vector2 ();
  private final Vector2 tempSize = new Vector2 ();
  private final BitmapFont countryArmyTextFont = new BitmapFont ();
  private final CountryArmyTextActor attackingCountryArmyTextActor = new DefaultCountryArmyTextActor (countryArmyTextFont);
  private final CountryArmyTextActor defendingCountryArmyTextActor = new DefaultCountryArmyTextActor (countryArmyTextFont);
  private final Label attackingPlayerNameLabel;
  private final Label defendingPlayerNameLabel;
  private final Label attackingCountryNameLabel;
  private final Label defendingCountryNameLabel;
  private final Label attackingArrowLabel;
  private final Dice attackerDice;
  private final Dice defenderDice;
  private final Stack attackingCountryStack;
  private final Stack defendingCountryStack;
  private Label autoAttackLabel;
  private Button autoAttackButton;
  private Button attackButton;
  private Button retreatButton;
  @Nullable
  private Timer.Task autoAttackTask;
  private Cell <Label> autoAttackLabelCell;
  // @formatter:on

  public BattlePopup (final ClassicModePlayScreenWidgetFactory widgetFactory,
                      final String title,
                      final Stage stage,
                      final BattlePopupListener listener,
                      final MBassador <Event> eventBus)
  {
    // @formatter:off
    super (widgetFactory,
           PopupStyle.builder ()
                   .windowStyle ("battle")
                   .resizable (false)
                   .movable (true)
                   .size (990, 432)
                   .position (405, ScreenSettings.REFERENCE_SCREEN_HEIGHT - 178)
                   .title (title)
                   .titleHeight (56)
                   .messageBox (false)
                   .border (28)
                   .buttonSize (90, 32)
                   .buttonSpacing (16)
                   .textButtonStyle ("popup")
                   .debug (DEBUG)
                   .build (),
           stage, listener);
    // @formatter:on

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
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
    autoAttackLabel = widgetFactory.createBattlePopupAutoAttackLabel ();
    attackingArrowLabel = widgetFactory.createBattlePopupArrowLabel ();
    attackerDice = widgetFactory.createAttackPopupAttackerDice ();
    defenderDice = widgetFactory.createAttackPopupDefenderDice ();

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
    diceTable.add (attackerDice.asActor ()).spaceRight (34).top ();
    diceTable.add (defenderDice.asActor ()).top ();
    diceTable.setDebug (DEBUG, true);

    final Table leftTable = new Table ().top ().pad (2);
    leftTable.add (attackingPlayerNameLabel).size (PLAYER_NAME_BOX_WIDTH, PLAYER_NAME_BOX_HEIGHT).space (2);
    leftTable.row ();
    leftTable.add (attackingCountryTable).size (COUNTRY_BOX_WIDTH, COUNTRY_BOX_HEIGHT)
            .padBottom (COUNTRY_BOX_INNER_PADDING + 3).space (2);
    leftTable.row ();
    leftTable.add (attackingCountryNameLabel).size (COUNTRY_NAME_BOX_WIDTH, COUNTRY_NAME_BOX_HEIGHT).space (2);
    leftTable.setDebug (DEBUG, true);

    final Table centerTable = new Table ().top ().padTop (2).padBottom (2);
    centerTable.add (attackingArrowLabel).padRight (8).padTop (15 - 4).padBottom (50 + 4);
    centerTable.row ();
    centerTable.add (diceTable).size (94, 116);
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

    autoAttackLabelCell.setActor (autoAttackLabel);
  }

  @Override
  public void update (final float delta)
  {
    super.update (delta);
  }

  @Override
  public void refreshAssets ()
  {
    super.refreshAssets ();

    attackingPlayerNameLabel.setStyle (widgetFactory.createBattlePopupPlayerNameLabelStyle ());
    defendingPlayerNameLabel.setStyle (widgetFactory.createBattlePopupPlayerNameLabelStyle ());
    attackingCountryNameLabel.setStyle (widgetFactory.createBattlePopupCountryNameLabelStyle ());
    defendingCountryNameLabel.setStyle (widgetFactory.createBattlePopupCountryNameLabelStyle ());
    autoAttackLabel.setStyle (widgetFactory.createBattlePopupAutoAttackLabelStyle ());
    attackingArrowLabel.setStyle (widgetFactory.createBattlePopupArrowLabelStyle ());
    attackerDice.refreshAssets ();
    defenderDice.refreshAssets ();
  }

  @Override
  protected void addButtons ()
  {
    autoAttackLabelCell = getButtonTable ().add ((Label) null).size (126, 32);

    autoAttackButton = addButton ("toggle", PopupAction.NONE, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        final boolean isAutoAttack = autoAttackButton.isChecked ();
        attackButton.setDisabled (isAutoAttack);

        if (isAutoAttack)
        {
          startAutoAttack ();
        }
        else
        {
          stopAutoAttack ();
        }

        listener.onToggleAutoAttack (isAutoAttack);
      }
    });

    retreatButton = addTextButton ("Retreat", PopupAction.HIDE, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        stopAutoAttack ();
        listener.onRetreat (getAttackingCountryName (), getDefendingCountryName ());
      }
    });

    attackButton = addTextButton ("Attack", PopupAction.NONE, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        listener.onAttack (getAttackingCountryName (), getDefendingCountryName ());
      }
    });
  }

  @Override
  protected void addKeys ()
  {
    addKey (Input.Keys.ESCAPE, PopupAction.NONE, new KeyListener ()
    {
      @Override
      public void keyDown ()
      {
        retreatButton.toggle ();
      }
    });

    addKey (Input.Keys.ENTER, PopupAction.NONE, new KeyListener ()
    {
      @Override
      public void keyDown ()
      {
        if (autoAttackButton.isChecked ()) return;

        attackButton.toggle ();
      }
    });
  }

  public void show (final CountryActor attackingCountryActor,
                    final CountryActor defendingCountryActor,
                    final String attackingPlayerName,
                    final String defendingPlayerName,
                    final int attackingCountryArmies,
                    final int defendingCountryArmies)
  {
    Arguments.checkIsNotNull (attackingCountryActor, "attackingCountryActor");
    Arguments.checkIsNotNull (defendingCountryActor, "defendingCountryActor");
    Arguments.checkIsNotNull (attackingPlayerName, "attackingPlayerName");
    Arguments.checkIsNotNull (defendingPlayerName, "defendingPlayerName");
    Arguments.checkIsNotNegative (attackingCountryArmies, "attackingCountryArmies");
    Arguments.checkIsNotNegative (defendingCountryArmies, "defendingCountryArmies");

    if (isShown ()) return;

    turnOffAutoAttack ();
    setAutoAttack (attackingCountryArmies);
    setAttackButton (attackingCountryArmies);
    setCountryActors (attackingCountryActor, defendingCountryActor);
    setCountryArmies (attackingCountryArmies, defendingCountryArmies);
    setPlayerNames (attackingPlayerName, defendingPlayerName);
    setDice (attackingCountryArmies, defendingCountryArmies);
    show ();
  }

  public void setAutoAttack (final int attackingCountryArmies)
  {
    autoAttackButton.setTouchable (attackingCountryArmies < 2 ? Touchable.disabled : Touchable.enabled);
  }

  public void setAttackButton (final int attackingCountryArmies)
  {
    attackButton.setDisabled (attackingCountryArmies < 2);
  }

  public void rollAttackerDice (final ImmutableList <DieFaceValue> dieFaceValues)
  {
    attackerDice.roll (dieFaceValues);
  }

  public void rollDefenderDice (final ImmutableList <DieFaceValue> dieFaceValues)
  {
    defenderDice.roll (dieFaceValues);
  }

  public String getAttackingCountryName ()
  {
    return attackingCountryNameLabel.getText ().toString ();
  }

  public String getDefendingCountryName ()
  {
    return defendingCountryNameLabel.getText ().toString ();
  }

  public String getAttackingPlayerName ()
  {
    return attackingPlayerNameLabel.getText ().toString ();
  }

  public String getDefendingPlayerName ()
  {
    return defendingPlayerNameLabel.getText ().toString ();
  }

  public int getActiveAttackerDieCount ()
  {
    return attackerDice.getActiveCount ();
  }

  public int getActiveDefenderDieCount ()
  {
    return defenderDice.getActiveCount ();
  }

  private static Image asImage (final CountryActor countryActor)
  {
    return new Image (countryActor.getCurrentPrimaryDrawable (), Scaling.none);
  }

  private void setDice (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    attackerDice.clampToMax (gameRules.getMinAttackerDieCount (attackingCountryArmies),
                             gameRules.getMaxAttackerDieCount (attackingCountryArmies));

    defenderDice.clampToMax (gameRules.getMinDefenderDieCount (defendingCountryArmies),
                             gameRules.getMaxDefenderDieCount (defendingCountryArmies));
  }

  private void startAutoAttack ()
  {
    if (!autoAttackButton.isChecked ()) return;
    if (autoAttackTask != null && autoAttackTask.isScheduled ()) return;

    autoAttackTask = Timer.schedule (new Timer.Task ()
    {
      @Override
      public void run ()
      {
        attackButton.toggle ();
      }
    }, 0.0f, GameSettings.AUTO_ATTACK_SPEED_SECONDS);
  }

  private void stopAutoAttack ()
  {
    if (autoAttackTask == null) return;

    autoAttackTask.cancel ();
  }

  private void turnOffAutoAttack ()
  {
    autoAttackButton.setChecked (false);
  }

  private void setCountryArmies (final int attackingCountryArmies, final int defendingCountryArmies)
  {
    setAttackingCountryArmies (attackingCountryArmies);
    setDefendingCountryArmies (defendingCountryArmies);
  }

  private void setAttackingCountryArmies (final int armies)
  {
    attackingCountryArmyTextActor.setArmies (armies);
  }

  private void setDefendingCountryArmies (final int armies)
  {
    defendingCountryArmyTextActor.setArmies (armies);
  }

  private void setCountryActors (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryNames (attackingCountryActor, defendingCountryActor);
    setCountryImages (attackingCountryActor, defendingCountryActor);
  }

  private void setCountryNames (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryNames (attackingCountryActor.asActor ().getName (), defendingCountryActor.asActor ().getName ());
  }

  private void setCountryImages (final CountryActor attackingCountryActor, final CountryActor defendingCountryActor)
  {
    setCountryImage (attackingCountryActor, attackingCountryArmyTextActor, attackingCountryStack);
    setCountryImage (defendingCountryActor, defendingCountryArmyTextActor, defendingCountryStack);
  }

  private void setCountryImage (final CountryActor countryActor,
                                final CountryArmyTextActor countryArmyTextActor,
                                final Stack countryStack)
  {
    final Image countryImage = asImage (countryActor);

    countryStack.clear ();
    countryStack.add (countryImage);
    countryStack.add (countryArmyTextActor.asActor ());

    getContentTable ().layout ();

    updateCountryArmyCircle (countryArmyTextActor, countryActor, countryImage);
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
