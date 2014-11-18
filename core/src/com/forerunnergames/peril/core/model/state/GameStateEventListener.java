package com.forerunnergames.peril.core.model.state;

public interface GameStateEventListener
{
  public void onCreateNewGameEvent();
  public void onPlayerJoinGameRequestEvent();
  public void onPlayerJoinGameSuccessEvent();
  public void onPlayerJoinGameDeniedEvent();
  public void onDeterminePlayerTurnOrderComplete();
}
