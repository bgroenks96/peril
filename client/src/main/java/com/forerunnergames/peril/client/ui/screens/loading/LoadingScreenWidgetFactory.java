package com.forerunnergames.peril.client.ui.screens.loading;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;

public final class LoadingScreenWidgetFactory extends AbstractWidgetFactory
{
  public LoadingScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  protected AssetDescriptor<Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.LOADING_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  public Image createBackground ()
  {
    return new Image (getSkin (), "background");
  }

  public ProgressBar createProgressBar (final float stepSize)
  {
    return createHorizontalProgressBar (0.0f, 1.0f, stepSize, "default-horizontal");
  }
}
