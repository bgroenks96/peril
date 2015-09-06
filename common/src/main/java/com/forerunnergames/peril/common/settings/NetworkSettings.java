package com.forerunnergames.peril.common.settings;

import com.forerunnergames.tools.common.Arguments;
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
  public static final Pattern VALID_SERVER_NAME_PATTERN = Pattern.compile ("^(?=.{" + MIN_SERVER_NAME_LENGTH + ","
          + MAX_SERVER_NAME_LENGTH + "}$)(?!.* {2,})[a-zA-Z0-9][a-zA-Z0-9 ]*[a-zA-Z0-9]$");
  public static final int CLIENT_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES = 8192;
  public static final int CLIENT_SERIALIZATION_READ_BUFFER_SIZE_BYTES = 8192;
  public static final int SERVER_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES = 16382;
  public static final int SERVER_SERIALIZATION_READ_BUFFER_SIZE_BYTES = 8192;
  public static final String EXTERNAL_IP_RESOLVER_URL = "http://ci.forerunnergames.com:8888/get-wan-ip/getwanip";
  public static final String EXTERNAL_IP_RESOLVER_BACKUP_URL = "http://getwanip.appspot.com/getmyip";

  // @formatter:off

  public static final String VALID_SERVER_NAME_DESCRIPTION =
            "1) " + MIN_SERVER_NAME_LENGTH + " to " + MAX_SERVER_NAME_LENGTH + " alphanumeric characters are allowed.\n"
          + "2) Any combination of uppercase or lowercase is allowed.\n"
          + "3) Single spaces are allowed, but cannot begin or end with a space.\n"
          + "4) No consecutive spaces.\n"
          + "5) No other type of whitespace.\n"
          + "6) No special characters.\n";

  public static final String VALID_SERVER_ADDRESS_DESCRIPTION =
            "1) IPv4 address, for example: 203.0.113.254\n"
          + "2) Ipv6 address, for example: FE80::0202:B3FF:FE1E:8329\n"
          + "3) Domain name, for example: server.example.com\n"
          + "4) Do not include the port number.\n";

  // @formatter:on

  public static boolean isValidServerName (final String serverName)
  {
    Arguments.checkIsNotNull (serverName, "serverName");

    return VALID_SERVER_NAME_PATTERN.matcher (serverName).matches ();
  }

  public static boolean isValidServerAddress (final String serverAddress)
  {
    return false;
  }

  private NetworkSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
