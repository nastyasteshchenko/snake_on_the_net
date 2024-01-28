**To run the app:**  
gradle run  
**_______________________________**  
# Snake on the net

Implement the multiplayer game "Snake".
Controlling your snake, you need to eat "food" and grow, while avoiding collisions with the "body" of your snake and with snakes controlled by other players.

## Rules of the game

The playing field is a rectangle with the sides specified in the game config.
The horizontal rows of cells are called rows, the vertical rows are called columns.
In this case, the field is logically closed from opposite sides: from the leftmost cell of the row, you can go left to the rightmost cell of the same row, similarly for the uppermost cell of the column (mathematically, this is a discrete torus).
The snake is a sequence of cells on the playing field forming a continuous polyline with right bending angles (see figure).
Thus, the snake can cross the boundaries of the field an unlimited number of times.
The cage at one end of the snake is the head, the direction of movement of which is controlled by the player, the cage at the opposite end is the tail.
One or more snakes can be placed on the field, each snake is controlled by one player.
Also, there is food in the amount calculated by the formula (food_static + (the number of ALIVE snakes)) on the field at each moment of time.
Each meal occupies 1 cell.
There cannot be two meals on the same cell.
The food on the field is placed randomly on empty squares.
If there are not enough cells on the field at the current turn to place the required amount of food, then as many as possible are placed.

The time in the game is discrete, the states change after a real-time interval in milliseconds specified in the config.
During the time between state changes (in one "turn"), players have the opportunity to change the direction of their snake's head using the SteerMsg command.
If the command forces the snake to turn in the direction occupied by the cell next to its head (an attempt to reverse), then such a command is ignored.
The player can manage to replace the previous team by sending a new team, but if the team did not manage to reach the moment of the state change, then it is applied on the next turn.

At the moment of the state change, the head of each snake moves 1 cell in its current direction.
If the cell where the head moved contained food, then the rest of the snake's cells do not move, and the player earns +1 point.
And if there was no food, then all subsequent snake cells move to the place of the previous ones, and the cell on which the tail was located is released.
Next, the resulting state of the target cell is checked, where the heads of the snakes have moved.
If the target cell is occupied by a snake (with its body, tail, or any other cell), then the "crashed" snake dies, and the player is eliminated from the game.
The cells occupied by the dead snake (after moving the head) turn into food with a probability of 0.5, and the remaining cells become empty.
The snake that was "crashed into" earns +1 point (if it did not crash into itself, of course).
The snake dies regardless of whether the snake it "crashed into" dies on the same turn.
If several heads hit the same cage, then all these snakes die in the same way.

Thus, for example, you can closely chase your own (or someone else's tail) without dying.
But if at the same time someone else's snake eats food, then we will "crash" into its tail on this move.
If there was food on the cage, and several heads ran over it at the same time, then the food is eaten, the tails of all these snakes remain in place, but then they all die.

## The logic of the program

It is necessary to implement a program that will allow the player:
1. Start a new game or join an existing game.
2. Play.
3. Exit the game.

The interaction of programs takes place over the UDP protocol on the peer-to-peer principle, the message format is described in the snakes.proto file.
Exactly one GameMessage type message is packed into the body of each UDP message, without any extra bytes.
All interaction must be implemented through two UDP sockets: one for receiving multicast messages (but not for sending them!), the second for everything else.
The second socket is bound to the port selected by the operating system.
This organization of work is convenient for testing multiple copies of the program on the same machine, because they will use different ports and will not interfere with each other.
A user who starts a new game has the opportunity to set the game config parameters within the acceptable values specified in the proto file.
The way to enter these parameters can be any reasonable (for example, input from the interface or enumeration in a text file).
After that, the game begins with the only snake of this player on the field, all the rules apply as usual.
In this case, the node of this player becomes the main one (MASTER).
A node with the MASTER role sends AnnouncementMsg messages with an interval of 1 second to the multicast address 239.192.0.4, port 9192.
Also, the node with the MASTER role responds to the DiscoverMsg message with the AnnouncementMsg message (without confirmations).

All other messages in the game are sent by unicast.
Since a node cannot generally know its own IP address and port with certainty (for example, in the case of NAT), the ip_address and port fields are missing in the description of the sending player in the GamePlayer object.
In these cases, the address and port of this node should be taken from the information about the sender of the UDP packet.

Other nodes receive AnnouncementMsg messages and display a list of ongoing games in the interface (when the player is not busy playing, or always).
The user can join an ongoing game instead of starting a new one.

At the same time, he must accept her config, and cannot influence it.
To join the game, a JoinMsg message is sent to the node from which the AnnouncementMsg message was received.
This message indicates the name of the game we want to join (in the current version of the task, it doesn't matter, it's left for the future).
The connection mode is also indicated, standard or "viewing only", in the second case, the player does not receive a snake, but can only view what is happening on the field.
When a new player joins the game, the MASTER node finds a 5x5 square of cells on the field, in which there are no cells occupied by snakes.
The square is searched taking into account that the edges of the field are closed.
A snake with a length of two cells is created for the new player, its head is placed in the center of the found square, and its tail is randomly placed in one of the four neighboring cells.
There should be no food on the two cages that the new snake will occupy, otherwise another location is being sought.
The initial direction of movement of the snake is opposite to the selected direction of the tail.
The number of points of the joined player is 0.
If a suitable 5x5 square cannot be found, an ErrorMsg is sent to the player trying to join with an error message.
If it is possible to place a new snake on the field, then the new player is assigned a unique identifier within the current state of the game.
In response to JoinMsg, an AckMsg message is sent, in which the player is informed of his ID in the receiver_id field.

As a result, the nodes participating in the same game form a "star" topology: in the center is a node with the role of MASTER, the other nodes are its immediate neighbors.

The central node is responsible for the current state of the game, and only it makes all changes to it.
When a non-central player wants to turn his snake's head, he sends a SteerMsg message to the central node.
The central node accumulates all such changes, with the newer change replacing the older one within the stroke (the order is determined by msg_seq in the messages).
For example, if a player crawled up, pressed left, and then changed his mind and pressed right, and both messages managed to reach and process before the turn was changed, then the snake will crawl to the right on the next turn.
When it's time to move on to the next turn, the central node applies all these turns, advances the snakes and replaces the eaten food with a new one in accordance with the rules of the game.
After that, the new status is sent to all players in the StateMsg message.
If a node has received a state in which state_order is less than or equal to the one already known to the node, then the contents of such a message are not processed.

Any message (except AnnouncementMsg, Discoversg, and AckMsg) is confirmed by sending an AckMsg message in response with the same msg_seq as in the original message.
If the sender has not received such confirmation, he resends the message after an interval equal to the value of the state_delay_ms parameter divided by 10.
If we did not send absolutely any unicast messages to the node during the interval from the previous paragraph, then we need to send a PingMsg message.

If we have not received absolutely any unicast messages from the node for 0.8 * state_delay_ms milliseconds, then we consider that the node has dropped out of the game.
There may be three situations:
a) A node with the NORMAL role noticed that the MASTER had fallen off. Then it replaces the information about the central node with the deputy, i.e. it starts sending all unicast messages towards the DEPUTY.
b) The node with the MASTER role noticed that the DEPUTY had fallen off. Then he chooses a new DEPUTY among the NORMALS, and informs the DEPUTY himself with the message RoleChangeMsg (the rest will learn about the new DEPUTY from the planned StatusMsg, they do not need to know this urgently). c
) The node with the role of DEPUTY noticed that the MASTER fell off. Then he becomes the MASTER himself (takes control of the game), chooses a new DEPUTY, and informs each player about this with a message RoleChangeMsg.

It is important that when changing the MASTER, messages previously sent to the old MASTER, but not yet confirmed, should continue to be forwarded to the new MASTER.
A node with the VIEWER role cannot simultaneously have the MASTER or DEPUTY role (this may seem counterintuitive to some, but this is the case in the current version of the protocol).

The player can exit the game either by timeout (see above) or explicitly using the RoleChangeMsg message, indicating that he wants to become a VIEWER.
In both cases, he loses control of his snake, and its SnakeState changes to a ZOMBIE.
Such a snake does not disappear, but continues to move in the same direction, observing all the rules of the game: it can eat food, crash and die, etc.
The game ends when the last player (MASTER) leaves it.
If nodes with the VIEWER role were connected at that moment, it is allowed not to process such a situation on them.
Information about the completed game is not required to be saved anywhere.

## Appearance and management requirements

To control the snake, you need to provide a way to set the direction of the snake, which is convenient for this type of device.
In the case of a computer application, these may be the keys on the keyboard or gamepad.
In the case of a mobile application, these can be buttons on the screen, swipes in the right direction, or keys on an external gamepad or keyboard.
An example of a convenient way: the direction of rotation is set by pressing one of the keys on the keyboard: w a s d.
An example of an inconvenient way: to turn up, you need to press w, then Enter.

The program interface should include:
1. Showing a list of ongoing games received via multicast messages, with the ability to join any of them.
1. Showing the playing field and the list of players with the number of points scored, with the option to exit the game (on one screen).
1. The opportunity to start a new game.

All this can be on one screen, or on several, between which transitions are organized.
The interface can be either graphical or textual in the terminal.
The displayed list of players contains all the players playing (i.e., except those in VIEWER mode).

## Implementation requirements

The number of threads in the program in any mode of its operation should be limited to any predetermined number.
This means that the number of threads should not depend on the number of things (players, snakes, food, etc.), the number of restarts of the game, etc.
