package project.elevator.src;

public class Motor {
    // The maximum speed of the elevator in m/s, 1.3 * 1.1
    private static final double maxSpeed = 1.43; 
    // The acceleration of the elevator in m/s^2
    // one floor uses different acceleration, like 0.1
    private static final double acceleration = 0.6;
    // The time the elevator takes to acceleration to maxSpeed and deceleration from maxSpeed in seconds
    // 1.43 / 0.6 = 2.383
    private final double accelerationTime = maxSpeed / acceleration;
    // The displacement the elevator takes to accelerate to maxSpeed and deceleration from maxSpeed
    // 0.5 * 0.6 * 2.383 ^ 2 = 1.7
    private final double accelerationDisplacement = 0.5 * acceleration * Math.pow(accelerationTime, 2);
    // 0 for stationary, 1 for up, -1 for down
    private int direction = 0;
    // current speed
    private double currentSpeed = 0;
    
    public Motor() {
        
    }
    
    /**
     * To simulate the movement of moving between floors
     * Not used. For correctness checking
     * @param displacement the distance the elevator should move
     */
    private void move(double displacement) {
        double movementTime;
        if (displacement < this.accelerationDisplacement * 2) {
            movementTime = Math.sqrt(2 * displacement / this.acceleration);
        } else {
            movementTime = (displacement - this.accelerationDisplacement * 2) / this.maxSpeed + this.accelerationTime * 2;
        }
        System.out.println(Thread.currentThread().getName() + ": " + displacement + "m; " + movementTime + " seconds.");
    }
    
    /**
     * To simulate the up movement of moving between floors
     */
    public void up(double speed) {
        this.direction = 1;
        this.currentSpeed = speed;
        start();
    }
    
    /**
     * To simulate the down movement of moving between floors
     */
    public void down(double speed) {
        this.direction = -1;
        this.currentSpeed = speed;
        start();
    }
    
    /**
     * To simulate the stop
     */
    public void stop() {
        this.direction = 0;
        this.currentSpeed = 0;
    }
    
    /**
     * start the motor
     */
    private void start() {
        
    }
    
    /**
     * get the speed of the motor
     * @return as described above
     */
    public double getSpeed() {
    	return this.currentSpeed;
    }
    
    /**
     * get direction of the motor
     * @return as described above
     */
    public int getDirection() {
    	return this.direction;
    }
    
    /**
     * get the maximum speed of the motor
     * @return as described above
     */
    public double getMaxSpeed() {
        return this.maxSpeed;
    }
    
    /**
     * get the displacement the elevator takes to accelerate to maxSpeed 
     * and deceleration from maxSpeed
     * @return as described above
     */
    public double getAccelerationDisplacement() {
        return this.accelerationDisplacement;
    }
    
    /**
     * get the time the elevator takes to acceleration to maxSpeed and 
     * deceleration from maxSpeed in seconds
     * @return as described above
     */
    public double getAccelerationTime() {
        return this.accelerationTime;
    }
}
