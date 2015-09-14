package com.forerunnergames.peril.client.assets;

import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.common.eventbus.DeadEventHandler;
import com.forerunnergames.tools.common.Arguments;

import net.engio.mbassy.bus.common.DeadMessage;

public class AssetLoadingErrorDeadEventHandler implements DeadEventHandler
{
  @Override
  public void onDeadMessage (final DeadMessage deadMessage)
  {
    Arguments.checkIsNotNull (deadMessage, "deadMessage");

    if (!(deadMessage.getMessage () instanceof AssetLoadingErrorEvent)) return;

    throw new RuntimeException (((AssetLoadingErrorEvent) deadMessage.getMessage ()).getThrowable ());
  }
}
