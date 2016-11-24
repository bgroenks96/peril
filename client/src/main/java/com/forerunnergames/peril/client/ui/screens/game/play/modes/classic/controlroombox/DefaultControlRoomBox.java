package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.controlroombox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.ClassicModePlayScreenWidgetFactory;
import com.forerunnergames.peril.common.net.packets.person.PersonPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;

public final class DefaultControlRoomBox implements ControlRoomBox
{
  private final ClassicModePlayScreenWidgetFactory widgetFactory;
  private final ImmutableMap <Button, ImageButton> buttons;
  private final ImmutableMap <Button, Label> buttonLabels;
  private final Table controlRoomBoxTable;
  private final Table titleTable;
  private final Label titleLabel;
  @Nullable
  private PersonPacket self;

  public DefaultControlRoomBox (final ClassicModePlayScreenWidgetFactory widgetFactory,
                                final EventListener tradeInButtonListener,
                                final EventListener fortifyButtonListener,
                                final EventListener endTurnButtonListener,
                                final EventListener mySettingsButtonListener,
                                final EventListener quitButtonListener)
  {
    Arguments.checkIsNotNull (tradeInButtonListener, "tradeInButtonListener");
    Arguments.checkIsNotNull (fortifyButtonListener, "fortifyButtonListener");
    Arguments.checkIsNotNull (endTurnButtonListener, "endTurnButtonListener");
    Arguments.checkIsNotNull (mySettingsButtonListener, "mySettingsButtonListener");
    Arguments.checkIsNotNull (quitButtonListener, "quitButtonListener");

    this.widgetFactory = widgetFactory;

    titleLabel = widgetFactory.createControlRoomBoxTitleLabel ("Control Room");
    final Label tradeInButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("Purchase Reinforcements");
    final ImageButton tradeInButton = widgetFactory.createControlRoomBoxTradeInButton (tradeInButtonListener);
    final Label fortifyButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("Post-Combat Maneuver");
    final ImageButton fortifyButton = widgetFactory.createControlRoomBoxFortifyButton (fortifyButtonListener);
    final Label endTurnButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("End Turn");
    final ImageButton endTurnButton = widgetFactory.createControlRoomBoxEndTurnButton (endTurnButtonListener);
    final Label mySettingsButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("My Settings");
    final ImageButton mySettingsButton = widgetFactory.createControlRoomBoxMySettingsButton (mySettingsButtonListener);
    final Label quitButtonLabel = widgetFactory.createControlRoomBoxButtonTextLabel ("Quit");
    final ImageButton quitButton = widgetFactory.createControlRoomBoxQuitButton (quitButtonListener);

    buttons = ImmutableMap.of (Button.TRADE_IN, tradeInButton, Button.FORTIFY, fortifyButton, Button.END_TURN,
                               endTurnButton, Button.MY_SETTINGS, mySettingsButton, Button.QUIT, quitButton);

    buttonLabels = ImmutableMap.of (Button.TRADE_IN, tradeInButtonLabel, Button.FORTIFY, fortifyButtonLabel,
                                    Button.END_TURN, endTurnButtonLabel, Button.MY_SETTINGS, mySettingsButtonLabel,
                                    Button.QUIT, quitButtonLabel);

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
    controlRoomBoxTable.add (quitButton).padLeft (16).spaceRight (10).fill ();
    controlRoomBoxTable.add (quitButtonLabel).spaceLeft (10).padRight (16).expandX ().fill ();
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
  public void disableButtonForSelf (final Button button, final PersonPacket person)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (person, "person");

    if (isSelf (person)) disableButton (button);
  }

  @Override
  public void disableButtonForEveryoneElse (final Button button, final PersonPacket person)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (person, "person");

    if (!isSelf (person)) disableButton (button);
  }

  @Override
  public void enableButton (final Button button)
  {
    Arguments.checkIsNotNull (button, "button");

    getButton (button).setDisabled (false);
  }

  @Override
  public void enableButtonForSelf (final Button button, final PersonPacket person)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (person, "person");

    if (isSelf (person)) enableButton (button);
  }

  @Override
  public void enableButtonForEveryoneElse (final Button button, final PersonPacket person)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (person, "person");

    if (!isSelf (person)) enableButton (button);
  }

  @Override
  public void setButtonText (final Button button, final String text)
  {
    Arguments.checkIsNotNull (button, "button");
    Arguments.checkIsNotNull (text, "text");

    getButtonLabel (button).setText (text);
    controlRoomBoxTable.invalidateHierarchy ();
  }

  @Override
  public void setButtonTextForSelf (final Button button, final PersonPacket person, final String text)
  {
    Arguments.checkIsNotNull (person, "person");

    if (isSelf (person)) setButtonText (button, text);
  }

  @Override
  public void setSelf (final PersonPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    self = person;
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

    getButton (Button.TRADE_IN).setStyle (widgetFactory.createControlRoomBoxTradeInButtonStyle ());
    getButton (Button.FORTIFY).setStyle (widgetFactory.createControlRoomBoxFortifyButtonStyle ());
    getButton (Button.END_TURN).setStyle (widgetFactory.createControlRoomBoxEndTurnButtonStyle ());
    getButton (Button.MY_SETTINGS).setStyle (widgetFactory.createControlRoomBoxMySettingsButtonStyle ());
    getButton (Button.QUIT).setStyle (widgetFactory.createControlRoomBoxQuitButtonStyle ());

    for (final Label buttonLabel : buttonLabels.values ())
    {
      buttonLabel.setStyle (widgetFactory.createControlRoomBoxButtonTextLabelStyle ());
    }
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

  private Label getButtonLabel (final Button button)
  {
    final Label label = buttonLabels.get (button);

    if (label == null)
    {
      throw new IllegalStateException (Strings.format ("{}: [{}] does not exist.", Button.class.getSimpleName (),
                                                       button));
    }

    return label;
  }

  private boolean isSelf (final PersonPacket person)
  {
    Arguments.checkIsNotNull (person, "person");

    return self != null && person.is (self);
  }
}
