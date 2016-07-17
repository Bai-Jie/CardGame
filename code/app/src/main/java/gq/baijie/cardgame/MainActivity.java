package gq.baijie.cardgame;

import android.content.Context;
import android.os.Bundle;
import android.support.percent.PercentFrameLayout;
import android.support.percent.PercentLayoutHelper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import gq.baijie.cardgame.business.SpiderSolitaire;
import gq.baijie.cardgame.business.SpiderSolitaires;
import gq.baijie.cardgame.domain.entity.Card;
import gq.baijie.cardgame.ui.widget.CardStackLayout;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends AppCompatActivity {

  private LinearLayout cardStackList;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    cardStackList = (LinearLayout) findViewById(R.id.card_stack_list);

    show(SpiderSolitaires.getSampleSpiderSolitaireState());
    setSelectListener();
  }

  private void show(SpiderSolitaire.State state) {
    if (cardStackList == null) {
      return; //TODO
    }
    cardStackList.removeAllViews();//TODO do this?
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, WRAP_CONTENT, 1);

    for (SpiderSolitaire.State.CardStack cardStack : state.cardStacks) {
      cardStackList.addView(newCardStackView(cardStackList.getContext(), cardStack), layoutParams);
    }
  }

  private static View newCardStackView(Context context, SpiderSolitaire.State.CardStack cardStack) {
    CardStackLayout result = new CardStackLayout(context);
    for (Card card : cardStack.cards) {
      result.addView(newCardView(context, card), MATCH_PARENT, WRAP_CONTENT);
    }
    return result;
  }

  private static View newCardView(Context context, Card card) {
    TextView content = new TextView(context);
    content.setText(toString(card));
    content.setBackgroundResource(R.drawable.card_background);

    PercentFrameLayout container = new PercentFrameLayout(context);
    container.addView(content, 0, 0);
    ((PercentFrameLayout.LayoutParams) content.getLayoutParams()).gravity = CENTER_HORIZONTAL;
    final PercentLayoutHelper.PercentLayoutInfo layoutInfo =
        ((PercentFrameLayout.LayoutParams) content.getLayoutParams()).getPercentLayoutInfo();
    // https://en.wikipedia.org/wiki/Standard_52-card_deck
    layoutInfo.aspectRatio = 0.71428571428571428571428571428571f;// 2.5 / 3.5
    layoutInfo.widthPercent = 0.9f;
    return container;
  }

  private static String toString(Card card) {
    String result;
    switch (card.getSuit()) {
      case CLUB:
        result = "♣";
        break;
      case DIAMOND:
        result = "♦";
        break;
      case HEART:
        result = "♥";
        break;
      case SPADE:
        result = "♠";
        break;
      default:
        result = "";
        break;
    }
    switch (card.getRank()) {
      case ACE:
        result += "A";
        break;
      case JACK:
        result += "J";
        break;
      case QUEEN:
        result += "Q";
        break;
      case KING:
        result += "K";
        break;
      default:
        result += card.getRank().getId();
        break;
    }
    return result;
  }


  // ########## Event Bus ##########
  private final Subject<Object, Object> eventBus = PublishSubject.create();

  private void setSelectListener() {
    if (cardStackList == null) {
      return; //TODO
    }
    for (int cardStackIndex = 0; cardStackIndex < cardStackList.getChildCount(); cardStackIndex++) {
      final ViewGroup cardStackView = (ViewGroup) cardStackList.getChildAt(cardStackIndex);
      for (int cardIndex = 0; cardIndex < cardStackView.getChildCount(); cardIndex++) {
        final int finalCardStackIndex = cardStackIndex;
        final int finalCardIndex = cardIndex;
        cardStackView.getChildAt(cardIndex).setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            eventBus.onNext(new SelectCardEvent(finalCardStackIndex, finalCardIndex));
          }
        });
      }
    }
  }

  {
    eventBus.ofType(SelectCardEvent.class).subscribe(new Action1<SelectCardEvent>() {
      @Override
      public void call(SelectCardEvent event) {
        System.out.printf("cardStackIndex: %d, selectedCardIndex: %d%n",
                          event.cardStackIndex, event.selectedCardIndex);
      }
    });
  }

  public static class SelectCardEvent {
    final int cardStackIndex;
    final int selectedCardIndex;

    public SelectCardEvent(int cardStackIndex, int selectedCardIndex) {
      this.cardStackIndex = cardStackIndex;
      this.selectedCardIndex = selectedCardIndex;
    }
  }

  // ########## Event Bus End ##########

}
