package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.nameOf;
import static com.forerunnergames.peril.core.model.people.player.PlayerFluency.withIdOf;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.*;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;

import com.forerunnergames.peril.core.model.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.events.EndGameEvent;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineCondition;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerLimitDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerLimitRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerColorSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerLimitSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Result;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private final PlayerModel playerModel;
  private final MBassador <Event> eventBus;

  public GameModel (final PlayerModel playerModel, final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.eventBus = eventBus;
  }

  @StateMachineAction
  public void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final Player player = PlayerFactory.create (withPlayerNameFrom (event));
    final Result <PlayerJoinGameDeniedEvent.REASON> result;

    result = playerModel.requestToAdd (player);

    if (result.isSuccessful())
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

    final int oldLimit = playerModel.getPlayerLimit();

    final Result <ChangePlayerLimitDeniedEvent.REASON> result;
    result = playerModel.requestToChangePlayerLimitBy (deltaFrom (event));

    final int newLimit = playerModel.getPlayerLimit();

    if (result.isSuccessful())
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

    if (result.isSuccessful())
    {
      eventBus.publish (new ChangePlayerColorSuccessEvent (event));
    }
    else
    {
      eventBus.publish (new ChangePlayerColorDeniedEvent (event, failureReasonFrom (result)));
    }
  }

  @StateMachineAction
  public void waitForGameToBegin()
  {
    log.info ("Waiting for game to begin...");
  }

  @StateMachineAction
  public void beginGame()
  {
    log.info ("Starting the game...");
  }

  @StateMachineAction
  public void determinePlayerTurnOrder()
  {
    log.info ("Determining player turn order...");

    // TODO Production: Remove
    eventBus.publish (new EndGameEvent());
  }

  @StateMachineAction
  public void endGame()
  {
    log.info ("Game over.");

    // TODO Production: Remove
    eventBus.publish (new DestroyGameEvent());
  }

  @StateMachineCondition
  public boolean isGameFull()
  {
    return playerModel.isFull();
  }
}
