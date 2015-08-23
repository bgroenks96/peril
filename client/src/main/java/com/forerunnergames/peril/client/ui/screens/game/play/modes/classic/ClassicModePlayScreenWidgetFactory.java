package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.PlayerBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.SideBar;
import com.forerunnergames.peril.client.ui.widgets.ChatBox;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.popup.PopupListener;
import com.forerunnergames.peril.common.map.MapMetadata;
import com.forerunnergames.peril.common.net.messages.ChatMessage;
import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

import net.engio.mbassy.bus.MBassador;

public final class ClassicModePlayScreenWidgetFactory extends WidgetFactory
{
  private static final int MESSAGE_BOX_ROW_HEIGHT = 24;
  private static final int MESSAGE_BOX_ROW_PADDING_LEFT = 12;
  private static final int MESSAGE_BOX_ROW_PADDING_RIGHT = 12;
  private static final int MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final int MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private static final MessageBoxRowStyle MESSAGE_BOX_ROW_STYLE = new MessageBoxRowStyle (MESSAGE_BOX_ROW_HEIGHT,
          MESSAGE_BOX_ROW_PADDING_LEFT, MESSAGE_BOX_ROW_PADDING_RIGHT);
  private final PlayMapActorFactory playMapActorFactory;
  @Nullable
  private ScrollPane.ScrollPaneStyle messageBoxScrollPaneStyle = null;

  public ClassicModePlayScreenWidgetFactory (final AssetManager assetManager,
                                             final ScreenSize screenSize,
                                             final MouseInput mouseInput,
                                             final MBassador <Event> eventBus)
  {
    super (assetManager);

    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    playMapActorFactory = new PlayMapActorFactory (assetManager, screenSize, mouseInput, eventBus);
  }

  public PlayMapActor createPlayMapActor (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    return playMapActorFactory.create (mapMetadata);
  }

  public void destroyPlayMapActor (final MapMetadata mapMetadata)
  {
    Arguments.checkIsNotNull (mapMetadata, "mapMetadata");

    playMapActorFactory.destroy (mapMetadata);
  }

  public MessageBox <StatusMessage> createStatusBox ()
  {
    return new DefaultMessageBox <> (getMessageBoxScrollPaneStyle (), this, MESSAGE_BOX_ROW_STYLE);
  }

  public MessageBox <ChatMessage> createChatBox (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new ChatBox (getMessageBoxScrollPaneStyle (), this, MESSAGE_BOX_ROW_STYLE,
            getSkinStyle (TextField.TextFieldStyle.class), eventBus);
  }

  public PlayerBox createPlayerBox ()
  {
    return new PlayerBox (createMessageBox ());
  }

  public <T extends Message> MessageBox <T> createMessageBox ()
  {
    return new DefaultMessageBox <> (getMessageBoxScrollPaneStyle (), this, MESSAGE_BOX_ROW_STYLE);
  }

  public Actor createSideBar (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new SideBar (this, eventBus);
  }

  public Actor createSideBarIcon (final SideBar.IconType iconType, final EventListener listener)
  {
    Arguments.checkIsNotNull (iconType, "iconType");
    Arguments.checkIsNotNull (listener, "listener");

    return createImageButton (iconType.getStyleName (), listener);
  }

  public MandatoryOccupationPopup createMandatoryOccupationPopup (final Stage stage,
                                                                  final MBassador <Event> eventBus,
                                                                  final PopupListener listener)
  {
    Arguments.checkIsNotNull (stage, "stage");
    Arguments.checkIsNotNull (eventBus, "eventBus");
    Arguments.checkIsNotNull (listener, "listener");

    return new MandatoryOccupationPopup (getSkin (), stage, getAssetManager (), eventBus, listener);
  }

  public Texture createBackground ()
  {
    return getAsset (AssetSettings.CLASSIC_MODE_PLAY_SCREEN_BACKGROUND_ASSET_DESCRIPTOR);
  }

  private ScrollPane.ScrollPaneStyle getMessageBoxScrollPaneStyle ()
  {
    if (messageBoxScrollPaneStyle == null) initializeMessageBoxScrollPaneStyle ();

    return messageBoxScrollPaneStyle;
  }

  private void initializeMessageBoxScrollPaneStyle ()
  {
    messageBoxScrollPaneStyle = getSkinStyle (ScrollPane.ScrollPaneStyle.class);

    if (messageBoxScrollPaneStyle.vScrollKnob != null)
    {
      messageBoxScrollPaneStyle.vScrollKnob.setMinWidth (MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH);
    }

    if (messageBoxScrollPaneStyle.hScrollKnob != null)
    {
      messageBoxScrollPaneStyle.hScrollKnob.setMinHeight (MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT);
    }
  }
}
