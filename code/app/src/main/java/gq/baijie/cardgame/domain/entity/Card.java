package gq.baijie.cardgame.domain.entity;

public class Card {

  private final Suit suit;
  private final Rank rank;

  public Card(Suit suit, Rank rank) {
    this.suit = suit;
    this.rank = rank;
  }

  public Suit getSuit() {
    return suit;
  }

  public Rank getRank() {
    return rank;
  }

  public enum Suit {
    SPADE,
    CLUB,
    HEART,
    DIAMOND
  }

  public enum Rank {
    ACE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
    KING(13),
    QUEEN(12),
    JACK(11);

    private final int id;

    Rank(int id) {
      this.id = id;
    }

    public int getId() {
      return id;
    }

  }

}
