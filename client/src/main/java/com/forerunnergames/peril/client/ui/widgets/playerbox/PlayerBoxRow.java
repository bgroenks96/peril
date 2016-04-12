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

package com.forerunnergames.peril.client.ui.widgets.playerbox;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowHighlighting;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxRowStyle;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Message;
import com.forerunnergames.tools.common.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerBoxRow extends DefaultMessageBoxRow <Message>
{
  private static final Logger log = LoggerFactory.getLogger (PlayerBoxRow.class);
  private final MessageBoxRowHighlighting highlighting;
  private final Stack stack = new Stack ();
  private PlayerPacket player;

  public PlayerBoxRow (final PlayerPacket player,
                       final MessageBoxRowStyle rowStyle,
                       final WidgetFactory widgetFactory,
                       final MessageBoxRowHighlighting highlighting)
  {
    super (new DefaultMessage (createMessageLabelText (player)), rowStyle, widgetFactory);

    Arguments.checkIsNotNull (player, "player");
    Arguments.checkIsNotNull (highlighting, "highlighting");

    this.player = player;
    this.highlighting = highlighting;

    stack.add (super.asActor ());
    stack.add (highlighting.asActor ());

    unhighlight ();
  }

  @Override
  public void refreshAssets ()
  {
    super.refreshAssets ();

    highlighting.refreshAssets ();

    stack.invalidate ();
  }

  @Override
  public Actor asActor ()
  {
    return stack;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{} | Player: {} | Highlighting: {} | Stack: {}", super.toString (), player, highlighting,
                           stack);
  }

  public boolean playerIsNot (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.isNot (player);
  }

  public boolean playerTurnOrderDiffersFrom (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    return player.doesNotHave (player.getTurnOrder ());
  }

  public void setPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    log.trace ("Setting player: Old player: [{}] | New player: [{}]", this.player, player);

    setMessage (new DefaultMessage (createMessageLabelText (player)));

    this.player = player;
  }

  public void highlight ()
  {
    highlighting.setVisible (true);
  }

  public void unhighlight ()
  {
    highlighting.setVisible (false);
  }

  private static String createMessageLabelText (final PlayerPacket player)
  {
    assert player != null;

    return Strings.toMixedOrdinal (player.getTurnOrder ()) + ". " + player.getName () + " "
            + Strings.pluralize (player.getArmiesInHand (), "army", "armies");
  }
}
