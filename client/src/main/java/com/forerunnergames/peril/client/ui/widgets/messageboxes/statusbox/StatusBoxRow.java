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

package com.forerunnergames.peril.client.ui.widgets.messageboxes.statusbox;

import com.forerunnergames.peril.client.messages.StatusMessage;
import com.forerunnergames.peril.client.ui.widgets.WidgetFactory;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.LabelMessageBoxRow;
import com.forerunnergames.peril.client.ui.widgets.messageboxes.MessageBoxRowStyle;

public final class StatusBoxRow extends LabelMessageBoxRow <StatusMessage>
{
  public StatusBoxRow (final StatusMessage message,
                       final MessageBoxRowStyle rowStyle,
                       final WidgetFactory widgetFactory)
  {
    super (message, rowStyle, widgetFactory);
  }
}
