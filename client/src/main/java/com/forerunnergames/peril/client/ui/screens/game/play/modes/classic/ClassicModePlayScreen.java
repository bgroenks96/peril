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

import com.forerunnergames.peril.client.events.DisconnectFromServerDebugEvent;
import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.RejoinGameErrorEvent;
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
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.InitialCountryAssignmentPhaseHandler;
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
import com.forerunnergames.peril.client.ui.widgets.dialogs.ErrorDialog;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.PlayerQuitGameRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerEndTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.inform.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.SpectatorJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.inform.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BeginGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.EndGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerBeginGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerCardsChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerEndGamePhaseNotificationEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerTurnOrderChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginRoundEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.GameResumedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.GameSuspendedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerDisconnectEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerRestoreGameStateEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.SpectatorJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PersonIdentity;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.settings.GameSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.ServerDisconnectionEvent;

import java.util.Map;
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
  private final ScreenShaker screenShaker;
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
  private final GamePhaseHandler initialCountryAssignmentPhaseHandler;
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

    screenShaker = new ScreenShaker (getViewport (), getScreenSize ());
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

        // TODO Production: Remove.
        publish (new DisconnectFromServerDebugEvent ());

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

        publish (new PlayerEndTurnRequestEvent ());
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
    final PlayScreenDialogListener errorDialogListener = new DefaultPlayScreenDialogListener (allDialogs, mouseInput, playMap);
    allDialogListeners.add (attackDialogListener, defendDialogListener, attackerBattleResultDialogListener, defenderBattleResultDialogListener,
            occupationDialogListener, fortificationDialogListener, notificationDialogListener, quitDialogListener, errorDialogListener);

    final Dialog attackerBattleResultDialog = widgetFactory.createAttackerBattleResultDialog (getStage (), attackerBattleResultDialogListener);
    final Dialog defenderBattleResultDialog = widgetFactory.createDefenderBattleResultDialog (getStage (), defenderBattleResultDialogListener);
    final OccupationDialog occupationDialog = widgetFactory.createOccupationDialog (getStage (), occupationDialogListener);
    final FortificationDialog fortificationDialog = widgetFactory.createFortificationDialog (getStage (), fortificationDialogListener);
    final ReinforcementDialog reinforcementDialog = widgetFactory.createReinforcementDialog (getStage (), personBox, new ReinforcementDialogListener ());
    final Dialog quitDialog = widgetFactory.createQuitDialog ("", getStage (), quitDialogListener);
    final Dialog errorDialog = widgetFactory.createErrorDialog (getStage (), errorDialogListener);
    notificationDialog = widgetFactory.createNotificationDialog (widgetFactory, getStage (), notificationDialogListener);
    allDialogs.add (attackerBattleResultDialog, defenderBattleResultDialog, occupationDialog, fortificationDialog, reinforcementDialog,
            notificationDialog, quitDialog, errorDialog);

    reinforcementPhaseHandler = new ReinforcementPhaseHandler (playMap, reinforcementDialog, eventBus);
    initialCountryAssignmentPhaseHandler = new InitialCountryAssignmentPhaseHandler (playMap, eventBus);
    occupationPhaseHandler = new OccupationPhaseHandler (playMap, occupationDialog, eventBus);
    fortificationPhaseHandler = new FortificationPhaseHandler (playMap, fortificationDialog, eventBus);
    gamePhaseHandlers.add (reinforcementPhaseHandler, initialCountryAssignmentPhaseHandler, occupationPhaseHandler, fortificationPhaseHandler);

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

    gamePhaseHandlers.shutDown ();
    gamePhaseHandlers.remove (attackingBattlePhaseHandler, defendingBattlePhaseHandler);

    attackingBattlePhaseHandler = BattlePhaseHandler.NULL;
    defendingBattlePhaseHandler = BattlePhaseHandler.NULL;

    allDialogs.hide (null);
    allDialogs.remove (AttackDialog.class, DefendDialog.class);

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

  // Note: Only occurs for self-player, not third-parties.
  @Handler
  public void onEvent (final ServerDisconnectionEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event [{}] received.", event);

    gamePhaseHandlers.shutDown ();

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        notificationDialog.setTitle ("Disconnected");
        notificationDialog.show ("You have been disconnected from the server. Attempting to reconnect you...");
      }
    });
  }

  @Handler
  void onEvent (final PlayGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    isSpectating.set (false);

    updateGameRules (event.getGameRules ());

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
    gamePhaseHandlers.setSelfPlayer (event.getSelfPlayer ());
    notificationDialog.setSelf (event.getSelfPlayer ());
    debugEventGenerator.makePlayersUnavailable (event.getAllPlayers ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.setPlayers (event.getAllPlayers ());
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

    // This event is only called for the self-player when reconnecting to the server while this screen is still active.
    // It will not be called for the self-player when rejoining a game from the main menu.
    // See the PlayGameEvent handler for the self-player when joining a game for the first time, or
    // rejoining a game from the main menu.
    if (event.hasIdentity (PersonIdentity.SELF))
    {
      log.info ("Successfully rejoined the game. SuccessEvent: [{}].", event);

      isSpectating.set (false);
      intelBox.setSelf (event.getPerson ());
      controlRoomBox.setSelf (event.getPerson ());
      gamePhaseHandlers.setSelfPlayer (event.getPerson ());
      notificationDialog.setSelf (event.getPerson ());
      debugEventGenerator.makePlayersUnavailable (event.getPlayersInGame ());

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          personBox.setPlayers (event.getPlayersInGame ());
        }
      });

      return;
    }

    debugEventGenerator.makePlayerUnavailable (event.getPerson ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        personBox.addPlayer (event.getPerson ());
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

    log.debug ("Event received [{}].", event);

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

    log.debug ("Event received [{}].", event);

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

    log.debug ("Event received [{}].", event);

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

    log.debug ("Event received [{}].", event);

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

    log.debug ("Event received [{}].", event);

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
        personBox.setPlayers (event.getPlayersSortedByTurnOrder ());
      }
    });
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
  void onEvent (final BeginPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
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

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
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
  void onEvent (final EndFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

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

  // Note: Only occurs for third-parties, not self player.
  @Handler
  void onEvent (final PlayerDisconnectEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    notificationDialog.setTitle ("Player Disconnected");
    notificationDialog.show (Strings.format (
                                             "{} has been disconnected from the server. We will try to reconnect "
                                                     + "them now. Thank you for your patience.",
                                             event.getPlayerName ()));
  }

  @Handler
  void onEvent (final GameSuspendedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    notificationDialog.setTitle ("Game Paused");

    final String message;

    switch (event.getReason ())
    {
      case PLAYER_UNAVAILABLE:
      {
        message = "A player has become disconnected from the server, and we are waiting for them to be reconnected.";
        break;
      }
      case REQUESTED_BY_HOST:
      {
        message = "The host has paused the game.";
        break;
      }
      default:
      {
        throw new IllegalStateException (Strings
                .format ("Unrecognized {}: [{}]", event.getReason ().getClass ().getSimpleName (), event.getReason ()));
      }
    }

    notificationDialog.show (message);
  }

  @Handler
  void onEvent (final GameResumedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    notificationDialog.setTitle ("Game Unpaused");
    notificationDialog.show ("The game has been unpaused and you can resume normal gameplay.");
  }

  @Handler
  void onEvent (final PlayerBeginGamePhaseNotificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    gamePhaseHandlers.activate (event.getPerson (), event.getGamePhase ());
  }

  @Handler
  void onEvent (final PlayerEndGamePhaseNotificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    gamePhaseHandlers.deactivate (event.getPerson (), event.getGamePhase ());
  }

  @Handler
  void onEvent (final BeginGamePhaseNotificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhase (event.getGamePhase ());
      }
    });

    if (event instanceof PlayerBeginGamePhaseNotificationEvent) return;

    gamePhaseHandlers.activate (event.getGamePhase ());
  }

  @Handler
  void onEvent (final EndGamePhaseNotificationEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (event instanceof PlayerEndGamePhaseNotificationEvent) return;

    log.debug ("Event received [{}].", event);

    gamePhaseHandlers.deactivate (event.getGamePhase ());
  }

  @Handler
  void onEvent (final PlayerRestoreGameStateEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.info ("Restoring game state...");

    updateGameRules (event.getGameRules ());

    gamePhaseHandlers.shutDown ();
    gamePhaseHandlers.setSelfPlayer (event.getSelfPlayer ());
    gamePhaseHandlers.activate (event.getCurrentPlayer (), event.getCurrentGamePhase ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGameServerConfiguration (event.getGameServerConfiguration ());
        intelBox.setGamePhase (event.getCurrentGamePhase ());
        intelBox.setGameRound (event.getCurrentGameRound ());
        intelBox.setOwnedCountriesForSelf (event.getSelfOwnedCountryCount (), event.getPerson ());

        // TODO Update state of control room box buttons.

        playMap.reset ();

        for (final Map.Entry <CountryPacket, PlayerPacket> playMapEntry : event.getCountriesToPlayerEntries ())
        {
          playMap.setCountryState (playMapEntry.getKey ().getName (),
                                   CountryPrimaryImageState.fromPlayerColor (playMapEntry.getValue ().getColor ()));
          playMap.setArmies (playMapEntry.getKey ().getArmyCount (), playMapEntry.getKey ().getName ());
        }

        log.info ("Finished restoring game state.");

        notificationDialog.setTitle ("Reconnected");
        notificationDialog.show ("You have been reconnected to the server, and can resume normal gameplay.");
      }
    });
  }

  @Handler
  void onEvent (final RejoinGameErrorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);
    log.warn ("Could not rejoin game. Reason: [{}]", event.getErrorMessage ());

    final Dialog errorDialog = allDialogs.get (ErrorDialog.class);
    errorDialog.setMessage (new DefaultMessage (event.getErrorMessage ()));
    errorDialog.show ();
  }

  private static String getDenialMessage (final SpectatorJoinGameDeniedEvent event)
  {
    final String reason;

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

  private void updateGameRules (final GameRules rules)
  {
    allDialogs.remove (AttackDialog.class, DefendDialog.class);

    final AttackDialog attackDialog = widgetFactory
            .createAttackDialog (getStage (), rules, screenShaker,
                                 allDialogListeners.get (DefaultAttackDialogListener.class));

    final DefendDialog defendDialog = widgetFactory
            .createDefendDialog (getStage (), rules, screenShaker,
                                 allDialogListeners.get (DefaultDefendDialogListener.class));

    allDialogs.add (attackDialog, defendDialog);

    gamePhaseHandlers.remove (attackingBattlePhaseHandler, defendingBattlePhaseHandler);

    attackingBattlePhaseHandler = new AttackingBattlePhaseHandler (playMap, attackDialog,
            allDialogs.get (AttackerBattleResultDialog.class), getEventBus ());

    defendingBattlePhaseHandler = new DefendingBattlePhaseHandler (playMap, defendDialog,
            allDialogs.get (DefenderBattleResultDialog.class), getEventBus ());

    gamePhaseHandlers.add (attackingBattlePhaseHandler, defendingBattlePhaseHandler);
  }

  private String getQuitButtonText (final boolean isGameInProgress, final boolean isSpectating)
  {
    return isGameInProgress && !isSpectating ? "Surrender & Quit" : "Quit";
  }

  private void updatePlayMap (final PlayMap playMap)
  {
    this.playMap = playMap;

    intelBox.setPlayMapMetadata (playMap.getPlayMapMetadata ());
    gamePhaseHandlers.setPlayMap (playMap);
    allDialogListeners.setPlayMap (playMap);
    debugInputProcessor.setPlayMap (playMap);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMapCell.setActor (playMap.asActor ());
        playMap.onMouseMoved (getMousePosition ());
      }
    });
  }

  private void quitGame ()
  {
    // PlayMapMetadata must be copied here because playMap will be reset to
    // PlayMap#NULL in #hide during the call to #toScreen.
    final PlayMapMetadata playMapMetadata = playMap.getPlayMapMetadata ();

    toScreen (ScreenId.PLAY_TO_MENU_LOADING);

    // The play-to-menu loading screen is now active & can therefore receive events.

    publishAsync (new PlayerQuitGameRequestEvent ()); // Courtesy goodbye notice to the server; ignore any response.
    publishAsync (new QuitGameEvent ()); // Disconnect from server.
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
