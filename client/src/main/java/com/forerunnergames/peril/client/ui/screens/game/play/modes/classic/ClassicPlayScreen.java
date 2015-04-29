package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.deltaArmyCountFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.hasAuthorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withAuthorNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withCountryNameFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withMessageFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withMessageTextFrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.input.GdxKeyRepeatListenerAdapter;
import com.forerunnergames.peril.client.input.GdxKeyRepeatSystem;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.InputSettings;
import com.forerunnergames.peril.client.settings.PlayMapSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugEventProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.debug.DebugInputProcessor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.popups.Popup;
import com.forerunnergames.peril.client.ui.widgets.popups.PopupListener;
import com.forerunnergames.peril.client.ui.widgets.popups.PopupListenerAdapter;
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

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class ClassicPlayScreen extends InputAdapter implements Screen
{
  private final PlayMapActor playMapActor;
  private final MouseInput mouseInput;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final MessageBox <StatusMessage> statusBox;
  private final MessageBox <ChatMessage> chatBox;
  private final MessageBox <Message> playerBox;
  private final InputProcessor inputProcessor;
  private final DebugEventProcessor debugEventProcessor;
  private final GdxKeyRepeatSystem keyRepeat;
  private final Popup quitPopup;
  private final Vector2 tempPosition = new Vector2 ();

  public ClassicPlayScreen (final PlayScreenWidgetFactory widgetFactory,
                            final ScreenChanger screenChanger,
                            final ScreenSize screenSize,
                            final MouseInput mouseInput,
                            final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenChanger, "screenChanger");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.eventBus = eventBus;
    this.mouseInput = mouseInput;

    debugEventProcessor = new DebugEventProcessor (eventBus);

    statusBox = widgetFactory.createStatusBox ();
    chatBox = widgetFactory.createChatBox (eventBus);
    playerBox = widgetFactory.createPlayerBox ();
    playMapActor = widgetFactory.createPlayMapActor (screenSize, mouseInput);

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.playScreenBackground));

    final Table playMapAndSideBarTable = new Table ();
    playMapAndSideBarTable.add (playMapActor).size (PlayMapSettings.ACTUAL_WIDTH, PlayMapSettings.ACTUAL_HEIGHT)
            .padRight (16);
    playMapAndSideBarTable.add (widgetFactory.createSideBar ()).top ();

    final Table foregroundTable = new Table ().pad (12);
    foregroundTable.add (playMapAndSideBarTable).colspan (3);
    foregroundTable.row ().expandY ().padTop (16 + 2);
    foregroundTable.add (statusBox.asActor ()).width (714).height (252 - 2 - 2).padRight (16).padBottom (2);
    foregroundTable.add (chatBox.asActor ()).width (714).height (252 - 2).padRight (16);
    foregroundTable.add (playerBox.asActor ()).width (436).height (252 - 2 - 2).padBottom (2);

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (screenSize.actualWidth (), screenSize.actualHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING, screenSize.referenceWidth (),
            screenSize.referenceHeight (), camera);

    stage = new Stage (viewport);

    final MandatoryOccupationPopup mandatoryOccupationPopup = widgetFactory
            .createMandatoryOccupationPopup (stage, eventBus, new PopupListenerAdapter ()
            {
              @Override
              public void onShow ()
              {
                playMapActor.disable ();
              }

              @Override
              public void onHide ()
              {
                playMapActor.enable (mouseInput.position ());
              }
            });

    quitPopup = widgetFactory
            .createQuitPopup ("Are you sure you want to quit?\nQuitting will end the game for everyone.", stage,
                              new PopupListener ()
                              {
                                @Override
                                public void onSubmit ()
                                {
                                  screenChanger.toPreviousScreenOr (ScreenId.MAIN_MENU);
                                }

                                @Override
                                public void onShow ()
                                {
                                  playMapActor.disable ();
                                }

                                @Override
                                public void onHide ()
                                {
                                  playMapActor.enable (mouseInput.position ());
                                }
                              });

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

    keyRepeat = new GdxKeyRepeatSystem (Gdx.input, new GdxKeyRepeatListenerAdapter ()
    {
      @Override
      public void keyDownRepeating (int keyCode)
      {
        mandatoryOccupationPopup.keyDownRepeating (keyCode);
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

    final DebugInputProcessor debugInputProcessor = new DebugInputProcessor (mouseInput, playMapActor, statusBox,
            chatBox, playerBox, mandatoryOccupationPopup, eventBus);

    inputProcessor = new InputMultiplexer (preInputProcessor, stage, this, debugInputProcessor);
  }

  @Override
  public void show ()
  {
    showCursor ();

    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (inputProcessor);

    stage.mouseMoved (mouseInput.x (), mouseInput.y ());
    playMapActor.mouseMoved (mouseInput.position ());
  }

  @Override
  public void render (final float delta)
  {
    Gdx.gl.glClearColor (0, 0, 0, 1);
    Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

    keyRepeat.update ();
    stage.act (delta);
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

    chatBox.clear ();
    statusBox.clear ();
    playerBox.clear ();
    playMapActor.reset ();
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);
    stage.dispose ();
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.ESCAPE:
      {
        quitPopup.show ();

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public boolean touchDown (final int screenX, final int screenY, final int pointer, final int button)
  {
    playMapActor.touchDown (tempPosition.set (screenX, screenY), button);

    return false;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    playMapActor.touchUp (tempPosition.set (screenX, screenY));

    return false;
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    playMapActor.mouseMoved (tempPosition.set (screenX, screenY));

    return false;
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

    playerBox.addMessage (new DefaultMessage (event.getPlayerTurnOrder ().toMixedOrdinal () + ". " + event.getPlayerName ()));
  }

  @Handler
  public void onCountryArmiesChangedEvent (final CountryArmiesChangedEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    playMapActor.changeArmiesBy (deltaArmyCountFrom (event), new CountryName (withCountryNameFrom (event)));
  }

  private void showCursor ()
  {
    Gdx.input.setCursorImage (Assets.playScreenNormalCursor, (int) InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.x, (int) InputSettings.PLAY_SCREEN_NORMAL_MOUSE_CURSOR_HOTSPOT.y);
  }

  private void hideCursor ()
  {
    Gdx.input.setCursorImage (null, 0, 0);
  }
}
