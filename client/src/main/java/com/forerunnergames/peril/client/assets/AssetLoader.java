package com.forerunnergames.peril.client.assets;

public interface AssetLoader
{
  void queueAssets ();
  void loadQueuedAssets ();
  void disposeAssets ();
}
