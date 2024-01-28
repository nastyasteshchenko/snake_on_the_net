package nsu.networks.model.snake;

public record Collision(int killerId, int victimId) {
    public boolean isSuicide() {
        return killerId == victimId;
    }
}
