package com.forerunnergames.peril.core.model.game.phase;

import com.forerunnergames.peril.common.net.events.server.notify.broadcast.SkipPlayerTurnEvent;
import com.forerunnergames.peril.common.net.packets.person.PlayerPacket;
import com.forerunnergames.peril.core.model.state.annotations.StateEntryAction;
import com.forerunnergames.peril.core.model.state.annotations.StateExitAction;
import com.forerunnergames.tools.common.id.Id;

public interface GamePhaseHandler
{
  @StateEntryAction
  void begin ();

  @StateExitAction
  void end ();

  boolean isActive ();

  void skipPlayerTurn (SkipPlayerTurnEvent event);

  Id getCurrentPlayerId ();

  PlayerPacket getCurrentPlayerPacket ();
}
