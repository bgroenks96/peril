package com.forerunnergames.peril.client.ui.screens.game.play;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

import com.forerunnergames.peril.client.ui.Assets;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.ArmyTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.PlayMapActor;
import com.forerunnergames.peril.client.ui.screens.game.play.map.actors.TerritoryTextActor;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.ChatBox;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.MandatoryOccupationPopup;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.PlayMapWidget;
import com.forerunnergames.peril.client.ui.screens.game.play.widgets.SideBar;
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
  private final Skin skin;
  private final MBassador <Event> eventBus;
  private final LabelFactory labelFactory;
  private final MessageBoxRowStyle messageBoxRowStyle;

  public PlayScreenWidgetFactory (final Skin skin, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (skin, "skin");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.skin = skin;
    this.eventBus = eventBus;

    messageBoxRowStyle = new MessageBoxRowStyle (MESSAGE_BOX_ROW_HEIGHT, MESSAGE_BOX_ROW_PADDING_LEFT,
            MESSAGE_BOX_ROW_PADDING_RIGHT);

    labelFactory = new LabelFactory (new Label.LabelStyle (Assets.aurulentSans16, Color.WHITE));
  }

  public MessageBox <StatusMessage> createStatusBox ()
  {
    return new DefaultMessageBox <> (skin.get (ScrollPane.ScrollPaneStyle.class), labelFactory, messageBoxRowStyle);
  }

  public MessageBox <ChatMessage> createChatBox ()
  {
    return new ChatBox (skin.get (ScrollPane.ScrollPaneStyle.class), labelFactory, messageBoxRowStyle,
            skin.get (TextField.TextFieldStyle.class), eventBus);
  }

  public MessageBox <Message> createPlayerBox ()
  {
    return new DefaultMessageBox <> (skin.get (ScrollPane.ScrollPaneStyle.class), labelFactory, messageBoxRowStyle);
  }

  public SideBar createSideBar ()
  {
    return new SideBar (this);
  }

  public Button createButton ()
  {
    return new Button (skin.get (Button.ButtonStyle.class));
  }

  public PlayMapWidget createPlayMapWidget (final PlayMapActor playMapActor,
                                            final ArmyTextActor armyTextActor,
                                            final TerritoryTextActor territoryTextActor)
  {
    Arguments.checkIsNotNull (playMapActor, "playMapActor");
    Arguments.checkIsNotNull (armyTextActor, "armyTextActor");
    Arguments.checkIsNotNull (territoryTextActor, "territoryTextActor");

    return new PlayMapWidget (new Image (Assets.playScreenMapBackground), playMapActor, armyTextActor,
            territoryTextActor);
  }

  public MandatoryOccupationPopup createMandatoryOccupationPopup (final Stage stage)
  {
    Arguments.checkIsNotNull (stage, "stage");

    return new MandatoryOccupationPopup (skin, stage, eventBus);
  }
}
