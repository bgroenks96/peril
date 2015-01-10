package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.idOf;
import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.nameOf;
import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.withIdOf;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.currentColorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.deltaFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.previousColorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withPlayerNameFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;

import com.forerunnergames.peril.core.model.armies.ArmyFactory;
import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineCondition;
import com.forerunnergames.peril.core.model.strategy.GameStrategy;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerColorSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerLimitSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private final PlayerModel playerModel;
  private final GameStrategy strategy;
  private final MBassador <Event> eventBus;

  public GameModel (final PlayerModel playerModel, final GameStrategy strategy, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (strategy, "strategy");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.strategy = strategy;
    this.eventBus = eventBus;
  }

  @StateMachineAction
  public void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Player player = PlayerFactory.create (withPlayerNameFrom (event));
    final Result <PlayerJoinGameDeniedEvent.REASON> result;

    result = playerModel.requestToAdd (player);

    if (result.isSuccessful ())
    {
      eventBus.publish (new PlayerJoinGameSuccessEvent (player));
    }
    else
    {
      eventBus.publish (new PlayerJoinGameDeniedEvent (nameOf (player), failureReasonFrom (result)));
    }
  }

  @StateMachineAction
  public void handleChangePlayerLimitRequest (final ChangePlayerLimitRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final int oldLimit = playerModel.getPlayerLimit ();

    final Result <ChangePlayerLimitDeniedEvent.REASON> result;
    result = playerModel.requestToChangePlayerLimitBy (deltaFrom (event));

    final int newLimit = playerModel.getPlayerLimit ();

    if (result.isSuccessful ())
    {
      eventBus.publish (new ChangePlayerLimitSuccessEvent (newLimit, oldLimit, deltaFrom (event)));
    }
    else
    {
      eventBus.publish (new ChangePlayerLimitDeniedEvent (deltaFrom (event), failureReasonFrom (result)));
    }
  }

  @StateMachineAction
  public void handleChangePlayerColorRequest (final ChangePlayerColorRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Player player = playerModel.playerWith (previousColorFrom (event));

    final Result <ChangePlayerColorDeniedEvent.REASON> result;
    result = playerModel.requestToChangeColorOfPlayer (withIdOf (player), currentColorFrom (event));

    if (result.isSuccessful ())
    {
      eventBus.publish (new ChangePlayerColorSuccessEvent (event));
    }
    else
    {
      eventBus.publish (new ChangePlayerColorDeniedEvent (event, failureReasonFrom (result)));
    }
  }

  @StateMachineAction
  public void beginGame ()
  {
    log.info ("Starting the game...");
  }

  @StateMachineAction
  public void determinePlayerTurnOrder ()
  {
    log.info ("Determining player turn order...");

    final ImmutableSet <Player> players = playerModel.getPlayers ();
    List <PlayerTurnOrder> validTurnOrders = new ArrayList <> (Arrays.asList (PlayerTurnOrder.values ()));
    validTurnOrders.remove (PlayerTurnOrder.UNKNOWN);
    validTurnOrders = Randomness.shuffle (validTurnOrders);

    final Iterator <PlayerTurnOrder> turnOrderItr = validTurnOrders.iterator ();
    for (final Player player : players)
    {
      playerModel.changeTurnOrderOfPlayer (idOf (player), turnOrderItr.next ());
    }

    eventBus.publish (new DeterminePlayerTurnOrderCompleteEvent (playerModel.getPlayers ()));
  }

  @StateMachineAction
  public void distributeInitialArmies ()
  {
    log.info ("Distributing initial player armies...");

    for (final Player player : playerModel.getPlayers ())
    {
      final int armyCount = strategy.computeInitialArmyCount (playerModel.getPlayerCount ());
      playerModel.addArmiesToHandOf (idOf (player), ArmyFactory.create (armyCount));
    }

    eventBus.publish (new DistributeInitialArmiesCompleteEvent (playerModel.getPlayers ()));
  }

  @StateMachineAction
  public void endGame ()
  {
    log.info ("Game over.");

    // TODO Production: Remove
    eventBus.publish (new DestroyGameEvent ());
  }

  @StateMachineCondition
  public boolean isFull ()
  {
    return playerModel.isFull ();
  }

  public boolean isNotFull ()
  {
    return playerModel.isNotFull ();
  }

  public boolean isEmpty ()
  {
    return playerModel.isEmpty ();
  }

  public boolean isNotEmpty ()
  {
    return playerModel.isNotEmpty ();
  }

  public int getPlayerCount ()
  {
    return playerModel.getPlayerCount ();
  }

  public boolean playerCountIs (final int count)
  {
    return playerModel.playerCountIs (count);
  }

  public boolean playerCountIsNot (final int count)
  {
    return playerModel.playerCountIsNot (count);
  }

  public int getPlayerLimit ()
  {
    return playerModel.getPlayerLimit ();
  }

  public boolean playerLimitIs (final int limit)
  {
    return playerModel.playerLimitIs (limit);
  }

  public boolean playerLimitIsAtLeast (final int limit)
  {
    return playerModel.playerLimitIsAtLeast (limit);
  }
}
