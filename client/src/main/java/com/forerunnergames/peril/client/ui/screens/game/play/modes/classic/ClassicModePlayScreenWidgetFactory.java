package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.input.MouseInput;
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
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import net.engio.mbassy.bus.MBassador;

public final class ClassicModePlayScreenWidgetFactory extends WidgetFactory
{
  private static final int MESSAGE_BOX_ROW_HEIGHT = 24;
  private static final int MESSAGE_BOX_ROW_PADDING_LEFT = 12;
  private static final int MESSAGE_BOX_ROW_PADDING_RIGHT = 12;
  private static final int MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final int MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private final Skin skin;
  private final MessageBoxRowStyle messageBoxRowStyle;
  private final ScrollPane.ScrollPaneStyle messageBoxScrollPaneStyle;

  public ClassicModePlayScreenWidgetFactory (final Skin skin)
  {
    super (skin);

    this.skin = skin;

    messageBoxRowStyle = new MessageBoxRowStyle (MESSAGE_BOX_ROW_HEIGHT, MESSAGE_BOX_ROW_PADDING_LEFT,
            MESSAGE_BOX_ROW_PADDING_RIGHT);

    messageBoxScrollPaneStyle = skin.get (ScrollPane.ScrollPaneStyle.class);

    if (messageBoxScrollPaneStyle.vScrollKnob != null)
    {
      messageBoxScrollPaneStyle.vScrollKnob.setMinWidth (MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH);
    }

    if (messageBoxScrollPaneStyle.hScrollKnob != null)
    {
      messageBoxScrollPaneStyle.hScrollKnob.setMinHeight (MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT);
    }
  }

  public static PlayMapActor createPlayMapActor (final ScreenSize screenSize,
                                                 final MouseInput mouseInput,
                                                 final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return PlayMapActorFactory.create (screenSize, mouseInput, eventBus);
  }

  public MessageBox <StatusMessage> createStatusBox ()
  {
    return new DefaultMessageBox <> (messageBoxScrollPaneStyle, this, messageBoxRowStyle);
  }

  public MessageBox <ChatMessage> createChatBox (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new ChatBox (messageBoxScrollPaneStyle, this, messageBoxRowStyle,
            skin.get (TextField.TextFieldStyle.class), eventBus);
  }

  public PlayerBox createPlayerBox ()
  {
    return new PlayerBox (createMessageBox ());
  }

  public <T extends Message> MessageBox <T> createMessageBox ()
  {
    return new DefaultMessageBox <> (messageBoxScrollPaneStyle, this, messageBoxRowStyle);
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

    return new MandatoryOccupationPopup (skin, stage, eventBus, listener);
  }
}
