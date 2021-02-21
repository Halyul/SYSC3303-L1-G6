package project.elevator.src;

public enum State {
    // receive: id:1;state:Stationary; => to State.Stationary (Sender.sendState)
    // send: role:Elevator;id:1;state:Stationary; => wait for new message => to State.Stationary (Sender.sendState)
    Stationary, 
    // receive: id:1;state:OpenDoor; (Sender.sendState)
    // send: role:Elevator;id:1;state:OpenDoor; (Sender.sendState)
    // send: role:Elevator;id:1;error:StuckOpenDoor;floor:1; (Sender.sendError)
    OpenDoor,
    // receive: id:1;state:CloseDoor; (Sender.sendState)
    // send: role:Elevator;id:1;state:CloseDoor; (Sender.sendState) => receive: id:1;state:Stationary; (Sender.sendState) or receive: id:1;state:Move;direction:1; (Sender.sendDirection)
    // send: role:Elevator;id:1;error:StuckCloseDoor;floor:1; (Sender.sendError)
    CloseDoor,
    // receive: id:1;state:Move;floor:1; (Sender.sendFloor) => wait for new message
    // elevator send: id:1;state:Move;floor:1; (Sender.sendFloor)
    // arrival sensor send: role:ArrivalSensor;id:1;elevatorID:1; (Sender.sendArrivalInfo) => elevator receive: id:1;state:Move;direction:1; (Sender.sendDirection) or elevator receive: id:1;state:Stop;floor:1; (Sender.sendFloor)
    Move,
    // receive: id:1;state:Stop;floor:1; (Sender.sendFloor) => switch lamp
    // send: role:Elevator;id:1;state:Stop; (Sender.sendFloor) => to State.Stop
    // send: role:Elevator;id:1;error:StuckBetweenFloor;floor:1; (Sender.sendError)
    Stop,
    // send: role:Elevator;id:1;error:Unknown;floor:1; (Sender.sendError)
    Error
}
