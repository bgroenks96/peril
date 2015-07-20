package com.forerunnergames.peril.core.model.people.player;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.colorOf;
import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.turnOrderOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;

import com.forerunnergames.peril.core.model.people.person.PersonIdentity;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.shared.net.events.server.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.math.IntMath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class PlayerModel
{
  private final Map <Id, Player> players = new HashMap <> ();
  private final GameRules rules;

  public PlayerModel (final GameRules rules)
  {
    Arguments.checkIsNotNull (rules, "rules");

    this.rules = rules;
  }

  public void addArmiesToHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canAddArmiesToHandOf (playerId, armies), "Cannot add " + armies
            + " armies to hand of player with id: [" + playerId + "]. That player already has "
            + getArmiesInHandOf (playerId) + " armies in hand. The maximum armies allowed in a player's hand is "
            + rules.getMaxArmiesInHand () + ".");

    playerWith (playerId).addArmiesToHand (armies);
  }

  public boolean canAddArmiesToHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerWith (playerId).getArmiesInHand () <= IntMath.checkedSubtract (rules.getMaxArmiesInHand (), armies);
  }

  public boolean canRemoveArmiesFromHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerWith (playerId).getArmiesInHand () >= IntMath.checkedAdd (rules.getMinArmiesInHand (), armies);
  }

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

  public boolean existsPlayerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    return players.containsKey (id);
  }

  public boolean existsPlayerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return true;
    }

    return false;
  }

  public boolean existsPlayerWith (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");

    for (final Player player : players ())
    {
      if (player.has (color)) return true;
    }

    return false;
  }

  public boolean existsPlayerWith (final PlayerTurnOrder turnOrder)
  {
    Arguments.checkIsNotNull (turnOrder, "turnOrder");

    for (final Player player : players ())
    {
      if (player.has (turnOrder)) return true;
    }

    return false;
  }

  public boolean existsPlayerWith (final PersonIdentity identity)
  {
    Arguments.checkIsNotNull (identity, "identity");

    for (final Player player : players ())
    {
      if (player.has (identity)) return true;
    }

    return false;
  }

  public boolean existsPlayerWithName (final String name)
  {
    return existsPlayerWith (name);
  }

  public int getArmiesInHandOf (final Id playerId)
  {
    Arguments.checkIsNotNull (playerId, "playerId");

    return playerWith (playerId).getArmiesInHand ();
  }

  public int getPlayerCount ()
  {
    return players.size ();
  }

  public int getPlayerLimit ()
  {
    return rules.getPlayerLimit ();
  }

  public ImmutableSet <Player> getPlayers ()
  {
    return ImmutableSet.copyOf (players ());
  }

  public ImmutableSortedSet <Player> getTurnOrderedPlayers ()
  {
    return ImmutableSortedSet.copyOf (Player.TURN_ORDER_COMPARATOR, players ());
  }

  public ImmutableSet <Player> getAllPlayersExcept (final Player player)
  {
    return ImmutableSet.copyOf (Collections2.filter (players.values (), not (equalTo (player))));
  }

  public boolean hasArmiesInHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");

    return playerWith (playerId).hasArmiesInHand (armies);
  }

  public boolean isEmpty ()
  {
    return players.size () == 0;
  }

  public boolean isFull ()
  {
    return players.size () > 0 && players.size () >= rules.getPlayerLimit ();
  }

  public boolean isNotEmpty ()
  {
    return !isEmpty ();
  }

  public boolean isNotFull ()
  {
    return !isFull ();
  }

  public boolean playerCountIs (final int count)
  {
    return players.size () == count;
  }

  public boolean playerCountIsNot (final int count)
  {
    return !playerCountIs (count);
  }

  public boolean playerLimitIs (final int limit)
  {
    return rules.getPlayerLimit () == limit;
  }

  public boolean playerLimitIsAtLeast (final int limit)
  {
    return rules.getPlayerLimit () >= limit;
  }

  public Player playerWith (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    final Player player = players.get (id);

    if (player == null) throw new IllegalStateException ("Cannot find any player with id [" + id + "].");

    return player;
  }

  public Player playerWith (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    for (final Player player : players ())
    {
      if (player.has (name)) return player;
    }

    throw new IllegalStateException ("Cannot find any player named: [" + name + "].");
  }

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

  public Player playerWithName (final String name)
  {
    return playerWith (name);
  }

  public void removeAllArmiesFromHandsOfAllPlayers ()
  {
    for (final Player player : players ())
    {
      player.removeAllArmiesFromHand ();
    }
  }

  public void removeArmiesFromHandOf (final Id playerId, final int armies)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNegative (armies, "armies");
    Preconditions.checkIsTrue (canRemoveArmiesFromHandOf (playerId, armies), "Cannot remove " + armies
            + " armies from hand of player with id: [" + playerId + "]. That player only has "
            + getArmiesInHandOf (playerId) + " armies in hand. The minimum armies allowed in a player's hand is "
            + rules.getMinArmiesInHand () + ".");

    playerWith (playerId).removeArmiesFromHand (armies);
  }

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

  public Result <ChangePlayerColorDeniedEvent.Reason> requestToChangeColorOfPlayer (final Id playerId,
                                                                                    final PlayerColor toColor)
  {
    Arguments.checkIsNotNull (playerId, "playerId");
    Arguments.checkIsNotNull (toColor, "toColor");

    final Player player = playerWith (playerId);

    // @formatter:off
    if (player.has (toColor)) return Result.failure (ChangePlayerColorDeniedEvent.Reason.REQUESTED_COLOR_EQUALS_EXISTING_COLOR);
    if (existsPlayerWith (toColor)) return Result.failure (ChangePlayerColorDeniedEvent.Reason.COLOR_ALREADY_TAKEN);
    if (toColor == PlayerColor.UNKNOWN) return Result.failure (ChangePlayerColorDeniedEvent.Reason.REQUESTED_COLOR_INVALID);
    // @formatter:on

    player.setColor (toColor);

    return Result.success ();
  }

  public void remove (final Player player)
  {
    Arguments.checkIsNotNull (player, "player");

    if (!existsPlayerWith (idOf (player))) return;

    deregister (player);
    fixTurnOrdersAfterRemovalOfPlayer (player);
  }

  private void fixTurnOrdersAfterRemovalOfPlayer (final Player removedPlayer)
  {
    // Ensure the removed player is really removed, or this will not work.
    assert !  players.containsValue (removedPlayer);

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

  public void removeByColor (final PlayerColor color)
  {
    Arguments.checkIsNotNull (color, "color");
    Arguments.checkIsTrue (color.isNot (PlayerColor.UNKNOWN), "Invalid color [" + color + "].");

    if (!existsPlayerWith (color)) return;

    remove (playerWith (color));
  }

  public void removeById (final Id id)
  {
    Arguments.checkIsNotNull (id, "id");

    if (!existsPlayerWith (id)) return;

    remove (playerWith (id));
  }

  public void removeByName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    if (!existsPlayerWith (name)) return;

    remove (playerWithName (name));
  }

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
    assert !players.containsValue (player);
    assert player.doesNotHave (PlayerColor.UNKNOWN);
    assert player.doesNotHave (PlayerTurnOrder.UNKNOWN);

    players.put (idOf (player), player);
  }
}
