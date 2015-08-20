package com.forerunnergames.peril.core.model;

import static com.forerunnergames.peril.core.shared.net.events.EventFluency.withPlayerNameFrom;
import static com.forerunnergames.tools.common.ResultFluency.failureReasonFrom;
import static com.forerunnergames.tools.common.assets.AssetFluency.idOf;
import static com.forerunnergames.tools.common.assets.AssetFluency.nameOf;

import com.forerunnergames.peril.core.model.map.DefaultPlayMapModel;
import com.forerunnergames.peril.core.model.map.PlayMapModel;
import com.forerunnergames.peril.core.model.map.continent.Continent;
import com.forerunnergames.peril.core.model.map.country.Country;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.Player;
import com.forerunnergames.peril.core.model.people.player.PlayerFactory;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;
import com.forerunnergames.peril.core.model.rules.GameRules;
import com.forerunnergames.peril.core.model.state.events.BeginManualCountrySelectionEvent;
import com.forerunnergames.peril.core.model.state.events.RandomlyAssignPlayerCountriesEvent;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.core.shared.eventbus.EventBusFactory;
import com.forerunnergames.peril.core.shared.net.events.client.request.PlayerJoinGameRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.client.request.response.PlayerSelectCountryResponseRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.denied.PlayerSelectCountryResponseDeniedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.factories.StatusMessageEventFactory;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DeterminePlayerTurnOrderCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.DistributeInitialArmiesCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerArmiesChangedEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.core.shared.net.events.server.notification.PlayerLeaveGameEvent;
import com.forerunnergames.peril.core.shared.net.events.server.request.PlayerSelectCountryRequestEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.core.shared.net.events.server.success.PlayerSelectCountryResponseSuccessEvent;
import com.forerunnergames.peril.core.shared.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.shared.net.packets.territory.CountryPacket;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.LetterCase;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.Result;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.common.id.Id;

import com.google.common.collect.ImmutableList;
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
  private final PlayerTurnModel playerTurnModel;
  private final GameRules rules;
  private final MBassador <Event> eventBus;

  public GameModel (final PlayerModel playerModel,
                    final PlayMapModel playMapModel,
                    final PlayerTurnModel playerTurnModel,
                    final GameRules rules,
                    final MBassador <Event> eventBus)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");
    Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");
    Arguments.checkIsNotNull (rules, "rules");
    Arguments.checkIsNotNull (eventBus, "eventBus");

    this.playerModel = playerModel;
    this.playMapModel = playMapModel;
    this.playerTurnModel = playerTurnModel;
    this.rules = rules;
    this.eventBus = eventBus;
  }

  void determinePlayerTurnOrder ()
  {
    log.info ("Determining player turn order randomly...");

    eventBus.publish (StatusMessageEventFactory.create ("Determining player turn order randomly...",
                                                        playerModel.getPlayers ()));

    final ImmutableSet <Player> players = playerModel.getPlayers ();
    final List <Player> shuffledPlayers = Randomness.shuffle (players);
    final Iterator <Player> randomPlayerItr = shuffledPlayers.iterator ();

    for (final PlayerTurnOrder turnOrder : PlayerTurnOrder.validSortedValues ())
    {
      if (!randomPlayerItr.hasNext ()) break;

      final Player player = randomPlayerItr.next ();
      playerModel.changeTurnOrderOfPlayer (player.getId (), turnOrder);

      log.info ("Set turn order of player [{}] to [{}].", player.getName (), turnOrder);
    }

    eventBus.publish (new DeterminePlayerTurnOrderCompleteEvent (Packets.fromPlayers (playerModel.getPlayers ())));

    // Create a status message listing which player is going in which turn order.
    final StringBuilder statusMessageBuilder = new StringBuilder ();
    for (final Player player : playerModel.getTurnOrderedPlayers ())
    {
      // @formatter:off
      statusMessageBuilder
              .append (player.getName ())
              .append (" is going ")
              .append (Strings.toMixedOrdinal (player.getTurnOrderPosition ()))
              .append (".\n");
      // @formatter:on
    }
    if (statusMessageBuilder.length () > 0) statusMessageBuilder.deleteCharAt (statusMessageBuilder.length () - 1);

    eventBus.publish (StatusMessageEventFactory.create (statusMessageBuilder.toString (), playerModel.getPlayers ()));
  }

  void distributeInitialArmies ()
  {
    final int armies = rules.getInitialArmies ();

    log.info ("Distributing {} armies each to {} players...", armies, playerModel.getPlayerCount ());

    eventBus.publish (StatusMessageEventFactory.create ("Distributing " + armies + " armies to each player...",
                                                        playerModel.getPlayers ()));

    // Create a status message listing which player received how many armies.
    final StringBuilder statusMessageBuilder = new StringBuilder ();
    for (final Player player : playerModel.getTurnOrderedPlayers ())
    {
      playerModel.addArmiesToHandOf (player.getId (), armies);

      eventBus.publish (new PlayerArmiesChangedEvent (player.getName (), armies));

      // @formatter:off
      statusMessageBuilder
              .append (player.getName ())
              .append (" received ")
              .append (armies)
              .append (" armies.\n");
      // @formatter:on
    }
    if (statusMessageBuilder.length () > 0) statusMessageBuilder.deleteCharAt (statusMessageBuilder.length () - 1);

    eventBus.publish (new DistributeInitialArmiesCompleteEvent (Packets.fromPlayers (playerModel.getPlayers ())));
    eventBus.publish (StatusMessageEventFactory.create (statusMessageBuilder.toString (), playerModel.getPlayers ()));
  }

  void waitForCountrySelectionToBegin ()
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

  void randomlyAssignPlayerCountries ()
  {
    // if there are no players, just give up now!
    if (playerModel.isEmpty ())
    {
      log.info ("Skipping random country assignment... no players!");
      return;
    }

    eventBus.publish (StatusMessageEventFactory.create ("Randomly assigning all countries to players...",
                                                        playerModel.getPlayers ()));

    final List <Country> countries = Randomness.shuffle (new HashSet <> (playMapModel.getCountries ()));
    final List <Player> players = Randomness.shuffle (playerModel.getPlayers ());
    final ImmutableList <Integer> playerCountryDistribution = rules
            .getInitialPlayerCountryDistribution (players.size ());

    log.info ("Randomly assigning {} countries to {} players...", countries.size (), players.size ());

    final Iterator <Country> countryItr = countries.iterator ();
    for (int i = 0; i < players.size (); ++i)
    {
      final Player nextPlayer = players.get (i);
      final int playerCountryCount = playerCountryDistribution.get (i);

      int assignSuccessCount = 0; // for logging purposes
      for (int count = 0; count < playerCountryCount && countryItr.hasNext (); count++)
      {
        final Country toAssign = countryItr.next ();
        final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
        result = playMapModel.requestToAssignCountryOwner (idOf (toAssign), idOf (nextPlayer));
        if (result.failed ())
        {
          log.warn ("Failed to assign country [{}] to [{}] | Reason: {}", toAssign.getName (), nextPlayer.getName (),
                    failureReasonFrom (result));
        }
        else
        {
          playerModel.removeArmiesFromHandOf (nextPlayer.getId (), 1);
          assignSuccessCount++;
        }
        countryItr.remove ();
      }

      log.info ("Assigned {} countries to [{}].", assignSuccessCount, nextPlayer.getName ());
      eventBus.publish (new PlayerArmiesChangedEvent (nextPlayer.getName (), -1 * assignSuccessCount));
    }

    // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
    final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
    playMapViewPackets = Packets.fromPlayMap (buildPlayMapViewFrom (playerModel, playMapModel));

    // Create a status message listing which player has which countries.
    final StringBuilder statusMessageBuilder = new StringBuilder ();
    for (final Player player : playerModel.getTurnOrderedPlayers ())
    {
      // @formatter:off
      statusMessageBuilder
              .append (player.getName ())
              .append (":\n")
              .append (Strings.toStringList (playMapModel.getCountryNamesOwnedBy (player.getId ()), "\n", LetterCase.PROPER, false))
              .append ("\n\n");
      // @formatter:on
    }
    if (statusMessageBuilder.length () > 1)
      statusMessageBuilder.delete (statusMessageBuilder.length () - 2, statusMessageBuilder.length ());

    eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));
    eventBus.publish (StatusMessageEventFactory.create (statusMessageBuilder.toString (), playerModel.getPlayers ()));
  }

  void beginRound ()
  {
    log.info ("Let the round begin.");

    // TODO
  }

  void handlePlayerJoinGameRequest (final PlayerJoinGameRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    final Player player = PlayerFactory.create (withPlayerNameFrom (event));
    final Result <PlayerJoinGameDeniedEvent.Reason> result;

    result = playerModel.requestToAdd (player);

    if (result.failed ())
    {
      eventBus.publish (new PlayerJoinGameDeniedEvent (nameOf (player), failureReasonFrom (result)));
      return;
    }

    final PlayerPacket playerPacket = Packets.from (player);

    eventBus.publish (new PlayerJoinGameSuccessEvent (playerPacket));
    eventBus.publish (StatusMessageEventFactory.create (player.getName () + " joined the game.",
                                                        playerModel.getAllPlayersExcept (player)));
    eventBus.publish (StatusMessageEventFactory.create ("Welcome, " + player.getName () + ".", player));

    if (isFull ())
    {
      return;
    }

    // We aren't full yet, let's do some talking...

    eventBus.publish (StatusMessageEventFactory
            .create ("This is a " + rules.getPlayerLimit () + " player classic game. You must conquer "
                    + rules.getWinPercentage () + "% of the map to achieve victory.", player));

    if (playerCountIs (1))
    {
      eventBus.publish (StatusMessageEventFactory.create ("It looks like you're the first one here.", player));
    }

    final int nMorePlayers = getAdditionalPlayerCountToBeFull ();

    eventBus.publish (StatusMessageEventFactory.create (
                                                        "The game will begin when "
                                                                + Strings.pluralizeS (nMorePlayers, "more player")
                                                                + Strings.pluralizeWord (nMorePlayers, " joins.",
                                                                                         " join."),
                                                        playerModel.getPlayers ()));
  }

  void handlePlayerLeaveGame (final PlayerLeaveGameEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.trace ("Event received [{}].", event);

    // if the player is somehow still in the game, log a warning and return;
    // this might indicate a bug in one of the event handlers
    if (playerModel.existsPlayerWith (event.getPlayerName ()))
    {
      log.warn ("Received [{}], but player [{}] still exists.", event, event.getPlayer ());
      return;
    }

    eventBus.publish (StatusMessageEventFactory.create (event.getPlayerName () + " left the game.",
                                                        playerModel.getPlayers ()));
  }

  void waitForPlayersToSelectInitialCountries ()
  {
    final Player currentPlayer = getCurrentPlayer ();

    if (playMapModel.allCountriesAreOwned ())
    {
      // create map of country -> player packets for PlayerCountryAssignmentCompleteEvent
      final ImmutableMap <CountryPacket, PlayerPacket> playMapViewPackets;
      playMapViewPackets = Packets.fromPlayMap (buildPlayMapViewFrom (playerModel, playMapModel));
      eventBus.publish (new PlayerCountryAssignmentCompleteEvent (playMapViewPackets));

      // Create a status message listing which player has which countries.
      final StringBuilder statusMessageBuilder = new StringBuilder ();
      for (final Player player : playerModel.getTurnOrderedPlayers ())
      {
        // @formatter:off
        statusMessageBuilder
                .append (player.getName ())
                .append (":\n")
                .append (Strings.toStringList (playMapModel.getCountryNamesOwnedBy (player.getId ()), "\n", LetterCase.PROPER, false))
                .append ("\n\n");
        // @formatter:on
      }
      if (statusMessageBuilder.length () > 0) statusMessageBuilder.deleteCharAt (statusMessageBuilder.length () - 1);

      eventBus.publish (StatusMessageEventFactory.create (statusMessageBuilder.toString (), playerModel.getPlayers ()));

      return;
    }

    log.info ("Waiting for player [{}] to select a country...", currentPlayer.getName ());
    eventBus.publish (new PlayerSelectCountryRequestEvent (Packets.from (currentPlayer)));
    eventBus.publish (StatusMessageEventFactory
            .create ("Waiting for " + currentPlayer.getName () + " to select a country...", playerModel.getPlayers ()));
  }

  boolean verifyPlayerCountrySelectionRequest (final PlayerSelectCountryResponseRequestEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}]", event);

    final Player currentPlayer = getCurrentPlayer ();

    final String selectedCountryName = event.getSelectedCountryName ();

    if (!playMapModel.existsCountryWith (selectedCountryName))
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (selectedCountryName,
              PlayerSelectCountryResponseDeniedEvent.Reason.COUNTRY_DOES_NOT_EXIST));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (Packets.from (currentPlayer)));
      return false;
    }

    final Result <PlayerSelectCountryResponseDeniedEvent.Reason> result;
    result = playMapModel.requestToAssignCountryOwner (idOf (playMapModel.countryWith (selectedCountryName)),
                                                       idOf (currentPlayer));
    if (result.failed ())
    {
      eventBus.publish (new PlayerSelectCountryResponseDeniedEvent (selectedCountryName, failureReasonFrom (result)));
      // send a new request
      eventBus.publish (new PlayerSelectCountryRequestEvent (Packets.from (currentPlayer)));
      return false;
    }

    eventBus.publish (new PlayerSelectCountryResponseSuccessEvent (selectedCountryName, Packets.from (currentPlayer)));
    eventBus.publish (new PlayerArmiesChangedEvent (currentPlayer.getName (), -1));
    eventBus.publish (StatusMessageEventFactory
            .create (currentPlayer.getName () + " chose " + selectedCountryName + ".", playerModel.getPlayers ()));

    playerTurnModel.advance ();

    return true;
  }

  PlayerModel getPlayerModel ()
  {
    return playerModel;
  }

  PlayMapModel getPlayMapModel ()
  {
    return playMapModel;
  }

  PlayerTurnModel getPlayerTurnModel ()
  {
    return playerTurnModel;
  }

  GameRules getRules ()
  {
    return rules;
  }

  MBassador <Event> getEventBus ()
  {
    return eventBus;
  }

  boolean isFull ()
  {
    return playerModel.isFull ();
  }

  boolean isNotFull ()
  {
    return playerModel.isNotFull ();
  }

  boolean isEmpty ()
  {
    return playerModel.isEmpty ();
  }

  boolean playerCountIs (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIs (count);
  }

  boolean playerCountIsNot (final int count)
  {
    Arguments.checkIsNotNegative (count, "count");

    return playerModel.playerCountIsNot (count);
  }

  boolean playerLimitIs (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIs (limit);
  }

  int getAdditionalPlayerCountToBeFull ()
  {
    return getPlayerLimit () - getPlayerCount ();
  }

  int getPlayerCount ()
  {
    return playerModel.getPlayerCount ();
  }

  int getPlayerLimit ()
  {
    return playerModel.getPlayerLimit ();
  }

  PlayerTurnOrder getTurn ()
  {
    return playerTurnModel.getTurnOrder ();
  }

  boolean playerLimitIsAtLeast (final int limit)
  {
    Arguments.checkIsNotNegative (limit, "limit");

    return playerModel.playerLimitIsAtLeast (limit);
  }

  private Player getCurrentPlayer ()
  {
    return playerModel.playerWith (playerTurnModel.getTurnOrder ());
  }

  private ImmutableMap <Country, Player> buildPlayMapViewFrom (final PlayerModel playerModel,
                                                               final PlayMapModel playMapModel)
  {
    Arguments.checkIsNotNull (playerModel, "playerModel");
    Arguments.checkIsNotNull (playMapModel, "playMapModel");

    final ImmutableSet <Country> countries = playMapModel.getCountries ();
    final ImmutableMap.Builder <Country, Player> playMapView = ImmutableMap.builder ();
    for (final Country country : countries)
    {
      if (!playMapModel.isCountryOwned (idOf (country))) continue;

      final Id ownerId = playMapModel.ownerOf (idOf (country));
      playMapView.put (country, playerModel.playerWith (ownerId));
    }
    return playMapView.build ();
  }

  public static Builder builder (final GameRules rules)
  {
    return new Builder (rules);
  }

  public static class Builder
  {
    private final GameRules gameRules;
    private PlayerModel playerModel;
    private PlayMapModel playMapModel;
    private PlayerTurnModel playerTurnModel;
    private MBassador <Event> eventBus = EventBusFactory.create ();

    private Builder (final GameRules gameRules)
    {
      Arguments.checkIsNotNull (gameRules, "gameRules");

      this.gameRules = gameRules;
      playerModel = new DefaultPlayerModel (gameRules);
      playMapModel = new DefaultPlayMapModel (DefaultPlayMapModel.generateDefaultCountries (gameRules),
              ImmutableSet.<Continent> of (), gameRules);
      playerTurnModel = new DefaultPlayerTurnModel (gameRules.getPlayerLimit ());
    }

    public GameModel build ()
    {
      return new GameModel (playerModel, playMapModel, playerTurnModel, gameRules, eventBus);
    }

    public Builder playerModel (final PlayerModel playerModel)
    {
      Arguments.checkIsNotNull (playerModel, "playerModel");

      this.playerModel = playerModel;
      return this;
    }

    public Builder playMapModel (final PlayMapModel playMapModel)
    {
      Arguments.checkIsNotNull (playMapModel, "playMapModel");

      this.playMapModel = playMapModel;
      return this;
    }

    public Builder playerTurnModel (final PlayerTurnModel playerTurnModel)
    {
      Arguments.checkIsNotNull (playerTurnModel, "playerTurnModel");

      this.playerTurnModel = playerTurnModel;
      return this;
    }

    public Builder eventBus (final MBassador <Event> eventBus)
    {
      Arguments.checkIsNotNull (eventBus, "eventBus");

      this.eventBus = eventBus;
      return this;
    }
  }
}
