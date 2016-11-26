package com.forerunnergames.peril.core.model.game.phase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Mockito.mock;

import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.eventbus.EventBusHandler;
import com.forerunnergames.peril.common.game.rules.ClassicGameRules;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.net.events.client.request.HumanPlayerJoinGameRequestEvent;
import com.forerunnergames.peril.common.net.events.server.denied.PlayerJoinGameDeniedEvent;
import com.forerunnergames.peril.common.net.events.server.interfaces.PlayerInputRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.PlayerCountryAssignmentCompleteEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerJoinGameSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.core.events.internal.player.DefaultInboundPlayerResponseRequestEvent;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.CardModelTest;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.game.DefaultGamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.GameModel;
import com.forerunnergames.peril.core.model.game.GameModelConfiguration;
import com.forerunnergames.peril.core.model.game.GamePhaseEventFactory;
import com.forerunnergames.peril.core.model.game.InternalCommunicationHandler;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModelTest;
import com.forerunnergames.peril.core.model.playmap.country.CountryArmyModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModelTest;
import com.forerunnergames.peril.core.model.playmap.country.CountryOwnerModel;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Randomness;
import com.forerunnergames.tools.common.graph.DefaultGraphModel;
import com.forerunnergames.tools.common.graph.GraphModel;
import com.forerunnergames.tools.common.id.Id;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.DeniedEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.engio.mbassy.bus.MBassador;

import org.junit.Before;

import org.apache.commons.lang3.ArrayUtils;

public abstract class AbstractGamePhaseHandlerTest
{
  protected final int defaultTestCountryCount = 30;
  protected final ImmutableList <String> defaultTestCountries = generateTestCountryNames (defaultTestCountryCount);
  protected MBassador <Event> eventBus;
  protected EventBusHandler eventHandler;
  protected int playerLimit;
  protected int initialArmies;
  protected int maxPlayers;
  protected GameModelConfiguration gameModelConfig;
  protected GameModel gameModel;
  protected PlayerModel playerModel;
  protected PlayerTurnModel playerTurnModel;
  protected PlayMapModel playMapModel;
  protected CountryOwnerModel countryOwnerModel;
  protected CountryArmyModel countryArmyModel;
  protected CountryGraphModel countryGraphModel;
  protected BattleModel battleModel;
  protected CardModel cardModel;
  protected InternalCommunicationHandler mockCommHandler;
  protected GamePhaseEventFactory eventFactory;
  protected ImmutableSet <Card> cardDeck = CardModelTest.generateTestCards ();
  protected GameRules gameRules;

  public static CountryGraphModel createDefaultTestCountryGraph (final ImmutableList <String> countryNames)
  {
    final DefaultGraphModel.Builder <String> countryNameGraphBuilder = DefaultGraphModel.builder ();
    // set every node adjacent to country 0
    for (int i = 1; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (0), countryNames.get (i));
    }
    // set each country 1-4 adjacent to its sequential neighbors
    for (int i = 2; i < countryNames.size (); i++)
    {
      countryNameGraphBuilder.setAdjacent (countryNames.get (i - 1), countryNames.get (i));
    }
    // complete the cycle by setting country 1 adjacent to last country
    countryNameGraphBuilder.setAdjacent (countryNames.get (countryNames.size () - 1), countryNames.get (1));
    final GraphModel <String> countryNameGraph = countryNameGraphBuilder.build ();
    return CountryGraphModelTest.createCountryGraphModelFrom (countryNameGraph);
  }

  @Before
  public void setup ()
  {
    eventBus = EventBusFactory.create (ImmutableSet.of (EventBusHandler.createEventBusFailureHandler ()));
    eventHandler = new EventBusHandler ();
    eventHandler.subscribe (eventBus);
    // crate default play map + game model
    gameRules = ClassicGameRules.builder ().maxHumanPlayers ().totalCountryCount (defaultTestCountryCount).build ();
    playMapModel = createPlayMapModelWithDisjointMapGraph (generateTestCountryNames (defaultTestCountryCount));
    mockCommHandler = mock (InternalCommunicationHandler.class);
    initializeGameModelWith (playMapModel);
    assert gameModel != null;

    eventFactory = new DefaultGamePhaseEventFactory (playerModel, playMapModel, cardModel, gameRules);

    setupTest ();
  }

  protected abstract void setupTest ();

  protected void advancePlayerTurn ()
  {
    playerTurnModel.advance ();
  }

  protected void verifyPlayerCountryAssignmentCompleteEvent ()
  {
    for (final Id country : countryGraphModel.getCountryIds ())
    {
      assertTrue (countryOwnerModel.isCountryOwned (country));
      final PlayerCountryAssignmentCompleteEvent event = eventHandler
              .lastEventOfType (PlayerCountryAssignmentCompleteEvent.class);
      final CountryPacket countryPacket = countryGraphModel.countryPacketWith (country);
      final Id player = countryOwnerModel.ownerOf (country);
      assertEquals (playerModel.playerPacketWith (player), event.getOwner (countryPacket));
    }
  }

  protected void addMaxPlayers ()
  {
    assertTrue (gameModel.playerLimitIs (maxPlayers));

    for (int i = 1; i <= playerLimit; i++)
    {
      gameModel.handlePlayerJoinGameRequest (new HumanPlayerJoinGameRequestEvent ("TestPlayer" + i));
    }

    assertTrue (gameModel.isFull ());
  }

  protected void addSinglePlayer ()
  {
    assertTrue (gameModel.isEmpty ());
    assertTrue (gameModel.isNotFull ());

    gameModel.handlePlayerJoinGameRequest (new HumanPlayerJoinGameRequestEvent ("TestPlayer"));

    assertTrue (gameModel.playerCountIs (1));
    assertTrue (eventHandler.wasFiredExactlyOnce (PlayerJoinGameSuccessEvent.class));
  }

  protected void assertLastEventWasNotDeniedEvent ()
  {
    if (eventHandler.lastEventWasType (DeniedEvent.class))
    {
      final DeniedEvent <?> event = eventHandler.lastEvent (DeniedEvent.class);
      fail (event.getReason ().toString ());
    }
  }

  /**
   * Publish the internal response request event so that it gets registered with InternalCommunicationHandler.
   */
  protected <T extends ResponseRequestEvent> void publishInternalResponseRequestEvent (final T event)
  {
    // this is so nasty... but it works for some reason O_o
    try
    {
      final Class <? extends ServerRequestEvent> requestType = event.getRequestType ();
      final Constructor <? extends ServerRequestEvent> ctor = requestType.getDeclaredConstructor ();
      ctor.setAccessible (true);
      eventBus.publish (new DefaultInboundPlayerResponseRequestEvent <> (mock (PlayerPacket.class), event,
              (PlayerInputRequestEvent) ctor.newInstance ()));
    }
    catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException
            | IllegalArgumentException | InvocationTargetException e)
    {
      fail (e.toString ());
    }
  }

  protected Id randomCountry ()
  {
    return Randomness.getRandomElementFrom (countryGraphModel.getCountryIds ());
  }

  protected ImmutableList <Id> countryIdsFor (final ImmutableList <String> countryNames,
                                              final ImmutableList <Integer> indices)
  {
    final ImmutableList.Builder <Id> countryIds = ImmutableList.builder ();
    for (final int i : indices)
    {
      countryIds.add (countryGraphModel.countryWith (countryNames.get (i)));
    }
    return countryIds.build ();
  }

  protected CountryAdjacencyIndices adj (final int... adjArr)
  {
    assert adjArr != null;
    assert adjArr.length >= 1;

    return new CountryAdjacencyIndices (adjArr [0], ArrayUtils.subarray (adjArr, 1, adjArr.length));
  }

  protected ImmutableMultimap <CountryPacket, CountryPacket> buildCountryMultimapFromIndices (final ImmutableList <String> countryNameList,
                                                                                              final CountryAdjacencyIndices... adjacencyIndices)
  {
    assert countryNameList != null;
    assert adjacencyIndices != null;

    final ImmutableMultimap.Builder <CountryPacket, CountryPacket> expectedFortifyVectors = ImmutableSetMultimap
            .builder ();
    for (final CountryAdjacencyIndices adjInd : adjacencyIndices)
    {
      final CountryPacket cp0 = countryGraphModel.countryPacketWith (countryNameList.get (adjInd.c0));
      for (final int adj : adjInd.adj)
      {
        final CountryPacket cpAdj = countryGraphModel.countryPacketWith (countryNameList.get (adj));
        expectedFortifyVectors.put (cp0, cpAdj);
      }
    }
    return expectedFortifyVectors.build ();
  }

  protected void initializeGameModelWith (final PlayMapModel playMapModel)
  {
    gameRules = playMapModel.getRules ();
    playerModel = new DefaultPlayerModel (gameRules);
    playerTurnModel = new DefaultPlayerTurnModel (gameRules);
    cardModel = new DefaultCardModel (gameRules, playerModel, cardDeck);
    battleModel = new DefaultBattleModel (playMapModel);
    countryGraphModel = playMapModel.getCountryGraphModel ();
    countryOwnerModel = playMapModel.getCountryOwnerModel ();
    countryArmyModel = playMapModel.getCountryArmyModel ();
    this.playMapModel = playMapModel;

    initialArmies = gameRules.getInitialArmies ();
    playerLimit = playerModel.getPlayerLimit ();
    maxPlayers = gameRules.getMaxTotalPlayers ();
    gameModelConfig = GameModelConfiguration.builder (gameRules).eventBus (eventBus).playMapModel (playMapModel)
            .battleModel (battleModel).playerModel (playerModel).playerTurnModel (playerTurnModel).cardModel (cardModel)
            .internalComms (mockCommHandler).build ();
    gameModel = GameModel.create (gameModelConfig);
  }

  protected PlayMapModel createPlayMapModelWithDisjointMapGraph (final ImmutableList <String> countryNames)
  {
    final CountryFactory factory = new CountryFactory ();
    for (final String name : countryNames)
    {
      factory.newCountryWith (name);
    }
    final CountryGraphModel countryGraphModel = CountryGraphModelTest.createDisjointCountryGraphModelWith (factory);

    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentGraphModel continentGraphModel = ContinentGraphModelTest
            .createContinentGraphModelWith (continentFactory, countryGraphModel);
    return new DefaultPlayMapModelFactory (gameRules).create (countryGraphModel, continentGraphModel);
  }

  protected PlayMapModel createPlayMapModelWithTestTerritoryGraphs (final ImmutableList <String> countryNames)
  {
    final CountryGraphModel countryGraphModel = createDefaultTestCountryGraph (countryNames);
    // create empty continent graph
    final ContinentFactory continentFactory = new ContinentFactory ();
    final ContinentGraphModel continentGraphModel = ContinentGraphModelTest
            .createContinentGraphModelWith (continentFactory, countryGraphModel);
    playMapModel = new DefaultPlayMapModelFactory (gameRules).create (countryGraphModel, continentGraphModel);
    return playMapModel;
  }

  protected static ImmutableList <String> generateTestCountryNames (final int totalCountryCount)
  {
    final ImmutableList.Builder <String> countryNames = ImmutableList.builder ();
    for (int i = 0; i < totalCountryCount; i++)
    {
      countryNames.add ("TestCountry-" + i);
    }
    return countryNames.build ();
  }

  protected static String playerNameFrom (final PlayerJoinGameDeniedEvent event)
  {
    return event.getPlayerName ();
  }

  protected static <T> T reasonFrom (final DeniedEvent <T> event)
  {
    return event.getReason ();
  }

  protected class CountryAdjacencyIndices
  {
    final int c0;
    final int[] adj;

    CountryAdjacencyIndices (final int c0, final int... adj)
    {
      this.c0 = c0;
      this.adj = adj;
    }
  }
}
