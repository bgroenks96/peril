package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.loading;

public interface JoinGameServerHandler
{
  void join (final String playerName, final String serverAddress, final JoinGameServerListener listener);
}
