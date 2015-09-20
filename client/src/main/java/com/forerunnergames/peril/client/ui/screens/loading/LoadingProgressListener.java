package com.forerunnergames.peril.client.ui.screens.loading;

public interface LoadingProgressListener
{
  void onStart ();

  void onUpdate (final float percentCompleted);

  void onFinish ();
}
