/*
 * Copyright © 2016 Forerunner Games, LLC.
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.forerunnergames.peril.client.assets.AssetManager;
import com.forerunnergames.peril.client.events.AssetLoadingErrorEvent;
import com.forerunnergames.peril.client.input.MouseInput;
import com.forerunnergames.peril.client.ui.screens.AbstractScreen;
import com.forerunnergames.peril.client.ui.screens.ScreenChanger;
import com.forerunnergames.peril.client.ui.screens.ScreenSize;
import com.forerunnergames.peril.client.ui.widgets.dialogs.CancellableDialogListenerAdapter;
import com.forerunnergames.peril.client.ui.widgets.dialogs.Dialog;
import com.forerunnergames.peril.client.ui.widgets.dialogs.DialogListenerAdapter;
import com.forerunnergames.peril.common.settings.CrashSettings;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.DefaultMessage;
import com.forerunnergames.tools.common.Event;
import com.forerunnergames.tools.common.Strings;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLoadingScreen extends AbstractScreen
{
  protected static final float ONE_HALF = 1.0f / 2.0f;
  protected static final float ONE_THIRD = 1.0f / 3.0f;
  protected static final float ONE_FOURTH = 1.0f / 4.0f;
  protected static final float TWO_THIRDS = 2.0f / 3.0f;
  protected static final float ONE_SIXTH = 1.0f / 6.0f;
  protected static final float ONE_NINTH = 1.0f / 9.0f;
  private final Logger log = LoggerFactory.getLogger (getClass ());
  private final AssetManager assetManager;
  private final LoadingScreenStyle style;
  private final ProgressBar progressBar;
  private final Label loadingStatusTextLabel;
  private final Dialog quitDialog;
  private final Dialog errorDialog;
  private final ResetProgressListener restoreProgressBarAnimationDurationListener = new RestoreProgressBarAnimationDurationListener ();
  private boolean isLoadingAssets;
  private boolean isOverallProgressFinished;
  private boolean isResettingOverallProgress;
  private float overallProgressPercent;
  private float currentAssetLoadingProgressPercent;
  private float previousAssetLoadingProgressPercent;
  private boolean shouldPrintStatusWithProgressPercent;
  private String statusMessageText = "";
  private ResetProgressListener resetOverallProgressCompleteListener;

  public AbstractLoadingScreen (final LoadingScreenWidgetFactory widgetFactory,
                                final ScreenChanger screenChanger,
                                final ScreenSize screenSize,
                                final MouseInput mouseInput,
                                final Batch batch,
                                final MBassador <Event> eventBus,
                                final AssetManager assetManager,
                                final LoadingScreenStyle style)
  {
    super (widgetFactory, screenChanger, screenSize, mouseInput, batch, eventBus);

    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");
    Arguments.checkIsNotNull (assetManager, "assetManager");
    Arguments.checkIsNotNull (style, "style");

    this.assetManager = assetManager;
    this.style = style;

    progressBar = widgetFactory.createProgressBar (style);

    final Label loadingTitleTextLabel = widgetFactory.createLoadingTitleLabel (style);
    final int loadingTitleTextLabelWidth = style.getLoadingTitleTextLabelWidth ();
    final int loadingTitleTextLabelHeight = style.getLoadingTitleTextLabelHeight ();

    loadingStatusTextLabel = widgetFactory.createLoadingStatusLabel (style);

    final Stack rootStack = new Stack ();
    rootStack.setFillParent (true);
    rootStack.add (widgetFactory.createBackground ());

    // @formatter:off

    final Table foregroundTable = new Table ().top ();
    foregroundTable.add ().height (style.getLoadingTitleTextVerticalSpacer ());
    foregroundTable.row ();
    foregroundTable.add (loadingTitleTextLabel).size (loadingTitleTextLabelWidth, loadingTitleTextLabelHeight);
    foregroundTable.row ().bottom ();
    foregroundTable.add (progressBar).size (style.getProgressBarWidth (), style.getProgressBarHeight ()).padBottom (style.getProgressBarStatusTextSpacer ());
    foregroundTable.row ();
    foregroundTable.add (loadingStatusTextLabel);

    // @formatter:on

    rootStack.add (foregroundTable);
    addRootActor (rootStack);

    quitDialog = createQuitDialog (style.getQuitDialogMessageText (), new QuitDialogListener ());
    errorDialog = createErrorDialog (new ErrorDialogListener ());
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void show ()
  {
    super.show ();

    quitDialog.refreshAssets ();
    errorDialog.refreshAssets ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  public void hide ()
  {
    super.hide ();

    isLoadingAssets = false;
    isOverallProgressFinished = false;
    isResettingOverallProgress = false;
    overallProgressPercent = progressBar.getMinValue ();
    currentAssetLoadingProgressPercent = progressBar.getMinValue ();
    previousAssetLoadingProgressPercent = progressBar.getMinValue ();
    shouldPrintStatusWithProgressPercent = false;
    loadingStatusTextLabel.setText ("");
    statusMessageText = "";

    quitDialog.hide (null);
    errorDialog.hide (null);
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected void update (final float delta)
  {
    super.update (delta);

    quitDialog.update (delta);
    errorDialog.update (delta);

    loadingStatusTextLabel.setText (statusMessageText
            + (shouldPrintStatusWithProgressPercent ? " " + getPrettyOverallProgressPercentText () : ""));

    if (!isOverallProgressFinished && isOverallProgressFinished ()) endProgress ();
    if (isResettingOverallProgress && isOverallProgressResettingFinished ()) endOverallProgressResetting ();
    if (!isLoadingAssets) return;

    updateAssetLoadingProgress ();

    if (assetLoadingProgressIncreased ()) increaseProgressBy (normalize (getAssetLoadingProgressPercentIncrease ()));
    if (isAssetLoadingFinished ()) endAssetLoading ();
  }

  @Override
  @OverridingMethodsMustInvokeSuper
  protected boolean onEscape ()
  {
    quitDialog.show ();
    return false;
  }

  protected abstract void onProgressFinished ();

  protected abstract void onQuitDialogSubmit ();

  protected abstract void onErrorDialogSubmit ();

  protected void onAssetLoadingFinished ()
  {
    // Empty base implementation
  }

  protected void onQuitDialogShow ()
  {
    // Empty base implementation.
  }

  protected void onQuitDialogCancel ()
  {
    // Empty base implementation.
  }

  protected void onErrorDialogShow ()
  {
    // Empty base implementation.
  }

  protected float normalize (final float assetLoadingProgressIncrease)
  {
    return assetLoadingProgressIncrease; // Base implementation performs no normalization.
  }

  protected final void loadAssetsAsync (final ImmutableList <AssetDescriptor <?>> descriptors)
  {
    Arguments.checkIsNotNull (descriptors, "descriptors");
    Arguments.checkHasNoNullElements (descriptors, "descriptors");

    isLoadingAssets = true;
    currentAssetLoadingProgressPercent = progressBar.getMinValue ();
    previousAssetLoadingProgressPercent = progressBar.getMinValue ();

    try
    {
      for (final AssetDescriptor <?> descriptor : descriptors)
      {
        assetManager.load (descriptor);
      }
    }
    catch (final RuntimeException e)
    {
      handleAssetLoadingException (e);
    }
  }

  protected final void loadAssetsAsync (final Runnable runnable)
  {
    isLoadingAssets = true;
    currentAssetLoadingProgressPercent = progressBar.getMinValue ();
    previousAssetLoadingProgressPercent = progressBar.getMinValue ();

    Gdx.app.postRunnable (runnable);
  }

  protected final boolean isFinishedLoadingAssets (final ImmutableList <AssetDescriptor <?>> descriptors)
  {
    if (!isLoadingAssets) return false;

    for (final AssetDescriptor <?> descriptor : descriptors)
    {
      if (!assetManager.isLoaded (descriptor.fileName)) return false;
    }

    return true;
  }

  protected final void unloadAssetsSync (final ImmutableList <AssetDescriptor <?>> descriptors)
  {
    try
    {
      for (final AssetDescriptor <?> descriptor : descriptors)
      {
        assetManager.unload (descriptor);
      }
    }
    catch (final RuntimeException e)
    {
      handleAssetUnloadingException (e);
    }
  }

  protected final void increaseProgressBy (final float percent)
  {
    overallProgressPercent += percent;
    progressBar.setValue (overallProgressPercent);
    log.debug ("Overall progress: {} (increased by {}).", overallProgressPercent, percent);
  }

  protected final void resetProgress (final ResetProgressListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");

    isResettingOverallProgress = true;
    overallProgressPercent = progressBar.getMinValue ();
    progressBar.setAnimateDuration (0.0f);
    progressBar.setValue (progressBar.getMinValue ());
    resetOverallProgressCompleteListener = listener;
  }

  protected final void resetProgress ()
  {
    resetProgress (restoreProgressBarAnimationDurationListener);
  }

  protected final void status (final String statusMessageText)
  {
    Arguments.checkIsNotNull (statusMessageText, "statusMessageText");

    this.statusMessageText = statusMessageText;
    shouldPrintStatusWithProgressPercent = false;
  }

  protected final void status (final String statusMessageText, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (statusMessageText, "statusMessageText");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");
    Arguments.checkHasNoNullElements (messageArgs, "messageArgs");

    status (Strings.format (statusMessageText, messageArgs));
  }

  protected final void statusWithProgressPercent (final String statusMessageText)
  {
    Arguments.checkIsNotNull (statusMessageText, "statusMessageText");

    this.statusMessageText = statusMessageText;
    shouldPrintStatusWithProgressPercent = true;
  }

  protected final void statusWithProgressPercent (final String statusMessageText, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (statusMessageText, "statusMessageText");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");
    Arguments.checkHasNoNullElements (messageArgs, "messageArgs");

    statusWithProgressPercent (Strings.format (statusMessageText, messageArgs));
  }

  protected final void handleError (final String message, final Object... messageArgs)
  {
    Arguments.checkIsNotNull (message, "message");
    Arguments.checkIsNotNull (messageArgs, "messageArgs");

    isLoadingAssets = false;

    log.error (message, messageArgs);

    errorDialog.setMessage (new DefaultMessage (Strings.format (message, messageArgs)));
    errorDialog.show ();
  }

  @Handler
  final void onEvent (final AssetLoadingErrorEvent event)
  {
    Arguments.checkIsNotNull (event, "event");

    log.debug ("Event received [{}].", event);

    Gdx.app.postRunnable (new Runnable ()
    {
      @Override
      public void run ()
      {
        handleAssetLoadingErrorEvent (event);
      }
    });
  }

  private static boolean greaterThanOrAlmostEqual (final float a, final float b, final float epsilon)
  {
    return a >= b || Math.abs (a - b) < epsilon;
  }

  private static boolean lessThanOrAlmostEqual (final float a, final float b, final float epsilon)
  {
    return a <= b || Math.abs (a - b) < epsilon;
  }

  private boolean isAssetLoadingFinished ()
  {
    return assetManager.getProgressLoading () >= 1.0f;
  }

  private void endAssetLoading ()
  {
    assert isLoadingAssets;
    isLoadingAssets = false;
    onAssetLoadingFinished ();
  }

  private boolean isOverallProgressFinished ()
  {
    return greaterThanOrAlmostEqual (overallProgressPercent, progressBar.getMaxValue (), 0.001f);
  }

  private void endProgress ()
  {
    isOverallProgressFinished = true;
    onProgressFinished ();
  }

  private void updateAssetLoadingProgress ()
  {
    assert isLoadingAssets;
    previousAssetLoadingProgressPercent = currentAssetLoadingProgressPercent;
    currentAssetLoadingProgressPercent = progressBar.getMinValue () + assetManager.getProgressLoading ();
  }

  private boolean assetLoadingProgressIncreased ()
  {
    return currentAssetLoadingProgressPercent > previousAssetLoadingProgressPercent;
  }

  private float getAssetLoadingProgressPercentIncrease ()
  {
    return currentAssetLoadingProgressPercent - previousAssetLoadingProgressPercent;
  }

  private boolean isOverallProgressResettingFinished ()
  {
    return lessThanOrAlmostEqual (progressBar.getVisualValue (), progressBar.getMinValue (), 0.001f);
  }

  private void endOverallProgressResetting ()
  {
    assert isResettingOverallProgress;
    assert resetOverallProgressCompleteListener != null;
    isResettingOverallProgress = false;
    progressBar.setAnimateDuration (style.getProgressBarAnimationDurationSeconds ());
    resetOverallProgressCompleteListener.onResetProgressComplete ();
  }

  private String getPrettyOverallProgressPercentText ()
  {
    // Math.min fixes a LibGDX bug that can take the visual percent to astronomical sizes.
    return Math.min (Math.round (progressBar.getVisualPercent () * 100.0f), 100) + " %";
  }

  private void handleAssetLoadingException (final RuntimeException e)
  {
    handleError ("A crash file has been created in \"{}\".\n\nThere was a problem loading a game resource.\n\n"
            + "Problem:\n\n{}\n\nDetails:\n\n{}", CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                 Throwables.getRootCause (e).getMessage (), Throwables.getStackTraceAsString (e));
  }

  private void handleAssetUnloadingException (final RuntimeException e)
  {
    handleError ("A crash file has been created in \"{}\".\n\nThere was a problem unloading a game resource.\n\n"
            + "Problem:\n\n{}\n\nDetails:\n\n{}", CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY,
                 Throwables.getRootCause (e).getMessage (), Throwables.getStackTraceAsString (e));
  }

  private void handleAssetLoadingErrorEvent (final AssetLoadingErrorEvent event)
  {
    handleError ("A crash file has been created in \"{}\".\n\nThere was a problem loading a game resource.\n\n"
            + "Resource Name: {}\nResource Type: {}\n\nProblem:\n\n{}\n\nDetails:\n\n{}",
                 CrashSettings.ABSOLUTE_EXTERNAL_CRASH_FILES_DIRECTORY, event.getFileName (),
                 event.getFileType ().getSimpleName (), Throwables.getRootCause (event.getThrowable ()).getMessage (),
                 Strings.toString (event.getThrowable ()));
  }

  private final class QuitDialogListener extends CancellableDialogListenerAdapter
  {
    @Override
    public void onCancel ()
    {
      onQuitDialogCancel ();
    }

    @Override
    public void onSubmit ()
    {
      isLoadingAssets = false;
      onQuitDialogSubmit ();
    }

    @Override
    public void onShow ()
    {
      onQuitDialogShow ();
    }
  }

  private final class ErrorDialogListener extends DialogListenerAdapter
  {
    @Override
    public void onSubmit ()
    {
      onErrorDialogSubmit ();
    }

    @Override
    public void onShow ()
    {
      onErrorDialogShow ();
    }
  }

  private final class RestoreProgressBarAnimationDurationListener implements ResetProgressListener
  {
    @Override
    public void onResetProgressComplete ()
    {
      progressBar.setAnimateDuration (style.getProgressBarAnimationDurationSeconds ());
    }
  }
}
