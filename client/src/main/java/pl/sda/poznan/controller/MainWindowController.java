package pl.sda.poznan.controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import pl.sda.poznan.Message;
import pl.sda.poznan.Transmission;
import pl.sda.poznan.viewmodel.ConnectionDialogViewModel;

public class MainWindowController {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private Transmission transmission;

  public void handleClick(MouseEvent mouseEvent) {
    Label source = (Label) mouseEvent.getSource();
    System.out.println(source.getId());
  }

  /**
   * Metoda wykonujaca sie przy klinieciu przycisku polacz do serwera z Menu Game
   */
  public void connectToServerAction(ActionEvent actionEvent) {
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(getClass().getResource("ConnectionDialogWindow.fxml"));

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Połącz do serwera");
    dialog.setHeaderText("Uzupelnij dane");

    try {
      dialog.getDialogPane().setContent(fxmlLoader.load());
    } catch (IOException e) {
      return;
    }
    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

    Optional<ButtonType> optionalType = dialog.showAndWait();
    optionalType.ifPresent(buttonType -> {
      ConnectionDialogController controller = fxmlLoader.getController();
      ConnectionDialogViewModel connectionDetails = controller.getConnectionDetails();
      connectToServer(connectionDetails);
    });
  }

  public void connectToServer(ConnectionDialogViewModel viewModel) {
    logger.log(Level.INFO, String.format(
        "Trying to connect to server at address %s with username %s", viewModel.getServerAddress(),
        viewModel.getPlayerName()));
    String[] address = viewModel.getServerAddress().split(":");
    String host = address[0];
    int port = Integer.parseInt(address[1]);
    // todo: add server address validation - display error dialog if sth is wrong
    try {
      Socket socket = new Socket();
      socket.connect(new InetSocketAddress(host, port));
      this.transmission = new Transmission(socket);

      transmission.sendObject(Message.builder()
          .header("CONNECT")
          .data(viewModel.getPlayerName())
          .build());

      try {
        Object o = transmission.readObject();
        Message message = (Message) o;
        logger.info(String.format("Received message %s", message.getHeader()));
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    } catch (IOException e) {
      logger.log(Level.INFO, "Cannot connect to server: " + e.getMessage());
      // todo: wyswietl okno z napisem "Nie udalo sie podlaczyc do serwera"
      // Skorzystaj z obiektu Alert
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Błąd");
      alert.setContentText("Nie udało się połączyć z serwerem");
      alert.showAndWait();
    }
  }

}
