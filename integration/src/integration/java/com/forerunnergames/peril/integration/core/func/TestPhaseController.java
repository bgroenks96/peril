package com.forerunnergames.peril.integration.core.func;

public interface TestPhaseController
{
  /**
   * Performs the minimum number of client actions required to advance the game state to the end of this phase. Game
   * configuration details may be provided at the discretion of the implementation.
   */
  void fastForwardGameState ();
}
