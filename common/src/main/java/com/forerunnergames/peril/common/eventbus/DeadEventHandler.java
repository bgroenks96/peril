package com.forerunnergames.peril.common.eventbus;

import net.engio.mbassy.bus.common.DeadMessage;

public interface DeadEventHandler
{
  void onDeadMessage (final DeadMessage deadMessage);
}
