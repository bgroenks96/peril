/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
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

import com.forerunnergames.tools.common.Message;

import javax.annotation.Nullable;

public interface Dialog
{
  void show ();

  void show (@Nullable final Action action);

  void hide ();

  void hide (@Nullable final Action action);

  void setTitle (final String title);

  void setMessage (final Message message);

  boolean isShown ();

  void addListener (final EventListener listener);

  void enableInput ();

  void disableInput ();

  void update (final float delta);

  void refreshAssets ();
}
