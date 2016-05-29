package com.forerunnergames.peril.integration.core.func.turn;

import com.forerunnergames.peril.common.net.events.server.notification.BeginReinforcementPhaseEvent;
import com.forerunnergames.peril.integration.TestUtil;
import com.forerunnergames.peril.integration.core.func.DedicatedGameSession;
import com.forerunnergames.peril.integration.core.func.TestPhaseController;
import com.forerunnergames.peril.integration.core.func.WaitForCommunicationActionResult;
import com.forerunnergames.peril.integration.core.func.init.InitialGamePhaseController;
import com.forerunnergames.peril.integration.server.TestClient;
import com.forerunnergames.peril.integration.server.TestClientPool;
import com.forerunnergames.peril.integration.server.TestClientPool.ClientEventCallback;
import com.forerunnergames.tools.common.Arguments;

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
