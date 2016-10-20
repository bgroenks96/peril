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

package com.forerunnergames.peril.client.ui.widgets.dialogs;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

public class NullDialog implements Dialog
{
  @Override
  public void show ()
  {
  }

  @Override
  public void show (@Nullable final Action action)
  {
  }

  @Override
  public void show (final String message)
  {
    Arguments.checkIsNotNull (message, "message");
  }

  @Override
  public void show (final String message, @Nullable final Action action)
  {
    Arguments.checkIsNotNull (message, "message");
  }

  @Override
  public void hide ()
  {
  }

  @Override
  public void hide (@Nullable final Action action)
  {
  }

  @Override
  public void setTitle (final String title)
  {
    Arguments.checkIsNotNull (title, "title");
  }

  @Override
  public void setMessage (final Message message)
  {
    Arguments.checkIsNotNull (message, "message");
  }

  @Override
  public boolean isShown ()
  {
    return false;
  }

  @Override
  public void addListener (final EventListener listener)
  {
    Arguments.checkIsNotNull (listener, "listener");
  }

  @Override
  public void enableInput ()
  {
  }

  @Override
  public void disableInput ()
  {
  }

  @Override
  public boolean isInputDisabled ()
  {
    return false;
  }

  @Override
  public void enableSubmission ()
  {
  }

  @Override
  public void disableSubmission ()
  {
  }

  @Override
  public boolean isSubmissionDisabled ()
  {
    return false;
  }

  @Override
  public void setSubmissionDisabled (final boolean isDisabled)
  {
  }

  @Override
  public void enableTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");
  }

  @Override
  public void disableTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");
  }

  @Override
  public void setTextButtonDisabled (final String buttonText, final boolean isDisabled)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");
  }

  @Override
  public boolean isDisabledTextButton (final String buttonText)
  {
    Arguments.checkIsNotNull (buttonText, "buttonText");

    return false;
  }

  @Override
  public void update (final float delta)
  {
  }

  @Override
  public void refreshAssets ()
  {
  }
}
