package com.forerunnergames.peril.core.model.game.phase.turn;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

import com.forerunnergames.peril.common.net.events.client.request.EndPlayerTurnRequestEvent;
import com.forerunnergames.peril.common.net.events.server.success.EndPlayerTurnSuccessEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.game.phase.AbstractGamePhaseHandlerTest;
import com.forerunnergames.peril.core.model.people.player.PlayerTurnOrder;

import com.google.common.base.Optional;

import org.junit.Test;

public class TurnPhaseHandlerTest extends AbstractGamePhaseHandlerTest
{
  private TurnPhaseHandler turnPhase;

  @Override
  protected void setupTest ()
  {
    turnPhase = new DefaultTurnPhaseHandler (gameModelConfig, eventFactory);
  }

  @Test
  public void testVerifyPlayerEndTurnRequest ()
  {
    addMaxPlayers ();

    final PlayerPacket player = playerModel.playerPacketWith (PlayerTurnOrder.FIRST);
    final EndPlayerTurnRequestEvent endTurnRequest = new EndPlayerTurnRequestEvent ();
    when (mockCommHandler.senderOf (endTurnRequest)).thenReturn (Optional.of (player));

    assertTrue (turnPhase.verifyPlayerEndTurnRequest (endTurnRequest));
    assertTrue (eventHandler.wasFiredExactlyOnce (EndPlayerTurnSuccessEvent.class));
  }

  @Test
  public void testVerifyPlayerEndTurnRequestFailsWithInvalidPlayer ()
  {
    addMaxPlayers ();

    final PlayerPacket player = playerModel.playerPacketWith (PlayerTurnOrder.SECOND);
    final EndPlayerTurnRequestEvent endTurnRequest = new EndPlayerTurnRequestEvent ();
    when (mockCommHandler.senderOf (endTurnRequest)).thenReturn (Optional.of (player));

    assertFalse (turnPhase.verifyPlayerEndTurnRequest (endTurnRequest));
    assertTrue (eventHandler.wasNeverFired (EndPlayerTurnSuccessEvent.class));
  }
}
