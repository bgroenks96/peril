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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerBox extends DefaultMessageBox <PlayerBoxRow>
{
  private static final Logger log = LoggerFactory.getLogger (PlayerBox.class);
  private final WidgetFactory widgetFactory;
  private final Collection <PlayerPacket> players = new HashSet <> ();
  private final Map <String, Integer> playerNamesToRowIndices = new HashMap <> ();
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

    players.clear ();
    playerNamesToRowIndices.clear ();
  }

  public void setPlayers (final ImmutableSet <PlayerPacket> players)
  {
    Arguments.checkIsNotNull (players, "players");
    Arguments.checkHasNoNullElements (players, "players");

    this.players.clear ();
    playerNamesToRowIndices.clear ();

    final boolean anyWereAdded = this.players.addAll (players);
    assert anyWereAdded;

    updateMessageBox ();
  }

  public void addPlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    internalAdd (player);
    updateMessageBox ();
  }

  public void removePlayer (final PlayerPacket player)
  {
    Arguments.checkIsNotNull (player, "player");

    internalRemove (player);
    updateMessageBox ();
    if (isHighlighted (player)) clearHighlighting ();
  }

  public void updatePlayerWithNewTurnOrder (final PlayerPacket player, final int oldTurnOrder)
  {
    assert existsRowWith (player);
    assert getRowWith (player).playerTurnOrderIs (oldTurnOrder);

    // Cannot use #updateExisting(PlayerPacket) because because it won't update the set of turn-ordered players.
    // Player must be removed and re-added.
    // Avoids unnecessarily updating the message box twice and superfluously de-highlighting/re-highlighting player by
    // not using the public API for add / remove.
    internalRemove (player);
    internalAdd (player);
    updateMessageBox ();
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

  public void setArmiesInHand (final int armies, final PlayerPacket player)
  {
    Arguments.checkIsNotNegative (armies, "armies");
    Arguments.checkIsNotNull (player, "player");

    getRowWith (player).setPlayerArmiesInHand (armies);
  }

  private void internalRemove (final PlayerPacket player)
  {
    if (!players.remove (player))
    {
      log.warn ("Not removing player [{}] from {}. (Player not found in {}.)", player,
                PlayerBox.class.getSimpleName (), PlayerBox.class.getSimpleName ());
    }
  }

  private void internalAdd (final PlayerPacket player)
  {
    if (!players.add (player))
    {
      log.warn ("Not adding player [{}] to {}. (Player already added, or duplicate turn order conflict.)", player,
                PlayerBox.class.getSimpleName ());
    }
  }

  private boolean existsRowWith (final PlayerPacket player)
  {
    return hasRowWithIndex (playerNamesToRowIndices.get (player.getName ()));
  }

  private PlayerBoxRow getRowWith (final PlayerPacket player)
  {
    final PlayerBoxRow row = getRowWith (player.getName ());
    assert row.playerIs (player);

    return row;
  }

  private PlayerBoxRow getRowWith (final String playerName)
  {
    final Integer index = playerNamesToRowIndices.get (playerName);
    assert index != null;
    assert hasRowWithIndex (index);

    final PlayerBoxRow row = getRowByIndex (index);
    assert row.playerHasName (playerName);

    return row;
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

  // Generally only needs to be called after adding or removing players.
  private void updateMessageBox ()
  {
    super.clear ();

    int rowIndex = 0;
    playerNamesToRowIndices.clear ();

    for (final PlayerPacket player : ImmutableSortedSet.copyOf (PlayerPacket.TURN_ORDER_COMPARATOR, players))
    {
      addRow (widgetFactory.createPlayerBoxRow (player));
      playerNamesToRowIndices.put (player.getName (), rowIndex);
      if (isHighlighted (player)) highlightPlayer (player);
      rowIndex++;
    }
  }
}
