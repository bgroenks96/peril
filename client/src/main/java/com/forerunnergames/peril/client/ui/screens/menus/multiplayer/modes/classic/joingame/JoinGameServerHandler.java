package com.forerunnergames.peril.client.ui.screens.menus.multiplayer.modes.classic.joingame;

public interface JoinGameServerHandler
{
  void join (final String playerName, final String serverAddress, final JoinGameServerListener listener);
}
