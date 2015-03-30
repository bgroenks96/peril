package com.forerunnergames.peril.core.model.map.territory;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

public abstract class AbstractTerritoryName implements TerritoryName
{
  private final String name;

  protected AbstractTerritoryName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
  }

  @Override
  public String asFileName (final String fileExtension)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (fileExtension, "fileExtension");
    Preconditions.checkIsFalse (name.isEmpty (), "Cannot convert empty territory name to file name.");
    Preconditions.checkIsFalse (Strings.isWhitespace (name), "Cannot convert blank territory name to file name.");

    final String[] words = name.split (" ");

    final StringBuilder fileNameBuilder = new StringBuilder ();

    boolean isFirstWord = true;

    for (final String word : words)
    {
      if (isFirstWord)
      {
        final String firstLetter = word.substring (0, 1);

        fileNameBuilder.append ((word.replaceFirst (firstLetter, firstLetter.toLowerCase ())));

        isFirstWord = false;
      }
      else
      {
        fileNameBuilder.append (word);
      }
    }

    fileNameBuilder.append (".").append (fileExtension);

    return fileNameBuilder.toString ();
  }

  @Override
  public String asString ()
  {
    return name;
  }

  @Override
  public boolean is (String name)
  {
    Arguments.checkIsNotNull (name, "name");

    return this.name.equals (name);
  }

  @Override
  public boolean isNot (String name)
  {
    return !is (name);
  }

  @Override
  public boolean isUnknown ()
  {
    return name.isEmpty ();
  }

  @Override
  public boolean equals (Object o)
  {
    if (this == o) return true;
    if (o == null || getClass () != o.getClass ()) return false;

    AbstractTerritoryName that = (AbstractTerritoryName) o;

    return name.equals (that.name);
  }

  @Override
  public int hashCode ()
  {
    return name.hashCode ();
  }

  @Override
  public String toString ()
  {
    return String.format ("%1$s: %2$s", getClass ().getSimpleName (), name);
  }
}
