package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.ChatBox;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.PlayMapWidget;
import com.forerunnergames.peril.client.ui.widgets.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.LabelFactory;
import com.forerunnergames.peril.client.ui.widgets.MessageBox;
import com.forerunnergames.peril.client.ui.widgets.RowStyle;
import com.forerunnergames.peril.core.shared.net.messages.ChatMessage;
import com.forerunnergames.peril.core.shared.net.messages.StatusMessage;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Message;

import net.engio.mbassy.bus.MBassador;

public final class PlayScreenWidgetFactory
{
  private static final float ROW_HEIGHT = 22;
  private static final float ROW_PADDING_LEFT = 8;
  private static final float ROW_PADDING_RIGHT = 8;
  private final Skin skin;
  private final MBassador <Event> eventBus;
  private final LabelFactory labelFactory;
  private final RowStyle rowStyle;

  public PlayScreenWidgetFactory (final Skin skin, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.skin = skin;
    this.eventBus = eventBus;

    rowStyle = new RowStyle (ROW_HEIGHT, ROW_PADDING_LEFT, ROW_PADDING_RIGHT);
    labelFactory = new LabelFactory (new Label.LabelStyle (Assets.aurulentSans16, Color.WHITE));
  }

  public MessageBox <StatusMessage> createStatusBox ()
  {
    return new DefaultMessageBox <> (skin.get (ScrollPane.ScrollPaneStyle.class), labelFactory, rowStyle);
  }

  public MessageBox <ChatMessage> createChatBox ()
  {
    return new ChatBox (skin.get (ScrollPane.ScrollPaneStyle.class), labelFactory, rowStyle,
        skin.get (TextField.TextFieldStyle.class), eventBus);
  }

  public MessageBox <Message> createPlayerBox ()
  {
    return new DefaultMessageBox <> (skin.get (ScrollPane.ScrollPaneStyle.class), labelFactory, rowStyle);
  }

  public Button createButton ()
  {
    return new Button (skin.get (Button.ButtonStyle.class));
  }

  public PlayMapWidget createPlayMapWidget (final PlayMapActor playMapActor, final TerritoryTextActor territoryTextActor)
  {
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");

    return new PlayMapWidget (new Image (Assets.playScreenMapBackground), playMapActor, territoryTextActor);
  }
}
