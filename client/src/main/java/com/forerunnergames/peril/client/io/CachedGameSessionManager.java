/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.DataResult;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CachedGameSessionManager
{
  private static final Logger log = LoggerFactory.getLogger (CachedGameSessionManager.class);
  private static final String RELATIVE_EXTERNAL_PERIL_HOME_DIRECTORY = "peril/";
  private static final String GAME_SESSION_FILE_NAME = ".game-session";

  public static void saveSession (final String playerName,
                                  final UUID playerSecretId,
                                  final ServerConfiguration serverConfiguration)
  {
    Arguments.checkIsNotNull (playerName, "playerName");
    Arguments.checkIsNotNull (playerSecretId, "playerSecretId");
    Arguments.checkIsNotNull (serverConfiguration, "serverConfiguration");

    final FileHandle file = getFile ();

    try (final ObjectOutput out = new ObjectOutputStream (file.write (false)))
    {
      out.writeObject (new CachedGameSession (playerName, playerSecretId, serverConfiguration));
    }
    catch (final IOException e)
    {
      log.error ("Could not write to [{}]:", file, e);
    }
  }

  public static DataResult <CachedGameSession, Exception> loadSession ()
  {
    final FileHandle file = getFile ();

    if (!file.exists ())
    {
      return DataResult.failureNoData ((Exception) new FileNotFoundException (
              Strings.format ("Session file not found: [{}]", file)));
    }

    try (final ObjectInputStream in = new ObjectInputStream (file.read ()))
    {
      return DataResult.success ((CachedGameSession) in.readObject ());
    }
    catch (final IOException | ClassNotFoundException e)
    {
      log.error ("Could not read [{}]:", file, e);
      return DataResult.failureNoData ((Exception) e);
    }
  }

  public static boolean existsSession ()
  {
    return getFile ().exists ();
  }

  public static void deleteSession ()
  {
    final FileHandle file = getFile ();

    try
    {
      file.delete ();
    }
    catch (final GdxRuntimeException e)
    {
      log.warn ("Could not delete game session: [{}]", file, e);
    }
  }

  private static FileHandle getFile ()
  {
    return Gdx.files.external (RELATIVE_EXTERNAL_PERIL_HOME_DIRECTORY + GAME_SESSION_FILE_NAME);
  }

  private CachedGameSessionManager ()
  {
    Classes.instantiationNotAllowed ();
  }

  public static final class CachedGameSession implements Serializable
  {
    private static final long serialVersionUID = -3884348080948440247L;
    private final String playerName;
    private final UUID playerSecretId;
    private final InternalCachedServerConfiguration serverConfig;

    public String getPlayerName ()
    {
      return playerName;
    }

    public UUID getPlayerSecretId ()
    {
      return playerSecretId;
    }

    public String getServerAddress ()
    {
      return serverConfig.getAddress ();
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: PlayerName: [{}] | PlayerSecretId: [{}] | ServerConfig: [{}]",
                             getClass ().getSimpleName (), playerName, playerSecretId, serverConfig);
    }

    private CachedGameSession (final String playerName,
                               final UUID playerSecretId,
                               final ServerConfiguration serverConfig)
    {
      this.playerName = playerName;
      this.playerSecretId = playerSecretId;
      this.serverConfig = new InternalCachedServerConfiguration (serverConfig.getAddress (), serverConfig.getPort ());
    }
  }

  private static final class InternalCachedServerConfiguration implements ServerConfiguration, Serializable
  {
    private static final long serialVersionUID = 978624922627934362L;
    private final String serverAddress;
    private final int serverPort;

    @Override
    public String getAddress ()
    {
      return serverAddress;
    }

    @Override
    public int getPort ()
    {
      return serverPort;
    }

    private InternalCachedServerConfiguration (final String serverAddress, final int serverPort)
    {
      this.serverAddress = serverAddress;
      this.serverPort = serverPort;
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: ServerAddress: [{}] | ServerPort: [{}]", getClass ().getSimpleName (), serverAddress,
                             serverPort);
    }
  }
}
