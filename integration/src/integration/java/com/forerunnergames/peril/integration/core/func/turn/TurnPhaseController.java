package com.forerunnergames.peril.integration.core.func.turn;

import com.forerunnergames.peril.common.net.events.client.request.PlayerReinforceCountryRequestEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.broadcast.EndReinforcementPhaseEvent;
import com.forerunnergames.peril.common.net.events.server.notify.direct.PlayerBeginCountryReinforcementEvent;
import com.forerunnergames.peril.common.net.events.server.success.PlayerReinforceCountrySuccessEvent;
import com.forerunnergames.peril.common.net.packets.territory.CountryPacket;
import com.forerunnergames.peril.integration.TestMonitor;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.TestPhaseController;
import com.forerunnergames.peril.integration.core.func.WaitForCommunicationActionResult;
import com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseController;
import com.forerunnergames.peril.integration.server.ClientEventProcessor;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Randomness;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TurnPhaseController implements TestPhaseController
{
  private static final Logger log = LoggerFactory.getLogger (TurnPhaseController.class);
  private final DedicatedGameSession session;
  private ImmutableSortedSet <TestClient> clients;
  private TestClient current;

  public TurnPhaseController (final DedicatedGameSession session)
  {
    Arguments.checkIsNotNull (session, "session");

    this.session = session;
  }

  @Override
  public void fastForwardGameState ()
  {
    setUpInitialGamePhase ();
    // TODO
  }

  // advances the game state to turn phase with only minimal error checking
  // see InitialGamePhaseTest for more rigorous testing of the initial game phase
  public void setUpInitialGamePhase ()
  {
    log.trace ("Setting up initial game phase for session {}", session.getName ());
    final InitialGamePhaseController initialPhase = new InitialGamePhaseController (session);
    initialPhase.fastForwardGameState ();
    clients = TestUtil.sortClientsByPlayerTurnOrder (session.getTestClientPool ());
    current = clients.first ();
  }

  public WaitForCommunicationActionResult waitForReinforcementPhaseToBegin ()
  {
    final TestClientPool clientPool = session.getTestClientPool ();
    final AtomicInteger verified = new AtomicInteger ();
    final ClientEventCallback <BeginReinforcementPhaseEvent> callback = new ClientEventCallback <BeginReinforcementPhaseEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <BeginReinforcementPhaseEvent> event, final TestClient client)
      {
        if (!event.isPresent ()) return;
        if (event.get ().getPlayer ().equals (current.getPlayer ())) verified.incrementAndGet ();
      }
    };

    final ImmutableSet <TestClient> failed = clientPool.waitForAllClientsToReceive (BeginReinforcementPhaseEvent.class,
                                                                                    callback);
    return new WaitForCommunicationActionResult (failed, verified.get ());
  }

  public void performRandomCountryReinforcement ()
  {
    final TestMonitor monitor = new TestMonitor (2);
    final TestClientPool clientPool = session.getTestClientPool ();
    final TestClient current = getClientInTurn ();
    // create a ClientEventProcessor for only this client
    final ClientEventProcessor processor = new ClientEventProcessor (ImmutableSet.of (current));
    final ClientEventCallback <PlayerBeginCountryReinforcementEvent> callback = new ClientEventCallback <PlayerBeginCountryReinforcementEvent> ()
    {
      @Override
      public void onEventReceived (final Optional <PlayerBeginCountryReinforcementEvent> maybe, final TestClient client)
      {
        monitor.assertTrue (maybe.isPresent ());
        final PlayerBeginCountryReinforcementEvent event = maybe.get ();
        final CountryPacket randomCountry = Randomness.getRandomElementFrom (event.getPlayerOwnedCountries ());
        final int randomArmyCount = Randomness.getRandomIntegerFrom (1, event.getMaxArmiesPerCountry ());
        client.send (new PlayerReinforceCountryRequestEvent (randomCountry.getName (), randomArmyCount));
        final ImmutableSet <TestClient> failed = clientPool
                .waitForAllClientsToReceive (PlayerReinforceCountrySuccessEvent.class);
        monitor.assertTrue (failed.isEmpty ());
      }
    };

    processor.registerCallback (PlayerBeginCountryReinforcementEvent.class, callback);
    processor.registerCompletionTask (new Runnable ()
    {
      @Override
      public void run ()
      {
        // our current client has already had the EndReinforcementPhaseEvent processed, so we need
        // a client pool reference that excludes it
        final ImmutableSet <TestClient> failed = clientPool.except (current)
                .waitForAllClientsToReceive (EndReinforcementPhaseEvent.class);
        monitor.assertTrue (failed.isEmpty ());
        monitor.checkIn ();
      }
    });

    processor.start (EndReinforcementPhaseEvent.class, monitor);

    monitor.awaitCompletion ();
  }

  public TestClient getClientInTurn ()
  {
    return current;
  }

  public TestClient getClientForNextTurn ()
  {
    return clients.higher (current);
  }

  public TestClient getClientFromPrevTurn ()
  {
    return clients.lower (current);
  }
}
