package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.currentColorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.previousColorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withPlayerNameFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.withIdOf;

import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineCondition;
import com.forerunnergames.peril.core.model.state.events.status.DestroyGameEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerColorSuccessEvent;
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
  private final GameRules rules;
  private final MBassador <Event> eventBus;

  public GameModel (final PlayerModel playerModel, final GameRules rules, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.rules = rules;
    this.eventBus = eventBus;
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
    final int armies = rules.getInitialArmies ();

    log.info ("Distributing {} armies each to {} players...", armies, playerModel.getPlayerCount ());

    for (final Player player : playerModel.getPlayers ())
    {
      playerModel.addArmiesToHandOf (idOf (player), armies);
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

  public int getPlayerCount ()
  {
    return playerModel.getPlayerCount ();
  }

  public int getPlayerLimit ()
  {
    return playerModel.getPlayerLimit ();
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

  public boolean isEmpty ()
  {
    return playerModel.isEmpty ();
  }

  @StateMachineCondition
  public boolean isFull ()
  {
    return playerModel.isFull ();
  }

  public boolean isNotEmpty ()
  {
    return playerModel.isNotEmpty ();
  }

  public boolean isNotFull ()
  {
    return playerModel.isNotFull ();
  }

  public boolean playerCountIs (final int count)
  {
    return playerModel.playerCountIs (count);
  }

  public boolean playerCountIsNot (final int count)
  {
    return playerModel.playerCountIsNot (count);
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
