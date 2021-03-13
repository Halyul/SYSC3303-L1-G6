SYSC3303-L1-G6
Iteration 3

Files included:
- Source Code:
    - project/src/elevator/Elevator.java: main class for elevator subsystem
    - project/src/elevator/src/*: components used by elevator subsystem
    - project/src/floor/Floor.java: main class for floor subsystem
    - project/src/floor/src/*: components used by floor subsystem
    - project/src/scheduler/Scheduler.java: main class for scheduler subsystem
    - project/src/scheduler/src/*: components used by scheduler subsystem
    - project/src/utils/*: all shared code by three subsystems
- Tests:
    - project/src/elevator/tests: tests for elevator subsystem
    - project/src/floor/tests: tests for floor subsystem
    - project/src/scheduler/tests: tests for scheduler subsystem
    - project/src/utils/tests: tests for shared components

- Eclipse Files:
    - project/.settings/org.eclipse.jdt.core.prefs
    - project/bin/.gitignore
    - project/.classpath
    - project/.project
    - project/.gitignore

- UML:
    - Documentation/Iteration3/class_diagram_elevator.png: class diagram for elevator subsystem
    - Documentation/Iteration3/class_diagram_floor.png: class diagram for floor subsystem
    - Documentation/Iteration3/class_diagram_scheduler.png: class diagram for scheduler subsystem
    - Documentation/Iteration3/seq_diagram.png: sequence diagram
    - Documentation/Iteration3/state_diagram_elevator.png: state diagram for elevator subsystem
    - Documentation/Iteration3/state_diagram_scheduler.png: state diagram for scheduler subsystem

Setup instructions:
    - This program runs on Eclipse v4.18.0
    - Open "project" folder as an Eclipse project
    - Run "Scheduler.java" under project/src/scheduler folder
    - Run "Elevator.java" under project/src/elevator folder
    - Run "Floor.java" under project/src/floor folder
    - Three programs will not automatically terminate, this is an intended behaviour.

Responsibilities:
    - Zijun Hu: project.scheduler.*, class_diagram_elevator.png, class_diagram_scheduler.png
    - Haoyu Xu: project.elevator.*, project.utils.*
    - Bingtao Liu: project.scheduler.*
    - Lyam Milbury: project.floor.*, class_diagram_floor.png