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
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.StatusMessageEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.input.GdxKeyRepeatListenerAdapter;
import com.forerunnergames.peril.client.input.GdxKeyRepeatSystem;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox.ControlRoomBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugEventGenerator;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.fortification.FortificationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.AbstractBattleDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.attack.AttackDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.defend.DefendDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.dialogs.battle.result.BattleResultDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.intelbox.IntelBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.AttackingBattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.BattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.DefendingBattlePhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.FortificationPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ManualCountryAssignmentPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.OccupationPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.phasehandlers.ReinforcementPhaseHandler;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
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
import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.events.client.request.PlayerTradeInCardsRequestEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.BattleSetupEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryOwnerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerCountryAssignmentEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndAttackPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndFortifyPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndInitialReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndPlayerTurnEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerLoseGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerWinGameEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerCardTradeInAvailableEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerFortifyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOccupyCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderAttackSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerOrderRetreatSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerTradeInCardsResponseSuccessEvent;
import com.forerunnergames.peril.common.net.packets.battle.BattleResultPacket;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassicModePlayScreen extends InputAdapter implements Screen
{
  private static final Logger log = LoggerFactory.getLogger (ClassicModePlayScreen.class);
  private static final boolean DEBUG = false;
  private static final Message QUIT_MESSAGE = new DefaultMessage (
          "Are you sure you want to quit the current game?\nIf you are the host, quitting will end the game for everyone.");
  private static final Message SURRENDER_AND_QUIT_MESSAGE = new DefaultMessage (
          "Are you sure you want to surrender & quit the current game?\nIf you are the host, quitting will end the game for everyone.");
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final ScreenChanger screenChanger;
  private final MouseInput mouseInput;
  private final Cursor normalCursor;
  private final MBassador <Event> eventBus;
  private final DebugEventGenerator debugEventGenerator;
  private final Stage stage;
  private final Image playMapTableForegroundImage;
  private final Table playMapTable;
  private final MessageBox <StatusBoxRow> statusBox;
  private final MessageBox <ChatBoxRow> chatBox;
  private final PlayerBox playerBox;
  private final IntelBox intelBox;
  private final ControlRoomBox controlRoomBox;
  private final InputProcessor inputProcessor;
  private final GdxKeyRepeatSystem keyRepeat;
  private final OccupationDialog occupationDialog;
  private final FortificationDialog fortificationDialog;
  private final AttackDialog attackDialog;
  private final DefendDialog defendDialog;
  private final BattleResultDialog attackerBattleResultDialog;
  private final BattleResultDialog defenderBattleResultDialog;
  private final Dialog quitDialog;
  private final Vector2 tempPosition = new Vector2 ();
  private final Cell <Actor> playMapCell;
  private final ReinforcementPhaseHandler reinforcementPhaseHandler;
  private final ManualCountryAssignmentPhaseHandler manualCountryAssignmentPhaseHandler;
  private final BattlePhaseHandler attackingBattlePhaseHandler;
  private final BattlePhaseHandler defendingBattlePhaseHandler;
  private final OccupationPhaseHandler occupationPhaseHandler;
  private final FortificationPhaseHandler fortificationPhaseHandler;
  private final DebugInputProcessor debugInputProcessor;
  private final CompositeDialog allDialogs;
  private PlayMap playMap = PlayMap.NULL_PLAY_MAP;
  private boolean endTurnFromAttackPhase;
  private boolean isSelfBattlePhase;
  private boolean isSelfFortificationPhase;
  @Nullable
  private PlayerPacket selfPlayer;

  public ClassicModePlayScreen (final ClassicModePlayScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize,
                                final MouseInput mouseInput,
                                final Batch batch,
                                final MBassador <Event> eventBus,
                                final DebugEventGenerator debugEventGenerator)
  {
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (batch, "batch");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (debugEventGenerator, "debugEventGenerator");

    this.widgetFactory = widgetFactory;
    this.screenChanger = screenChanger;
    this.mouseInput = mouseInput;
    this.eventBus = eventBus;
    this.debugEventGenerator = debugEventGenerator;

    normalCursor = widgetFactory.createNormalCursor ();
    playMapTableForegroundImage = widgetFactory.createPlayMapTableForegroundImage ();
    statusBox = widgetFactory.createStatusBox ();
    chatBox = widgetFactory.createChatBox (eventBus);
    playerBox = widgetFactory.createPlayerBox ();

    intelBox = widgetFactory.createIntelBox (new ClickListener ()
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y) // Detailed Report Button
      {
        // TODO Implement detailed report button.
      }
    });

    controlRoomBox = widgetFactory.createControlRoomBox (new ClickListener ()
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y) // Trade In Button
      {
        // TODO Implement trade-in button.
      }
    }, new ClickListener ()
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y) // Fortify Button
      {
        if (isSelfBattlePhase) attackingBattlePhaseHandler.onEndBattlePhase ();
      }
    }, new ClickListener ()
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y) // End Turn Button
      {
        if (isSelfBattlePhase)
        {
          attackingBattlePhaseHandler.onEndBattlePhase ();
          endTurnFromAttackPhase = true;
        }

        if (isSelfFortificationPhase) fortificationPhaseHandler.onEndFortificationPhase ();
      }
    }, new ClickListener ()
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y) // My Settings Button
      {
        // TODO Implement my settings button.
      }
    }, new ClickListener ()
    {
      @Override
      public void clicked (final InputEvent event, final float x, final float y) // Surrender Button
      {
        quitDialog.setTitle ("Surrender & Quit?");
        quitDialog.setMessage (SURRENDER_AND_QUIT_MESSAGE);
        quitDialog.show ();
      }
    });

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);

    // @formatter:off
    final Stack playMapTableStack = new Stack ();
    playMapTable = new Table ().top ().left ().pad (4);
    playMapCell = playMapTable.add (playMap.asActor ()).expand ().fill ();
    playMapTableStack.add (playMapTable);
    playMapTableStack.add (playMapTableForegroundImage);
    // @formatter:on

    final Table sideBarTable = new Table ().top ().left ();
    sideBarTable.add (intelBox.asActor ()).size (296, 484).spaceBottom (6).fill ();
    sideBarTable.row ();
    sideBarTable.add (controlRoomBox.asActor ()).size (296, 318).spaceTop (6).fill ();
    // sideBarTable.debugAll ();

    // @formatter:off
    final Table topTable = new Table ().top ().left ();
    topTable.add (playMapTableStack).size (PlayMapSettings.ACTUAL_WIDTH + 8, PlayMapSettings.ACTUAL_HEIGHT + 8).spaceRight (6).fill ();
    topTable.add (sideBarTable).spaceLeft (6).size (296, PlayMapSettings.ACTUAL_HEIGHT + 8).fill ();
    //topTable.debugAll ();
    // @formatter:on

    final Table bottomTable = new Table ().top ().left ();
    bottomTable.add (statusBox.asActor ()).size (700, 256).spaceRight (6).fill ();
    bottomTable.add (chatBox.asActor ()).size (700, 256).spaceLeft (6).spaceRight (6).fill ();
    bottomTable.add (playerBox.asActor ()).size (498, 256).spaceLeft (6).fill ();
    // bottomTable.debugAll ();

    final Table screenTable = new Table ().top ().left ().pad (5);
    screenTable.add (topTable).height (808).left ().spaceBottom (6).fill ();
    screenTable.row ().spaceTop (6);
    screenTable.add (bottomTable).height (256).fill ();
    // screenTable.debugAll ();

    rootStack.add (screenTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    final ScreenShaker screenShaker = new ScreenShaker (viewport, screenSize);

    stage = new Stage (viewport, batch);

    // @formatter:off

    attackDialog = widgetFactory.createAttackDialog (stage, screenShaker, new DefaultAttackDialogListener ());
    defendDialog = widgetFactory.createDefendDialog (stage, screenShaker, new DefaultDefendDialogListener ());
    attackerBattleResultDialog = widgetFactory.createAttackerBattleResultDialog (stage, new AttackerBattleResultDialogListener ());
    defenderBattleResultDialog = widgetFactory.createDefenderBattleResultDialog (stage, new DefenderBattleResultDialogListener ());
    occupationDialog = widgetFactory.createOccupationDialog (stage, new OccupationDialogListener ());
    fortificationDialog = widgetFactory.createFortificationDialog (stage, new FortificationDialogListener ());
    quitDialog = widgetFactory.createQuitDialog (stage, new QuitDialogListener ());
    allDialogs = new CompositeDialog (attackDialog, defendDialog, attackerBattleResultDialog, defenderBattleResultDialog,
                                      occupationDialog, fortificationDialog, quitDialog);

    reinforcementPhaseHandler = new ReinforcementPhaseHandler (playMap, eventBus);
    manualCountryAssignmentPhaseHandler = new ManualCountryAssignmentPhaseHandler (playMap, eventBus);
    attackingBattlePhaseHandler = new AttackingBattlePhaseHandler (playMap, attackDialog, attackerBattleResultDialog, eventBus);
    defendingBattlePhaseHandler = new DefendingBattlePhaseHandler (playMap, defendDialog, defenderBattleResultDialog, eventBus);
    occupationPhaseHandler = new OccupationPhaseHandler (playMap, occupationDialog, eventBus);
    fortificationPhaseHandler = new FortificationPhaseHandler (playMap, fortificationDialog, eventBus);

    // @formatter:on

    stage.addActor (rootStack);

    stage.addCaptureListener (new InputListener ()
    {
      @Override
      public boolean keyDown (final InputEvent event, final int keycode)
      {
        switch (keycode)
        {
          case Input.Keys.ESCAPE:
          {
            if (attackDialog.isShown () || fortificationDialog.isShown ()) return false;

            quitDialog.setTitle ("Quit?");
            quitDialog.setMessage (QUIT_MESSAGE);
            quitDialog.show ();

            return false;
          }
          default:
          {
            return false;
          }
        }
      }
    });

    stage.addListener (new ClickListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event,
                                final float x,
                                final float y,
                                final int pointer,
                                final int button)
      {
        stage.setKeyboardFocus (event.getTarget ());

        return false;
      }
    });

    final InputProcessor preInputProcessor = new InputAdapter ()
    {
      @Override
      public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
      {
        stage.setKeyboardFocus (null);

        return false;
      }
    };

    keyRepeat = new GdxKeyRepeatSystem (Gdx.input, new GdxKeyRepeatListenerAdapter ()
    {
      @Override
      public void keyDownRepeating (final int keyCode)
      {
        occupationDialog.keyDownRepeating (keyCode);
        fortificationDialog.keyDownRepeating (keyCode);
      }
    });

    keyRepeat.setKeyRepeatRate (Input.Keys.LEFT, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.RIGHT, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.UP, 50);
    keyRepeat.setKeyRepeatRate (Input.Keys.DOWN, 50);
    keyRepeat.setKeyRepeat (Input.Keys.LEFT, true);
    keyRepeat.setKeyRepeat (Input.Keys.RIGHT, true);
    keyRepeat.setKeyRepeat (Input.Keys.UP, true);
    keyRepeat.setKeyRepeat (Input.Keys.DOWN, true);
    keyRepeat.setKeyRepeat (Input.Keys.BACKSPACE, true);
    keyRepeat.setKeyRepeat (Input.Keys.FORWARD_DEL, true);

    final InputMultiplexer inputMultiplexer = new InputMultiplexer (preInputProcessor, stage, this);

    debugInputProcessor = new DebugInputProcessor (debugEventGenerator, mouseInput, playMap, statusBox, chatBox,
            playerBox, occupationDialog, fortificationDialog, attackDialog, defendDialog, eventBus);

    if (DEBUG) inputMultiplexer.addProcessor (debugInputProcessor);

    inputProcessor = inputMultiplexer;
  }

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);
    eventBus.subscribe (reinforcementPhaseHandler);

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (mouseInput.x (), mouseInput.y ());
    playMap.mouseMoved (mouseInput.position ());

    playMapTableForegroundImage.setDrawable (widgetFactory.createPlayMapTableForegroundImageDrawable ());

    intelBox.refreshAssets ();
    controlRoomBox.refreshAssets ();
    statusBox.refreshAssets ();
    chatBox.refreshAssets ();
    playerBox.refreshAssets ();
    allDialogs.refreshAssets ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0.0f, 0.0f, 0.0f, 1.0f);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    keyRepeat.update ();
    stage.act (delta);
    allDialogs.update (delta);
    stage.draw ();
  }

  @Override
  public void resize (final int width, final int height)
  {
    stage.getViewport ().update (width, height, true);
    stage.getViewport ().setScreenPosition (InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_X,
                                            InputSettings.ACTUAL_INPUT_SPACE_TO_ACTUAL_SCREEN_SPACE_TRANSLATION_Y);
  }

  @Override
  public void pause ()
  {
  }

  @Override
  public void resume ()
  {
  }

  @Override
  public void hide ()
  {
    eventBus.unsubscribe (this);
    eventBus.unsubscribe (reinforcementPhaseHandler);
    eventBus.unsubscribe (manualCountryAssignmentPhaseHandler);
    eventBus.unsubscribe (attackingBattlePhaseHandler);
    eventBus.unsubscribe (defendingBattlePhaseHandler);
    eventBus.unsubscribe (occupationPhaseHandler);
    eventBus.unsubscribe (fortificationPhaseHandler);

    stage.unfocusAll ();

    Gdx.input.setInputProcessor (null);

    hideCursor ();

    intelBox.clear ();
    chatBox.clear ();
    statusBox.clear ();
    playerBox.clear ();
    attackingBattlePhaseHandler.reset ();
    defendingBattlePhaseHandler.reset ();
    debugInputProcessor.reset ();

    clearPlayMap ();

    allDialogs.hide (null);
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);
    stage.dispose ();
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

    selfPlayer = event.getSelfPlayer ();

    attackingBattlePhaseHandler.setSelfPlayer (selfPlayer);
    defendingBattlePhaseHandler.setSelfPlayer (selfPlayer);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        updatePlayMap (event.getPlayMap ());
        intelBox.setMapMetadata (event.getMapMetadata ());
        intelBox.setGameServerConfiguration (event.getGameServerConfiguration ());
        intelBox.setClientConfiguration (event.getClientConfiguration ());
        intelBox.setPlayer (event.getSelfPlayer ());
        playerBox.setPlayers (event.getAllPlayers ());
        debugEventGenerator.makePlayersUnavailable (event.getAllPlayers ());
      }
    });

    final GameServerConfiguration config = event.getGameServerConfiguration ();
    final int nMorePlayers = config.getPlayerLimit () - event.getPlayerCount ();

    status ("Welcome, {}.", event.getSelfPlayerName ());
    statusOn (event.isFirstPlayerInGame (), "It looks like you're the first one here.");
    statusOn (nMorePlayers > 0, "The game will begin when {}.",
              Strings.pluralize (nMorePlayers, "more player joins", "more players join"));
    statusOn (nMorePlayers > 0, "This is a {} player {} Mode game. You must conquer {}% of the map to achieve victory.",
              config.getPlayerLimit (), Strings.toProperCase (config.getGameMode ().toString ()),
              config.getWinPercentage ());
  }

  @Handler
  void onEvent (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        statusBox.addRow (widgetFactory.createStatusMessageBoxRow (event.getMessage ()));
        statusBox.showLastRow ();
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

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playerBox.addPlayer (event.getPlayer ());
        debugEventGenerator.makePlayerUnavailable (event.getPlayer ());
      }
    });

    statusOn (!isSelf (event.getPlayer ()), "{} joined the game.", event.getPlayerName ());
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
        playMap.setCountryState (event.getCountryName (),
                                 CountryPrimaryImageState.fromPlayerColor (event.getNewOwner ().getColor ()));
      }
    });

    statusOn (!isSelf (event.getNewOwner ()), "{} now owns {}.", event.getNewOwnerName (), event.getCountryName ());
    statusOn (isSelf (event.getNewOwner ()), "You now own {}.", event.getCountryName ());
  }

  @Handler
  void onEvent (final CountryArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    final String countryName = event.getCountryName ();

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.setArmies (event.getCountryArmyCount (), countryName);
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

    status ("The order of turns has been decided.");

    for (final PlayerPacket player : event.getPlayersSortedByTurnOrder ())
    {
      final String turn = Strings.toMixedOrdinal (player.getTurnOrder ());
      statusOn (!isSelf (player), "{} is going {}.", player.getName (), turn);
      statusOn (isSelf (player), "You are going {}.", turn);
    }
  }

  @Handler
  void onEvent (final BeginPlayerCountryAssignmentEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (event.getAssignmentMode ().is (InitialCountryAssignment.MANUAL))
    {
      eventBus.subscribe (manualCountryAssignmentPhaseHandler);
    }

    final String mode = Strings.toProperCase (String.valueOf (event.getAssignmentMode ()));

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName (Strings.format ("{} Country Assignment", mode));
      }
    });

    status ("{} country assignment phase.", mode);
  }

  @Handler
  void onEvent (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (event.getAssignmentMode ().is (InitialCountryAssignment.MANUAL))
    {
      eventBus.unsubscribe (manualCountryAssignmentPhaseHandler);
    }
  }

  @Handler
  void onEvent (final BeginInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    eventBus.subscribe (reinforcementPhaseHandler);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Initial Reinforcement");
      }
    });

    statusOn (isSelf (event.getPlayer ()), "Initial Reinforcement phase.");
    statusOn (!isSelf (event.getPlayer ()), "{} is placing initial reinforcements...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerReinforceCountrySuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String playerName = isSelf (event.getPlayer ()) ? "You" : event.getPlayerName ();

    status ("{} placed {} on {}.", playerName,
            Strings.pluralize (Math.abs (event.getPlayerDeltaArmyCount ()), "army", "armies"), event.getCountryName ());
  }

  @Handler
  void onEvent (final EndInitialReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    eventBus.unsubscribe (reinforcementPhaseHandler);
  }

  @Handler
  void onEvent (final BeginPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    statusOn (isSelf (event.getPlayer ()), "It is your turn.");
    statusOn (!isSelf (event.getPlayer ()), "It is {}'s turn.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BeginReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    eventBus.subscribe (reinforcementPhaseHandler);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Reinforcement");
      }
    });

    statusOn (isSelf (event.getPlayer ()), "Reinforcement phase.");
    statusOn (!isSelf (event.getPlayer ()), "{} is placing reinforcements...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerCardTradeInAvailableEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    // TODO Implement trade-in.

    if (!event.isTradeInRequired ()) return;

    eventBus.publish (new PlayerTradeInCardsRequestEvent (event.getMatches ().iterator ().next ()));
  }

  @Handler
  void onEvent (final PlayerTradeInCardsResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String playerName = isSelf (event.getPlayer ()) ? "You" : event.getPlayerName ();

    status ("{} purchased {} reinforcements!", playerName, event.getTradeInBonus ());
  }

  @Handler
  void onEvent (final EndReinforcementPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    eventBus.unsubscribe (reinforcementPhaseHandler);

    statusOn (!isSelf (event.getPlayer ()), "{} is finished reinforcing.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BeginAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isSelf (event.getPlayer ()))
    {
      isSelfBattlePhase = true;
      attackingBattlePhaseHandler.reset ();
      occupationPhaseHandler.reset ();
      eventBus.subscribe (attackingBattlePhaseHandler);
      eventBus.subscribe (occupationPhaseHandler);
    }
    else
    {
      defendingBattlePhaseHandler.reset ();
      eventBus.subscribe (defendingBattlePhaseHandler);
    }

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Attack");
      }
    });

    statusOn (isSelf (event.getPlayer ()), "Attack phase.");
    statusOn (!isSelf (event.getPlayer ()), "{} is deciding where to attack...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BattleSetupEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final boolean isAttacker = isSelf (event.getAttackingPlayer ());
    final boolean isDefender = isSelf (event.getDefendingPlayer ());
    final String attacker = isAttacker ? "You" : event.getAttackingPlayerName ();
    final String defender = isDefender ? "You" : event.getDefendingPlayerName ();
    final String attackerCountry = event.getAttackingCountryName ();
    final String defenderCountry = event.getDefendingCountryName ();

    status ("{} {} attacking {} in {} from {}!", attacker, isAttacker ? "are" : "is", defender, defenderCountry,
            attackerCountry);
  }

  @Handler
  void onEvent (final PlayerOrderAttackSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final int defenderLoss = Math.abs (event.getDefendingCountryArmyDelta ());
    final int attackerLoss = Math.abs (event.getAttackingCountryArmyDelta ());
    final boolean defenderLostArmies = defenderLoss > 0;
    final boolean attackerLostArmies = attackerLoss > 0;
    final boolean defenderOnlyLostArmies = defenderLostArmies && !attackerLostArmies;
    final boolean attackerOnlyLostArmies = !defenderLostArmies && attackerLostArmies;
    final boolean bothLostArmies = attackerLostArmies && defenderLostArmies;
    final String attacker = isSelf (event.getAttackingPlayer ()) ? "You" : event.getAttackingPlayerName ();
    final String defender = isSelf (event.getDefendingPlayer ()) ? "you" : event.getDefendingPlayerName ();
    final String attackerCountry = event.getAttackingCountryName ();
    final String defenderCountry = event.getDefendingCountryName ();
    final String defenderLossInWords = Strings.pluralize (defenderLoss, "no armies", "an army",
                                                          defenderLoss + " armies");
    final String attackerLossInWords = Strings.pluralize (attackerLoss, "no armies", "an army",
                                                          defenderLoss + " armies");

    statusOn (bothLostArmies, "{} attacked {} in {} from {}, destroying {} & losing {}!", attacker, defender,
              defenderCountry, attackerCountry, defenderLossInWords, attackerLossInWords);

    statusOn (attackerOnlyLostArmies, "{} attacked {} in {} from {} & lost {}!", attacker, defender, defenderCountry,
              attackerCountry, attackerLossInWords);

    statusOn (defenderOnlyLostArmies, "{} attacked {} in {} from {} & destroyed {}!", attacker, defender,
              defenderCountry, attackerCountry, defenderLossInWords);

    statusOn (event.battleOutcomeIs (BattleOutcome.ATTACKER_VICTORIOUS), "{} conquered {}, defeating {} in battle.",
              attacker, defenderCountry, defender);

    statusOn (event.battleOutcomeIs (BattleOutcome.ATTACKER_DEFEATED),
              "{} failed to conquer {}, defeated by {} in battle.", attacker, defenderCountry, defender);
  }

  @Handler
  void onEvent (final PlayerOrderRetreatSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    statusOn (isSelf (event.getAttackingPlayer ()), "You stopped attacking {} in {} from {}.",
              event.getDefendingPlayerName (), event.getDefendingCountryName (), event.getAttackingCountryName ());

    statusOn (isSelf (event.getDefendingPlayer ()), "{} stopped attacking you in {} from {}.",
              event.getAttackingPlayerName (), event.getDefendingCountryName (), event.getAttackingCountryName ());

    statusOn (!isSelf (event.getAttackingPlayer ()) && !isSelf (event.getDefendingPlayer ()),
              "{} stopped attacking {} in {} from {}.", event.getAttackingPlayerName (),
              event.getDefendingPlayerName (), event.getDefendingCountryName (), event.getAttackingCountryName ());
  }

  @Handler
  void onEvent (final PlayerLoseGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    statusOn (isSelf (event.getPlayer ()), "General, we have lost the war.");
    statusOn (!isSelf (event.getPlayer ()), "{} was annihilated.", event.getPlayerName ());
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
      }
    });

    statusOn (isSelf (event.getPlayer ()), "General, we won the war!");
    statusOn (!isSelf (event.getPlayer ()), "{} won the game.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerOccupyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String player = isSelf (event.getPlayer ()) ? "You" : event.getPlayerName ();
    final String country = event.getDestinationCountryName ();
    final int armies = Math.abs (event.getDeltaArmyCount ());

    status ("{} occupied {} with {}.", player, country, Strings.pluralize (armies, "army", "armies"));
    statusOn (armies == 1, "Looks like an easy target.");
  }

  @Handler
  void onEvent (final EndAttackPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isSelf (event.getPlayer ()))
    {
      isSelfBattlePhase = false;
      attackingBattlePhaseHandler.reset ();
      occupationPhaseHandler.reset ();
      eventBus.unsubscribe (attackingBattlePhaseHandler);
      eventBus.unsubscribe (occupationPhaseHandler);
    }
    else
    {
      defendingBattlePhaseHandler.reset ();
      eventBus.unsubscribe (defendingBattlePhaseHandler);
    }

    statusOn (!isSelf (event.getPlayer ()), "{} is finished attacking.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final BeginFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (endTurnFromAttackPhase)
    {
      endTurnFromAttackPhase = false;
      fortificationPhaseHandler.onEndFortificationPhase ();
      return;
    }

    if (isSelf (event.getPlayer ()))
    {
      isSelfFortificationPhase = true;
      fortificationPhaseHandler.reset ();
      eventBus.subscribe (fortificationPhaseHandler);
    }

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        intelBox.setGamePhaseName ("Post-Combat Maneuver");
      }
    });

    statusOn (isSelf (event.getPlayer ()), "Post-Combat Maneuver phase.");
    statusOn (!isSelf (event.getPlayer ()), "{} is considering a post-combat maneuver...", event.getPlayerName ());
  }

  @Handler
  void onEvent (final PlayerFortifyCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    final String player = isSelf (event.getPlayer ()) ? "You" : event.getPlayerName ();
    final String sourceCountry = event.getSourceCountryName ();
    final String destCountry = event.getTargetCountryName ();
    final String armies = Strings.pluralize (event.getDeltaArmyCount (), "army", "armies");

    status ("{} maneuvered {} into {} from {}.", player, armies, destCountry, sourceCountry);
  }

  @Handler
  void onEvent (final EndFortifyPhaseEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    if (isSelf (event.getPlayer ()))
    {
      isSelfFortificationPhase = false;
      fortificationPhaseHandler.reset ();
      eventBus.unsubscribe (fortificationPhaseHandler);
    }

    statusOn (!isSelf (event.getPlayer ()), "{} is finished maneuvering.", event.getPlayerName ());
  }

  @Handler
  void onEvent (final EndPlayerTurnEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    statusOn (isSelf (event.getPlayer ()), "Your turn is over.");
    statusOn (!isSelf (event.getPlayer ()), "{}'s turn is over.", event.getPlayerName ());
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

    statusOn (!isSelf (event.getPlayer ()), "{} left the game.", event.getPlayerName ());
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
        screenChanger.toScreen (ScreenId.PLAY_TO_MENU_LOADING);
      }
    });
  }

  private static void hideCursor ()
  {
    Gdx.graphics.setSystemCursor (Cursor.SystemCursor.Arrow);
  }

  private void status (final String statusMessage)
  {
    eventBus.publish (StatusMessageEventFactory.create (statusMessage));
  }

  private void status (final String statusMessage, final Object... args)
  {
    eventBus.publish (StatusMessageEventFactory.create (statusMessage, args));
  }

  private void statusOn (final boolean condition, final String statusMessage)
  {
    if (!condition) return;
    status (statusMessage);
  }

  private void statusOn (final boolean condition, final String statusMessage, final Object... args)
  {
    if (!condition) return;
    status (statusMessage, args);
  }

  private boolean isSelf (final PlayerPacket player)
  {
    return selfPlayer != null && player.is (selfPlayer);
  }

  private void showCursor ()
  {
    Gdx.graphics.setCursor (normalCursor);
  }

  private void updatePlayMap (final PlayMap playMap)
  {
    this.playMap = playMap;
    playMapCell.setActor (this.playMap.asActor ());
    intelBox.setMapMetadata (playMap.getMapMetadata ());
    reinforcementPhaseHandler.setPlayMap (playMap);
    manualCountryAssignmentPhaseHandler.setPlayMap (playMap);
    attackingBattlePhaseHandler.setPlayMap (playMap);
    defendingBattlePhaseHandler.setPlayMap (playMap);
    occupationPhaseHandler.setPlayMap (playMap);
    fortificationPhaseHandler.setPlayMap (playMap);
    debugInputProcessor.setPlayMap (this.playMap);
  }

  private void clearPlayMap ()
  {
    playMap.reset ();
    playMapCell.clearActor ();
    widgetFactory.destroyPlayMap (playMap.getMapMetadata ());
    updatePlayMap (PlayMap.NULL_PLAY_MAP);
  }

  private final class DefaultAttackDialogListener extends AbstractBattleDialogListener implements AttackDialogListener
  {
    @Override
    public void onBattle ()
    {
      attackingBattlePhaseHandler.onBattle ();
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
    public void onRetreat ()
    {
      attackingBattlePhaseHandler.onRetreat ();
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

      playMap.enable (mouseInput.position ());
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

      playMap.enable (mouseInput.position ());
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
      attackDialog.hide ();
      if (attackerBattleResultDialog.battleOutcomeIs (BattleOutcome.ATTACKER_VICTORIOUS)) occupationDialog.show ();
      if (!occupationDialog.isShown () && !quitDialog.isShown ()) playMap.enable (mouseInput.position ());
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
      defendDialog.hide ();
      if (!quitDialog.isShown ()) playMap.enable (mouseInput.position ());
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
      if (!quitDialog.isShown ()) playMap.enable (mouseInput.position ());
    }
  }

  private final class FortificationDialogListener implements CancellableDialogListener
  {
    @Override
    public void onSubmit ()
    {
      fortificationPhaseHandler.onFortify ();
    }

    @Override
    public void onCancel ()
    {
      fortificationPhaseHandler.onCancel ();
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (!quitDialog.isShown ()) playMap.enable (mouseInput.position ());
    }
  }

  private final class QuitDialogListener extends CancellableDialogListenerAdapter
  {
    @Override
    public void onSubmit ()
    {
      screenChanger.toScreen (ScreenId.PLAY_TO_MENU_LOADING);
      eventBus.publishAsync (new QuitGameEvent ());
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onCancel ()
    {
      if (!attackDialog.isShown () && !defendDialog.isShown () && !occupationDialog.isShown ()
              && !fortificationDialog.isShown () && !attackerBattleResultDialog.isShown ()
              && !defenderBattleResultDialog.isShown ())
      {
        playMap.enable (mouseInput.position ());
      }
    }
  }
}
