/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.server.application;

import com.forerunnergames.peril.ai.application.AiApplication;
import com.forerunnergames.peril.ai.net.AiClientCommunicator;
import com.forerunnergames.peril.common.eventbus.EventBusFactory;
import com.forerunnergames.peril.common.game.DefaultGameConfiguration;
import com.forerunnergames.peril.common.game.GameConfiguration;
import com.forerunnergames.peril.common.game.PersonLimits;
import com.forerunnergames.peril.common.game.rules.GameRules;
import com.forerunnergames.peril.common.game.rules.GameRulesFactory;
import com.forerunnergames.peril.common.net.DefaultGameServerConfiguration;
import com.forerunnergames.peril.common.net.GameServerConfiguration;
import com.forerunnergames.peril.common.net.kryonet.KryonetRegistration;
import com.forerunnergames.peril.common.playmap.PlayMapMetadata;
import com.forerunnergames.peril.common.playmap.io.PlayMapMetadataFinder;
import com.forerunnergames.peril.common.settings.NetworkSettings;
import com.forerunnergames.peril.core.model.GameModel;
import com.forerunnergames.peril.core.model.battle.BattleModel;
import com.forerunnergames.peril.core.model.battle.DefaultBattleModel;
import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardModel;
import com.forerunnergames.peril.core.model.card.DefaultCardModel;
import com.forerunnergames.peril.core.model.card.io.CardModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.people.player.DefaultPlayerModel;
import com.forerunnergames.peril.core.model.people.player.PlayerModel;
import com.forerunnergames.peril.core.model.playmap.DefaultPlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.PlayMapModel;
import com.forerunnergames.peril.core.model.playmap.PlayMapModelFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentFactory;
import com.forerunnergames.peril.core.model.playmap.continent.ContinentGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.CountryFactory;
import com.forerunnergames.peril.core.model.playmap.country.CountryGraphModel;
import com.forerunnergames.peril.core.model.playmap.country.DefaultCountryIdResolver;
import com.forerunnergames.peril.core.model.playmap.io.CorePlayMapMetadataFinderFactory;
import com.forerunnergames.peril.core.model.playmap.io.PlayMapModelDataFactory;
import com.forerunnergames.peril.core.model.playmap.io.PlayMapModelDataFactoryCreator;
import com.forerunnergames.peril.core.model.state.StateMachineEventHandler;
import com.forerunnergames.peril.core.model.turn.DefaultPlayerTurnModel;
import com.forerunnergames.peril.core.model.turn.PlayerTurnModel;
import com.forerunnergames.peril.server.communicators.AiPlayerCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultCoreCommunicator;
import com.forerunnergames.peril.server.communicators.DefaultSpectatorCommunicator;
import com.forerunnergames.peril.server.communicators.HumanPlayerCommunicator;
import com.forerunnergames.peril.server.controllers.EventBasedServerController;
import com.forerunnergames.peril.server.controllers.MultiplayerController;
import com.forerunnergames.peril.server.kryonet.KryonetServer;
import com.forerunnergames.peril.server.main.args.CommandLineArgs;
import com.forerunnergames.tools.common.Application;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.controllers.Controller;
import com.forerunnergames.tools.net.DefaultExternalAddressResolver;
import com.forerunnergames.tools.net.ExternalAddressResolver;
import com.forerunnergames.tools.net.server.Server;
import com.forerunnergames.tools.net.server.ServerController;
import com.forerunnergames.tools.net.server.configuration.DefaultServerConfiguration;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import com.google.common.collect.ImmutableSet;

import de.matthiasmann.AsyncExecution;

import net.engio.mbassy.bus.MBassador;

public final class ServerApplicationFactory
{
  public static Application create (final CommandLineArgs args)
  {
    Arguments.checkIsNotNull (args, "args");

    final MBassador <Event> serverEventBus = EventBusFactory.create ();

    final Server server = new KryonetServer ();

    final AsyncExecution mainThreadExecutor = new AsyncExecution ();

    final ServerController serverController = new EventBasedServerController (server, args.serverTcpPort,
            KryonetRegistration.CLASSES, serverEventBus, mainThreadExecutor);

    final PlayMapMetadataFinder playMapMetadataFinder = CorePlayMapMetadataFinderFactory.create (args.gameMode);
    final PlayMapMetadata playMapMetadata = playMapMetadataFinder.find (args.playMapName);

    final PlayMapModelDataFactory dataFactory = PlayMapModelDataFactoryCreator.create (args.gameMode);

    final CountryFactory countryFactory = dataFactory.createCountries (playMapMetadata);

    final ContinentFactory continentFactory = dataFactory.createContinents (playMapMetadata,
                                                                            new DefaultCountryIdResolver (
                                                                                    countryFactory));

    final CountryGraphModel countryGraphModel = dataFactory.createCountryGraphModel (playMapMetadata, countryFactory);

    final ContinentGraphModel continentGraphModel = dataFactory.createContinentGraphModel (playMapMetadata,
                                                                                           continentFactory,
                                                                                           countryGraphModel);

    final ImmutableSet <Card> cards = CardModelDataFactoryCreator.create (args.gameMode).createCards (playMapMetadata);

    final PersonLimits personLimits = PersonLimits.builder ().humanPlayers (args.humanPlayerLimit)
            .aiPlayers (args.aiPlayerLimit).spectators (args.spectatorLimit).build ();

    final GameRules gameRules = GameRulesFactory.create (args.gameMode, personLimits.getPlayerLimit (),
                                                         args.winPercentage, countryFactory.getCountryCount (),
                                                         args.initialCountryAssignment);

    final PlayMapModelFactory playMapModelFactory = new DefaultPlayMapModelFactory (gameRules);

    final PlayerModel playerModel = new DefaultPlayerModel (gameRules);
    final PlayMapModel playMapModel = playMapModelFactory.create (countryGraphModel, continentGraphModel);
    final CardModel cardModel = new DefaultCardModel (gameRules, playerModel, cards);
    final BattleModel battleModel = new DefaultBattleModel (playMapModel);
    final PlayerTurnModel playerTurnModel = new DefaultPlayerTurnModel (gameRules);
    final GameModel gameModel = GameModel.builder (gameRules).playMapModel (playMapModel).playerModel (playerModel)
            .cardModel (cardModel).battleModel (battleModel).playerTurnModel (playerTurnModel)
            .eventBus (serverEventBus).build ();
    final StateMachineEventHandler gameStateMachine = new StateMachineEventHandler (gameModel);

    final GameConfiguration gameConfig = new DefaultGameConfiguration (args.gameMode, personLimits, args.winPercentage,
            args.initialCountryAssignment, playMapMetadata, gameRules);

    final ExternalAddressResolver externalAddressResolver = new DefaultExternalAddressResolver (
            NetworkSettings.EXTERNAL_IP_RESOLVER_URL);

    final ServerConfiguration serverConfig = new DefaultServerConfiguration (externalAddressResolver.resolveIp (),
            args.serverTcpPort);

    final GameServerConfiguration gameServerConfig = new DefaultGameServerConfiguration (args.gameServerName,
            args.gameServerType, gameConfig, serverConfig);

    final MBassador <Event> aiEventBus = EventBusFactory.create ();

    final Controller multiplayerController = new MultiplayerController (gameServerConfig, serverController,
            new HumanPlayerCommunicator (serverController), new AiPlayerCommunicator (new AiClientCommunicator (
                    aiEventBus)), new DefaultSpectatorCommunicator (serverController), new DefaultCoreCommunicator (
                    serverEventBus), serverEventBus);

    final AiApplication aiApplication = new AiApplication (gameServerConfig, serverEventBus, aiEventBus);

    return new ServerApplication (gameStateMachine, serverEventBus, mainThreadExecutor, serverController,
            aiApplication, multiplayerController);
  }

  private ServerApplicationFactory ()
  {
    Classes.instantiationNotAllowed ();
  }
}
