package com.forerunnergames.peril.common.net.settings;

import com.forerunnergames.tools.common.Classes;

import java.util.regex.Pattern;

public final class NetworkSettings
{
  public static final int DEFAULT_TCP_PORT = 55555;
  public static final int CLIENT_CONNECTION_TIMEOUT_MS = 10000;
  public static final int SERVER_CONNECTION_TIMEOUT_MS = 1000;
  public static final int MAX_SERVER_CONNECTION_ATTEMPTS = 3;
  public static final String SERVER_JAR_NAME = "peril-server-0.1-SNAPSHOT.jar"; // TODO This is bad...
  public static final int MIN_SERVER_NAME_LENGTH = 3;
  public static final int MAX_SERVER_NAME_LENGTH = 30;
  public static final Pattern SERVER_NAME_PATTERN = Pattern.compile ("[A-Za-z0-9 ]");
  public static final int CLIENT_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES = 8192;
  public static final int CLIENT_SERIALIZATION_READ_BUFFER_SIZE_BYTES = 8192;
  public static final int SERVER_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES = 16382;
  public static final int SERVER_SERIALIZATION_READ_BUFFER_SIZE_BYTES = 8192;
  public static final String EXTERNAL_IP_RESOLVER_URL = "http://ci.forerunnergames.com:8888/get-wan-ip/getwanip";
  public static final String EXTERNAL_IP_RESOLVER_BACKUP_URL = "http://getwanip.appspot.com/getmyip";

  private NetworkSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
