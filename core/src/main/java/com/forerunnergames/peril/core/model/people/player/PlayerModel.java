package com.forerunnergames.peril.core.model.people.player;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.*;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.settings.GameSettings;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerLeaveGameDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;
import com.forerunnergames.tools.common.Result;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class PlayerModel
{
  private final Map <Id, Player> players = new HashMap<>();
  private int playerLimit;

  public PlayerModel (final int initialPlayerLimit)
  {
    Arguments.checkIsNotNegative (initialPlayerLimit, "initialPlayerLimit");
    Arguments.checkUpperInclusiveBound (initialPlayerLimit, GameSettings.MAX_PLAYERS, "initialPlayerLimit", "GameSettings.MAX_PLAYERS");

    playerLimit = initialPlayerLimit;
  }

  public Result <PlayerJoinGameDeniedEvent.REASON> requestToAdd (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (isFull()) return Result.failure (PlayerJoinGameDeniedEvent.REASON.GAME_IS_FULL);
    if (existsPlayerWith (idOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_ID);
    if (existsPlayerWith (nameOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_NAME);
    if (existsPlayerWith (colorOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_COLOR);
    if (existsPlayerWith (turnOrderOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_TURN_ORDER);
    if (player.has (PersonIdentity.SELF) && existsPlayerWith (PersonIdentity.SELF)) return Result.failure (PlayerJoinGameDeniedEvent.REASON.DUPLICATE_SELF_IDENTITY);

    add (player);

    return Result.success();
  }

  public boolean isFull()
  {
    return players.size() > 0 && players.size() >= playerLimit;
  }

  public boolean isNotFull()
  {
    return ! isFull();
  }

  public boolean existsPlayerWith (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    for (final Player player : players())
    {
      if (player.has (identity)) return true;
    }

    return false;
  }

  public boolean existsPlayerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    return players.containsKey (id);
  }

  public boolean existsPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players())
    {
      if (player.has (name)) return true;
    }

    return false;
  }

  public boolean existsPlayerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    for (final Player player : players())
    {
      if (player.has (color)) return true;
    }

    return false;
  }

  public boolean existsPlayerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    for (final Player player : players())
    {
      if (player.has (turnOrder)) return true;
    }

    return false;
  }

  public Result <ChangePlayerLimitDeniedEvent.REASON> requestToSetPlayerLimitTo (final int limit)
  {
    if (limit < 0) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO);
    if (limit > GameSettings.MAX_PLAYERS) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS);

    return requestToChangePlayerLimitBy (limit - playerLimit);
  }

  public Result <ChangePlayerLimitDeniedEvent.REASON> requestToChangePlayerLimitBy (final int delta)
  {
    if (delta == 0) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.REQUESTED_LIMIT_EQUALS_EXISTING_LIMIT);
    if (delta < -GameSettings.MAX_PLAYERS) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO);
    if (delta > GameSettings.MAX_PLAYERS) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS);
    if (playerLimit + delta < 0) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_ZERO);
    if (playerLimit + delta > GameSettings.MAX_PLAYERS) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_INCREASE_ABOVE_MAX_PLAYERS);
    if (playerLimit + delta < players.size()) return Result.failure (ChangePlayerLimitDeniedEvent.REASON.CANNOT_DECREASE_BELOW_CURRENT_PLAYER_COUNT);

    playerLimit += delta;

    return Result.success();
  }

  public Result <ChangePlayerColorDeniedEvent.REASON> requestToChangeColorOfPlayer (final Id playerId, final PlayerColor toColor)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (toColor, "toColor");

    final Player player = playerWith (playerId);

    if (player.has (toColor)) return Result.failure (ChangePlayerColorDeniedEvent.REASON.REQUESTED_COLOR_EQUALS_EXISTING_COLOR);
    if (existsPlayerWith (toColor)) return Result.failure (ChangePlayerColorDeniedEvent.REASON.COLOR_ALREADY_TAKEN);
    if (toColor == PlayerColor.UNKNOWN) return Result.failure (ChangePlayerColorDeniedEvent.REASON.REQUESTED_COLOR_INALID);

    player.setColor (toColor);

    return Result.success();
  }

  public Result <PlayerLeaveGameDeniedEvent.REASON> requestToRemoveByName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    if (! existsPlayerWith (name)) return Result.failure (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST);

    return requestToRemove (playerWithName (name));
  }

  public Result <PlayerLeaveGameDeniedEvent.REASON> requestToRemove (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (! existsPlayerWith (idOf (player))) return Result.failure (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST);

    deregister (player);

    return Result.success();
  }

  public Result <PlayerLeaveGameDeniedEvent.REASON> requestToRemoveById (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (! existsPlayerWith (id)) return Result.failure (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST);

    return requestToRemove (playerWith (id));
  }

  public Result <PlayerLeaveGameDeniedEvent.REASON> requestToRemoveByColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    if (! existsPlayerWith (color)) return Result.failure (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST);

    return requestToRemove (playerWith (color));
  }

  public Player playerWithName (final String name)
  {
    return playerWith (name);
  }

  public Player playerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players())
    {
      if (player.has (name)) return player;
    }

    throw new IllegalStateException ("Cannot find any player named: [" + name + "].");
  }

  public Player playerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    final Player player = players.get (id);

    if (player == null) throw new IllegalStateException ("Cannot find any player with id [" + id + "].");

    return player;
  }

  public Player playerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    for (final Player player : getPlayers())
    {
      if (player.has (color)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with color: [" + color + "].");
  }

  public ImmutableSet <Player> getPlayers()
  {
    return ImmutableSet.copyOf (players.values());
  }

  public Result <PlayerLeaveGameDeniedEvent.REASON> requestToRemoveByTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    if (! existsPlayerWith (turnOrder)) return Result.failure (PlayerLeaveGameDeniedEvent.REASON.PLAYER_DOES_NOT_EXIST);

    return requestToRemove (playerWith (turnOrder));
  }

  public void changeTurnOrderOfPlayer (final Id playerId, final PlayerTurnOrder toTurnOrder)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (toTurnOrder, "turnOrder");
    Arguments.checkIsFalse (toTurnOrder.is (PlayerTurnOrder.UNKNOWN), "Invalid player turn order.");

    final Player player = playerWith (playerId);

    if (player.has (toTurnOrder)) return;

    if (existsPlayerWith (toTurnOrder))
    {
      final Player old = playerWith (toTurnOrder);
      old.setTurnOrder (PlayerTurnOrder.UNKNOWN);
      player.setTurnOrder (toTurnOrder);
      old.setTurnOrder (nextAvailableTurnOrder());
    }
    else
    {
      player.setTurnOrder (toTurnOrder);
    }
  }

  public Player playerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    for (final Player player : getPlayers())
    {
      if (player.has (turnOrder)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with turn order: [" + turnOrder + "].");
  }

  public int getPlayerLimit()
  {
    return playerLimit;
  }

  public boolean playerLimitIs (final int limit)
  {
    return playerLimit == limit;
  }

  public boolean playerLimitIsAtLeast (final int limit)
  {
    return playerLimit >= limit;
  }

  public int getPlayerCount()
  {
    return players.size();
  }

  public boolean playerCountIs (final int count)
  {
    return players.size() == count;
  }

  public boolean playerCountIsNot (final int count)
  {
    return ! playerCountIs (count);
  }

  public boolean isEmpty()
  {
    return players.size() == 0;
  }

  public boolean isNotEmpty()
  {
    return ! isEmpty();
  }

  public boolean existsPlayerWithName (final String name)
  {
    return existsPlayerWith (name);
  }

  private void add (final Player player)
  {
    if (player.has (PlayerColor.UNKNOWN)) player.setColor (nextAvailableColor());
    if (player.has (PlayerTurnOrder.UNKNOWN)) player.setTurnOrder (nextAvailableTurnOrder());

    register (player);
  }

  private void register (final Player player)
  {
    assert ! players.containsValue (player);
    assert player.doesNotHave (PlayerColor.UNKNOWN);
    assert player.doesNotHave (PlayerTurnOrder.UNKNOWN);

    players.put (idOf (player), player);
  }

  private void deregister (final Player player)
  {
    assert players.containsValue (player);

    players.remove (idOf (player));
  }

  private Collection <Player> players()
  {
    return players.values();
  }

  private PlayerColor nextAvailableColor()
  {
    for (final PlayerColor color : playerColors())
    {
      if (isAvailable (color)) return color;
    }

    throw new IllegalStateException ("There are no available player colors.");
  }

  private PlayerTurnOrder nextAvailableTurnOrder()
  {
    for (final PlayerTurnOrder turnOrder : playerTurnOrders())
    {
      if (isAvailable (turnOrder)) return turnOrder;
    }

    throw new IllegalStateException ("There are no available player turn orders.");
  }

  private PlayerColor[] playerColors()
  {
    return PlayerColor.values();
  }

  private boolean isAvailable (final PlayerColor color)
  {
    return ! existsPlayerWith (color) && color.isNot (PlayerColor.UNKNOWN);
  }

  private PlayerTurnOrder[] playerTurnOrders()
  {
    return PlayerTurnOrder.values();
  }

  private boolean isAvailable (final PlayerTurnOrder turnOrder)
  {
    return ! existsPlayerWith (turnOrder) && turnOrder.isNot (PlayerTurnOrder.UNKNOWN);
  }
}
