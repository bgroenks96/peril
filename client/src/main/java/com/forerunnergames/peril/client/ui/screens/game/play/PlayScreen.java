package com.forerunnergames.peril.client.ui.screens.game.play;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withMessageTextFrom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.forerunnergames.peril.client.settings.GraphicsSettings;
import com.forerunnergames.peril.client.settings.MusicSettings;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenController;
import com.forerunnergames.peril.client.ui.screens.ScreenId;
import com.forerunnergames.peril.client.ui.screens.ScreenMusic;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerColor;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.defaults.DefaultStatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.ChatMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.interfaces.StatusMessageEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.DefaultStatusMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.geometry.Point2D;
import com.forerunnergames.tools.common.geometry.Size2D;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

public final class PlayScreen extends InputAdapter implements Screen
{
  private final ScreenController screenController;
  private final PlayMapActor playMapActor;
  private final TerritoryTextActor territoryTextActor;
  private final ScreenMusic music;
  private final MBassador <Event> eventBus;
  private final Stage stage;
  private final Skin skin;
  private final Label.LabelStyle labelStyle;
  private final ImmutableList <String> wordList = ImmutableList.of ("Lorem", "ipsum", "dolor", "sit", "amet,",
                  "consectetur", "adipiscing", "elit.", "Mauris", "elementum", "nunc", "id", "dolor", "imperdiet",
                  "tincidunt.", "Proin", "rutrum", "leo", "orci,", "nec", "interdum", "mauris", "pretium", "ut.",
                  "Suspendisse", "faucibus,", "purus", "vitae", "finibus", "euismod,", "libero", "urna", "fermentum",
                  "diam,", "at", "pretium", "quam", "lacus", "vitae", "metus.", "Suspendisse", "ac", "tincidunt",
                  "leo.", "Morbi", "a", "tellus", "purus.", "Aenean", "a", "arcu", "ante.", "Nulla", "facilisi.",
                  "Aliquam", "pharetra", "sed", "urna", "nec", "efficitur.", "Maecenas", "pulvinar", "libero", "eget",
                  "pellentesque", "sodales.", "Donec", "a", "metus", "eget", "mi", "tempus", "feugiat.", "Etiam",
                  "fringilla", "ullamcorper", "justo", "ut", "mattis.", "Nam", "egestas", "elit", "at", "luctus",
                  "molestie.");
  private Table statusBoxScrollTable;
  private ScrollPane statusBoxScrollPane;
  private Table chatBoxScrollTable;
  private ScrollPane chatBoxScrollPane;
  private TextField chatBoxTextField;
  private Table playerBoxScrollTable;
  private ScrollPane playerBoxScrollPane;
  private Size2D currentScreenSize;
  private UnmodifiableIterator <PlayerTurnOrder> playerTurnOrderIterator;
  private Set <String> availablePlayerNames = new HashSet <> ();

  public PlayScreen (final ScreenController screenController,
                     final PlayMapActor playMapActor,
                     final TerritoryTextActor territoryTextActor,
                     final ScreenMusic music,
                     final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenController, "screenController");
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");
    Arguments.checkIsNotNull (music, "music");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.screenController = screenController;
    this.playMapActor = playMapActor;
    this.territoryTextActor = territoryTextActor;
    this.music = music;
    this.eventBus = eventBus;

    skin = new Skin (Gdx.files.internal ("ui/uiskin.json"));
    labelStyle = new Label.LabelStyle (new BitmapFont (
                    Gdx.files.internal ("ui/fonts/aurulentsans/aurulent-sans-16.fnt")), Color.WHITE);

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (new Image (Assets.playScreenBackground));

    final Table playMapAndSideBarTable = new Table ();
    playMapAndSideBarTable.add (createPlayMapActor ()).padRight (14);
    playMapAndSideBarTable.add (createSideBarActor ());

    final Table foregroundTable = new Table ().pad (14);
    foregroundTable.add (playMapAndSideBarTable).colspan (3);
    foregroundTable.row ().expandY ().padTop (16);
    foregroundTable.add (createStatusBoxActor ()).width (750).height (230).padRight (15).padBottom (2);
    foregroundTable.add (createChatBoxActor ()).width (750).height (232).padRight (15);
    foregroundTable.add (createPlayerBoxActor ()).width (361).height (230).padRight (1).padBottom (2);

    rootStack.add (foregroundTable);

    final Camera camera = new OrthographicCamera (Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
    final Viewport viewport = new ScalingViewport (GraphicsSettings.VIEWPORT_SCALING,
                    GraphicsSettings.REFERENCE_SCREEN_WIDTH, GraphicsSettings.REFERENCE_SCREEN_HEIGHT, camera);

    stage = new Stage (viewport);
    stage.addActor (rootStack);

    stage.addListener (new ClickListener ()
    {
      @Override
      public boolean touchDown (final InputEvent event, final float x, final float y, final int pointer, final int button)
      {
        stage.setKeyboardFocus (event.getTarget ());

        return true;
      }
    });
  }

  @Handler
  public void onStatusMessageEvent (final StatusMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    addStatusBoxText (withMessageTextFrom (event));
  }

  @Handler
  public void onChatMessageEvent (final ChatMessageEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    addChatBoxText (withMessageTextFrom (event));
  }

  @Handler
  public void onPlayerJoinGameSuccessEvent (final PlayerJoinGameSuccessEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    addPlayerBoxText (event.getPlayerTurnOrder ().toMixedOrdinal () + ". " + event.getPlayerName ());
  }

  @Override
  public boolean keyDown (final int keycode)
  {
    switch (keycode)
    {
      case Input.Keys.LEFT:
      {
        screenController.toPreviousScreenOr (ScreenId.MAIN_MENU);

        return true;
      }
      case Input.Keys.ESCAPE:
      {
        Gdx.app.exit ();

        return true;
      }
      case Input.Keys.NUM_1:
      {
        playMapActor.clearCountryColors ();

        return true;
      }
      case Input.Keys.NUM_2:
      {
        playMapActor.randomizeCountryColors ();

        return true;
      }
      case Input.Keys.NUM_3:
      {
        playMapActor.randomizeCountryColorsUsingNRandomColors (Randomness.getRandomIntegerFrom (1, 10));

        return true;
      }
      case Input.Keys.NUM_4:
      {
        playMapActor.randomizeCountryColorsUsingNRandomColors (2);

        return true;
      }
      case Input.Keys.NUM_5:
      {
        playMapActor.randomizeCountryColorsUsingNRandomColors (3);

        return true;
      }
      case Input.Keys.NUM_6:
      {
        playMapActor.setClassicCountryColors ();

        return true;
      }
      case Input.Keys.NUM_7:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.TEAL, PlayerColor.CYAN);

        return true;
      }
      case Input.Keys.NUM_8:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.BLUE, PlayerColor.CYAN);

        return true;
      }
      case Input.Keys.NUM_9:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.PINK, PlayerColor.PURPLE);

        return true;
      }
      case Input.Keys.NUM_0:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.RED, PlayerColor.BROWN);

        return true;
      }
      case Input.Keys.MINUS:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.CYAN, PlayerColor.SILVER);

        return true;
      }
      case Input.Keys.EQUALS:
      {
        playMapActor.randomizeCountryColorsUsingOnly (PlayerColor.TEAL, PlayerColor.GREEN);

        return true;
      }
      case Input.Keys.Q:
      {
        playMapActor.setCountriesTo (PlayerColor.BLUE);

        return true;
      }
      case Input.Keys.W:
      {
        playMapActor.setCountriesTo (PlayerColor.BROWN);

        return true;
      }
      case Input.Keys.E:
      {
        playMapActor.setCountriesTo (PlayerColor.CYAN);

        return true;
      }
      case Input.Keys.R:
      {
        playMapActor.setCountriesTo (PlayerColor.GOLD);

        return true;
      }
      case Input.Keys.T:
      {
        playMapActor.setCountriesTo (PlayerColor.GREEN);

        return true;
      }
      case Input.Keys.Y:
      {
        playMapActor.setCountriesTo (PlayerColor.PINK);

        return true;
      }
      case Input.Keys.U:
      {
        playMapActor.setCountriesTo (PlayerColor.PURPLE);

        return true;
      }
      case Input.Keys.I:
      {
        playMapActor.setCountriesTo (PlayerColor.RED);

        return true;
      }
      case Input.Keys.O:
      {
        playMapActor.setCountriesTo (PlayerColor.SILVER);

        return true;
      }
      case Input.Keys.LEFT_BRACKET:
      {
        playMapActor.setCountriesTo (PlayerColor.TEAL);

        return true;
      }
      case Input.Keys.F:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playMapActor.setCountryTextureFiltering (Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        return true;
      }
      case Input.Keys.G:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearNearest,
                        Texture.TextureFilter.Nearest);
        playMapActor.setCountryTextureFiltering (Texture.TextureFilter.MipMapLinearNearest,
                        Texture.TextureFilter.Nearest);

        return true;
      }
      case Input.Keys.H:
      {
        Assets.playScreenMapBackground.setFilter (Texture.TextureFilter.MipMapLinearLinear,
                        Texture.TextureFilter.Linear);
        playMapActor.setCountryTextureFiltering (Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        return true;
      }
      default:
      {
        return false;
      }
    }
  }

  @Override
  public boolean keyTyped (final char character)
  {
    switch (character)
    {
      case 's':
      {
        eventBus.publish (createStatusMessageEvent ());

        return false;
      }
      case 'S':
      {
        clearStatusBox ();

        return false;
      }
      case 'c':
      {
        eventBus.publish (createChatMessageEvent ());

        return false;
      }
      case 'C':
      {
        clearChatBox ();

        return false;
      }
      case 'p':
      {
        eventBus.publish (createPlayerJoinGameSuccessEvent ());

        return false;
      }
      case 'P':
      {
        clearPlayerBox ();

        return false;
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
    final boolean isHandled = playMapActor.touchDown (new Point2D (screenX, screenY), button, getScreenSize ());

    territoryTextActor.setHoveredCountryColor (playMapActor.getHoveredCountryColor ());

    return isHandled;
  }

  @Override
  public boolean touchUp (final int screenX, final int screenY, final int pointer, final int button)
  {
    return playMapActor.touchUp (new Point2D (screenX, screenY), button, getScreenSize ());
  }

  @Override
  public boolean mouseMoved (final int screenX, final int screenY)
  {
    final boolean isHandled = playMapActor.mouseMoved (new Point2D (screenX, screenY), getScreenSize ());

    territoryTextActor.setHoveredCountryColor (playMapActor.getHoveredCountryColor ());

    return isHandled;
  }

  @Override
  public void show ()
  {
    eventBus.subscribe (this);

    Gdx.input.setInputProcessor (new InputMultiplexer (this, stage));

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
  }

  @Override
  public void dispose ()
  {
    eventBus.unsubscribe (this);

    stage.dispose ();
    skin.dispose ();
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

  private Actor createSideBarActor ()
  {
    final Table sideBarTable = new Table ().top ().padTop (33).padBottom (33).padLeft (32).padRight (32);

    sideBarTable.add (new Button (skin)).top ().width (42).height (42);
    sideBarTable.add (new Button (skin)).top ().width (42).height (42).padLeft (26);

    final int rowsOfButtons = 13;

    for (int i = 0; i < rowsOfButtons - 1; ++i)
    {
      sideBarTable.row ().padTop (16);
      sideBarTable.add (new Button (skin)).top ().width (42).height (42);
      sideBarTable.add (new Button (skin)).top ().width (42).height (42).padLeft (26);
    }

    return sideBarTable;
  }

  private Actor createPlayMapActor ()
  {
    final Stack stack = new Stack ();

    stack.add (new Image (Assets.playScreenMapBackground));
    stack.add (playMapActor);
    stack.add (territoryTextActor);

    return stack;
  }

  private Actor createStatusBoxActor ()
  {
    statusBoxScrollTable = new Table ().top ().padLeft (8).padRight (8);
    statusBoxScrollPane = new ScrollPane (statusBoxScrollTable, skin);
    statusBoxScrollPane.setOverscroll (false, false);
    statusBoxScrollPane.setForceScroll (false, true);
    statusBoxScrollPane.setFadeScrollBars (false);
    statusBoxScrollPane.setScrollingDisabled (true, false);
    statusBoxScrollPane.setScrollBarPositions (true, true);
    statusBoxScrollPane.setScrollbarsOnTop (false);
    statusBoxScrollPane.setSmoothScrolling (true);

    return statusBoxScrollPane;
  }

  private Actor createChatBoxActor ()
  {
    chatBoxScrollTable = new Table ().top ().padLeft (8).padRight (8);
    chatBoxScrollPane = new ScrollPane (chatBoxScrollTable, skin);
    chatBoxScrollPane.setOverscroll (false, false);
    chatBoxScrollPane.setForceScroll (false, true);
    chatBoxScrollPane.setFadeScrollBars (false);
    chatBoxScrollPane.setScrollingDisabled (true, false);
    chatBoxScrollPane.setScrollBarPositions (true, true);
    chatBoxScrollPane.setScrollbarsOnTop (false);
    chatBoxScrollPane.setSmoothScrolling (true);
    chatBoxTextField = new TextField ("", skin);

    chatBoxTextField.addListener (new InputListener ()
    {
      @Override
      public boolean keyDown (final InputEvent event, final int keycode)
      {
        switch (keycode)
        {
          case Input.Keys.ENTER:
          {
            final String textFieldText = Strings.compressWhitespace (chatBoxTextField.getText ().trim ());

            chatBoxTextField.setText ("");

            if (Strings.isPrintable (textFieldText))
            {
              eventBus.publish (new DefaultChatMessageEvent (new DefaultChatMessage (textFieldText)));
            }

            return true;
          }
          default:
          {
            return false;
          }
        }
      }
    });

    final Table chatBoxAndTextFieldTable = new Table ().top ();
    chatBoxAndTextFieldTable.add (chatBoxScrollPane).expandX ().fillX ().height (199).padBottom (2);
    chatBoxAndTextFieldTable.row ();
    chatBoxAndTextFieldTable.add (chatBoxTextField).expandX ().fillX ().height (26).padTop (5).padLeft (4).padRight (4);

    return chatBoxAndTextFieldTable;
  }

  private Actor createPlayerBoxActor ()
  {
    playerBoxScrollTable = new Table ().top ().padLeft (8).padRight (8);
    playerBoxScrollPane = new ScrollPane (playerBoxScrollTable, skin);
    playerBoxScrollPane.setOverscroll (false, false);
    playerBoxScrollPane.setForceScroll (false, true);
    playerBoxScrollPane.setFadeScrollBars (false);
    playerBoxScrollPane.setScrollingDisabled (true, false);
    playerBoxScrollPane.setScrollBarPositions (true, true);
    playerBoxScrollPane.setScrollbarsOnTop (false);
    playerBoxScrollPane.setSmoothScrolling (true);

    return playerBoxScrollPane;
  }

  private void addStatusBoxText (final String text)
  {
    statusBoxScrollTable.row ().expandX ().fillX ().prefHeight (22);
    statusBoxScrollTable.add (createMessageBoxLabel (text));
    statusBoxScrollTable.layout ();
    statusBoxScrollPane.layout ();
    statusBoxScrollPane.setScrollY (statusBoxScrollPane.getMaxY ());
  }

  private void addChatBoxText (final String text)
  {
    chatBoxScrollTable.row ().expandX ().fillX ().prefHeight (22);
    chatBoxScrollTable.add (createMessageBoxLabel (text));
    chatBoxScrollTable.layout ();
    chatBoxScrollPane.layout ();
    chatBoxScrollPane.setScrollY (chatBoxScrollPane.getMaxY ());
  }

  private void addPlayerBoxText (final String text)
  {
    playerBoxScrollTable.row ().expandX ().fillX ().prefHeight (22);
    playerBoxScrollTable.add (createMessageBoxLabel (text));
    playerBoxScrollTable.layout ();
    playerBoxScrollPane.layout ();
  }

  private Label createMessageBoxLabel (final String text)
  {
    final Label label = new Label (text, labelStyle);

    label.setWrap (true);

    return label;
  }

  private StatusMessageEvent createStatusMessageEvent ()
  {
    return new DefaultStatusMessageEvent (createStatusMessage ());
  }

  private StatusMessage createStatusMessage ()
  {
    return new DefaultStatusMessage (createMessageText ());
  }

  private ChatMessageEvent createChatMessageEvent ()
  {
    return new DefaultChatMessageEvent (createChatMessage ());
  }

  private ChatMessage createChatMessage ()
  {
    return new DefaultChatMessage (createMessageText ());
  }

  private PlayerJoinGameSuccessEvent createPlayerJoinGameSuccessEvent ()
  {
    return new PlayerJoinGameSuccessEvent (createPlayer ());
  }

  private Player createPlayer ()
  {
    return PlayerFactory.builder (createPlayerName ()).turnOrder (createPlayerTurnOrder ()).build ();
  }

  private String createPlayerName ()
  {
    if (availablePlayerNames.isEmpty ())
    {
      availablePlayerNames.addAll (Arrays.asList ("Ben", "Bob", "Jerry", "Oscar", "Evelyn", "Josh", "Eliza", "Aaron",
                      "Maddy", "Brittany", "Jonathan", "Adam", "Brian"));
    }

    final String playerName = Randomness.getRandomElementFrom (availablePlayerNames);

    availablePlayerNames.remove (playerName);

    return playerName;
  }

  private PlayerTurnOrder createPlayerTurnOrder ()
  {
    if (playerTurnOrderIterator == null) playerTurnOrderIterator = createPlayerTurnOrderIterator ();

    return playerTurnOrderIterator.hasNext () ? playerTurnOrderIterator.next () : PlayerTurnOrder.UNKNOWN;
  }

  private String createMessageText ()
  {
    final ImmutableList <String> randomSubsetWordList = wordList.subList (0, Randomness.getRandomIntegerFrom (1, 30));
    final StringBuilder randomSubsetWordListStringBuilder = new StringBuilder ();

    for (final String word : randomSubsetWordList)
    {
      randomSubsetWordListStringBuilder.append (word).append (" ");
    }

    randomSubsetWordListStringBuilder.deleteCharAt (randomSubsetWordListStringBuilder.lastIndexOf (" "));

    return randomSubsetWordListStringBuilder.toString () + " "
                    + Randomness.getRandomIntegerFrom (0, Integer.MAX_VALUE - 1)
                    + " aaa WW W W W W W W W WWWWWWWWWWWWWWWW";
  }

  private void clearStatusBox ()
  {
    statusBoxScrollTable.reset ();
    statusBoxScrollTable.top ().padLeft (8).padRight (8);
  }

  private void clearChatBox ()
  {
    chatBoxScrollTable.reset ();
    chatBoxScrollTable.top ().padLeft (8).padRight (8);
  }

  private void clearPlayerBox ()
  {
    playerBoxScrollTable.reset ();
    playerBoxScrollTable.top ().padLeft (8).padRight (8);
    playerTurnOrderIterator = createPlayerTurnOrderIterator ();
  }

  private UnmodifiableIterator <PlayerTurnOrder> createPlayerTurnOrderIterator ()
  {
    return PlayerTurnOrder.validValues ().iterator ();
  }
}
