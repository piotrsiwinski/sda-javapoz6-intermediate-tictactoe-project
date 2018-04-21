package pl.sda.poznan;

public class MessageRequestListener implements RequestListener {

  @Override
  public Message onMessageReceived(Message request) {
    return Message.builder()
        .header("Hello")
        .data("World")
        .build();
  }
}
