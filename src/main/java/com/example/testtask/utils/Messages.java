package com.example.testtask.utils;

public class Messages {

    public static final String GET_CAR = "Could not get car: car with id %d not found";
    public static final String ADD_CAR = "Could not add new car: car with number %s already exists";
    public static final String UPDATE_CAR = "Could not update car: car with id %d not found";
    public static final String DELETE_CAR = "Could not delete car: car with id %d not found";
    public static final String CAR_NUMBER_IS_EMPTY = "Car number should not be empty";
    public static final String CAR_NUMBER_IS_NOT_VALID = "Car number is not valid";
    public static final String CAR_DRIVER_ID_IS_PRESENT = "You can not add driver here";
    public static final String CAR_TYPE_IS_EMPTY = "Car type should not be empty";

    public static final String GET_DRIVER = "Could not get driver: driver with id %d not found";
    public static final String ADD_DRIVER = "Could not add new driver: driver with license %d already exists";
    public static final String UPDATE_DRIVER = "Could not update driver: driver with id %d not found";
    public static final String DELETE_DRIVER = "Could not delete driver: driver with id %d not found";
    public static final String DRIVER_LICENSE_IS_EMPTY = "Driver's license should not be empty";
    public static final String ADD_CAR_TO_DRIVER_THAT_NOT_FOUND = "Could not add new car to driver: driver with id %d not found";
    public static final String ADD_DUPLICATE_CAR_TO_DRIVER = "Could not add new car to driver: this driver already has a car with id %d";
    public static final String ADD_CAR_TO_DRIVER_ABOVE_LIMIT = "Could not add new car to driver: this driver already has 3 cars";
    public static final String REMOVE_CAR_FROM_DRIVER_THAT_NOT_FOUND = "Could not remove car from driver: driver with id %d not found";
    public static final String REMOVE_CAR_THAT_IS_NOT_FOUND_FROM_DRIVER = "Could not remove car from driver: this driver does not have car with id %d";

    public static final String DRIVERS_LICENSE_NUMBER_IS_NOT_VALID = "Driver's license number should be 10";
    public static final String DRIVERS_LICENSE_NUMBER_IS_EMPTY = "Driver's license number should not be empty";
    public static final String DRIVERS_LICENSE_CATEGORY_IS_EMPTY = "Driver's license category should not be empty";
    public static final String DRIVERS_LICENSE_EXPIRATION_TIME_IS_EMPTY = "Driver's license expiration time should not be empty";
    public static final String DRIVERS_LICENSE_EXPIRED = "Driver's license expired";
    public static final String ADD_CAR_TO_DRIVER_MISMATCH = "Could not add new driver to car: driver's license does not allow driving this car";
    public static final String ADD_CAR_THAT_HAS_DRIVER_TO_ANOTHER_DRIVER = "Could not add new driver to car: this car already has a driver";
    public static final String ADD_CAR_TO_DRIVER_WITH_EXPIRED_LICENSE = "Driver's license expired";

}
