package com.forerunnergames.peril.client.assets;

import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.common.eventbus.DeadEventHandler;
import com.forerunnergames.tools.common.Arguments;

import net.engio.mbassy.bus.common.DeadMessage;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

@Listener (references = References.Strong)
public class AssetLoadingErrorDeadEventHandler implements DeadEventHandler
{
  @Handler
  @Override
  public void onDeadMessage (final DeadMessage deadMessage)
  {
    Arguments.checkIsNotNull (deadMessage, "deadMessage");

    if (!(deadMessage.getMessage () instanceof AssetLoadingErrorEvent)) return;

    throw new RuntimeException (((AssetLoadingErrorEvent) deadMessage.getMessage ()).getThrowable ());
  }
}
