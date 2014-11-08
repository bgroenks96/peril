package com.forerunnergames.peril.core.model.player;

import static com.google.common.collect.Maps.newHashMap;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Id;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlayerModel
{
  private static final Logger log = LoggerFactory.getLogger (PlayerModel.class);
  private final Map <Id, Player> players = newHashMap();
  private final int maxPlayers;
  private int playerLimit;
  private int nextAvailableIdValue;

  public PlayerModel (final int maxPlayers, final int initialPlayerLimit)
  {
    Arguments.checkLowerInclusiveBound (maxPlayers, 1, "maxPlayers");
    Arguments.checkIsNotNegative (initialPlayerLimit, "initialPlayerLimit");
    Arguments.checkUpperInclusiveBound (initialPlayerLimit, maxPlayers, "initialPlayerLimit", "maxPlayers");

    this.maxPlayers = maxPlayers;
    playerLimit = initialPlayerLimit;
  }

  public ImmutableSet <Player> getPlayers()
  {
    return ImmutableSet.copyOf (players.values());
  }

  public boolean isFull()
  {
    return players.size() > 0 && players.size() >= playerLimit;
  }

  public boolean isEmpty()
  {
    return players.size() == 0;
  }

  public Result add (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (isFull())
    {
      return Result.failure ("The game is full.");
    }
    else if (existsPlayerWith (PlayerInterpreter.idOf (player)))
    {
      return Result.failure ("You are already in the game.");
    }
    else if (existsPlayerWith (PlayerInterpreter.nameOf (player)))
    {
      return Result.failure ("The name \"" + PlayerInterpreter.nameOf (player) + "\" is already taken.");
    }
    else if (existsPlayerWith (PlayerInterpreter.colorOf (player)))
    {
      return Result.failure ("The color " + PlayerInterpreter.colorOf (player).toLowerCase() + " is already taken.");
    }
    else if (existsPlayerWith (PlayerInterpreter.turnOrderOf (player)))
    {
      return Result.failure (Strings.toProperCase (PlayerInterpreter.turnOrderOf (player).toMixedOrdinal()) + " place is already taken.");
    }

    player.setColor (nextAvailableColor());
    player.setTurnOrder (nextAvailableTurnOrder());
    register (player);

    return Result.success();
  }

  public boolean existsPlayerWith (final Id id)
  {
    return players.containsKey (id);
  }

  private boolean existsPlayerWith (final String name)
  {
    for (final Player player : players())
    {
      if (player.has (name)) return true;
    }

    return false;
  }

  private boolean existsPlayerWith (final PlayerColor color)
  {
    if (color.is (PlayerColor.UNKNOWN)) return false;

    for (final Player player : players())
    {
      if (player.has (color)) return true;
    }

    return false;
  }

  private boolean existsPlayerWith (final PlayerTurnOrder turnOrder)
  {
    if (turnOrder.is (PlayerTurnOrder.UNKNOWN)) return false;

    for (final Player player : players())
    {
      if (player.has (turnOrder)) return true;
    }

    return false;
  }

  private void register (final Player player)
  {
    assert ! players.containsValue (player);
    assert ! player.has (PlayerColor.UNKNOWN);
    assert ! player.has (PlayerTurnOrder.UNKNOWN);

    players.put (PlayerInterpreter.idOf (player), player);
  }

  private Collection <Player> players()
  {
    return players.values();
  }

  public void remove (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (! existsPlayerWith (PlayerInterpreter.idOf (player)))
    {
      log.warn ("Prevented attempt to remove non-existent player [{}].", player);
      return;
    }

    deregister (player);
  }

  private void deregister (final Player player)
  {
    assert players.containsValue (player);
    players.remove (PlayerInterpreter.idOf (player));
  }

  public void reset()
  {
    players.clear();
    playerLimit = 0;
  }

  public PlayerColor nextAvailableColor()
  {
    for (final PlayerColor color : playerColors())
    {
      if (isAvailable (color)) return color;
    }

    throw new IllegalStateException ("There are no available player colors.");
  }

  private PlayerColor[] playerColors()
  {
    return PlayerColor.values();
  }

  private boolean isAvailable (final PlayerColor color)
  {
   return ! existsPlayerWith (color) && color.isNot (PlayerColor.UNKNOWN);
  }

  public PlayerTurnOrder nextAvailableTurnOrder()
  {
    for (final PlayerTurnOrder turnOrder : playerTurnOrders())
    {
      if (isAvailable (turnOrder)) return turnOrder;
    }

    throw new IllegalStateException ("There are no available player turn orders.");
  }

  private PlayerTurnOrder[] playerTurnOrders()
  {
    return PlayerTurnOrder.values();
  }

  private boolean isAvailable (final PlayerTurnOrder turnOrder)
  {
    return ! existsPlayerWith (turnOrder) && turnOrder.isNot (PlayerTurnOrder.UNKNOWN);
  }

  public Id nextAvailableId()
  {
    nextAvailableIdValue = 0;

    while (existsPlayerWith (nextAvailableIdValue))
    {
      ++nextAvailableIdValue;
    }

    return new Id (nextAvailableIdValue);
  }

  private boolean existsPlayerWith (final int idValue)
  {
    for (final Player player : players())
    {
      if (PlayerInterpreter.idValueOf (player) == idValue) return true;
    }

    return false;
  }

  public int getPlayerLimit()
  {
    return playerLimit;
  }

  public void changePlayerLimitBy (final int delta)
  {
    if (delta == 0)
    {
      throw new IllegalStateException ("Cannot change player limit [" + playerLimit + "] by delta [" + delta + "].");
    }
    else if (playerLimit + delta > maxPlayers)
    {
      throw new IllegalStateException ("Cannot increase player limit [" + playerLimit + "] by delta [" + delta +
              "] because player limit cannot increase beyond max players [" + maxPlayers + "].");
    }
    else if (playerLimit + delta < 0)
    {
      throw new IllegalStateException ("Cannot decrease player limit [" + playerLimit + "] by delta [" + delta +
              "] because player limit cannot decrease below 0.");
    }
    else if (playerLimit + delta < players.size())
    {
      throw new IllegalStateException ("Cannot decrease player limit [" + playerLimit + "] by delta [" + delta +
              "] because player limit cannot decrease below current player count [" + players.size() + "].");
    }

    playerLimit += delta;
  }

  public Result changeColorOf (final Id playerId, final PlayerColor toColor, final PlayerColor fromColor)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (toColor, "toColor");
    Arguments.checkIsNotNull (fromColor, "fromColor");

    if (! existsPlayerWith (playerId))
    {
      throw new IllegalStateException ("Cannot change color of player with id [" + playerId + "] from [" +
              fromColor + "] to [" + toColor + "] because a player with that id does not exist.");
    }

    if (anyPlayerHasColorExcept (playerId, toColor))
    {
      return Result.failure ("Bummer! " + nameOfPlayerWith (toColor) + " actually grabbed the color " +
              toColor.toLowerCase() + " just before you did.");
    }

    playerWith(playerId).setColor (toColor);

    return Result.success();
  }

  private boolean anyPlayerHasColorExcept (final Id playerId, final PlayerColor color)
  {
    for (final Player otherPlayer : getPlayers())
    {
      if (otherPlayer.has (color) && otherPlayer.doesNotHave (playerId)) return true;
    }

    return false;
  }

  private Player playerWith (final PlayerColor color)
  {
    for (final Player player : getPlayers())
    {
      if (player.has (color)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with color: [" + color + "].");
  }

  private String nameOfPlayerWith (final PlayerColor color)
  {
    return PlayerInterpreter.nameOf (playerWith (color));
  }

  public Player playerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (! players.containsKey (id))
    {
      throw new IllegalStateException ("Cannot find player with id [" + id + "].");
    }

    return players.get (id);
  }
}
