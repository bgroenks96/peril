package com.forerunnergames.peril.common.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GameSettingsTest
{
  @Test
  public void testValidMapNamePatternPassesNoSpacesAllLowerCase ()
  {
    final String testString = "mapname";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNoSpacesAllUpperCase ()
  {
    final String testString = "MAPNAME";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNoSpacesMixedCase ()
  {
    final String testString = "MapName";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesSpacesMixedCase ()
  {
    final String testString = "A Map name";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsMoreThanOneSpaceBetweenWords ()
  {
    final String testString = "A Map  name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsSingleSpaceBeginning ()
  {
    final String testString = " A Map name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsSingleSpaceEnd ()
  {
    final String testString = "A Map name ";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsMultipleSpacesBeginning ()
  {
    final String testString = "   A Map name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsMultipleSpacesEnd ()
  {
    final String testString = "A Map name   ";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsIllegalCharactersMiddle ()
  {
    final String testString = "A Map @#$& name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsIllegalCharacterBeginning ()
  {
    final String testString = "!A Map name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsIllegalCharacterEnd ()
  {
    final String testString = "A Map name]";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsBeyondMaxLength ()
  {
    final String testString = "A Map name that is way too long to be Valid";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsLessThanMinLength ()
  {
    final String testString = "";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsNumbers ()
  {
    final String testString = "A m4p name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }
}
