package nsu.networks.controllers;

import com.google.common.eventbus.EventBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import nsu.networks.controllers.utils.CheckUserInputUtils;
import nsu.networks.controllers.utils.InformUtils;
import nsu.networks.events.JoinGameEvent;
import nsu.networks.events.switching.SwitchToStartPageEvent;
import nsu.networks.exceptions.UserInputException;
import nsu.networks.model.GameAnnouncement;
import nsu.networks.model.Model;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AvailableGamesListController {

    @FXML
    private ListView<GameAnnouncement> gamesList;
    @FXML
    private TextField nicknameField;
    @FXML
    private CheckBox viewerModeCheckBox;
    private EventBus eventBus;
    private Model model;
    private ScheduledExecutorService scheduler;

    void updateListOfGames(List<GameAnnouncement> gameAnnouncementList) {
        ObservableList<GameAnnouncement> items = FXCollections.observableArrayList(gameAnnouncementList);
        gamesList.setItems(items);
    }

    void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    void setModel(Model model) {
        model.setControllersEventBus(eventBus);
        this.model = model;
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(model::removeExpiredGames, 1, 1, TimeUnit.SECONDS);
    }

    @FXML
    private void back() {
        scheduler.shutdownNow();
        eventBus.post(new SwitchToStartPageEvent());
    }

    @FXML
    private void chooseGame() {
        try {
            GameAnnouncement chosenGame = gamesList.getSelectionModel().getSelectedItem();
            if (chosenGame != null) {
                String nickname = nicknameField.getText();
                CheckUserInputUtils.checkGameName(nickname);
                eventBus.post(new JoinGameEvent(model, chosenGame, nickname, viewerModeCheckBox.isSelected()));
                scheduler.shutdownNow();
            }
        } catch (UserInputException e) {
            InformUtils.inform(e.getMessage());
        }
    }

    @FXML
    private void discover() throws IOException {
        model.sendDiscoverMsg();
    }
}
