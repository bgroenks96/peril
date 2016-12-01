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

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.UnloadPlayMapRequestEvent;
import com.forerunnergames.peril.client.events.UnloadPlayScreenAssetsRequestEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.AbstractScreen;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox.ControlRoomBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugEventGenerator;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DefaultDebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.CompositePlayScreenDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.DefaultPlayScreenCancellableDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.DefaultPlayScreenDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.PlayScreenCancellableDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.PlayScreenDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification.FortificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.AbstractBattleDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.AttackerBattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.DefenderBattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.notification.NotificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.quit.PlayScreenQuitDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox.IntelBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.personbox.PersonBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.AttackingBattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.BattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.CompositeGamePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.DefendingBattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.FortificationPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.GamePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ManualCountryAssignmentPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.OccupationPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ReinforcementDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ReinforcementPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.status.StatusMessageGenerator;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CompositeDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerCardsChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.SpectatorJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassicModePlayScreen extends AbstractScreen
{
  private static final Logger log = LoggerFactory.getLogger (ClassicModePlayScreen.class);
  private static final boolean DEBUG = false;
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final DebugEventGenerator debugEventGenerator;
  private final Image playMapTableForegroundImage;
  private final MessageBox <StatusBoxRow> statusBox;
  private final MessageBox <ChatBoxRow> chatBox;
  private final PersonBox personBox;
  private final IntelBox intelBox;
  private final ControlRoomBox controlRoomBox;
  private final Cell <Actor> playMapCell;
  private final AtomicBoolean isGameInProgress = new AtomicBoolean ();
  private final AtomicBoolean isSpectating = new AtomicBoolean ();
  private final Vector2 tempPosition = new Vector2 ();
  private final NotificationDialog notificationDialog;
  private final GamePhaseHandler reinforcementPhaseHandler;
  private final GamePhaseHandler manualCountryAssignmentPhaseHandler;
  private final GamePhaseHandler occupationPhaseHandler;
  private final GamePhaseHandler fortificationPhaseHandler;
  private final StatusMessageGenerator statusMessageGenerator;
  private final CompositeGamePhaseHandler gamePhaseHandlers = new CompositeGamePhaseHandler ();
  private final CompositeDialog allDialogs = new CompositeDialog ();
  private final CompositePlayScreenDialogListener allDialogListeners = new CompositePlayScreenDialogListener ();
  private PlayMap playMap = PlayMap.NULL;
  private BattlePhaseHandler attackingBattlePhaseHandler = BattlePhaseHandler.NULL;
  private BattlePhaseHandler defendingBattlePhaseHandler = BattlePhaseHandler.NULL;
  private DebugInputProcessor debugInputProcessor = DebugInputProcessor.NULL;
  @Nullable
  private PlayerCardTradeInAvailableEvent tradeInEvent; // TODO Production: Remove.

  public ClassicModePlayScreen (final ClassicModePlayScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize,
                                final MouseInput mouseInput,
                                final Batch batch,
                                final MBassador <Event> eventBus,
                                final DebugEventGenerator debugEventGenerator)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (debugEventGenerator, "debugEventGenerator");

    this.widgetFactory = widgetFactory;
    this.debugEventGenerator = debugEventGenerator;

    playMapTableForegroundImage = widgetFactory.createPlayMapTableForegroundImage ();
    statusBox = widgetFactory.createStatusBox ();
    chatBox = widgetFactory.createChatBox (eventBus);
    personBox = widgetFactory.createPersonBox ();

    intelBox = widgetFactory.createIntelBox (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        // TODO Implement detailed report button.

        log.debug ("Clicked detailed report button");
      }
    });

    controlRoomBox = widgetFactory.createControlRoomBox (new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        // TODO Implement TradeInDialog & TradeInPhaseHandler.

        log.debug ("Clicked trade-in button");

        if (tradeInEvent == null) return; // TODO Production: Remove.

        publish (new PlayerTradeInCardsRequestEvent (tradeInEvent.getMatches ().iterator ().next ()));
        controlRoomBox.disableButton (ControlRoomBox.Button.TRADE_IN);
        tradeInEvent = null; // TODO Production: Remove.
      }
    }, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        log.debug ("Clicked fortify button");

        attackingBattlePhaseHandler.onEndBattlePhase ();
        controlRoomBox.disableButton (ControlRoomBox.Button.FORTIFY);
        controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
      }
    }, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        log.debug ("Clicked end turn button");

        publish (new EndPlayerTurnRequestEvent ());
        controlRoomBox.disableButton (ControlRoomBox.Button.FORTIFY);
        controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
      }
    }, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        log.debug ("Clicked my settings button");

        // TODO Implement My Settings button.
      }
    }, new ChangeListener ()
    {
      @Override
      public void changed (final ChangeEvent event, final Actor actor)
      {
        log.debug ("Clicked quit button");
        final PlayScreenQuitDialog quitDialog = allDialogs.get (PlayScreenQuitDialog.class);
        quitDialog.show (isGameInProgress.get (), isSpectating.get ());
      }
    });

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.setDebug (DEBUG, true);

    final Stack playMapTableStack = new Stack ();
    final Table playMapTable = new Table ().top ().left ().pad (4);
    playMapCell = playMapTable.add (playMap.asActor ()).expand ().fill ();
    playMapTableStack.add (playMapTable);
    playMapTableStack.add (playMapTableForegroundImage);
    playMapTableStack.setDebug (DEBUG, true);
    playMapTable.setDebug (DEBUG, true);

    final Table sideBarTable = new Table ().top ().left ();
    sideBarTable.add (intelBox.asActor ()).size (296, 484).spaceBottom (6).fill ();
    sideBarTable.row ();
    sideBarTable.add (controlRoomBox.asActor ()).size (296, 318).spaceTop (6).fill ();
    sideBarTable.setDebug (DEBUG, true);

    // @formatter:off

    final Table topTable = new Table ().top ().left ();
    topTable.add (playMapTableStack).size (PlayMapSettings.ACTUAL_WIDTH + 8, PlayMapSettings.ACTUAL_HEIGHT + 8).spaceRight (6).fill ();
    topTable.add (sideBarTable).spaceLeft (6).size (296, PlayMapSettings.ACTUAL_HEIGHT + 8).fill ();
    topTable.setDebug (DEBUG, true);

    // @formatter:on

    final Table bottomTable = new Table ().top ().left ();
    bottomTable.add (statusBox.asActor ()).size (700, 256).spaceRight (6).fill ();
    bottomTable.add (chatBox.asActor ()).size (700, 256).spaceLeft (6).spaceRight (6).fill ();
    bottomTable.add (personBox.asActor ()).size (498, 256).spaceLeft (6).fill ();
    bottomTable.setDebug (DEBUG, true);

    final Table screenTable = new Table ().top ().left ().pad (5);
    screenTable.add (topTable).height (808).left ().spaceBottom (6).fill ();
    screenTable.row ().spaceTop (6);
    screenTable.add (bottomTable).height (256).fill ();
    screenTable.setDebug (DEBUG, true);

    rootStack.add (screenTable);
    addRootActor (rootStack);

    // @formatter:off

    statusMessageGenerator = new StatusMessageGenerator (statusBox, widgetFactory);

    final BattleDialogListener attackDialogListener = new DefaultAttackDialogListener (allDialogs, playMap, mouseInput);
    final BattleDialogListener defendDialogListener = new DefaultDefendDialogListener (allDialogs, playMap, mouseInput);
    final PlayScreenDialogListener attackerBattleResultDialogListener = new AttackerBattleResultDialogListener (allDialogs, playMap, mouseInput);
    final PlayScreenDialogListener defenderBattleResultDialogListener = new DefenderBattleResultDialogListener (allDialogs, playMap, mouseInput);
    final PlayScreenDialogListener occupationDialogListener = new OccupationDialogListener (allDialogs, playMap, mouseInput);
    final PlayScreenCancellableDialogListener fortificationDialogListener = new FortificationDialogListener (allDialogs, playMap, mouseInput);
    final PlayScreenDialogListener notificationDialogListener = new DefaultPlayScreenDialogListener (allDialogs, mouseInput, playMap);
    final PlayScreenCancellableDialogListener quitDialogListener = new QuitDialogListener (allDialogs, playMap, mouseInput);
    allDialogListeners.add (attackDialogListener, defendDialogListener, attackerBattleResultDialogListener, defenderBattleResultDialogListener,
            occupationDialogListener, fortificationDialogListener, notificationDialogListener, quitDialogListener);

    final Dialog attackerBattleResultDialog = widgetFactory.createAttackerBattleResultDialog (getStage (), attackerBattleResultDialogListener);
    final Dialog defenderBattleResultDialog = widgetFactory.createDefenderBattleResultDialog (getStage (), defenderBattleResultDialogListener);
    final OccupationDialog occupationDialog = widgetFactory.createOccupationDialog (getStage (), occupationDialogListener);
    final FortificationDialog fortificationDialog = widgetFactory.createFortificationDialog (getStage (), fortificationDialogListener);
    final ReinforcementDialog reinforcementDialog = widgetFactory.createReinforcementDialog (getStage (), personBox, new ReinforcementDialogListener ());
    final Dialog quitDialog = widgetFactory.createQuitDialog ("", getStage (), quitDialogListener);
    notificationDialog = widgetFactory.createNotificationDialog (widgetFactory, getStage (), notificationDialogListener);
    allDialogs.add (attackerBattleResultDialog, defenderBattleResultDialog, occupationDialog, fortificationDialog, reinforcementDialog,
            notificationDialog, quitDialog);

    reinforcementPhaseHandler = new ReinforcementPhaseHandler (playMap, reinforcementDialog, eventBus);
    manualCountryAssignmentPhaseHandler = new ManualCountryAssignmentPhaseHandler (playMap, eventBus);
    occupationPhaseHandler = new OccupationPhaseHandler (playMap, occupationDialog, eventBus);
    fortificationPhaseHandler = new FortificationPhaseHandler (playMap, fortificationDialog, eventBus);
    gamePhaseHandlers.add (reinforcementPhaseHandler, manualCountryAssignmentPhaseHandler, occupationPhaseHandler, fortificationPhaseHandler);

    // @formatter:on
  }

  @Override
  public void show ()
  {
    super.show ();

    subscribe (statusMessageGenerator);

    playMapTableForegroundImage.setDrawable (widgetFactory.createPlayMapTableForegroundImageDrawable ());

    intelBox.refreshAssets ();
    controlRoomBox.refreshAssets ();
    statusBox.refreshAssets ();
    chatBox.refreshAssets ();
    personBox.refreshAssets ();
    allDialogs.refreshAssets ();

    controlRoomBox.disableButton (ControlRoomBox.Button.TRADE_IN);
    controlRoomBox.disableButton (ControlRoomBox.Button.FORTIFY);
    controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
    controlRoomBox.enableButton (ControlRoomBox.Button.MY_SETTINGS);
    controlRoomBox.enableButton (ControlRoomBox.Button.QUIT);
  }

  @Override
  public void hide ()
  {
    super.hide ();

    unsubscribe (statusMessageGenerator);

    intelBox.clear ();
    chatBox.clear ();
    statusBox.clear ();
    personBox.clear ();

    updatePlayMap (PlayMap.NULL);

    gamePhaseHandlers.deactivate ();
    gamePhaseHandlers.remove (attackingBattlePhaseHandler, defendingBattlePhaseHandler);

    attackingBattlePhaseHandler = BattlePhaseHandler.NULL;
    defendingBattlePhaseHandler = BattlePhaseHandler.NULL;

    allDialogs.hide (null);

    isGameInProgress.set (false);
    isSpectating.set (false);
    controlRoomBox.setButtonText (ControlRoomBox.Button.QUIT,
                                  getQuitButtonText (isGameInProgress.get (), isSpectating.get ()));
    tradeInEvent = null; // TODO Production: Remove.

    debugInputProcessor.reset ();
  }

  @Override
  public void dispose ()
  {
    super.dispose ();
    allDialogs.dispose ();
    allDialogListeners.dispose ();
  }

  @Override
  protected void update (final float delta)
  {
    super.update (delta);
    allDialogs.update (delta);
  }

  @Override
  protected boolean onEscape ()
  {
    if (allDialogs.noneAreShown () || !allDialogs.onlyAreShownOf (CancellableDialog.class))
    {
      controlRoomBox.pressButton (ControlRoomBox.Button.QUIT);
    }

    return false;
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    switch (button)
    {
      case Input.Buttons.LEFT:
      {
        playMap.onLeftButtonDown (tempPosition.set (screenX, screenY));
        break;
      }
      case Input.Buttons.RIGHT:
      {
        playMap.onRightButtonDown (tempPosition.set (screenX, screenY));
        break;
      }
    }

    return false;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    switch (button)
    {
      case Input.Buttons.LEFT:
      {
        playMap.onLeftButtonUp (tempPosition.set (screenX, screenY));
        break;
      }
      case Input.Buttons.RIGHT:
      {
        playMap.onRightButtonUp (tempPosition.set (screenX, screenY));
        break;
      }
    }

    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    playMap.onMouseMoved (tempPosition.set (screenX, screenY));
    return false;
  }

  @Handler
  void onEvent (final PlayGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    // @formatter:off

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        final ScreenShaker screenShaker = new ScreenShaker (getViewport (), getScreenSize ());

        final AttackDialog attackDialog = widgetFactory.createAttackDialog (getStage (), event.getGameRules (),
                screenShaker, allDialogListeners.get (DefaultAttackDialogListener.class));

        final DefendDialog defendDialog = widgetFactory.createDefendDialog (getStage (), event.getGameRules (),
                screenShaker, allDialogListeners.get (DefaultDefendDialogListener.class));

        allDialogs.add (attackDialog, defendDialog);

        attackingBattlePhaseHandler = new AttackingBattlePhaseHandler (playMap, attackDialog,
                allDialogs.get (AttackerBattleResultDialog.class), getEventBus ());

        defendingBattlePhaseHandler = new DefendingBattlePhaseHandler (playMap, defendDialog,
                allDialogs.get (DefenderBattleResultDialog.class), getEventBus ());

        // @formatter:on

        gamePhaseHandlers.add (attackingBattlePhaseHandler, defendingBattlePhaseHandler);

        if (DEBUG)
        {
          debugInputProcessor = new DefaultDebugInputProcessor (debugEventGenerator, widgetFactory, getMouseInput (),
                  playMap, statusBox, chatBox, personBox, allDialogs, getEventBus ());

          addInputProcessor (debugInputProcessor);
        }

        updatePlayMap (event.getPlayMap ());

        intelBox.setGameServerConfiguration (event.getGameServerConfiguration ());
        intelBox.setClientConfiguration (event.getClientConfiguration ());
        intelBox.setOwnedCountriesForSelf (0, event.getSelfPlayer ());
        intelBox.setSelf (event.getSelfPlayer ());
        controlRoomBox.setSelf (event.getSelfPlayer ());
        personBox.setPlayers (event.getAllPlayers ());
        gamePhaseHandlers.setSelfPlayer (event.getSelfPlayer ());
        notificationDialog.setSelf (event.getSelfPlayer ());
        debugEventGenerator.makePlayersUnavailable (event.getAllPlayers ());
      }
    });
  }

  @Handler
  void onEvent (final BeginGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    isGameInProgress.set (true);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.setButtonText (ControlRoomBox.Button.QUIT,
                                      getQuitButtonText (isGameInProgress.get (), isSpectating.get ()));
      }
    });
  }

  @Handler
  void onEvent (final ChatMessageSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        chatBox.addRow (widgetFactory.createChatMessageBoxRow (event.getMessage ()));
        chatBox.showLastRow ();
      }
    });
  }

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (event.hasIdentity (PersonIdentity.SELF)) isSpectating.set (false);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.addPlayer (event.getPerson ());
        debugEventGenerator.makePlayerUnavailable (event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final SpectatorJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (event.hasIdentity (PersonIdentity.SELF)) isSpectating.set (true);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        if (event.hasIdentity (PersonIdentity.SELF)) intelBox.setSelf (event.getPerson ());
        controlRoomBox.setButtonTextForSelf (ControlRoomBox.Button.QUIT, event.getPerson (),
                                             getQuitButtonText (isGameInProgress.get (), isSpectating.get ()));
        notificationDialog.setTitleForSelf (event.getPerson (), "Spectating");
        notificationDialog.showForSelf (event.getPerson (), "Welcome, {}.\nYou are now spectating this game.",
                                        event.getPersonName ());
        personBox.addSpectator (event.getPerson ());
        debugEventGenerator.makePlayerNameUnavailable (event.getPersonName ());
      }
    });
  }

  @Handler
  void onEvent (final SpectatorJoinGameDeniedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        notificationDialog.setTitle ("Warning");
        notificationDialog.show (getDenialMessage (event));
      }
    });
  }

  @Handler
  void onEvent (final ActivePlayerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.updateExisting (event.getPerson ());
        personBox.highlightPlayer (event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final CountryOwnerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.addOwnedCountryForSelf (event.getNewOwner ());
        intelBox.removeOwnedCountryForSelf (event.getPreviousOwner ());
        playMap.setCountryState (event.getCountryName (),
                                 event.hasNewOwner ()
                                         ? CountryPrimaryImageState.fromPlayerColor (event.getNewOwnerColor ())
                                         : CountryPrimaryImageState.UNOWNED);
      }
    });
  }

  @Handler
  void onEvent (final CountryArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.setArmies (event.getCountryArmyCount (), event.getCountryName ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.updateExisting (event.getPerson ());
        gamePhaseHandlers.updatePlayerForSelf (event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerCardsChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.updateExisting (event.getPerson ());
        gamePhaseHandlers.updatePlayerForSelf (event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerTurnOrderChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.updatePlayerWithNewTurnOrder (event.getPerson (), event.getOldTurnOrder ());
        gamePhaseHandlers.updatePlayerForSelf (event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Turn Order");
        personBox.setPlayers (event.getPlayersSortedByTurnOrder ());
      }
    });
  }

  @Handler
  void onEvent (final BeginPlayerCountryAssignmentEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (event.assignmentModeIs (InitialCountryAssignment.MANUAL)) manualCountryAssignmentPhaseHandler.activate ();

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName (Strings.format ("{} Country Assignment",
                                                   Strings.toProperCase (String.valueOf (event.getAssignmentMode ()))));
      }
    });
  }

  @Handler
  void onEvent (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (event.assignmentModeIs (InitialCountryAssignment.MANUAL)) manualCountryAssignmentPhaseHandler.deactivate ();
  }

  @Handler
  void onEvent (final BeginInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    reinforcementPhaseHandler.activate ();

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Initial Reinforcement");
      }
    });
  }

  @Handler
  void onEvent (final EndInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    reinforcementPhaseHandler.deactivate ();
  }

  @Handler
  void onEvent (final BeginRoundEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGameRound (event.getRound ());
      }
    });
  }

  @Handler
  void onEvent (final BeginReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    reinforcementPhaseHandler.activateForSelf (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Reinforcement");
        notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
        notificationDialog.showForSelf (event.getPerson (), "It is your turn, sir. What are your orders?");
      }
    });
  }

  @Handler
  void onEvent (final PlayerCardTradeInAvailableEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        tradeInEvent = event; // TODO Production: Remove.
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPerson ());

        final String reinforcementsPhrase = Strings.pluralizeS (event.getNextTradeInBonus (),
                                                                "additional reinforcement");

        if (!event.isTradeInRequired () && event.getPlayerCardsInHand () == 3)
        {
          notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
          notificationDialog.showForSelf (event.getPerson (),
                                          "Congratulations, sir, you have just enough matching cards to "
                                                  + "purchase {}! If you so desire, that is, sir. Good things come to "
                                                  + "those who wait, General.",
                                          reinforcementsPhrase);
        }
        else if (!event.isTradeInRequired () && event.getPlayerCardsInHand () > 3)
        {
          notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
          notificationDialog.showForSelf (event.getPerson (),
                                          "You may now use 3 of your {} matching cards to purchase {}! If "
                                                  + "you so desire, that is, sir. Fortune rewards the patient, General.",
                                          event.getPlayerCardsInHand (), reinforcementsPhrase);
        }
        else if (event.isTradeInRequired ())
        {
          // TODO Show trade-in dialog first, which will (desirably) block play map even after notification dialog...
          // TODO ...is closed to prevent premature reinforcing.
          notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
          notificationDialog.showForSelf (event.getPerson (),
                                          "You now have so many matching cards that you must now use some of them to "
                                                  + "purchase {}!",
                                          reinforcementsPhrase);
        }
      }
    });
  }

  @Handler
  void onEvent (final PlayerTradeInCardsResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        notificationDialog.setTitle ("Ahem, General");

        notificationDialog.showForSelf (event.getPerson (),
                                        "You used 3 cards to purchase {}!\n\nYou now have {} remaining.\n\nThe next "
                                                + "purchase will yield {}.",
                                        Strings.pluralizeS (event.getTradeInBonus (), "additional reinforcement"),
                                        Strings.pluralizeSZeroIsNo (event.getPlayerCardsInHand (), "card"),
                                        Strings.pluralizeS (event.getNextTradeInBonus (), "reinforcement"));

        notificationDialog.showForEveryoneElse (event.getPerson (),
                                                "{} used 3 cards to purchase {}!\n\n{} has {} remaining.\n\nThe next "
                                                        + "purchase will yield {}.",
                                                event.getPersonName (),
                                                Strings.pluralizeS (event.getTradeInBonus (),
                                                                    "additional reinforcement"),
                                                event.getPersonName (),
                                                Strings.pluralizeSZeroIsNo (event.getPlayerCardsInHand (), "card"),
                                                Strings.pluralizeS (event.getNextTradeInBonus (), "reinforcement"));
      }
    });
  }

  @Handler
  void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    tradeInEvent = null; // TODO Production: Remove.
    reinforcementPhaseHandler.deactivateForSelf (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final BeginAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    defendingBattlePhaseHandler.activateForEveryoneElse (event.getPerson ());
    attackingBattlePhaseHandler.activateForSelf (event.getPerson ());
    occupationPhaseHandler.activateForSelf (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Attack");
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPerson ());
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerOccupyCountryRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerLoseGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.removePlayer (event.getPerson ());
        notificationDialog.setTitle ("Ahem, General");
        notificationDialog.showForSelf (event.getPerson (), "We have been annihilated.", event.getPersonName ());
        notificationDialog.showForEveryoneElse (event.getPerson (), "{} has been annihilated.", event.getPersonName ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerWinGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Game Over");
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPerson ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPerson ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());
        notificationDialog.setTitle ("Ahem, General");
        notificationDialog.showForSelf (event.getPerson (),
                                        "You have won the war! Let the celebrations begin, in your honor, sir!");
        notificationDialog.showForSelf (event.getPerson (), "We have lost the war for you, sir... *gulp*");
      }
    });
  }

  @Handler
  void onEvent (final EndAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    defendingBattlePhaseHandler.deactivateForEveryoneElse (event.getPerson ());
    attackingBattlePhaseHandler.deactivateForSelf (event.getPerson ());
    occupationPhaseHandler.deactivateForSelf (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerEndAttackPhaseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final SkipFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
    notificationDialog.showForSelf (event.getPerson (), "We do not have any valid post-combat maneuvers.");
  }

  @Handler
  void onEvent (final BeginFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    fortificationPhaseHandler.activateForSelf (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Post-Combat Maneuver");
      }
    });
  }

  @Handler
  void onEvent (final EndFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    fortificationPhaseHandler.deactivateForSelf (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final EndPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPerson ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPerson ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPerson ());

        if (event.wasCardReceived ())
        {
          notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
          notificationDialog.showForSelf (event.getPerson (),
                                          "You earned a card ({} (MSV: {})) as a reward for your military genius!",
                                          event.getCardName (), event.getCardType ());
        }
        else
        {
          notificationDialog.setTitleForSelf (event.getPerson (), "Ahem, General");
          notificationDialog.showForSelf (event.getPerson (),
                                          "We failed to earn you a card, sir. We will try harder next time, sir...");
        }
      }
    });
  }

  @Handler
  void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.removePlayer (event.getPerson ());
        debugEventGenerator.makePlayerAvailable (event.getPerson ());
      }
    });
  }

  @Handler
  void onEvent (final QuitGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    isGameInProgress.set (false);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        notificationDialog.setTitle ("Disconnected");
        notificationDialog.show ("You have been disconnected from the server.");
        controlRoomBox.setButtonText (ControlRoomBox.Button.QUIT,
                                      getQuitButtonText (isGameInProgress.get (), isSpectating.get ()));
      }
    });
  }

  @Handler
  void onEvent (final EndGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    isGameInProgress.set (false);

    tradeInEvent = null; // TODO Production: Remove.

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButton (ControlRoomBox.Button.TRADE_IN);
        controlRoomBox.disableButton (ControlRoomBox.Button.FORTIFY);
        controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
        controlRoomBox.setButtonText (ControlRoomBox.Button.QUIT,
                                      getQuitButtonText (isGameInProgress.get (), isSpectating.get ()));
      }
    });
  }

  private static String getDenialMessage (final SpectatorJoinGameDeniedEvent event)
  {
    String reason;

    switch (event.getReason ())
    {
      case GAME_IS_FULL:
      {
        reason = Strings.format (
                                 "the maximum number of spectators allowed by this server ({}) has already been reached.",
                                 event.getSpectatorLimit ());
        break;
      }
      case SPECTATING_DISABLED:
      {
        reason = "this server doesn't allow spectating.";
        break;
      }
      case INVALID_NAME:
      {
        reason = Strings.format ("your name is invalid. Try rejoining with a valid name.\n\nValid name rules:\n\n{}",
                                 GameSettings.VALID_PLAYER_NAME_DESCRIPTION);
        break;
      }
      case DUPLICATE_PLAYER_NAME:
      {
        reason = "your name is already taken by another player. Try rejoining with a unique name.";
        break;
      }
      case DUPLICATE_SPECTATOR_NAME:
      {
        reason = "your name is already taken by another spectator. Try rejoining with a unique name.";
        break;
      }
      default:
      {
        reason = "of some unknown issue. Try rejoining and see what happens.";
        break;
      }
    }

    return Strings.format ("{}, unfortunately, you can't spectate this game because {}", event.getSpectatorName (),
                           reason);
  }

  private String getQuitButtonText (final boolean isGameInProgress, final boolean isSpectating)
  {
    return isGameInProgress && !isSpectating ? "Surrender & Quit" : "Quit";
  }

  private void updatePlayMap (final PlayMap playMap)
  {
    this.playMap = playMap;
    playMapCell.setActor (playMap.asActor ());
    intelBox.setPlayMapMetadata (playMap.getPlayMapMetadata ());
    gamePhaseHandlers.setPlayMap (playMap);
    allDialogListeners.setPlayMap (playMap);
    debugInputProcessor.setPlayMap (playMap);
    playMap.onMouseMoved (getMousePosition ());
  }

  private void quitGame ()
  {
    // playMap must be used here because it will be reset to
    // PlayMap#NULL in #hide during the call to #toScreen.
    final PlayMapMetadata playMapMetadata = playMap.getPlayMapMetadata ();

    toScreen (ScreenId.PLAY_TO_MENU_LOADING);

    // The play-to-menu loading screen is now active & can therefore receive events.

    publishAsync (new UnloadPlayScreenAssetsRequestEvent (GameMode.CLASSIC));
    publishAsync (new UnloadPlayMapRequestEvent (playMapMetadata));
  }

  private final class DefaultAttackDialogListener extends AbstractBattleDialogListener implements AttackDialogListener
  {
    DefaultAttackDialogListener (final CompositeDialog allDialogs, final PlayMap playMap, final MouseInput mouseInput)
    {
      super (allDialogs, playMap, mouseInput);
    }

    @Override
    public void onBattle ()
    {
      attackingBattlePhaseHandler.execute ();
    }

    @Override
    public void onRetreat ()
    {
      attackingBattlePhaseHandler.cancel ();
    }

    @Override
    public void onResultAttackerVictorious (final BattleResultPacket result)
    {
      Arguments.checkIsNotNull (result, "result");

      attackingBattlePhaseHandler.onResultAttackerVictorious (result);
    }

    @Override
    public void onResultAttackerDefeated (final BattleResultPacket result)
    {
      Arguments.checkIsNotNull (result, "result");

      attackingBattlePhaseHandler.onResultAttackerDefeated (result);
    }
  }

  private final class DefaultDefendDialogListener extends AbstractBattleDialogListener implements DefendDialogListener
  {
    DefaultDefendDialogListener (final CompositeDialog allDialogs, final PlayMap playMap, final MouseInput mouseInput)
    {
      super (allDialogs, playMap, mouseInput);
    }

    @Override
    public void onBattle ()
    {
      defendingBattlePhaseHandler.execute ();
    }

    @Override
    public void onResultAttackerVictorious (final BattleResultPacket result)
    {
      Arguments.checkIsNotNull (result, "result");

      defendingBattlePhaseHandler.onResultAttackerVictorious (result);
    }

    @Override
    public void onResultAttackerDefeated (final BattleResultPacket result)
    {
      Arguments.checkIsNotNull (result, "result");

      defendingBattlePhaseHandler.onResultAttackerDefeated (result);
    }
  }

  private final class AttackerBattleResultDialogListener extends DefaultPlayScreenDialogListener
  {
    AttackerBattleResultDialogListener (final CompositeDialog allDialogs,
                                        final PlayMap playMap,
                                        final MouseInput mouseInput)
    {
      super (allDialogs, mouseInput, playMap);
    }

    @Override
    public void onHide ()
    {
      final BattleDialog attackDialog = allDialogs.get (AttackDialog.class);
      if (!attackDialog.isBattling ()) attackDialog.hide ();
      final AttackerBattleResultDialog resultDialog = allDialogs.get (AttackerBattleResultDialog.class);
      final OccupationDialog occupationDialog = allDialogs.get (OccupationDialog.class);

      if (resultDialog.battleOutcomeIs (BattleOutcome.ATTACKER_VICTORIOUS))
      {
        // Update occupation dialog to match preemptive play map changes (defending country ownership change).
        occupationDialog.updateCountries (playMap.getCountryWithName (resultDialog.getAttackingCountryName ()),
                                          playMap.getCountryWithName (resultDialog.getDefendingCountryName ()));

        // Show the occupation dialog here (rather than immediately after battle, in response to
        // PlayerOccupyCountryRequestEvent in OccupationPhaseHandler) so that it doesn't appear until the attacker
        // battle result dialog is closed. This allows the attack dialog with the final battle result to still be seen
        // underneath the battle result dialog in the meantime.
        //
        // OccupationPhaseHandler usually has already set the occupation dialog data by this point. If for some reason
        // (extreme network latency) the PlayerOccupyCountryRequestEvent hasn't yet arrived, the dialog will still be
        // shown, but it will not be functional. It's still not a problem because the event will soon arrive and update
        // the dialog.
        occupationDialog.show ();
      }

      super.onHide ();
    }
  }

  private final class DefenderBattleResultDialogListener extends DefaultPlayScreenDialogListener
  {
    DefenderBattleResultDialogListener (final CompositeDialog allDialogs,
                                        final PlayMap playMap,
                                        final MouseInput mouseInput)
    {
      super (allDialogs, mouseInput, playMap);
    }

    @Override
    public void onHide ()
    {
      final BattleDialog defendDialog = allDialogs.get (DefendDialog.class);

      if (!defendDialog.isBattling ()) defendDialog.hide ();

      super.onHide ();
    }
  }

  private final class OccupationDialogListener extends DefaultPlayScreenDialogListener
  {
    OccupationDialogListener (final CompositeDialog allDialogs, final PlayMap playMap, final MouseInput mouseInput)
    {
      super (allDialogs, mouseInput, playMap);
    }

    @Override
    public void onSubmit ()
    {
      occupationPhaseHandler.execute ();
    }
  }

  private final class FortificationDialogListener extends DefaultPlayScreenCancellableDialogListener
  {
    FortificationDialogListener (final CompositeDialog allDialogs, final PlayMap playMap, final MouseInput mouseInput)
    {
      super (allDialogs, mouseInput, playMap);
    }

    @Override
    public void onCancel ()
    {
      fortificationPhaseHandler.cancel ();
    }

    @Override
    public void onSubmit ()
    {
      fortificationPhaseHandler.execute ();

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
        }
      });
    }
  }

  private final class QuitDialogListener extends DefaultPlayScreenCancellableDialogListener
  {
    QuitDialogListener (final CompositeDialog allDialogs, final PlayMap playMap, final MouseInput mouseInput)
    {
      super (allDialogs, mouseInput, playMap);
    }

    @Override
    protected void reshowQuitDialogOnTopIfShown ()
    {
      // The quit dialog itself will always be on top when shown, so don't attempt to re-show it on top.
      // This override prevents infinite callback recursion.
    }

    @Override
    public void onSubmit ()
    {
      quitGame ();
      publishAsync (new QuitGameEvent ());
    }
  }

  private final class ReinforcementDialogListener extends CancellableDialogListenerAdapter
  {
    @Override
    public void onCancel ()
    {
      reinforcementPhaseHandler.cancel ();
    }

    @Override
    public void onSubmit ()
    {
      reinforcementPhaseHandler.execute ();
    }
  }
}
