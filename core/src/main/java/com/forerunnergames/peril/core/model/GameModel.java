package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.colorFrom;
import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withPlayerNameFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.withIdOf;

import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineAction;
import com.forerunnergames.peril.core.model.state.annotations.StateMachineCondition;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountrySelectionEvent;
import com.forerunnergames.peril.core.model.state.events.DestroyGameEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.success.ChangePlayerColorSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameModel
{
  private static final Logger log = LoggerFactory.getLogger (GameModel.class);
  private final PlayerModel playerModel;
  private final PlayMapModel playMapModel;
  private final GameRules rules;
  private final MBassador <Event> eventBus;

  public GameModel (final PlayerModel playerModel,
                    final PlayMapModel playMapModel,
                    final GameRules rules,
                    final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.rules = rules;
    this.eventBus = eventBus;
  }

  // temporary debug constructor to prevent shit from breaking
  public GameModel (final PlayerModel playerModel, final GameRules rules, final MBassador <Event> eventBus)
  {
    this (playerModel, new PlayMapModel (ImmutableSet.<Country> of (), rules), rules, eventBus);
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
    final List <PlayerTurnOrder> randomTurnOrders = Randomness.shuffle (PlayerTurnOrder.validValues ());
    final Iterator <PlayerTurnOrder> randomTurnOrderItr = randomTurnOrders.iterator ();

    for (final Player player : players)
    {
      playerModel.changeTurnOrderOfPlayer (idOf (player), randomTurnOrderItr.next ());
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
  public void waitForCountrySelectionToBegin ()
  {
    switch (rules.getInitialCountryAssignment ())
    {
      case RANDOM:
      {
        log.info ("Initial country assignment = RANDOM");
        eventBus.publish (new RandomlyAssignPlayerCountriesEvent ());
        break;
      }
      case MANUAL:
      {
        log.info ("Initial country assignment = MANUAL");
        eventBus.publish (new BeginManualCountrySelectionEvent ());
        break;
      }
      default:
      {
        log.info ("Unrecognized value for InitialCountryAssignment.");
        break;
      }
    }
  }

  @StateMachineAction
  public void randomlyAssignPlayerCountries ()
  {
    log.info ("Randomly assigning player countries...");

    final Set <Country> countries = new HashSet <> (playMapModel.getCountries ());
    final ImmutableSet <Player> players = playerModel.getPlayers ();
    // TODO: this should be done more accordingly with GameRules or something
    final int countriesPerPlayer = countries.size () / players.size () + 1;
    for (final Player player : players)
    {
      int count = 0;
      final Iterator <Country> itr = countries.iterator ();
      while (itr.hasNext () && count < countriesPerPlayer)
      {
        final Country toAssign = itr.next ();
        playMapModel.assignCountryOwner (toAssign.getId (), player.getId ());
        itr.remove ();
        count++;
      }
    }

    // prepare immutable view of PlayMapModel for completion event
    final ImmutableSet <Country> assignedCountries = playMapModel.getAssignedCountries ();
    final ImmutableMap.Builder <Country, Player> playMapBuilder = ImmutableMap.builder ();
    for (final Country country : assignedCountries)
    {
      playMapBuilder.put (country, playerModel.playerWith (playMapModel.getOwnerOf (country.getId ())));
    }
    eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapBuilder.build ()));
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

    final Player player = playerModel.playerWith (colorFrom (event));

    final Result <ChangePlayerColorDeniedEvent.Reason> result;
    result = playerModel.requestToChangeColorOfPlayer (withIdOf (player), colorFrom (event));

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
    final Result <PlayerJoinGameDeniedEvent.Reason> result;

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
