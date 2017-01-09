/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.loading;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.settings.AssetSettings;
import com.forerunnergames.peril.client.ui.widgets.AbstractWidgetFactory;
import com.forerunnergames.tools.common.Arguments;

public class LoadingScreenWidgetFactory extends AbstractWidgetFactory
{
  public LoadingScreenWidgetFactory (final AssetManager assetManager)
  {
    super (assetManager);
  }

  @Override
  protected AssetDescriptor <Skin> getSkinAssetDescriptor ()
  {
    return AssetSettings.LOADING_SCREEN_SKIN_ASSET_DESCRIPTOR;
  }

  public Image createBackground ()
  {
    return new Image (getSkin (), "background");
  }

  public ProgressBar createProgressBar (final LoadingScreenStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    final ProgressBar progressBar = createHorizontalProgressBar (style.getProgressBarMinValue (),
                                                                 style.getProgressBarMaxValue (),
                                                                 style.getProgressBarStepSize (),
                                                                 style.getProgressBarStyleName ());

    progressBar.setAnimateDuration (style.getProgressBarAnimationDurationSeconds ());

    return progressBar;
  }

  public Label createLoadingTitleLabel (final LoadingScreenStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    return createLabel (style.getLoadingTitleText (), style.getLoadingTitleTextAlignment (),
                        style.getLoadingTitleTextLabelStyleName ());
  }

  public Label createLoadingStatusLabel (final LoadingScreenStyle style)
  {
    Arguments.checkIsNotNull (style, "style");

    return createLabel (style.getLoadingStatusInitialText (), style.getLoadingStatusTextAlignment (),
                        style.getLoadingStatusLabelStyleName ());
  }
}
