package com.forerunnergames.peril.core.model.io;

import com.forerunnergames.tools.common.io.StreamParser;

public interface StreamParserFactory
{
  StreamParser create (final String fileName);
}
