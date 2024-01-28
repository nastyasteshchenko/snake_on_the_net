package nsu.networks.model.communication.converters;

import nsu.networks.SnakesProto;
import nsu.networks.model.communication.NodeRole;

public class NodeRoleConverter {
    private final static NodeRoleConverter INSTANCE = new NodeRoleConverter();

    private NodeRoleConverter() {
    }

    public static NodeRoleConverter getInstance() {
        return INSTANCE;
    }

    public NodeRole snakesProtoToNodeRole(SnakesProto.NodeRole role) {
        return switch (role) {
            case MASTER -> NodeRole.MASTER;
            case NORMAL -> NodeRole.NORMAL;
            case VIEWER -> NodeRole.VIEWER;
            case DEPUTY -> NodeRole.DEPUTY;
        };
    }

    public SnakesProto.NodeRole nodeRoleToSnakesProto(NodeRole nodeRole) {
        return switch (nodeRole) {
            case MASTER -> SnakesProto.NodeRole.MASTER;
            case NORMAL -> SnakesProto.NodeRole.NORMAL;
            case VIEWER -> SnakesProto.NodeRole.VIEWER;
            case DEPUTY -> SnakesProto.NodeRole.DEPUTY;
        };
    }
}
