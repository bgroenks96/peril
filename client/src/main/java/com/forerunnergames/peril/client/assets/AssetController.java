package com.forerunnergames.peril.client.assets;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

public final class AssetController extends ControllerAdapter
{
  private final AssetUpdater assetUpdater;
  private final AssetLoader assetLoader;

  public AssetController (final AssetUpdater assetUpdater, final AssetLoader assetLoader)
  {
    Arguments.checkIsNotNull (assetUpdater, "assetUpdater");
    Arguments.checkIsNotNull (assetLoader, "assetLoader");

    this.assetUpdater = assetUpdater;
    this.assetLoader = assetLoader;
  }

  @Override
  public void initialize ()
  {
    assetUpdater.updateAssets ();
    assetLoader.queueAssets ();
  }

  @Override
  public void update ()
  {
    assetLoader.loadQueuedAssets ();
  }

  @Override
  public void shutDown ()
  {
    assetLoader.disposeAssets ();
  }
}
