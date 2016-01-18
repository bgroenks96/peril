package com.forerunnergames.peril.common.net.events.server.notification;

import com.forerunnergames.peril.common.game.InitialCountryAssignment;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerNotificationEvent;

public final class BeginPlayerCountryAssignmentEvent implements ServerNotificationEvent
{
  private final InitialCountryAssignment assignmentMode;

  public BeginPlayerCountryAssignmentEvent (final InitialCountryAssignment assignmentMode)
  {
    this.assignmentMode = assignmentMode;
  }

  public InitialCountryAssignment getAssignmentMode ()
  {
    return assignmentMode;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: AssignmentMode: {}", getClass ().getSimpleName (), assignmentMode);
  }

  @RequiredForNetworkSerialization
  private BeginPlayerCountryAssignmentEvent ()
  {
    assignmentMode = null;
  }
}
