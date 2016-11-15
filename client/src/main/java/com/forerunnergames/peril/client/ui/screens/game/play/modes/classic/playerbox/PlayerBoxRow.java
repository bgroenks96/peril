/*
 * Copyright © 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.playerbox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowHighlighting;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.client.ui.widgets.playercoloricons.PlayerColorIcon;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerBoxRow implements MessageBoxRow <Message>
{
  private static final Logger log = LoggerFactory.getLogger (PlayerBoxRow.class);
  private final WidgetFactory widgetFactory;
  private final MessageBoxRowStyle rowStyle;
  private final MessageBoxRowHighlighting highlighting;
  private final Table table = new Table ();
  private final Stack stack = new Stack ();
  private final Cell <Actor> messageRowLeftCell;
  private final Cell <Actor> messageRowRightCell;
  private final Cell <Actor> playerColorIconCell;
  private PlayerColorIcon playerColorIcon;
  private MessageBoxRow <Message> messageRowLeft;
  private MessageBoxRow <Message> messageRowRight;
  private Message message;
  private PlayerPacket player;

  public PlayerBoxRow (final PlayerPacket player, final MessageBoxRowStyle rowStyle, final WidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (rowStyle, "rowStyle");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.rowStyle = rowStyle;
    this.widgetFactory = widgetFactory;

    highlighting = widgetFactory.createMessageBoxRowHighlighting ();
    playerColorIcon = widgetFactory.createPlayerColorIcon (player);

    table.left ();
    messageRowLeftCell = table.add ((Actor) null).padLeft (10).width (40);
    playerColorIconCell = table.add (playerColorIcon.asActor ()).spaceRight (8);
    messageRowRightCell = table.add ((Actor) null).spaceLeft (8);

    stack.add (highlighting.asActor ());
    stack.add (table);

    setPlayer (player);
    unhighlight ();
  }

  @Override
  public Message getMessage ()
  {
    return message;
  }

  @Override
  public String getMessageText ()
  {
    return message.getText ();
  }

  @Override
  public void refreshAssets ()
  {
    messageRowLeft.refreshAssets ();
    messageRowRight.refreshAssets ();
    highlighting.refreshAssets ();
    playerColorIcon.refreshAssets ();

    table.invalidateHierarchy ();
  }

  @Override
  public Actor asActor ()
  {
    return stack;
  }

  public boolean playerIsNot (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return this.player.isNot (player);
  }

  public boolean playerTurnOrderDiffersFrom (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return this.player.doesNotHave (player.getTurnOrder ());
  }

  public void setPlayerArmiesInHand (final int armies)
  {
    Arguments.checkIsNotNegative (armies, "armies");

    assert player != null;

    messageRowRight = createMessageRow (createMessageTextRight (player.getName (), armies, player.getCardsInHand ()));
    messageRowRightCell.setActor (messageRowRight.asActor ());
    message = createMessage ();

    table.invalidateHierarchy ();
  }

  public void highlight ()
  {
    highlighting.setVisible (true);
  }

  public void unhighlight ()
  {
    highlighting.setVisible (false);
  }

  public boolean playerIs (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return this.player.is (player);
  }

  public boolean playerTurnOrderIs (final int turnOrder)
  {
    Arguments.checkIsNotNegative (turnOrder, "turnOrder");

    return player.has (turnOrder);
  }

  public PlayerPacket getPlayer ()
  {
    return player;
  }

  public void setPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    log.trace ("Setting player: Old player: [{}] | New player: [{}]", this.player, player);

    this.player = player;

    messageRowLeft = createMessageRow (createMessageTextLeft (player.getTurnOrder ()));
    messageRowRight = createMessageRow (createMessageTextRight (player.getName (), player.getArmiesInHand (),
                                                                player.getCardsInHand ()));
    message = createMessage ();

    messageRowLeftCell.setActor (messageRowLeft.asActor ());
    messageRowRightCell.setActor (messageRowRight.asActor ());

    playerColorIcon = widgetFactory.createPlayerColorIcon (player);
    playerColorIconCell.setActor (playerColorIcon.asActor ());

    table.invalidateHierarchy ();
  }

  public boolean playerHasName (final String playerName)
  {
    Arguments.checkIsNotNull (playerName, "playerName");

    return player.hasName (playerName);
  }

  private static String createMessageTextLeft (final int playerTurnOrder)
  {
    return Strings.toMixedOrdinal (playerTurnOrder);
  }

  private static String createMessageTextRight (final String playerName,
                                                final int playerArmiesInHand,
                                                final int playerCardsInHand)
  {
    return playerName + ", " + Strings.pluralize (playerArmiesInHand, "army", "armies") + ", "
            + Strings.pluralizeS (playerCardsInHand, "card");
  }

  private MessageBoxRow <Message> createMessageRow (final String messageText)
  {
    return widgetFactory.createMessageBoxRow (new DefaultMessage (messageText), rowStyle);
  }

  private Message createMessage ()
  {
    return new DefaultMessage (messageRowLeft.getMessageText () + " " + messageRowRight.getMessageText ());
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: {} | Highlighting: {} | Player Color Icon: {} | Message: {} | "
                                   + " Message Row Left: {} | Message Row Right: {} | Row Style: {} | Table: {} | "
                                   + "Stack: {}",
                           super.toString (), player, highlighting, playerColorIcon, message, messageRowLeft,
                           messageRowRight, rowStyle, table, stack);
  }
}
