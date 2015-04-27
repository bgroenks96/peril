package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.map.actors.PlayMapActorFactory;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.ChatBox;
import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets.SideBar;
import com.forerunnergames.peril.client.ui.widgets.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.LabelFactory;
import com.forerunnergames.peril.client.ui.widgets.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.MessageBoxRowStyle;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import net.engio.mbassy.bus.MBassador;

public final class PlayScreenWidgetFactory
{
  private static final int MESSAGE_BOX_ROW_HEIGHT = 24;
  private static final int MESSAGE_BOX_ROW_PADDING_LEFT = 12;
  private static final int MESSAGE_BOX_ROW_PADDING_RIGHT = 12;
  private static final int MESSAGE_BOX_VERTICAL_SCROLLBAR_WIDTH = 14;
  private static final int MESSAGE_BOX_HORIZONTAL_SCROLLBAR_HEIGHT = 14;
  private final Skin skin;
  private final LabelFactory labelFactory;
  private final MessageBoxRowStyle messageBoxRowStyle;
  private final ScrollPane.ScrollPaneStyle messageBoxScrollPaneStyle;

  public PlayScreenWidgetFactory (final Skin skin)
  {
    Arguments.checkIsNotNull (skin, "skin");

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

    labelFactory = new LabelFactory (new Label.LabelStyle (Assets.aurulentSans16, Color.WHITE));
  }

  public PlayMapActor createPlayMapActor (final ScreenSize screenSize, final MouseInput mouseInput)
  {
    Arguments.checkIsNotNull (screenSize, "screenSize");
    Arguments.checkIsNotNull (mouseInput, "mouseInput");

    return PlayMapActorFactory.create (screenSize, mouseInput);
  }

  public MessageBox <StatusMessage> createStatusBox ()
  {
    return new DefaultMessageBox <> (messageBoxScrollPaneStyle, labelFactory, messageBoxRowStyle);
  }

  public MessageBox <ChatMessage> createChatBox (final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (eventBus, "eventBus");

    return new ChatBox (messageBoxScrollPaneStyle, labelFactory, messageBoxRowStyle,
            skin.get (TextField.TextFieldStyle.class), eventBus);
  }

  public MessageBox <Message> createPlayerBox ()
  {
    return new DefaultMessageBox <> (messageBoxScrollPaneStyle, labelFactory, messageBoxRowStyle);
  }

  public SideBar createSideBar ()
  {
    return new SideBar (this);
  }

  public Button createButton ()
  {
    return new Button (skin.get (Button.ButtonStyle.class));
  }

  public Skin getSkin ()
  {
    return skin;
  }
}
