package project.elevator.src;

public class ArrivalSensor {
    // The height of each floor in meters
    private static final double floorHeight = 3.57; 
    // floor number
    private int number;
    
    public ArrivalSensor(int number) {
        this.number = number;
    }
    /**
     * Check if the elevator arrives at current this floor
     * SHOULD USE A REAL HARDWARE to notify the elevator
     * The method simulate this behavior. 
     * The arrival sensor calculates the time the elevator arrives,
     * and then sleeps. When the elevator exits from this method,
     * means the elevator arrived at this floor.
     * If the maximum speed and acceleration changed in the Motor class,
     * this method should be rewrited.
     * The logic is based on maximum speed of 1.43 m/s and 
     * acceleration 0.6 m/s^2
     * Correctness can be checked using Motor.move(displacement)
     * @param speed initial speed of the elevator
     * @param maxSpeed maximum speed of the elevator
     * @param accelerationDisplacement The displacement the elevator takes 
     * 	to accelerate to maxSpeed and deceleration from maxSpeed
     * @param accelerationTime The time the elevator takes to acceleration to maxSpeed and deceleration from maxSpeed in seconds
     * @param toFloor the destination floor
     * @return new speed of the elevator
     */
    public double check(double speed, double maxSpeed, double accelerationDisplacement, double accelerationTime, int toFloor) {
        double movementTime;
        // based on the data we have, the elevator can accelerate and decelerate within one floor
        // then the elevator will reach max speed
        if (speed == 0) {
            // stationary to move
            if (toFloor == this.number) {
                // to the adjacent floor
                movementTime = (this.floorHeight - accelerationDisplacement * 2) / maxSpeed + accelerationTime * 2;
                speed = 0;
            } else {
                // accelerate to constant
                movementTime = accelerationTime + (this.floorHeight - accelerationDisplacement) / maxSpeed;
                speed = maxSpeed;
            }
        } else {
            // move to stationary, or keep constant speed
            if (toFloor == this.number) {
                // constant to decelerate
                movementTime = accelerationTime + (this.floorHeight - accelerationDisplacement) / maxSpeed;
                speed = 0;
            } else {
                // keep constant speed
                movementTime = this.floorHeight / maxSpeed;
                speed = maxSpeed;
            }
        }
        try {
            Thread.sleep((long) (movementTime * 1000));
        } catch(InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return speed;
    }
}