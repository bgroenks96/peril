package com.forerunnergames.peril.core.shared.io;

import com.forerunnergames.tools.common.io.StreamParser;

public interface StreamParserFactory
{
  StreamParser create (final String fileName);
}
