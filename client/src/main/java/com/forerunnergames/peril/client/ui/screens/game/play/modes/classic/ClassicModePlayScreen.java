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
import com.badlogic.gdx.audio.Sound;
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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.events.DefaultStatusMessageEvent;
import com.forerunnergames.peril.client.events.PlayGameEvent;
import com.forerunnergames.peril.client.events.QuitGameEvent;
import com.forerunnergames.peril.client.events.StatusMessageEvent;
import com.forerunnergames.peril.client.events.StatusMessageEventFactory;
import com.forerunnergames.peril.client.input.GdxKeyRepeatListenerAdapter;
import com.forerunnergames.peril.client.input.GdxKeyRepeatSystem;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.messages.DefaultStatusMessage;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenShaker;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugEventGenerator;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugPackets;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.Country;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.actors.PlayMap;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playmap.images.CountryPrimaryImageState;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.controlroombox.ControlRoomBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.armymovement.occupation.OccupationDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.armymovement.reinforcement.ReinforcementDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.AbstractBattleDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.BattleOutcome;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.attack.AttackDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.attack.AttackDialogListener;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.dialogs.battle.defend.DefendDialog;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.intelbox.IntelBox;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListener;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.chatbox.ChatBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox.PlayerBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.statusbox.StatusBoxRow;
import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.peril.common.net.events.server.defaults.DefaultCountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.CountryArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.ActivePlayerChangedEvent;
import com.forerunnergames.peril.common.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.common.net.events.server.success.ChatMessageSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerClaimCountryResponseSuccessEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ClassicModePlayScreen extends InputAdapter implements Screen
{
  private static final Logger log = LoggerFactory.getLogger (ClassicModePlayScreen.class);
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
  private final ReinforcementDialog reinforcementDialog;
  private final AttackDialog attackDialog;
  private final DefendDialog defendDialog;
  private final Dialog battleResultDialog;
  private final Dialog quitDialog;
  private final Vector2 tempPosition = new Vector2 ();
  private final BattleOutcome tempOutcome = new BattleOutcome ();
  private final Cell <Actor> playMapCell;
  private final ScreenShaker screenShaker;
  private final DebugInputProcessor debugInputProcessor;
  private final ImmutableCollection <Dialog> dialogs;
  private Sound battleSingleExplosionSound;
  private PlayMap playMap = PlayMap.NULL_PLAY_MAP;

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
      public void clicked (final InputEvent event, final float x, final float y)
      {
        // TODO Implement detailed report button.
        // TODO Production: Remove.
        eventBus.publish (StatusMessageEventFactory.create ("Detailed Report button clicked."));
      }
    });

    controlRoomBox = widgetFactory.createControlRoomBox (
            new ClickListener ()
            {
              @Override
              public void clicked (final InputEvent event, final float x, final float y)
              {
                // TODO Implement trade-in button.
                // TODO Production: Remove.
                eventBus.publish (StatusMessageEventFactory.create ("Purchase Reinforcements button clicked."));
              }
            },
            new ClickListener ()
            {
              @Override
              public void clicked (final InputEvent event, final float x, final float y)
              {
                // TODO Implement fortify button.
                // TODO Production: Remove.
                eventBus.publish (StatusMessageEventFactory.create ("Post-Combat Maneuver button clicked."));
              }
            },
            new ClickListener ()
            {
              @Override
              public void clicked (final InputEvent event, final float x, final float y)
              {
                // TODO Implement end turn button.
                // TODO Production: Remove.
                eventBus.publish (StatusMessageEventFactory.create ("End Turn button clicked."));
              }
            },
            new ClickListener ()
            {
              @Override
              public void clicked (final InputEvent event, final float x, final float y)
              {
                // TODO Implement my settings button.
                // TODO Production: Remove.
                eventBus.publish (StatusMessageEventFactory.create ("My Settings button clicked."));
              }
            },
            new ClickListener ()
            {
              @Override
              public void clicked (final InputEvent event, final float x, final float y)
              {
                // TODO Implement surrender button.
                // TODO Production: Remove.
                eventBus.publish (StatusMessageEventFactory.create ("Surrender button clicked."));
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
    //sideBarTable.debugAll ();

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
    //bottomTable.debugAll ();

    final Table screenTable = new Table ().top ().left ().pad (5);
    screenTable.add (topTable).height (808).left ().spaceBottom (6).fill ();
    screenTable.row ().spaceTop (6);
    screenTable.add (bottomTable).height (256).fill ();
    // screenTable.debugAll ();

    rootStack.add (screenTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    screenShaker = new ScreenShaker (viewport, screenSize);

    stage = new Stage (viewport, batch);

    occupationDialog = widgetFactory.createOccupationDialog (stage, eventBus, new OccupationDialogListener ());
    reinforcementDialog = widgetFactory.createReinforcementDialog (stage, eventBus, new ReinforcementDialogListener ());
    attackDialog = widgetFactory.createAttackDialog (stage, eventBus, new DefaultAttackDialogListener ());
    defendDialog = widgetFactory.createDefendDialog (stage, eventBus, new DefendDialogListener ());
    battleResultDialog = widgetFactory.createBattleResultDialog (stage, new BattleResultDialogListener ());
    quitDialog = widgetFactory.createQuitDialog (stage, new QuitDialogListener ());

    dialogs = ImmutableList.of (occupationDialog, reinforcementDialog, attackDialog, defendDialog, battleResultDialog,
                                quitDialog);

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
            if (!attackDialog.isShown () && !reinforcementDialog.isShown ()) quitDialog.show ();

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
        reinforcementDialog.keyDownRepeating (keyCode);
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

    debugInputProcessor = new DebugInputProcessor (debugEventGenerator, mouseInput, playMap, statusBox, chatBox,
            playerBox, occupationDialog, reinforcementDialog, attackDialog, defendDialog, eventBus);

    battleSingleExplosionSound = widgetFactory.createBattleSingleExplosionSound ();

    inputProcessor = new InputMultiplexer (preInputProcessor, stage, this, debugInputProcessor);
  }

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (mouseInput.x (), mouseInput.y ());
    playMap.mouseMoved (mouseInput.position ());

    playMapTableForegroundImage.setDrawable (widgetFactory.createPlayMapTableForegroundImageDrawable ());

    intelBox.refreshAssets ();
    controlRoomBox.refreshAssets ();
    statusBox.refreshAssets ();
    chatBox.refreshAssets ();
    playerBox.refreshAssets ();

    for (final Dialog dialog : dialogs)
    {
      dialog.refreshAssets ();
    }

    battleSingleExplosionSound = widgetFactory.createBattleSingleExplosionSound ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0.0f, 0.0f, 0.0f, 0.0f);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    keyRepeat.update ();
    stage.act (delta);

    for (final Dialog dialog : dialogs)
    {
      dialog.update (delta);
    }

    screenShaker.update (delta);
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

    stage.unfocusAll ();

    Gdx.input.setInputProcessor (null);

    hideCursor ();

    intelBox.clear ();
    chatBox.clear ();
    statusBox.clear ();
    playerBox.clear ();
    debugInputProcessor.reset ();

    clearPlayMap ();

    for (final Dialog dialog : dialogs)
    {
      dialog.hide (null);
    }

    battleSingleExplosionSound.stop ();
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

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        updatePlayMap (event.getPlayMap ());
        intelBox.setMapMetadata (event.getMapMetadata ());
        intelBox.setGameServerConfiguration (event.getGameServerConfiguration ());
        intelBox.setClientConfiguration (event.getClientConfiguration ());
        playerBox.setPlayers (event.getPlayersInGame ());
        debugEventGenerator.makePlayersUnavailable (event.getPlayersInGame ());
      }
    });
  }

  @Handler
  void onEvent (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

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

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.debug ("Event received [{}].", event);

        chatBox.addRow (widgetFactory.createChatMessageBoxRow (event.getMessage ()));
        chatBox.showLastRow ();
      }
    });
  }

  @Handler
  void onEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playerBox.addPlayer (event.getPlayer ());
        intelBox.setPlayer (event.getPlayer ());
        debugEventGenerator.makePlayerUnavailable (event.getPlayer ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        log.trace ("Event received [{}].", event);

        playerBox.removePlayer (event.getPlayer ());
        debugEventGenerator.makePlayerAvailable (event.getPlayer ());
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
        playMap.changeArmiesBy (event.getCountryDeltaArmyCount (), event.getCountryName ());
      }
    });
  }

  @Handler
  void onEvent (final DeterminePlayerTurnOrderCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playerBox.setPlayers (event.getPlayersSortedByTurnOrder ());
      }
    });
  }

  @Handler
  void onEvent (final PlayerClaimCountryResponseSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        playMap.setCountryState (event.getCountryName (), CountryPrimaryImageState.valueOf (Strings.toCase (event
                .getPlayerColor (), LetterCase.UPPER)));
      }
    });
  }

  @Handler
  void onEvent (final PlayerCountryAssignmentCompleteEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        for (final CountryPacket country : event.getCountries ())
        {
          final CountryPrimaryImageState state = CountryPrimaryImageState.valueOf (Strings.toCase (event
                  .getOwnerColor (country), LetterCase.UPPER));

          if (playMap.primaryImageStateOfCountryIs (state, country.getName ())) continue;

          playMap.setCountryState (country.getName (), state);
        }
      }
    });
  }

  @Handler
  void onEvent (final ActivePlayerChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

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
  void onEvent (final QuitGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

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

  private void showCursor ()
  {
    Gdx.graphics.setCursor (normalCursor);
  }

  private void updatePlayMap (final PlayMap playMap)
  {
    this.playMap = playMap;
    playMapCell.setActor (this.playMap.asActor ());
    intelBox.setMapMetadata (playMap.getMapMetadata ());
    debugInputProcessor.setPlayMap (this.playMap);
  }

  private void clearPlayMap ()
  {
    playMap.reset ();
    playMapCell.clearActor ();
    widgetFactory.destroyPlayMap (playMap.getMapMetadata ());
    playMap = PlayMap.NULL_PLAY_MAP;
    playMapCell.setActor (playMap.asActor ());
    intelBox.setMapMetadata (playMap.getMapMetadata ());
    debugInputProcessor.setPlayMap (playMap);
  }

  private void playBattleEffects (final int attackingCountryDeltaArmies, final int defendingCountryDeltaArmies)
  {
    if (attackingCountryDeltaArmies == 0 && defendingCountryDeltaArmies == 0) return;

    battleSingleExplosionSound.play ();
    screenShaker.shake ();

    if (attackingCountryDeltaArmies == -2 || defendingCountryDeltaArmies == -2)
    {
      Timer.schedule (new Timer.Task ()
      {
        @Override
        public void run ()
        {
          battleSingleExplosionSound.play ();
          screenShaker.shake ();
        }
      }, 0.25f);
    }
  }

  private final class OccupationDialogListener implements DialogListener
  {
    @Override
    public void onSubmit ()
    {
      final int deltaArmies = occupationDialog.getDeltaArmies ();
      final String sourceCountryName = occupationDialog.getSourceCountryName ();
      final String destinationCountryName = occupationDialog.getDestinationCountryName ();

      // TODO Production: Remove
      eventBus.publish (new DefaultStatusMessageEvent (new DefaultStatusMessage ("You occupied "
              + destinationCountryName + " with " + Strings.pluralize (deltaArmies, "army", "armies") + " from "
              + sourceCountryName + "."), ImmutableSet.<PlayerPacket> of ()));

      // TODO Production: Remove
      eventBus.publish (new DefaultCountryArmiesChangedEvent (DebugPackets.from (sourceCountryName), -deltaArmies));

      // TODO Production: Remove
      eventBus.publish (new DefaultCountryArmiesChangedEvent (DebugPackets.from (destinationCountryName), deltaArmies));

      // TODO: Production: Publish event (OccupyCountryRequestEvent?)
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (quitDialog.isShown ()) return;

      playMap.enable (mouseInput.position ());
    }
  }

  private final class ReinforcementDialogListener implements DialogListener
  {
    @Override
    public void onSubmit ()
    {
      final int deltaArmies = reinforcementDialog.getDeltaArmies ();
      final String sourceCountryName = reinforcementDialog.getSourceCountryName ();
      final String destinationCountryName = reinforcementDialog.getDestinationCountryName ();

      // TODO Production: Remove
      eventBus.publish (new DefaultStatusMessageEvent (new DefaultStatusMessage ("You reinforced "
              + destinationCountryName + " with " + Strings.pluralize (deltaArmies, "army", "armies") + " from "
              + sourceCountryName + "."), ImmutableSet.<PlayerPacket> of ()));

      // TODO Production: Remove
      eventBus.publish (new DefaultCountryArmiesChangedEvent (DebugPackets.from (sourceCountryName), -deltaArmies));

      // TODO Production: Remove
      eventBus.publish (new DefaultCountryArmiesChangedEvent (DebugPackets.from (destinationCountryName), deltaArmies));

      // TODO: Production: Publish event (OccupyCountryRequestEvent?)
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();
    }

    @Override
    public void onHide ()
    {
      if (quitDialog.isShown ()) return;

      playMap.enable (mouseInput.position ());
    }
  }

  private final class DefaultAttackDialogListener extends AbstractBattleDialogListener implements AttackDialogListener
  {
    @Override
    public void onBattle ()
    {
      final ImmutableList.Builder <DieFaceValue> attackerDieFaceValuesBuilder = ImmutableList.builder ();

      for (int i = 0; i < attackDialog.getActiveAttackerDieCount (); ++i)
      {
        attackerDieFaceValuesBuilder.add (Randomness.getRandomElementFrom (DieFaceValue.values ()));
      }

      final ImmutableList <DieFaceValue> attackerDieFaceValues = attackerDieFaceValuesBuilder.build ();

      attackDialog.rollAttackerDice (attackerDieFaceValues);

      final ImmutableList.Builder <DieFaceValue> defenderDieFaceValuesBuilder = ImmutableList.builder ();

      for (int i = 0; i < attackDialog.getActiveDefenderDieCount (); ++i)
      {
        defenderDieFaceValuesBuilder.add (Randomness.getRandomElementFrom (DieFaceValue.values ()));
      }

      final ImmutableList <DieFaceValue> defenderDieFaceValues = defenderDieFaceValuesBuilder.build ();

      attackDialog.rollDefenderDice (defenderDieFaceValues);

      tempOutcome.set (attackDialog.determineOutcome (attackerDieFaceValues, defenderDieFaceValues));

      final String attackingCountryName = tempOutcome.getAttackingCountryName ();
      final String defendingCountryName = tempOutcome.getDefendingCountryName ();
      final String defendingPlayerName = tempOutcome.getDefendingPlayerName ();
      final int attackingCountryDeltaArmies = tempOutcome.getAttackingCountryDeltaArmies ();
      final int defendingCountryDeltaArmies = tempOutcome.getDefendingCountryDeltaArmies ();

      playBattleEffects (attackingCountryDeltaArmies, defendingCountryDeltaArmies);

      // TODO Production: Remove
      if (attackingCountryDeltaArmies != 0)
      {
        final CountryPacket attackingCountry = DebugPackets.from (attackingCountryName);
        eventBus.publish (new DefaultCountryArmiesChangedEvent (attackingCountry, attackingCountryDeltaArmies));
      }

      // TODO Production: Remove
      if (defendingCountryDeltaArmies != 0)
      {
        final CountryPacket defendingCountry = DebugPackets.from (defendingCountryName);
        eventBus.publish (new DefaultCountryArmiesChangedEvent (defendingCountry, defendingCountryDeltaArmies));
      }

      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("You attacked {} in {} from {}, destroying {} & losing {}!", defendingPlayerName,
                       defendingCountryName, attackingCountryName,
                       Strings.pluralize (Math.abs (defendingCountryDeltaArmies), "army", "armies"),
                       Strings.pluralize (Math.abs (attackingCountryDeltaArmies), "army", "armies")), ImmutableSet
              .<PlayerPacket> of ()));
    }

    @Override
    public void onRetreat ()
    {
      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings.format ("You stopped attacking {} in {} from {}.",
                                                                          attackDialog.getDefendingPlayerName (),
                                                                          attackDialog.getDefendingCountryName (),
                                                                          attackDialog.getAttackingCountryName ()),
                                                          ImmutableSet.<PlayerPacket> of ()));
    }

    @Override
    public void onAttackerWinFinal ()
    {
      // @formatter:off
      final String attackingCountryName = attackDialog.getAttackingCountryName ();
      final String defendingCountryName = attackDialog.getDefendingCountryName ();
      final CountryPrimaryImageState attackingCountryPrimaryImageState = playMap.getPrimaryImageStateOf (attackingCountryName);
      final int totalArmies = attackDialog.getAttackingCountryArmies ();
      final int minDestinationArmies = attackDialog.getActiveAttackerDieCount ();
      final int maxDestinationArmies = totalArmies - 1;
      final Country sourceCountry = attackDialog.getAttackingCountry ();
      final Country destinationCountry = attackDialog.getDefendingCountry ();
      // @formatter:on

      // TODO Production: Remove
      if (attackingCountryPrimaryImageState != null)
      {
        attackDialog.getDefendingCountry ().changePrimaryStateTo (attackingCountryPrimaryImageState);
        playMap.setCountryState (defendingCountryName, attackingCountryPrimaryImageState);
      }

      attackDialog.hide ();

      occupationDialog
              .show (minDestinationArmies, maxDestinationArmies, sourceCountry, destinationCountry, totalArmies);

      battleResultDialog.setTitle ("Victory");
      battleResultDialog.setMessage (new DefaultMessage ("General, you conquered " + defendingCountryName
              + "!\nWe must now occupy it quickly."));
      battleResultDialog.show ();

      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings.format ("You conquered {}!",
                                                                          attackDialog.getDefendingCountryName ()),
                                                          ImmutableSet.<PlayerPacket> of ()));
    }

    @Override
    public void onAttackerLoseFinal ()
    {
      attackDialog.hide ();

      battleResultDialog.setTitle ("Defeat");
      battleResultDialog.setMessage (new DefaultMessage ("General, we have failed to conquer "
              + attackDialog.getDefendingCountryName () + "."));
      battleResultDialog.show ();

      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings.format ("You failed to conquer {}.",
                                                                          attackDialog.getDefendingCountryName ()),
                                                          ImmutableSet.<PlayerPacket> of ()));
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();

      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("You are preparing to attack {} in {} from {}.", attackDialog.getDefendingPlayerName (),
                       attackDialog.getDefendingCountryName (), attackDialog.getAttackingCountryName ()), ImmutableSet
              .<PlayerPacket> of ()));

      attackDialog.startBattle ();
    }

    @Override
    public void onHide ()
    {
    }
  }

  private final class DefendDialogListener extends AbstractBattleDialogListener
  {
    @Override
    public void onBattle ()
    {
      final ImmutableList.Builder <DieFaceValue> defenderDieFaceValuesBuilder = ImmutableList.builder ();

      for (int i = 0; i < defendDialog.getActiveDefenderDieCount (); ++i)
      {
        defenderDieFaceValuesBuilder.add (Randomness.getRandomElementFrom (DieFaceValue.values ()));
      }

      final ImmutableList <DieFaceValue> defenderDieFaceValues = defenderDieFaceValuesBuilder.build ();

      defendDialog.rollDefenderDice (defenderDieFaceValues);

      final ImmutableList.Builder <DieFaceValue> attackerDieFaceValuesBuilder = ImmutableList.builder ();

      for (int i = 0; i < defendDialog.getActiveAttackerDieCount (); ++i)
      {
        attackerDieFaceValuesBuilder.add (Randomness.getRandomElementFrom (DieFaceValue.values ()));
      }

      final ImmutableList <DieFaceValue> attackerDieFaceValues = attackerDieFaceValuesBuilder.build ();

      defendDialog.rollAttackerDice (attackerDieFaceValues);

      tempOutcome.set (defendDialog.determineOutcome (attackerDieFaceValues, defenderDieFaceValues));

      final String attackingCountryName = tempOutcome.getAttackingCountryName ();
      final String defendingCountryName = tempOutcome.getDefendingCountryName ();
      final String attackingPlayerName = tempOutcome.getAttackingPlayerName ();
      final int attackingCountryDeltaArmies = tempOutcome.getAttackingCountryDeltaArmies ();
      final int defendingCountryDeltaArmies = tempOutcome.getDefendingCountryDeltaArmies ();

      playBattleEffects (attackingCountryDeltaArmies, defendingCountryDeltaArmies);

      // TODO Production: Remove
      final CountryPacket attackingCountryPacket = DebugPackets.from (attackingCountryName);
      eventBus.publish (new DefaultCountryArmiesChangedEvent (attackingCountryPacket, attackingCountryDeltaArmies));

      // TODO Production: Remove
      final CountryPacket defendingCountryPacket = DebugPackets.from (defendingCountryName);
      eventBus.publish (new DefaultCountryArmiesChangedEvent (defendingCountryPacket, defendingCountryDeltaArmies));

      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("You defended {} against {} in {}, destroying {} & losing {}!", defendingCountryName,
                       attackingPlayerName, attackingCountryName,
                       Strings.pluralize (Math.abs (attackingCountryDeltaArmies), "army", "armies"),
                       Strings.pluralize (Math.abs (defendingCountryDeltaArmies), "army", "armies")), ImmutableSet
              .<PlayerPacket> of ()));
    }

    @Override
    public void onAttackerWinFinal ()
    {
      defendDialog.hide ();

      battleResultDialog.setTitle ("Defeat");
      battleResultDialog.setMessage (new DefaultMessage ("General, we have failed to defend "
              + defendDialog.getDefendingCountryName () + ".\nThe enemy has taken it."));
      battleResultDialog.show ();

      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("You were defeated in {} & it has been conquered by {}!",
                       defendDialog.getDefendingCountryName (), defendDialog.getAttackingPlayerName ()), ImmutableSet
              .<PlayerPacket> of ()));
    }

    @Override
    public void onAttackerLoseFinal ()
    {
      defendDialog.hide ();

      battleResultDialog.setTitle ("Victory");
      battleResultDialog.setMessage (new DefaultMessage ("General, we have successfully protected "
              + defendDialog.getDefendingCountryName () + " from the enemy!"));
      battleResultDialog.show ();

      // TODO Production: Remove
      eventBus.publish (StatusMessageEventFactory.create (Strings.format ("You defeated {} in {}!",
                                                                          defendDialog.getAttackingPlayerName (),
                                                                          defendDialog.getAttackingCountryName ()),
                                                          ImmutableSet.<PlayerPacket> of ()));
    }

    @Override
    public void onShow ()
    {
      playMap.disable ();

      eventBus.publish (StatusMessageEventFactory.create (Strings
              .format ("You are preparing to defend {} against {} in {}.", defendDialog.getDefendingCountryName (),
                       defendDialog.getAttackingPlayerName (), defendDialog.getAttackingCountryName ()), ImmutableSet
              .<PlayerPacket> of ()));

      defendDialog.startBattle ();
    }

    @Override
    public void onHide ()
    {
    }
  }

  private final class QuitDialogListener implements DialogListener
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
    public void onHide ()
    {
      if (defendDialog.isShown () || occupationDialog.isShown () || battleResultDialog.isShown ()) return;

      playMap.enable (mouseInput.position ());
    }
  }

  private final class BattleResultDialogListener extends DialogListenerAdapter
  {
    @Override
    public void onShow ()
    {
      if (quitDialog.isShown ())
      {
        quitDialog.hide (null);
        quitDialog.show (null);
      }

      if (occupationDialog.isShown ()) occupationDialog.disableInput ();
    }

    @Override
    public void onHide ()
    {
      if (occupationDialog.isShown ())
      {
        occupationDialog.enableInput ();
        return;
      }

      playMap.enable (mouseInput.position ());
    }
  }
}
