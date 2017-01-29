package com.forerunnergames.peril.server.main.args;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import com.forerunnergames.tools.common.Strings;
import com.forerunnergames.tools.net.NetworkConstants;

public class CallbackPortParameterValidator implements IParameterValidator
{
  @Override
  public void validate (final String name, final String value)
  {
    final int[] out = new int [1];
    if (!tryParse (value, out))
    {
      throw new ParameterException (Strings.format ("Value {} for parameter {} has invalid format.", value, name));
    }

    if (out [0] > NetworkConstants.MAX_PORT || out [0] < 0)
    {
      throw new ParameterException (Strings.format ("Value {} for parameter {} is out of range.", value, name));
    }
  }

  private boolean tryParse (final String value, final int[] out)
  {
    assert out != null && out.length > 0;

    try
    {
      out [0] = Integer.parseInt (value);
      return true;
    }
    catch (final NumberFormatException e)
    {
      return false;
    }
  }
}
