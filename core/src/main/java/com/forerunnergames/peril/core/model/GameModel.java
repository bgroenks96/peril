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
import com.forerunnergames.peril.core.shared.net.events.client.request.ChangePlayerColorRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.response.PlayerSelectCountryInputResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.ChangePlayerColorDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryInputResponseDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.request.PlayerSelectCountryInputRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.ChangePlayerColorSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerSelectCountryInputResponseSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.CountryPacket;
import com.forerunnergames.peril.core.shared.net.packets.PlayerPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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

  // TODO might be unnecessary; address later
  private PlayerTurnOrder playerTurn = PlayerTurnOrder.FIRST;

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

    eventBus.publish (new DeterminePlayerTurnOrderCompleteEvent (GamePackets.fromPlayers (playerModel.getPlayers ())));
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

    eventBus.publish (new DistributeInitialArmiesCompleteEvent (GamePackets.fromPlayers (playerModel.getPlayers ())));
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

    final List <Country> countries = Randomness.shuffle (new HashSet <> (playMapModel.getCountries ()));
    final ImmutableSet <Player> players = playerModel.getPlayers ();
    // TODO: army distribution should be handled by GameRules
    final Iterator <Country> itr = countries.iterator ();
    // first use floor value of [country count] / [player count]
    int countriesPerPlayer = countries.size () / players.size ();
    while (itr.hasNext ())
    {
      for (final Player player : players)
      {
        for (int count = 0; count < countriesPerPlayer; count++)
        {
          final Country toAssign = itr.next ();
          log.info ("Assigning country [" + toAssign.getCountryName ().asString () + "] to [" + player.getName ()
                  + "].");
          playMapModel.requestToAssignCountryOwner (idOf (toAssign), idOf (player));
          itr.remove ();
        }
      }
      // once each player has received the floor minimum, distribute 1 to each player until the
      // remaining countries are depleted
      countriesPerPlayer = 1;
    }

    // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
    final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
    playMapViewPackets = GamePackets.fromPlayMap (buildPlayMapViewFrom (playerModel, playMapModel));
    eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
  }

  @StateMachineAction
  public void waitForPlayersToSelectInitialCountries ()
  {
    final Player currentPlayer = playerModel.playerWith (playerTurn);
    if (playMapModel.hasUnassignedCountries ())
    {
      log.info ("Waiting for player [" + currentPlayer.getName () + "] to select a country...");
      playerTurn = playerTurn.hasNextValid () ? playerTurn.nextValid () : playerTurn.first ();
      eventBus.publish (new PlayerSelectCountryInputRequestEvent (GamePackets.fromPlayer (currentPlayer)));
    }
    else
    {
      // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = GamePackets.fromPlayMap (buildPlayMapViewFrom (playerModel, playMapModel));
      eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
    }
  }

  @StateMachineAction
  public void handlePlayerCountrySelectionRequest (final PlayerSelectCountryInputResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    final String selectedCountryName = event.getSelectedCountryName ();
    final Player currentPlayer = playerModel.playerWith (playerTurn);
    final Result <PlayerSelectCountryInputResponseDeniedEvent.Reason> result;
    result = playMapModel.requestToAssignCountryOwner (idOf (playMapModel.countryWith (selectedCountryName)),
                                                       idOf (currentPlayer));
    if (result.isSuccessful ())
    {
      eventBus.publish (new PlayerSelectCountryInputResponseSuccessEvent (selectedCountryName));
    }
    else
    {
      eventBus.publish (new PlayerSelectCountryInputResponseDeniedEvent (selectedCountryName,
              failureReasonFrom (result)));
    }
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

  private ImmutableMap <Country, Player> buildPlayMapViewFrom (final PlayerModel playerModel,
                                                               final PlayMapModel playMapModel)
  {
    final ImmutableSet <Country> countries = playMapModel.getCountries ();
    final ImmutableMap.Builder <Country, Player> playMapView = ImmutableMap.builder ();
    for (final Country country : countries)
    {
      if (playMapModel.isCountryAssigned (idOf (country)))
      {
        final Id ownerId = playMapModel.getOwnerOf (idOf (country));
        playMapView.put (country, playerModel.playerWith (ownerId));
      }
    }
    return playMapView.build ();
  }
}
