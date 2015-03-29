package com.forerunnergames.peril.client.ui.screens.game.play;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.hasAuthorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withAuthorNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withCountryNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withMessageFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withMessageTextFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.deltaArmyCountFrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;
import com.forerunnergames.peril.client.ui.screens.game.play.debug.DebugEventProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.debug.DebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.ArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.widgets.MessageBox;
import com.forerunnergames.peril.core.model.map.country.CountryName;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.CountryArmiesChangedEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class PlayScreen extends InputAdapter implements Screen
{
  private final PlayMapActor playMapActor;
  private final ArmyTextActor armyTextActor;
  private final ScreenMusic music;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final MessageBox <StatusMessage> statusBox;
  private final MessageBox <ChatMessage> chatBox;
  private final MessageBox <Message> playerBox;
  private final MandatoryOccupationPopup mandatoryOccupationPopup;
  private final InputProcessor inputProcessor;
  private final DebugEventProcessor debugEventProcessor;
  private Size2D currentScreenSize;

  public PlayScreen (final ScreenController screenController,
                     final PlayScreenWidgetFactory widgetFactory,
                     final PlayMapActor playMapActor,
                     final ArmyTextActor armyTextActor,
                     final TerritoryTextActor territoryTextActor,
                     final ScreenMusic music,
                     final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (armyTextActor, "armyTextActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");
    Arguments.checkIsNotNull (music, "music");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playMapActor = playMapActor;
    this.armyTextActor = armyTextActor;
    this.music = music;
    this.eventBus = eventBus;

    debugEventProcessor = new DebugEventProcessor (eventBus);

    statusBox = widgetFactory.createStatusBox ();
    chatBox = widgetFactory.createChatBox ();
    playerBox = widgetFactory.createPlayerBox ();

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.playScreenBackground));

    final Table playMapAndSideBarTable = new Table ();
    playMapAndSideBarTable.add (widgetFactory.createPlayMapWidget (playMapActor, armyTextActor, territoryTextActor))
            .padRight (16);
    playMapAndSideBarTable.add (widgetFactory.createSideBar ()).top ();

    final Table foregroundTable = new Table ().pad (12);
    foregroundTable.add (playMapAndSideBarTable).colspan (3);
    foregroundTable.row ().expandY ().padTop (16 + 2);
    foregroundTable.add (statusBox.asActor ()).width (714).height (252 - 2 - 2).padRight (16).padBottom (2);
    foregroundTable.add (chatBox.asActor ()).width (714).height (252 - 2).padRight (16);
    foregroundTable.add (playerBox.asActor ()).width (436).height (252 - 2 - 2).padBottom (2);

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING,
            GraphicsSettings.REFERENCE_SCREEN_WIDTH, GraphicsSettings.REFERENCE_SCREEN_HEIGHT, camera);

    stage = new Stage (viewport)
    {
      @Override
      public boolean keyDown (int keyCode)
      {
        if (keyCode == Input.Keys.ESCAPE) return false;

        return super.keyDown (keyCode);
      }
    };

    mandatoryOccupationPopup = widgetFactory.createMandatoryOccupationPopup (stage);

    stage.addActor (rootStack);

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
      public boolean touchDown (int screenX, int screenY, int pointer, int button)
      {
        stage.setKeyboardFocus (null);

        return false;
      }
    };

    final DebugInputProcessor debugInputProcessor = new DebugInputProcessor (screenController, playMapActor,
            armyTextActor, territoryTextActor, statusBox, chatBox, playerBox, mandatoryOccupationPopup, eventBus);

    inputProcessor = new InputMultiplexer (preInputProcessor, stage, this, debugInputProcessor);
  }

  @Handler
  public void onStatusMessageEvent (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    statusBox.addMessage (withMessageFrom (event));
    statusBox.showLastMessage ();
  }

  @Handler
  public void onChatMessageSuccessEvent (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    if (!hasAuthorFrom (event)) return;

    chatBox.addMessage (new DefaultChatMessage (withAuthorNameFrom (event) + ": " + withMessageTextFrom (event)));
    chatBox.showLastMessage ();
  }

  @Handler
  public void onPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    playerBox.addMessage (new DefaultMessage (event.getPlayerTurnOrder ().toMixedOrdinal () + ". "
            + event.getPlayerName ()));
  }

  @Handler
  public void onCountryArmiesChangedEvent (final CountryArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    armyTextActor.changeArmyCountBy (deltaArmyCountFrom (event), new CountryName (withCountryNameFrom (event)));
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    playMapActor.touchDown (new Point2D (screenX, screenY), button, getScreenSize ());
    armyTextActor.touchDown (playMapActor.getCountryNameAt (new Point2D (screenX, screenY), getScreenSize ()), button);

    return false;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    playMapActor.touchUp (new Point2D (screenX, screenY), button, getScreenSize ());

    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    playMapActor.mouseMoved (new Point2D (screenX, screenY), getScreenSize ());

    return false;
  }

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    if (MusicSettings.IS_ENABLED) music.start ();
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0, 0, 0, 1);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    stage.act (delta);
    stage.draw ();
  }

  @Override
  public void resize (final int width, final int height)
  {
    stage.getViewport ().update (width, height, true);
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

    Gdx.input.setInputProcessor (null);

    if (MusicSettings.IS_ENABLED) music.stop ();

    hideCursor ();
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);

    mandatoryOccupationPopup.dispose ();
    stage.dispose ();
  }

  private Size2D getScreenSize ()
  {
    if (currentScreenSize != null && currentScreenSize.getWidth () == Gdx.graphics.getWidth ()
            && currentScreenSize.getHeight () == Gdx.graphics.getHeight ())
    {
      return currentScreenSize;
    }

    currentScreenSize = new Size2D (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());

    return currentScreenSize;
  }

  private void showCursor ()
  {
    Gdx.input.setCursorImage (Assets.playScreenNormalCursor,
                              (int) InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.getX (),
                              (int) InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.getY ());
  }

  private void hideCursor ()
  {
    Gdx.input.setCursorImage (null, 0, 0);
  }
}
