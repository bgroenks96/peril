package com.forerunnergames.peril.client.assets;

import com.badlogic.gdx.assets.AssetDescriptor;

import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.controllers.ControllerAdapter;

public final class AssetController extends ControllerAdapter
{
  private final AssetManager assetManager;

  public AssetController (final AssetManager assetManager)
  {
    Arguments.checkIsNotNull (assetManager, "assetManager");

    this.assetManager = assetManager;
  }

  @Override
  public void initialize ()
  {
    for (final AssetDescriptor <?> descriptor : AssetSettings.LOAD_BEFORE_SPLASH_SCREEN_ASSET_DESCRIPTORS)
    {
      assetManager.load (descriptor);
      assetManager.finishLoading (descriptor);
    }
  }

  @Override
  public void update ()
  {
    assetManager.update ();
  }

  @Override
  public void shutDown ()
  {
    assetManager.dispose ();
  }
}
