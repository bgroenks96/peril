package com.forerunnergames.peril.client.io;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import com.forerunnergames.tools.common.Classes;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.server.configuration.ServerConfiguration;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameServerCacheManager
{
  private static final Logger log = LoggerFactory.getLogger (GameServerCacheManager.class);
  private static final String cacheFileName = "peril/settings/.game-session";

  public static void writeToCache (final String playerName,
                                   final UUID playerSecretId,
                                   final ServerConfiguration serverConfiguration)
  {
    final FileHandle file = Gdx.files.external (cacheFileName);
    if (file.exists ())
    {
      file.delete ();
    }

    try (final ObjectOutputStream out = new ObjectOutputStream (file.write (false)))
    {
      out.writeObject (new CachedGameSession (playerName, playerSecretId, serverConfiguration));
    }
    catch (final IOException e)
    {
      log.error ("Error writing to cache file:", e);
    }
  }

  public static CachedGameSession readFromCache ()
  {
    final FileHandle file = Gdx.files.external (cacheFileName);
    if (!file.exists ())
    {
      return null;
    }

    try (final ObjectInputStream in = new ObjectInputStream (file.read ()))
    {
      return (CachedGameSession) in.readObject ();
    }
    catch (final IOException | ClassNotFoundException e)
    {
      log.error ("Error writing to cache file:", e);
      return null;
    }
  }

  public static boolean existsCachedSession ()
  {
    return Gdx.files.external (cacheFileName).exists ();
  }

  public static boolean deleteCache ()
  {
    return Gdx.files.external (cacheFileName).delete ();
  }

  public static class CachedGameSession implements Serializable
  {
    private final String playerName;
    private final UUID playerSecretId;
    private final InternalCachedServerConfiguration serverConfiguration;

    private CachedGameSession (final String playerName,
                               final UUID playerSecretId,
                               final ServerConfiguration serverConfiguration)
    {
      this.playerName = playerName;
      this.playerSecretId = playerSecretId;
      this.serverConfiguration = new InternalCachedServerConfiguration (serverConfiguration.getAddress (),
              serverConfiguration.getPort ());
    }

    public String getPlayerName ()
    {
      return playerName;
    }

    public UUID getPlayerSecretId ()
    {
      return playerSecretId;
    }

    public ServerConfiguration getServerConfiguration ()
    {
      return serverConfiguration;
    }

    @Override
    public String toString ()
    {
      return Strings.format ("{}: Player: {} | SecretId: {} | ServerConfiguration: [{}]", playerName,
                             getClass ().getSimpleName (), playerSecretId, serverConfiguration);
    }
  }

  private static class InternalCachedServerConfiguration implements ServerConfiguration, Serializable
  {
    private final String address;
    private final int port;

    public InternalCachedServerConfiguration (final String address, final int port)
    {
      this.address = address;
      this.port = port;
    }

    @Override
    public String getAddress ()
    {
      return address;
    }

    @Override
    public int getPort ()
    {
      return port;
    }
  }

  private GameServerCacheManager ()
  {
    Classes.instantiationNotAllowed ();
  }
}
