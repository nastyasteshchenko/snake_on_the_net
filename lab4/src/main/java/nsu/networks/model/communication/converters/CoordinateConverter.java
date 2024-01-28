package nsu.networks.model.communication.converters;

import nsu.networks.model.Coordinate;
import nsu.networks.SnakesProto;

class CoordinateConverter {
    private final static CoordinateConverter INSTANCE = new CoordinateConverter();

    private CoordinateConverter() {
    }

    static CoordinateConverter getInstance() {
        return INSTANCE;
    }

    Coordinate snakesProtoToCoordinate(SnakesProto.GameState.Coord coord) {
        return new Coordinate(coord.getX(), coord.getY());
    }

    SnakesProto.GameState.Coord coordinateToSnakeProto(Coordinate coordinate) {
        SnakesProto.GameState.Coord.Builder builder = SnakesProto.GameState.Coord.newBuilder();

        return builder.setX(coordinate.x())
                .setY(coordinate.y())
                .build();
    }
}
