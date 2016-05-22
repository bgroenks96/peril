/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

package com.forerunnergames.peril.client.ui.widgets.messagebox.playerbox;

import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messagebox.DefaultMessageBox;
import com.forerunnergames.peril.client.ui.widgets.messagebox.MessageBoxStyle;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerBox extends DefaultMessageBox <PlayerBoxRow>
{
  private static final Logger log = LoggerFactory.getLogger (PlayerBox.class);
  private final WidgetFactory widgetFactory;
  private final Collection <PlayerPacket> turnOrderedPlayers = new TreeSet <> (PlayerPacket.TURN_ORDER_COMPARATOR);
  @Nullable
  private PlayerPacket highlightedPlayer;

  public PlayerBox (final MessageBoxStyle style, final WidgetFactory widgetFactory)
  {
    super (style, widgetFactory);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.widgetFactory = widgetFactory;
  }

  @Override
  public void clear ()
  {
    super.clear ();

    turnOrderedPlayers.clear ();
  }

  public void setPlayers (final ImmutableSet <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    turnOrderedPlayers.clear ();

    if (!turnOrderedPlayers.addAll (players))
    {
      log.warn ("Not adding any players [{}] to {}. (Players already added, or duplicate turn order conflict.)",
                players, PlayerBox.class.getSimpleName ());
      return;
    }

    updateMessageBox ();
  }

  public void addPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!turnOrderedPlayers.add (player))
    {
      log.warn ("Not adding player [{}] to {}. (Player already added, or duplicate turn order conflict.)", player,
                PlayerBox.class.getSimpleName ());
      return;
    }

    updateMessageBox ();
  }

  public void removePlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!turnOrderedPlayers.remove (player))
    {
      log.warn ("Not removing player [{}] from {}. (Player not found in {}.)", player,
                PlayerBox.class.getSimpleName (), PlayerBox.class.getSimpleName ());
      return;
    }

    updateMessageBox ();

    if (isHighlighted (player)) clearHighlighting ();
  }

  public void highlightPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!existsRowWith (player))
    {
      log.warn ("Cannot highlight player [{}] (row does not exist).", player);
      return;
    }

    clearHighlighting ();

    getRowWith (player).highlight ();

    highlightedPlayer = player;
  }

  public void updateExisting (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!existsRowWith (player))
    {
      log.warn ("Cannot update player [{}] (not an existing player).", player);
      return;
    }

    final PlayerBoxRow row = getRowWith (player);

    if (row.playerIsNot (player))
    {
      log.warn ("Cannot update player [{}] (player id has changed).", player);
      return;
    }

    if (row.playerTurnOrderDiffersFrom (player))
    {
      log.warn ("Cannot update player [{}] (turn order has changed).", player);
      return;
    }

    row.setPlayer (player);
  }

  private boolean existsRowWith (final PlayerPacket player)
  {
    return hasRowWithIndex (player.getTurnOrder () - 1);
  }

  private PlayerBoxRow getRowWith (final PlayerPacket player)
  {
    final int index = player.getTurnOrder () - 1;

    if (!hasRowWithIndex (index))
    {
      throw new IllegalStateException (Strings.format ("Row with index {} does not exist.", index));
    }

    return getRowByIndex (index);
  }

  private boolean isHighlighted (final PlayerPacket player)
  {
    return highlightedPlayer != null && player.is (highlightedPlayer);
  }

  private void clearHighlighting ()
  {
    for (final PlayerBoxRow row : getRows ())
    {
      row.unhighlight ();
    }
  }

  private void updateMessageBox ()
  {
    super.clear ();

    for (final PlayerPacket player : turnOrderedPlayers)
    {
      addRow (widgetFactory.createPlayerBoxRow (player));

      if (isHighlighted (player)) highlightPlayer (player);
    }
  }
}
