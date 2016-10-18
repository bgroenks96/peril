package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

public final class DefaultControlRoomBox implements ControlRoomBox
{
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final ImmutableMap <Button, ImageButton> buttons;
  private final Table controlRoomBoxTable;
  private final Table titleTable;
  private final Label titleLabel;
  private final Label tradeInButtonLabel;
  private final Label fortifyButtonLabel;
  private final Label endTurnButtonLabel;
  private final Label mySettingsButtonLabel;
  private final Label surrenderButtonLabel;
  private final ImageButton tradeInButton;
  private final ImageButton fortifyButton;
  private final ImageButton endTurnButton;
  private final ImageButton mySettingsButton;
  private final ImageButton surrenderButton;
  @Nullable
  private PlayerPacket selfPlayer;

  public DefaultControlRoomBox (final ClassicModePlayScreenWidgetFactory widgetFactory,
                                final EventListener tradeInButtonListener,
                                final EventListener fortifyButtonListener,
                                final EventListener endTurnButtonListener,
                                final EventListener mySettingsButtonListener,
                                final EventListener surrenderButtonListener)
  {
    Arguments.checkIsNotNull (tradeInButtonListener, "tradeInButtonListener");
    Arguments.checkIsNotNull (fortifyButtonListener, "fortifyButtonListener");
    Arguments.checkIsNotNull (endTurnButtonListener, "endTurnButtonListener");
    Arguments.checkIsNotNull (mySettingsButtonListener, "mySettingsButtonListener");
    Arguments.checkIsNotNull (surrenderButtonListener, "surrenderButtonListener");

    this.widgetFactory = widgetFactory;

    titleLabel = widgetFactory.createControlRoomBoxTitleLabel ("Control Room");
    tradeInButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("Purchase Reinforcements");
    tradeInButton = widgetFactory.createControlRoomBoxTradeInButton (tradeInButtonListener);
    fortifyButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("Post-Combat Maneuver");
    fortifyButton = widgetFactory.createControlRoomBoxFortifyButton (fortifyButtonListener);
    endTurnButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("End Turn");
    endTurnButton = widgetFactory.createControlRoomBoxEndTurnButton (endTurnButtonListener);
    mySettingsButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("My Settings");
    mySettingsButton = widgetFactory.createControlRoomBoxMySettingsButton (mySettingsButtonListener);
    surrenderButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("Surrender & Quit");
    surrenderButton = widgetFactory.createControlRoomBoxSurrenderButton (surrenderButtonListener);

    buttons = ImmutableMap.of (Button.TRADE_IN, tradeInButton, Button.FORTIFY, fortifyButton, Button.END_TURN,
                               endTurnButton, Button.MY_SETTINGS, mySettingsButton, Button.SURRENDER_AND_QUIT,
                               surrenderButton);

    controlRoomBoxTable = new Table ().top ().left ().pad (4);
    controlRoomBoxTable.setBackground (widgetFactory.createControlRoomBoxBackgroundDrawable ());

    titleTable = new Table ();
    titleTable.setBackground (widgetFactory.createControlRoomBoxTitleBackgroundDrawable ());
    titleTable.add (titleLabel).padLeft (16).expand ().fill ();
    controlRoomBoxTable.add (titleTable).height (40).fill ().colspan (2);

    controlRoomBoxTable.row ();
    controlRoomBoxTable.add ().expandY ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add (tradeInButton).padLeft (16).spaceRight (10).fill ();
    controlRoomBoxTable.add (tradeInButtonLabel).spaceLeft (10).padRight (16).expandX ().fill ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add ().expandY ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add (fortifyButton).padLeft (16).spaceRight (10).fill ();
    controlRoomBoxTable.add (fortifyButtonLabel).spaceLeft (10).padRight (16).expandX ().fill ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add ().expandY ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add (endTurnButton).padLeft (16).spaceRight (10).fill ();
    controlRoomBoxTable.add (endTurnButtonLabel).spaceLeft (10).padRight (16).expandX ().fill ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add ().expandY ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add (mySettingsButton).padLeft (16).spaceRight (10).fill ();
    controlRoomBoxTable.add (mySettingsButtonLabel).spaceLeft (10).padRight (16).expandX ().fill ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add ().expandY ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add (surrenderButton).padLeft (16).spaceRight (10).fill ();
    controlRoomBoxTable.add (surrenderButtonLabel).spaceLeft (10).padRight (16).expandX ().fill ();
    controlRoomBoxTable.row ();
    controlRoomBoxTable.add ().expandY ();
  }

  @Override
  public void pressButton (final Button button)
  {
    Arguments.checkIsNotNull (button, "button");

    getButton (button).toggle ();
  }

  @Override
  public void disableButton (final Button button)
  {
    Arguments.checkIsNotNull (button, "button");

    getButton (button).setDisabled (true);
  }

  @Override
  public void disableButtonForSelf (final Button button, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (player, "player");

    if (isSelf (player)) disableButton (button);
  }

  @Override
  public void disableButtonForEveryoneElse (final Button button, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (player, "player");

    if (!isSelf (player)) disableButton (button);
  }

  @Override
  public void enableButton (final Button button)
  {
    Arguments.checkIsNotNull (button, "button");

    getButton (button).setDisabled (false);
  }

  @Override
  public void enableButtonForSelf (final Button button, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (player, "player");

    if (isSelf (player)) enableButton (button);
  }

  @Override
  public void enableButtonForEveryoneElse (final Button button, final PlayerPacket player)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (player, "player");

    if (!isSelf (player)) enableButton (button);
  }

  @Override
  public void setSelfPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    selfPlayer = player;
  }

  @Override
  public Actor asActor ()
  {
    return controlRoomBoxTable;
  }

  @Override
  public void refreshAssets ()
  {
    controlRoomBoxTable.setBackground (widgetFactory.createControlRoomBoxBackgroundDrawable ());
    titleTable.setBackground (widgetFactory.createControlRoomBoxTitleBackgroundDrawable ());
    titleLabel.setStyle (widgetFactory.createControlRoomBoxTitleLabelStyle ());
    tradeInButton.setStyle (widgetFactory.createControlRoomBoxTradeInButtonStyle ());
    tradeInButtonLabel.setStyle (widgetFactory.createControlRoomBoxButtonTextLabelStyle ());
    fortifyButton.setStyle (widgetFactory.createControlRoomBoxFortifyButtonStyle ());
    fortifyButtonLabel.setStyle (widgetFactory.createControlRoomBoxButtonTextLabelStyle ());
    endTurnButton.setStyle (widgetFactory.createControlRoomBoxEndTurnButtonStyle ());
    endTurnButtonLabel.setStyle (widgetFactory.createControlRoomBoxButtonTextLabelStyle ());
    mySettingsButton.setStyle (widgetFactory.createControlRoomBoxMySettingsButtonStyle ());
    mySettingsButtonLabel.setStyle (widgetFactory.createControlRoomBoxButtonTextLabelStyle ());
    surrenderButton.setStyle (widgetFactory.createControlRoomBoxSurrenderButtonStyle ());
    surrenderButtonLabel.setStyle (widgetFactory.createControlRoomBoxButtonTextLabelStyle ());
  }

  private ImageButton getButton (final Button button)
  {
    final ImageButton imageButton = buttons.get (button);

    if (imageButton == null)
    {
      throw new IllegalStateException (Strings.format ("{}: [{}] does not exist.", Button.class.getSimpleName (),
                                                       button));
    }

    return imageButton;
  }

  private boolean isSelf (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return selfPlayer != null && player.is (selfPlayer);
  }
}
