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
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.NullDebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification.FortificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.AbstractBattleDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.BattleDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.BattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox.IntelBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.AttackingBattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.BattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.CompositeGamePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.DefendingBattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.FortificationPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ManualCountryAssignmentPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.OccupationPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ReinforcementPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.status.StatusMessageGenerator;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CompositeDialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.game.BattleOutcome;
import com.forerunnergames.peril.common.game.GameMode;
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
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
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.request.PlayerOccupyCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerEndAttackPhaseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;
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
  private static final String QUIT_DIALOG_TITLE_GAME_NOT_IN_PROGRESS = "Quit?";
  private static final Message QUIT_DIALOG_MESSAGE_GAME_NOT_IN_PROGRESS = new DefaultMessage (
          "Are you sure you want to quit?\nIf you are the host, quitting will shut down the server for everyone.");
  private static final String QUIT_DIALOG_TITLE_GAME_IN_PROGRESS = "Surrender & Quit?";
  private static final Message QUIT_DIALOG_MESSAGE_GAME_IN_PROGRESS = new DefaultMessage (
          "Are you sure you want to surrender & quit?\nIf you are the host, quitting will shut down the server for everyone.");
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final DebugEventGenerator debugEventGenerator;
  private final Image playMapTableForegroundImage;
  private final MessageBox <StatusBoxRow> statusBox;
  private final MessageBox <ChatBoxRow> chatBox;
  private final PlayerBox playerBox;
  private final IntelBox intelBox;
  private final ControlRoomBox controlRoomBox;
  private final Dialog quitDialog;
  private final Cell <Actor> playMapCell;
  private final AtomicBoolean isGameInProgress = new AtomicBoolean ();
  private final Vector2 tempPosition = new Vector2 ();
  private final OccupationDialog occupationDialog;
  private final FortificationDialog fortificationDialog;
  private final BattleResultDialog attackerBattleResultDialog;
  private final BattleResultDialog defenderBattleResultDialog;
  private final ReinforcementPhaseHandler reinforcementPhaseHandler;
  private final ManualCountryAssignmentPhaseHandler manualCountryAssignmentPhaseHandler;
  private final OccupationPhaseHandler occupationPhaseHandler;
  private final FortificationPhaseHandler fortificationPhaseHandler;
  private final StatusMessageGenerator statusMessageGenerator;
  private final CompositeGamePhaseHandler gamePhaseHandlers = new CompositeGamePhaseHandler ();
  private final CompositeDialog dialogs = new CompositeDialog ();
  private PlayMap playMap = PlayMap.NULL_PLAY_MAP;
  private BattlePhaseHandler attackingBattlePhaseHandler = BattlePhaseHandler.NULL;
  private BattlePhaseHandler defendingBattlePhaseHandler = BattlePhaseHandler.NULL;
  private BattleDialog attackDialog = BattleDialog.NULL;
  private BattleDialog defendDialog = BattleDialog.NULL;
  private DebugInputProcessor debugInputProcessor = new NullDebugInputProcessor ();
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
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (debugEventGenerator, "debugEventGenerator");

    this.widgetFactory = widgetFactory;
    this.debugEventGenerator = debugEventGenerator;

    playMapTableForegroundImage = widgetFactory.createPlayMapTableForegroundImage ();
    statusBox = widgetFactory.createStatusBox ();
    chatBox = widgetFactory.createChatBox (eventBus);
    playerBox = widgetFactory.createPlayerBox ();

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
        log.debug ("Clicked surrender & quit button");

        quitDialog.setTitle (getQuitDialogTitle ());
        quitDialog.setMessage (getQuitDialogMessage ());
        quitDialog.show ();
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
    bottomTable.add (playerBox.asActor ()).size (498, 256).spaceLeft (6).fill ();
    bottomTable.setDebug (DEBUG, true);

    final Table screenTable = new Table ().top ().left ().pad (5);
    screenTable.add (topTable).height (808).left ().spaceBottom (6).fill ();
    screenTable.row ().spaceTop (6);
    screenTable.add (bottomTable).height (256).fill ();
    screenTable.setDebug (DEBUG, true);

    rootStack.add (screenTable);
    addRootActor (rootStack);

    statusMessageGenerator = new StatusMessageGenerator (statusBox, widgetFactory);

    attackerBattleResultDialog = widgetFactory
            .createAttackerBattleResultDialog (getStage (), new AttackerBattleResultDialogListener ());
    defenderBattleResultDialog = widgetFactory
            .createDefenderBattleResultDialog (getStage (), new DefenderBattleResultDialogListener ());
    occupationDialog = widgetFactory.createOccupationDialog (getStage (), new OccupationDialogListener ());
    fortificationDialog = widgetFactory.createFortificationDialog (getStage (), new FortificationDialogListener ());
    quitDialog = widgetFactory.createQuitDialog (getQuitDialogMessageText (), getStage (), new QuitDialogListener ());
    dialogs.add (attackerBattleResultDialog, defenderBattleResultDialog, occupationDialog, fortificationDialog,
                    quitDialog);

    reinforcementPhaseHandler = new ReinforcementPhaseHandler (playMap, eventBus);
    manualCountryAssignmentPhaseHandler = new ManualCountryAssignmentPhaseHandler (playMap, eventBus);
    occupationPhaseHandler = new OccupationPhaseHandler (playMap, occupationDialog, eventBus);
    fortificationPhaseHandler = new FortificationPhaseHandler (playMap, fortificationDialog, eventBus);
    gamePhaseHandlers.add (reinforcementPhaseHandler, manualCountryAssignmentPhaseHandler, occupationPhaseHandler,
                              fortificationPhaseHandler);
  }

  @Override
  public void show ()
  {
    super.show ();

    subscribe (statusMessageGenerator);

    playMap.mouseMoved (getMousePosition ());

    playMapTableForegroundImage.setDrawable (widgetFactory.createPlayMapTableForegroundImageDrawable ());

    intelBox.refreshAssets ();
    controlRoomBox.refreshAssets ();
    statusBox.refreshAssets ();
    chatBox.refreshAssets ();
    playerBox.refreshAssets ();
    dialogs.refreshAssets ();

    controlRoomBox.disableButton (ControlRoomBox.Button.TRADE_IN);
    controlRoomBox.disableButton (ControlRoomBox.Button.FORTIFY);
    controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
    controlRoomBox.enableButton (ControlRoomBox.Button.MY_SETTINGS);
    controlRoomBox.enableButton (ControlRoomBox.Button.SURRENDER_AND_QUIT);
  }

  @Override
  public void hide ()
  {
    super.hide ();

    unsubscribe (statusMessageGenerator);

    intelBox.clear ();
    chatBox.clear ();
    statusBox.clear ();
    playerBox.clear ();

    updatePlayMap (PlayMap.NULL_PLAY_MAP);

    gamePhaseHandlers.deactivate ();
    gamePhaseHandlers.remove (attackingBattlePhaseHandler, defendingBattlePhaseHandler);

    attackingBattlePhaseHandler = BattlePhaseHandler.NULL;
    defendingBattlePhaseHandler = BattlePhaseHandler.NULL;

    dialogs.hide (null);
    dialogs.remove (attackDialog, defendDialog);

    attackDialog = BattleDialog.NULL;
    defendDialog = BattleDialog.NULL;

    isGameInProgress.set (false);
    tradeInEvent = null; // TODO Production: Remove.

    debugInputProcessor.reset ();
  }

  @Override
  protected void update (final float delta)
  {
    super.update (delta);
    dialogs.update (delta);
  }

  @Override
  protected boolean onEscape ()
  {
    if (!attackDialog.isShown () && !fortificationDialog.isShown ())
    {
      controlRoomBox.pressButton (ControlRoomBox.Button.SURRENDER_AND_QUIT);
    }
    return false;
  }

  @Override
  protected void onKeyDownRepeating (final int keyCode)
  {
    occupationDialog.keyDownRepeating (keyCode);
    fortificationDialog.keyDownRepeating (keyCode);
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    playMap.touchDown (tempPosition.set (screenX, screenY), button);
    return false;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    playMap.touchUp (tempPosition.set (screenX, screenY));
    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    playMap.mouseMoved (tempPosition.set (screenX, screenY));
    return false;
  }

  @Handler
  void onEvent (final PlayGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        final ScreenShaker screenShaker = new ScreenShaker (getViewport (), getScreenSize ());

        attackDialog = widgetFactory.createAttackDialog (getStage (), event.getGameRules (), screenShaker,
                                                         new DefaultAttackDialogListener ());

        defendDialog = widgetFactory.createDefendDialog (getStage (), event.getGameRules (), screenShaker,
                                                         new DefaultDefendDialogListener ());

        dialogs.add (attackDialog, defendDialog);

        attackingBattlePhaseHandler = new AttackingBattlePhaseHandler (playMap, attackDialog,
                attackerBattleResultDialog, getEventBus ());

        defendingBattlePhaseHandler = new DefendingBattlePhaseHandler (playMap, defendDialog,
                defenderBattleResultDialog, getEventBus ());

        gamePhaseHandlers.add (attackingBattlePhaseHandler, defendingBattlePhaseHandler);

        if (DEBUG)
        {
          debugInputProcessor = new DefaultDebugInputProcessor (debugEventGenerator, widgetFactory, getMouseInput (),
                  playMap, statusBox, chatBox, playerBox, occupationDialog, fortificationDialog, attackDialog,
                  defendDialog, getEventBus ());

          addInputProcessor (debugInputProcessor);
        }

        updatePlayMap (event.getPlayMap ());

        intelBox.setGameServerConfiguration (event.getGameServerConfiguration ());
        intelBox.setClientConfiguration (event.getClientConfiguration ());
        intelBox.setOwnedCountriesForSelf (0, event.getSelfPlayer ());
        intelBox.setSelfPlayer (event.getSelfPlayer ());
        controlRoomBox.setSelfPlayer (event.getSelfPlayer ());
        playerBox.setPlayers (event.getAllPlayers ());
        gamePhaseHandlers.setSelfPlayer (event.getSelfPlayer ());
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

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playerBox.addPlayer (event.getPlayer ());
        debugEventGenerator.makePlayerUnavailable (event.getPlayer ());
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
        playerBox.updateExisting (event.getPlayer ());
        playerBox.highlightPlayer (event.getPlayer ());
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
                                 CountryPrimaryImageState.fromPlayerColor (event.getNewOwner ().getColor ()));
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
        playerBox.updateExisting (event.getPlayer ());
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
        playerBox.setPlayers (event.getPlayersSortedByTurnOrder ());
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

    reinforcementPhaseHandler.activateForSelf (event.getPlayer ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Reinforcement");
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
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPlayer ());
      }
    });
  }

  @Handler
  void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    tradeInEvent = null; // TODO Production: Remove.
    reinforcementPhaseHandler.deactivateForSelf (event.getPlayer ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPlayer ());
      }
    });
  }

  @Handler
  void onEvent (final BeginAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    defendingBattlePhaseHandler.activateForEveryoneElse (event.getPlayer ());
    attackingBattlePhaseHandler.activateForSelf (event.getPlayer ());
    occupationPhaseHandler.activateForSelf (event.getPlayer ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Attack");
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPlayer ());
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
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
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPlayer ());
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
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
        controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPlayer ());
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
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPlayer ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPlayer ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
      }
    });
  }

  @Handler
  void onEvent (final EndAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    defendingBattlePhaseHandler.deactivateForEveryoneElse (event.getPlayer ());
    attackingBattlePhaseHandler.deactivateForSelf (event.getPlayer ());
    occupationPhaseHandler.deactivateForSelf (event.getPlayer ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPlayer ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerEndAttackPhaseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    controlRoomBox.enableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
  }

  @Handler
  void onEvent (final BeginFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    fortificationPhaseHandler.activateForSelf (event.getPlayer ());

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

    fortificationPhaseHandler.deactivateForSelf (event.getPlayer ());

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
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
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.TRADE_IN, event.getPlayer ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.FORTIFY, event.getPlayer ());
        controlRoomBox.disableButtonForSelf (ControlRoomBox.Button.END_TURN, event.getPlayer ());
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
        playerBox.removePlayer (event.getPlayer ());
        debugEventGenerator.makePlayerAvailable (event.getPlayer ());
      }
    });
  }

  @Handler
  void onEvent (final QuitGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        quitGame ();
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
      }
    });
  }

  private void updatePlayMap (final PlayMap playMap)
  {
    this.playMap = playMap;
    playMapCell.setActor (this.playMap.asActor ());
    intelBox.setMapMetadata (playMap.getMapMetadata ());
    gamePhaseHandlers.setPlayMap (playMap);
    debugInputProcessor.setPlayMap (this.playMap);
  }

  private String getQuitDialogTitle ()
  {
    return isGameInProgress.get () ? QUIT_DIALOG_TITLE_GAME_IN_PROGRESS : QUIT_DIALOG_TITLE_GAME_NOT_IN_PROGRESS;
  }

  private Message getQuitDialogMessage ()
  {
    return isGameInProgress.get () ? QUIT_DIALOG_MESSAGE_GAME_IN_PROGRESS : QUIT_DIALOG_MESSAGE_GAME_NOT_IN_PROGRESS;
  }

  private String getQuitDialogMessageText ()
  {
    return getQuitDialogMessage ().getText ();
  }

  private void quitGame ()
  {
    // playMap must be used here because it will be reset to
    // PlayMap#NULL_PLAY_MAP in #hide during the call to #toScreen.
    final MapMetadata mapMetadata = playMap.getMapMetadata ();

    toScreen (ScreenId.PLAY_TO_MENU_LOADING);

    // The play-to-menu loading screen is now active & can therefore receive events.

    publishAsync (new UnloadPlayScreenAssetsRequestEvent (GameMode.CLASSIC));
    publishAsync (new UnloadPlayMapRequestEvent (mapMetadata));
  }

  private final class DefaultAttackDialogListener extends AbstractBattleDialogListener implements AttackDialogListener
  {
    @Override
    public void onBattle ()
    {
      attackingBattlePhaseHandler.onBattle ();
    }

    @Override
    public void onRetreat ()
    {
      attackingBattlePhaseHandler.onRetreat ();
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

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (occupationDialog.isShown () || attackerBattleResultDialog.isShown () || quitDialog.isShown ()) return;

      playMap.enable (getMousePosition ());
    }
  }

  private final class DefaultDefendDialogListener extends AbstractBattleDialogListener implements DefendDialogListener
  {
    @Override
    public void onBattle ()
    {
      defendingBattlePhaseHandler.onBattle ();
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

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (attackerBattleResultDialog.isShown () || quitDialog.isShown ()) return;

      playMap.enable (getMousePosition ());
    }
  }

  private final class AttackerBattleResultDialogListener extends DialogListenerAdapter
  {
    @Override
    public void onShow ()
    {
      if (quitDialog.isShown ())
      {
        quitDialog.hide (null);
        quitDialog.show (null);
      }

      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (!attackDialog.isBattling ()) attackDialog.hide ();

      if (attackerBattleResultDialog.battleOutcomeIs (BattleOutcome.ATTACKER_VICTORIOUS))
      {
        // Update occupation dialog to match preemptive play map changes (defending country ownership change).
        occupationDialog.updateCountries (playMap.getCountryWithName (attackerBattleResultDialog
                .getAttackingCountryName ()), playMap.getCountryWithName (attackerBattleResultDialog
                .getDefendingCountryName ()));

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

      if (!occupationDialog.isShown () && !quitDialog.isShown ()) playMap.enable (getMousePosition ());
    }
  }

  private final class DefenderBattleResultDialogListener extends DialogListenerAdapter
  {
    @Override
    public void onShow ()
    {
      if (quitDialog.isShown ())
      {
        quitDialog.hide (null);
        quitDialog.show (null);
      }

      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (!defendDialog.isBattling ()) defendDialog.hide ();
      if (!quitDialog.isShown ()) playMap.enable (getMousePosition ());
    }
  }

  private final class OccupationDialogListener implements DialogListener
  {
    @Override
    public void onSubmit ()
    {
      occupationPhaseHandler.onOccupy ();
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (!quitDialog.isShown ()) playMap.enable (getMousePosition ());
    }
  }

  private final class FortificationDialogListener implements CancellableDialogListener
  {
    @Override
    public void onCancel ()
    {
      fortificationPhaseHandler.onCancel ();
    }

    @Override
    public void onSubmit ()
    {
      fortificationPhaseHandler.onFortify ();

      Gdx.app.postRunnable (new Runnable ()
      {
        @Override
        public void run ()
        {
          controlRoomBox.disableButton (ControlRoomBox.Button.END_TURN);
        }
      });
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (!quitDialog.isShown ()) playMap.enable (getMousePosition ());
    }
  }

  private final class QuitDialogListener extends CancellableDialogListenerAdapter
  {
    @Override
    public void onSubmit ()
    {
      quitGame ();
      publishAsync (new QuitGameEvent ());
    }

    @Override
    public void onCancel ()
    {
      if (!attackDialog.isShown () && !defendDialog.isShown () && !occupationDialog.isShown ()
              && !fortificationDialog.isShown () && !attackerBattleResultDialog.isShown ()
              && !defenderBattleResultDialog.isShown ())
      {
        playMap.enable (getMousePosition ());
      }
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }
  }
}
