package com.forerunnergames.peril.core.shared.net.settings;

import com.forerunnergames.tools.common.Classes;

public final class NetworkSettings
{
  public static final int DEFAULT_TCP_PORT = 55555;
  public static final int CONNECTION_TIMEOUT_MS = 1000;
  public static final String LOCALHOST_ADDRESS = "127.0.0.1";
  public static final int MAX_CONNECTION_ATTEMPTS = 3;
  public static final String SERVER_JAR_NAME = "peril-server-0.1-SNAPSHOT.jar";

  private NetworkSettings()
  {
    Classes.instantiationNotAllowed();
  }
}
