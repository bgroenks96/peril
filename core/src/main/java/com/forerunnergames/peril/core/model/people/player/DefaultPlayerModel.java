package com.forerunnergames.peril.core.model.people.player;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.colorOf;
import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.turnOrderOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;

import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PersonIdentity;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.math.IntMath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class DefaultPlayerModel implements PlayerModel
{
  private final Map <Id, Player> players = new HashMap <> ();
  private final GameRules rules;

  public DefaultPlayerModel (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  @Override
  public void addArmiesToHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canAddArmiesToHandOf (playerId, armies),
                               "Cannot add " + armies + " armies to hand of player with id: [" + playerId
                                       + "]. That player already has " + getArmiesInHand (playerId)
                                       + " armies in hand. The maximum armies allowed in a player's hand is "
                                       + rules.getMaxArmiesInHand () + ".");

    playerWith (playerId).addArmiesToHand (armies);
  }

  @Override
  public boolean canAddArmiesToHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerWith (playerId).getArmiesInHand () <= IntMath.checkedSubtract (rules.getMaxArmiesInHand (), armies);
  }

  @Override
  public boolean canRemoveArmiesFromHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerWith (playerId).getArmiesInHand () >= IntMath.checkedAdd (rules.getMinArmiesInHand (), armies);
  }

  @Override
  public void changeTurnOrderOfPlayer (final Id playerId, final PlayerTurnOrder toTurnOrder)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (toTurnOrder, "toTurnOrder");
    Arguments.checkIsFalse (toTurnOrder.is (PlayerTurnOrder.UNKNOWN), "Invalid player turn order.");

    final Player player = playerWith (playerId);

    if (player.has (toTurnOrder)) return;

    if (existsPlayerWith (toTurnOrder))
    {
      final Player old = playerWith (toTurnOrder);
      old.setTurnOrder (PlayerTurnOrder.UNKNOWN);
      player.setTurnOrder (toTurnOrder);
      old.setTurnOrder (nextAvailableTurnOrder ());
    }
    else
    {
      player.setTurnOrder (toTurnOrder);
    }
  }

  @Override
  public boolean existsPlayerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    return players.containsKey (id);
  }

  @Override
  public boolean existsPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    for (final Player player : players ())
    {
      if (player.has (color)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    for (final Player player : players ())
    {
      if (player.has (turnOrder)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWith (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    for (final Player player : players ())
    {
      if (player.has (identity)) return true;
    }

    return false;
  }

  @Override
  public boolean existsPlayerWithName (final String name)
  {
    return existsPlayerWith (name);
  }

  @Override
  public int getArmiesInHand (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerWith (playerId).getArmiesInHand ();
  }

  @Override
  public int getPlayerCount ()
  {
    return players.size ();
  }

  @Override
  public int getPlayerLimit ()
  {
    return rules.getPlayerLimit ();
  }

  @Override
  public ImmutableSet <Player> getPlayers ()
  {
    return ImmutableSet.copyOf (players ());
  }

  @Override
  public ImmutableSortedSet <Player> getTurnOrderedPlayers ()
  {
    return ImmutableSortedSet.copyOf (Player.TURN_ORDER_COMPARATOR, players ());
  }

  @Override
  public ImmutableSet <Player> getAllPlayersExcept (final Player player)
  {
    return ImmutableSet.copyOf (Collections2.filter (players.values (), not (equalTo (player))));
  }

  @Override
  public boolean hasArmiesInHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerWith (playerId).hasArmiesInHand (armies);
  }

  @Override
  public boolean isEmpty ()
  {
    return players.size () == 0;
  }

  @Override
  public boolean isFull ()
  {
    return players.size () > 0 && players.size () >= rules.getPlayerLimit ();
  }

  @Override
  public boolean isNotEmpty ()
  {
    return !isEmpty ();
  }

  @Override
  public boolean isNotFull ()
  {
    return !isFull ();
  }

  @Override
  public boolean playerCountIs (final int count)
  {
    return players.size () == count;
  }

  @Override
  public boolean playerCountIsNot (final int count)
  {
    return !playerCountIs (count);
  }

  @Override
  public boolean playerLimitIs (final int limit)
  {
    return rules.getPlayerLimit () == limit;
  }

  @Override
  public boolean playerLimitIsAtLeast (final int limit)
  {
    return rules.getPlayerLimit () >= limit;
  }

  @Override
  public Player playerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    final Player player = players.get (id);

    if (player == null) throw new IllegalStateException ("Cannot find any player with id [" + id + "].");

    return player;
  }

  @Override
  public Player playerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return player;
    }

    throw new IllegalStateException ("Cannot find any player named: [" + name + "].");
  }

  @Override
  public Player playerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    for (final Player player : getPlayers ())
    {
      if (player.has (color)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with color: [" + color + "].");
  }

  @Override
  public Player playerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    for (final Player player : getPlayers ())
    {
      if (player.has (turnOrder)) return player;
    }

    throw new IllegalStateException ("Cannot find any player with turn order: [" + turnOrder + "].");
  }

  @Override
  public Player playerWithName (final String name)
  {
    return playerWith (name);
  }

  @Override
  public void removeAllArmiesFromHandsOfAllPlayers ()
  {
    for (final Player player : players ())
    {
      player.removeAllArmiesFromHand ();
    }
  }

  @Override
  public void removeArmiesFromHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canRemoveArmiesFromHandOf (playerId, armies),
                               "Cannot remove " + armies + " armies from hand of player with id: [" + playerId
                                       + "]. That player only has " + getArmiesInHand (playerId)
                                       + " armies in hand. The minimum armies allowed in a player's hand is "
                                       + rules.getMinArmiesInHand () + ".");

    playerWith (playerId).removeArmiesFromHand (armies);
  }

  @Override
  public Result <PlayerJoinGameDeniedEvent.Reason> requestToAdd (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    // @formatter:off
    if (isFull ()) return Result.failure (PlayerJoinGameDeniedEvent.Reason.GAME_IS_FULL);
    if (existsPlayerWith (idOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_ID);
    if (existsPlayerWith (nameOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_NAME);
    if (existsPlayerWith (colorOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_COLOR);
    if (existsPlayerWith (turnOrderOf (player))) return Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_TURN_ORDER);
    if (player.has (PersonIdentity.SELF) && existsPlayerWith (PersonIdentity.SELF)) return Result.failure (PlayerJoinGameDeniedEvent.Reason.DUPLICATE_SELF_IDENTITY);
    // @formatter:on

    add (player);

    return Result.success ();
  }

  @Override
  public void remove (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!existsPlayerWith (idOf (player))) return;

    deregister (player);
    fixTurnOrdersAfterRemovalOfPlayer (player);
  }

  @Override
  public void removeByColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    if (!existsPlayerWith (color)) return;

    remove (playerWith (color));
  }

  @Override
  public void removeById (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (!existsPlayerWith (id)) return;

    remove (playerWith (id));
  }

  @Override
  public void removeByName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    if (!existsPlayerWith (name)) return;

    remove (playerWithName (name));
  }

  @Override
  public void removeByTurnOrder (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");
    Arguments.checkIsTrue (turnOrder.isNot (PlayerTurnOrder.UNKNOWN), "Invalid turn order [" + turnOrder + "].");

    if (!existsPlayerWith (turnOrder)) return;

    remove (playerWith (turnOrder));
  }

  private void add (final Player player)
  {
    if (player.has (PlayerColor.UNKNOWN))
    {
      player.setColor (nextAvailableColor ());
    }
    if (player.has (PlayerTurnOrder.UNKNOWN))
    {
      player.setTurnOrder (nextAvailableTurnOrder ());
    }

    register (player);
  }

  private void deregister (final Player player)
  {
    assert players.containsValue (player);

    players.remove (idOf (player));
  }

  private void fixTurnOrdersAfterRemovalOfPlayer (final Player removedPlayer)
  {
    // Ensure the removed player is really removed, or this will not work.
    // @formatter:off
    assert !players.containsValue (removedPlayer);
    // @formatter:on

    final int danglingTurnOrderPosition = removedPlayer.getTurnOrderPosition ();

    // Lower the turn orders of all players having a turn order higher than the discontinuity, by one.
    // For example, consider where the 2nd player is removed, leaving: 1st, ___, 3rd, 4th, 5th
    // 2nd is the dangling turn order position
    // All positions after the dangling must be bumped up by one, IN ORDER FROM LOWEST TO HIGHEST, to avoid conflicts:
    // 1st, 3rd => 2nd, 4th => 3rd, 5th => 4th, leaving:
    // 1st, 2nd, 3rd, 4th, effectively filling the gap left by the player that was removed.
    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!existsPlayerWith (turnOrder)) continue;
      if (turnOrder.getPosition () <= danglingTurnOrderPosition) continue;

      playerWith (turnOrder).setTurnOrderByPosition (turnOrder.getPosition () - 1);
    }
  }

  private PlayerColor nextAvailableColor ()
  {
    for (final PlayerColor color : PlayerColor.validValues ())
    {
      if (!existsPlayerWith (color)) return color;
    }

    throw new IllegalStateException ("There are no available player colors.");
  }

  private PlayerTurnOrder nextAvailableTurnOrder ()
  {
    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!existsPlayerWith (turnOrder)) return turnOrder;
    }

    throw new IllegalStateException ("There are no available player turn orders.");
  }

  private Collection <Player> players ()
  {
    return players.values ();
  }

  private void register (final Player player)
  {
    assert!players.containsValue (player);
    assert player.doesNotHave (PlayerColor.UNKNOWN);
    assert player.doesNotHave (PlayerTurnOrder.UNKNOWN);

    players.put (idOf (player), player);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Rules [{}] | Players [{}]", getClass ().getSimpleName (), rules.getClass (),
                           players.values ());
  }
}
