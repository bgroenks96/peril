package com.forerunnergames.peril.core.model.country;

import com.forerunnergames.peril.core.model.territory.TerritoryName;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Preconditions;
import com.forerunnergames.tools.common.Strings;

public final class CountryName implements TerritoryName
{
  private final String name;

  public CountryName (final String name)
  {
    Arguments.checkIsNotNull (name, "name");

    this.name = name;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public boolean isUnknown()
  {
    return name.isEmpty();
  }

  public String asFileName (final String fileExtension)
  {
    Arguments.checkIsNotNullOrEmptyOrBlank (fileExtension, "fileExtension");
    Preconditions.checkIsFalse (name.isEmpty(), "Cannot convert empty country name to file name.");
    Preconditions.checkIsFalse (Strings.isWhitespace (name), "Cannot convert blank country name to file name.");

    final String[] words = name.split (" ");

    final StringBuilder fileNameBuilder = new StringBuilder();

    boolean isFirstWord = true;

    for (final String word : words)
    {
      if (isFirstWord)
      {
        final String firstLetter = word.substring (0, 1);

        fileNameBuilder.append ((word.replaceFirst (firstLetter, firstLetter.toLowerCase())));

        isFirstWord = false;
      }
      else
      {
        fileNameBuilder.append (word);
      }
    }

    fileNameBuilder.append (".").append (fileExtension);

    return fileNameBuilder.toString();
  }

  @Override
  public boolean equals (final Object o)
  {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final CountryName that = (CountryName) o;

    return name.equals (that.name);
  }

  @Override
  public int hashCode()
  {
    return name.hashCode();
  }

  @Override
  public String toString()
  {
    return String.format ("%1$s: %2$s", getClass().getSimpleName(), name);
  }
}
