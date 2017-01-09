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

import com.badlogic.gdx.utils.Align;

import com.forerunnergames.peril.client.settings.StyleSettings;
import com.forerunnergames.tools.common.Arguments;

public final class LoadingScreenStyle
{
  public static final int DEFAULT_PROGRESS_BAR_WIDTH = 700;
  public static final int DEFAULT_PROGRESS_BAR_HEIGHT = 20;
  public static final float DEFAULT_PROGRESS_BAR_STEP_SIZE = 0.01f;
  public static final float DEFAULT_PROGRESS_BAR_ANIMATION_DURATION_SECONDS = 0.1f;
  public static final float DEFAULT_PROGRESS_BAR_MIN_VALUE = 0.0f;
  public static final float DEFAULT_PROGRESS_BAR_MAX_VALUE = 1.0f;
  public static final int DEFAULT_PROGRESS_BAR_STATUS_TEXT_SPACER = 10;
  public static final String DEFAULT_PROGRESS_BAR_STYLE_NAME = StyleSettings.LOADING_SCREEN_PROGRESS_BAR_STYLE;
  public static final String DEFAULT_LOADING_TITLE_TEXT = "LOADING";
  public static final String DEFAULT_LOADING_TITLE_TEXT_LABEL_STYLE_NAME = StyleSettings.LOADING_SCREEN_LOADING_TITLE_TEXT_LABEL_STYLE;
  public static final int DEFAULT_LOADING_TITLE_TEXT_LABEL_WIDTH = 700;
  public static final int DEFAULT_LOADING_TITLE_TEXT_LABEL_HEIGHT = 62;
  public static final int DEFAULT_LOADING_TITLE_TEXT_VERTICAL_SPACER = 870;
  public static final int DEFAULT_LOADING_TITLE_TEXT_ALIGNMENT = Align.center;
  public static final String DEFAULT_LOADING_STATUS_INITIAL_TEXT = "";
  public static final String DEFAULT_LOADING_STATUS_TEXT_LABEL_STYLE_NAME = StyleSettings.LOADING_SCREEN_LOADING_STATUS_TEXT_LABEL_STYLE;
  public static final int DEFAULT_LOADING_STATUS_TEXT_ALIGNMENT = Align.left;
  public static final String DEFAULT_QUIT_DIALOG_MESSAGE_TEXT = "Are you sure you want to quit Peril?";
  private final int progressBarWidth;
  private final int progressBarHeight;
  private final float progressBarStepSize;
  private final float progressBarAnimationDurationSeconds;
  private final float progressBarMinValue;
  private final float progressBarMaxValue;
  private final int progressBarStatusTextSpacer;
  private final String progressBarStyleName;
  private final String loadingTitleText;
  private final String loadingTitleTextLabelStyleName;
  private final int loadingTitleTextLabelWidth;
  private final int loadingTitleTextLabelHeight;
  private final int loadingTitleTextVerticalSpacer;
  private final int loadingTitleTextAlignment;
  private final String loadingStatusInitialText;
  private final String loadingStatusLabelStyleName;
  private final int loadingStatusTextAlignment;
  private final String quitDialogMessageText;

  public static LoadingScreenStyleBuilder builder ()
  {
    return new LoadingScreenStyleBuilder ();
  }

  public int getProgressBarWidth ()
  {
    return progressBarWidth;
  }

  public int getProgressBarHeight ()
  {
    return progressBarHeight;
  }

  public float getProgressBarStepSize ()
  {
    return progressBarStepSize;
  }

  public float getProgressBarAnimationDurationSeconds ()
  {
    return progressBarAnimationDurationSeconds;
  }

  public float getProgressBarMinValue ()
  {
    return progressBarMinValue;
  }

  public float getProgressBarMaxValue ()
  {
    return progressBarMaxValue;
  }

  public int getProgressBarStatusTextSpacer ()
  {
    return progressBarStatusTextSpacer;
  }

  public String getProgressBarStyleName ()
  {
    return progressBarStyleName;
  }

  public String getLoadingTitleText ()
  {
    return loadingTitleText;
  }

  public String getLoadingTitleTextLabelStyleName ()
  {
    return loadingTitleTextLabelStyleName;
  }

  public int getLoadingTitleTextLabelWidth ()
  {
    return loadingTitleTextLabelWidth;
  }

  public int getLoadingTitleTextLabelHeight ()
  {
    return loadingTitleTextLabelHeight;
  }

  public int getLoadingTitleTextVerticalSpacer ()
  {
    return loadingTitleTextVerticalSpacer;
  }

  public int getLoadingTitleTextAlignment ()
  {
    return loadingTitleTextAlignment;
  }

  public String getLoadingStatusInitialText ()
  {
    return loadingStatusInitialText;
  }

  public String getLoadingStatusLabelStyleName ()
  {
    return loadingStatusLabelStyleName;
  }

  public int getLoadingStatusTextAlignment ()
  {
    return loadingStatusTextAlignment;
  }

  public String getQuitDialogMessageText ()
  {
    return quitDialogMessageText;
  }

  private LoadingScreenStyle (final int progressBarWidth,
                              final int progressBarHeight,
                              final float progressBarStepSize,
                              final float progressBarAnimationDurationSeconds,
                              final float progressBarMinValue,
                              final float progressBarMaxValue,
                              final int progressBarStatusTextSpacer,
                              final String progressBarStyleName,
                              final String loadingTitleText,
                              final String loadingTitleTextLabelStyleName,
                              final int loadingTitleTextLabelWidth,
                              final int loadingTitleTextLabelHeight,
                              final int loadingTitleTextVerticalSpacer,
                              final int loadingTitleTextAlignment,
                              final String loadingStatusInitialText,
                              final String loadingStatusLabelStyleName,
                              final int loadingStatusTextAlignment,
                              final String quitDialogMessageText)
  {
    Arguments.checkIsNotNegative (progressBarWidth, "progressBarWidth");
    Arguments.checkIsNotNegative (progressBarHeight, "progressBarHeight");
    Arguments.checkIsNotNegative (progressBarStepSize, "progressBarStepSize");
    Arguments.checkIsNotNegative (progressBarAnimationDurationSeconds, "progressBarAnimationDurationSeconds");
    Arguments.checkIsNotNegative (progressBarMinValue, "progressBarMinValue");
    Arguments.checkIsNotNegative (progressBarMaxValue, "progressBarMaxValue");
    Arguments.checkIsNotNegative (progressBarStatusTextSpacer, "progressBarStatusTextSpacer");
    Arguments.checkIsNotNull (progressBarStyleName, "progressBarStyleName");
    Arguments.checkIsNotNull (loadingTitleText, "loadingTitleText");
    Arguments.checkIsNotNull (loadingTitleTextLabelStyleName, "loadingTitleTextLabelStyleName");
    Arguments.checkIsNotNegative (loadingTitleTextLabelWidth, "loadingTitleTextLabelWidth");
    Arguments.checkIsNotNegative (loadingTitleTextLabelHeight, "loadingTitleTextLabelHeight");
    Arguments.checkIsNotNegative (loadingTitleTextVerticalSpacer, "loadingTitleTextVerticalSpacer");
    Arguments.checkIsNotNegative (loadingTitleTextAlignment, "loadingTitleTextAlignment");
    Arguments.checkIsNotNull (loadingStatusInitialText, "loadingStatusInitialText");
    Arguments.checkIsNotNull (loadingStatusLabelStyleName, "loadingStatusLabelStyleName");
    Arguments.checkIsNotNegative (loadingStatusTextAlignment, "loadingStatusTextAlignment");
    Arguments.checkIsNotNull (quitDialogMessageText, "quitDialogMessageText");

    this.progressBarWidth = progressBarWidth;
    this.progressBarHeight = progressBarHeight;
    this.progressBarStepSize = progressBarStepSize;
    this.progressBarAnimationDurationSeconds = progressBarAnimationDurationSeconds;
    this.progressBarMinValue = progressBarMinValue;
    this.progressBarMaxValue = progressBarMaxValue;
    this.progressBarStatusTextSpacer = progressBarStatusTextSpacer;
    this.progressBarStyleName = progressBarStyleName;
    this.loadingTitleText = loadingTitleText;
    this.loadingTitleTextLabelStyleName = loadingTitleTextLabelStyleName;
    this.loadingTitleTextLabelWidth = loadingTitleTextLabelWidth;
    this.loadingTitleTextLabelHeight = loadingTitleTextLabelHeight;
    this.loadingTitleTextVerticalSpacer = loadingTitleTextVerticalSpacer;
    this.loadingTitleTextAlignment = loadingTitleTextAlignment;
    this.loadingStatusInitialText = loadingStatusInitialText;
    this.loadingStatusLabelStyleName = loadingStatusLabelStyleName;
    this.loadingStatusTextAlignment = loadingStatusTextAlignment;
    this.quitDialogMessageText = quitDialogMessageText;
  }

  public static final class LoadingScreenStyleBuilder
  {
    private int progressBarWidth = DEFAULT_PROGRESS_BAR_WIDTH;
    private int progressBarHeight = DEFAULT_PROGRESS_BAR_HEIGHT;
    private float progressBarStepSize = DEFAULT_PROGRESS_BAR_STEP_SIZE;
    private float progressBarAnimationDurationSeconds = DEFAULT_PROGRESS_BAR_ANIMATION_DURATION_SECONDS;
    private float progressBarMinValue = DEFAULT_PROGRESS_BAR_MIN_VALUE;
    private float progressBarMaxValue = DEFAULT_PROGRESS_BAR_MAX_VALUE;
    private int progressBarStatusTextSpacer = DEFAULT_PROGRESS_BAR_STATUS_TEXT_SPACER;
    private String progressBarStyleName = DEFAULT_PROGRESS_BAR_STYLE_NAME;
    private String loadingTitleText = DEFAULT_LOADING_TITLE_TEXT;
    private String loadingTitleTextLabelStyleName = DEFAULT_LOADING_TITLE_TEXT_LABEL_STYLE_NAME;
    private int loadingTitleTextLabelWidth = DEFAULT_LOADING_TITLE_TEXT_LABEL_WIDTH;
    private int loadingTitleTextLabelHeight = DEFAULT_LOADING_TITLE_TEXT_LABEL_HEIGHT;
    private int loadingTitleTextVerticalSpacer = DEFAULT_LOADING_TITLE_TEXT_VERTICAL_SPACER;
    private int loadingTitleTextAlignment = DEFAULT_LOADING_TITLE_TEXT_ALIGNMENT;
    private String loadingStatusInitialText = DEFAULT_LOADING_STATUS_INITIAL_TEXT;
    private String loadingStatusTextLabelStyleName = DEFAULT_LOADING_STATUS_TEXT_LABEL_STYLE_NAME;
    private int loadingStatusTextAlignment = DEFAULT_LOADING_STATUS_TEXT_ALIGNMENT;
    private String quitDialogMessageText = DEFAULT_QUIT_DIALOG_MESSAGE_TEXT;

    public LoadingScreenStyle build ()
    {
      return new LoadingScreenStyle (progressBarWidth, progressBarHeight, progressBarStepSize,
              progressBarAnimationDurationSeconds, progressBarMinValue, progressBarMaxValue,
              progressBarStatusTextSpacer, progressBarStyleName, loadingTitleText, loadingTitleTextLabelStyleName,
              loadingTitleTextLabelWidth, loadingTitleTextLabelHeight, loadingTitleTextVerticalSpacer,
              loadingTitleTextAlignment, loadingStatusInitialText, loadingStatusTextLabelStyleName,
              loadingStatusTextAlignment, quitDialogMessageText);
    }

    public LoadingScreenStyleBuilder progressBarSize (final int width, final int height)
    {
      return progressBarWidth (width).progressBarHeight (height);
    }

    public LoadingScreenStyleBuilder progressBarWidth (final int width)
    {
      Arguments.checkIsNotNegative (width, "width");

      progressBarWidth = width;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarHeight (final int height)
    {
      Arguments.checkIsNotNegative (height, "height");

      progressBarHeight = height;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarStepSize (final float stepSize)
    {
      Arguments.checkIsNotNegative (stepSize, "stepSize");

      progressBarStepSize = stepSize;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarAnimationDurationSeconds (final float seconds)
    {
      Arguments.checkIsNotNegative (seconds, "seconds");

      progressBarAnimationDurationSeconds = seconds;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarMinValue (final float value)
    {
      Arguments.checkIsNotNegative (value, "value");

      progressBarMinValue = value;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarMaxValue (final float value)
    {
      Arguments.checkIsNotNegative (value, "value");

      progressBarMaxValue = value;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarStatusTextSpacer (final int spacer)
    {
      Arguments.checkIsNotNegative (spacer, "spacer");

      progressBarStatusTextSpacer = spacer;

      return this;
    }

    public LoadingScreenStyleBuilder progressBarStyle (final String styleName)
    {
      Arguments.checkIsNotNull (styleName, "styleName");

      progressBarStyleName = styleName;

      return this;
    }

    public LoadingScreenStyleBuilder loadingTitleText (final String text)
    {
      Arguments.checkIsNotNull (text, "text");

      loadingTitleText = text;

      return this;
    }

    public LoadingScreenStyleBuilder loadingTitleTextLabelStyle (final String styleName)
    {
      Arguments.checkIsNotNull (styleName, "styleName");

      loadingTitleTextLabelStyleName = styleName;

      return this;
    }

    public LoadingScreenStyleBuilder loadingTitleTextLabelSize (final int width, final int height)
    {
      return loadingTitleTextLabelWidth (width).loadingTitleTextLabelHeight (height);
    }

    public LoadingScreenStyleBuilder loadingTitleTextLabelWidth (final int width)
    {
      Arguments.checkIsNotNegative (width, "width");

      loadingTitleTextLabelWidth = width;

      return this;
    }

    public LoadingScreenStyleBuilder loadingTitleTextLabelHeight (final int height)
    {
      Arguments.checkIsNotNegative (height, "height");

      loadingTitleTextLabelHeight = height;

      return this;
    }

    public LoadingScreenStyleBuilder loadingTitleTextVerticalSpacer (final int verticalSpacer)
    {
      Arguments.checkIsNotNegative (verticalSpacer, "verticalSpacer");

      loadingTitleTextVerticalSpacer = verticalSpacer;

      return this;
    }

    public LoadingScreenStyleBuilder loadingTitleTextAlignment (final int alignment)
    {
      Arguments.checkIsNotNegative (alignment, "alignment");

      loadingTitleTextAlignment = alignment;

      return this;
    }

    public LoadingScreenStyleBuilder loadingStatusInitialText (final String text)
    {
      Arguments.checkIsNotNull (text, "text");

      loadingStatusInitialText = text;

      return this;
    }

    public LoadingScreenStyleBuilder loadingStatusTextLabelStyle (final String styleName)
    {
      Arguments.checkIsNotNull (styleName, "styleName");

      loadingStatusTextLabelStyleName = styleName;

      return this;
    }

    public LoadingScreenStyleBuilder loadingStatusTextAlignment (final int alignment)
    {
      Arguments.checkIsNotNegative (alignment, "alignment");

      loadingStatusTextAlignment = alignment;

      return this;
    }

    public LoadingScreenStyleBuilder quitDialogMessageText (final String text)
    {
      Arguments.checkIsNotNull (text, "text");

      quitDialogMessageText = text;

      return this;
    }

    private LoadingScreenStyleBuilder ()
    {
    }
  }
}
