package com.forerunnergames.peril.core.model.io;

import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.forerunnergames.peril.core.model.card.Card;
import com.forerunnergames.peril.core.model.card.CardType;
import com.forerunnergames.peril.core.shared.io.DataLoader;
import com.forerunnergames.tools.common.id.Id;

import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import org.junit.BeforeClass;

public class CardModelDataLoaderTest extends DataLoaderTest <Id, Card>
{
  private static final String TEST_CARDS_FILENAME = "test-cards.txt";
  private static final int EXPECTED_CARD_COUNT_FROM_FILE = 7;
  private static final int[] EXPECTED_CARD_TYPES_FROM_FILE = { 3, 3, 2, 1, 1, 2, 0 };
  private static Collection <Matcher <? super Card>> cardMatchers = new ArrayList <> ();

  @BeforeClass
  public static void setupClass ()
  {
    for (int i = 1; i <= EXPECTED_CARD_COUNT_FROM_FILE; ++i)
    {
      final Card expectedCard = mock (Card.class);
      final String expectedCardName = "Test-Card-" + i;

      when (expectedCard.getName ()).thenReturn (expectedCardName);
      when (expectedCard.getType ()).thenReturn (CardType.fromValue (EXPECTED_CARD_TYPES_FROM_FILE [i - 1]));

      cardMatchers.add (hasName (equalTo (expectedCardName)));
    }
  }

  @Override
  protected DataLoader <Id, Card> createDataLoader ()
  {
    return new CardModelDataLoader (new InternalStreamParserFactory ());
  }

  @Override
  protected Collection <Matcher <? super Card>> getDataMatchers ()
  {
    return cardMatchers;
  }

  @Override
  protected String getTestDataFileName ()
  {
    return TEST_CARDS_FILENAME;
  }

  private static Matcher <Card> hasName (final Matcher <String> nameMatcher)
  {
    return new FeatureMatcher <Card, String> (nameMatcher, "Card with name", "name")
    {
      @Override
      protected String featureValueOf (final Card actual)
      {
        return actual.getName ();
      }
    };
  }
}
