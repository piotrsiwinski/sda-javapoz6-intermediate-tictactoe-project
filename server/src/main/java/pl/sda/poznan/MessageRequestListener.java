package pl.sda.poznan;

import pl.sda.poznan.model.Game;

public class MessageRequestListener implements RequestListener {

  private Game game;

  public MessageRequestListener(Game game) {
    this.game = game;
  }

  @Override
  public Message onMessageReceived(Message request) {
    // zrobic switch case po naglowku wiadomosci i wyslac ten komunikat tylko jesli wiadomosc zaczyna sie od connect
    // obiekt game zawiera pole isFirstPlayerConnected
    // jesli nie -> to odsylamy wiadomosc WAITING_FOR_SECOND_CLIENT i ustawiamy zmienna na true
    switch (request.getHeader()) {
      case MessageHeaders.CONNECT: {
        if (!game.isFirstPlayerConnected()) {
          return Message.builder()
              .header(MessageHeaders.WAITING_FOR_SECOND_CLIENT)
              .build();
        } else {

        }
      }
    }



    return Message.builder()
        .header("Hello")
        .data("World")
        .build();
  }
}
