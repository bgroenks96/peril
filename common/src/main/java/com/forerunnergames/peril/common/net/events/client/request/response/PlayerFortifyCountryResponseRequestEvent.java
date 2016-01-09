package com.forerunnergames.peril.common.net.events.client.request.response;

import com.forerunnergames.peril.common.net.events.server.request.PlayerFortifyCountryRequestEvent;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.remote.origin.client.ResponseRequestEvent;
import com.forerunnergames.tools.net.events.remote.origin.server.ServerRequestEvent;

import com.google.common.base.Optional;

public final class PlayerFortifyCountryResponseRequestEvent implements ResponseRequestEvent
{
  private final Optional <String> sourceCountry;
  private final Optional <String> targetCountry;
  private final int fortifyArmyCount;

  public PlayerFortifyCountryResponseRequestEvent (final String sourceCountry,
                                                   final String targetCountry,
                                                   final int fortifyArmyCount)
  {
    Arguments.checkIsNotNull (sourceCountry, "sourceCountry");
    Arguments.checkIsNotNull (targetCountry, "targetCountry");
    Arguments.checkLowerExclusiveBound (fortifyArmyCount, 0, "fortifyArmyCount");

    this.sourceCountry = Optional.of (sourceCountry);
    this.targetCountry = Optional.of (targetCountry);
    this.fortifyArmyCount = fortifyArmyCount;
  }

  /**
   * Empty response constructor signifies that no fortification move was made.
   */
  public PlayerFortifyCountryResponseRequestEvent ()
  {
    sourceCountry = Optional.absent ();
    targetCountry = Optional.absent ();
    fortifyArmyCount = 0;
  }

  public boolean isCountryDataPresent ()
  {
    return sourceCountry.isPresent () && targetCountry.isPresent ();
  }

  public Optional <String> getSourceCountry ()
  {
    return sourceCountry;
  }

  public Optional <String> getTargetCountry ()
  {
    return targetCountry;
  }

  public int getFortifyArmyCount ()
  {
    return fortifyArmyCount;
  }

  @Override
  public Class <? extends ServerRequestEvent> getRequestType ()
  {
    return PlayerFortifyCountryRequestEvent.class;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: SourceCountry: {} | TargetCountry: {} | FortifyArmyCount: {}", sourceCountry,
                           targetCountry, fortifyArmyCount);
  }
}
