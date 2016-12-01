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

package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.peril.common.io.StreamParserFactory;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.io.StreamParser;

import java.io.FileNotFoundException;
import java.io.InputStream;

public final class InternalStreamParserFactory implements StreamParserFactory
{
  @Override
  public StreamParser create (final String fileName)
  {
    Arguments.checkIsNotNull (fileName, "fileName");

    final InputStream inputStream = InternalStreamParserFactory.class.getResourceAsStream (fileName);

    if (inputStream == null) throw new RuntimeException (
            new FileNotFoundException ("Could not find model resource file: [" + fileName + "]."));

    // @formatter:off
    return new StreamParser (inputStream)
                    .withComments (StreamParser.CommentType.SLASH_SLASH, StreamParser.CommentStatus.ENABLED)
                    .withCSVSyntax ();
    // @formatter:on
  }
}
