package com.forerunnergames.peril.client.assets;

public interface AssetUpdater
{
  void updateAssets ();

  boolean isFinished ();

  float getProgressPercent ();

  void shutDown ();
}
