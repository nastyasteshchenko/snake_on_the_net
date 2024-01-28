module nsu.networks {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.protobuf;
    requires com.google.common;

    opens nsu.networks to javafx.fxml;
    opens nsu.networks.controllers to javafx.fxml;
    opens nsu.networks.model to com.google.common;
    exports nsu.networks;
    exports nsu.networks.controllers;
    exports nsu.networks.events;
    exports nsu.networks.exceptions;
    exports nsu.networks.model;
    exports nsu.networks.model.snake;
    exports nsu.networks.events.switching;
    exports nsu.networks.controllers.utils;
    exports nsu.networks.model.communication.converters;
    exports nsu.networks.model.communication;
    opens nsu.networks.controllers.utils to javafx.fxml;
    exports nsu.networks.model.communication.udp;
    exports nsu.networks.model.communication.gameplayers;
    exports nsu.networks.events.model;
}