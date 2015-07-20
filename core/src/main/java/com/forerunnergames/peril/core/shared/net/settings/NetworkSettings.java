package com.forerunnergames.peril.core.shared.net.settings;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Classes;

import java.util.regex.Pattern;

public final class NetworkSettings
{
  public static final int DEFAULT_TCP_PORT = 55555;
  public static final int CLIENT_CONNECTION_TIMEOUT_MS = 5000;
  public static final int SERVER_CONNECTION_TIMEOUT_MS = 1000;
  public static final String LOCALHOST_ADDRESS = "127.0.0.1";
  public static final String LOCALHOST_NAME = "localhost";
  public static final int MAX_SERVER_CONNECTION_ATTEMPTS = 3;
  public static final int MAX_PORT_VALUE = 65535;
  public static final String SERVER_JAR_NAME = "peril-server-0.1-SNAPSHOT.jar"; // TODO This is bad...
  public static final int MIN_SERVER_ADDRESS_LENGTH = 4;
  public static final int MAX_SERVER_ADDRESS_LENGTH = 255;
  public static final int MIN_SERVER_NAME_LENGTH = 3;
  public static final int MAX_SERVER_NAME_LENGTH = 30;
  public static final int MIN_SERVER_PORT_LENGTH = 1;
  public static final int MAX_SERVER_PORT_LENGTH = 5;
  public static final Pattern SERVER_ADDRESS_PATTERN = Pattern.compile ("[A-Za-z0-9.]");
  public static final Pattern SERVER_PORT_PATTERN = Pattern.compile ("[0-9]{1,5}");
  public static final Pattern SERVER_NAME_PATTERN = Pattern.compile ("[A-Za-z0-9 ]");
  public static final int CLIENT_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES = 8192;
  public static final int CLIENT_SERIALIZATION_READ_BUFFER_SIZE_BYTES = 8192;
  public static final int SERVER_SERIALIZATION_WRITE_BUFFER_SIZE_BYTES = 16382;
  public static final int SERVER_SERIALIZATION_READ_BUFFER_SIZE_BYTES = 8192;

  public static boolean isLocalhostAddress (final String address)
  {
    Arguments.checkIsNotNull (address, "address");

    return address.equals (LOCALHOST_ADDRESS) || address.equalsIgnoreCase (LOCALHOST_NAME);
  }

  private NetworkSettings ()
  {
    Classes.instantiationNotAllowed ();
  }
}
