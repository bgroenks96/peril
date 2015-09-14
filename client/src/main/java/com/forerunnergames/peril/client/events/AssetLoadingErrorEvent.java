package com.forerunnergames.peril.client.events;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.files.FileHandle;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.events.local.LocalEvent;

import com.google.common.base.Throwables;

public class AssetLoadingErrorEvent implements LocalEvent
{
  private final AssetDescriptor <?> assetDescriptor;
  private final Throwable throwable;

  public AssetLoadingErrorEvent (final AssetDescriptor <?> assetDescriptor, final Throwable throwable)
  {
    Arguments.checkIsNotNull (assetDescriptor, "assetDescriptor");
    Arguments.checkIsNotNull (throwable, "throwable");

    this.assetDescriptor = assetDescriptor;
    this.throwable = throwable;
  }

  public AssetDescriptor <?> getAssetDescriptor ()
  {
    return assetDescriptor;
  }

  public String getFileName ()
  {
    return assetDescriptor.fileName;
  }

  public Class <?> getFileType ()
  {
    return assetDescriptor.type;
  }

  public FileHandle getFile ()
  {
    return assetDescriptor.file;
  }

  public Throwable getThrowable ()
  {
    return throwable;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: {}: [{}] | {}: [{}]", getClass ().getSimpleName (),
                           AssetDescriptor.class.getSimpleName (), assetDescriptor, Throwable.class.getSimpleName (),
                           Throwables.getStackTraceAsString (throwable));
  }
}
