package com.forerunnergames.peril.client.assets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import java.io.File;

public final class DefaultS3AssetMetadata implements S3AssetMetadata
{
  private final String key;
  private final File file;

  public DefaultS3AssetMetadata (final String key, final File file)
  {
    Arguments.checkIsNotNull (key, "key");
    Arguments.checkIsNotNull (file, "file");

    this.key = key;
    this.file = file;
  }

  @Override
  public String getKey ()
  {
    return key;
  }

  @Override
  public File getFile ()
  {
    return file;
  }

  @Override
  public int hashCode ()
  {
    int result = getKey ().hashCode ();
    result = 31 * result + getFile ().hashCode ();
    return result;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass () != o.getClass ()) return false;

    final DefaultS3AssetMetadata that = (DefaultS3AssetMetadata) o;

    return key.equals (that.key) && file.equals (that.file);
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Key [{}] | File [{}]", getClass ().getSimpleName (), key, file);
  }
}
