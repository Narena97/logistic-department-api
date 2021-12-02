package com.example.testtask.utils;

public class Messages {

    public static final String GET_CAR = "Could not get car: car with id %d not found";
    public static final String ADD_CAR = "Could not add new car: car with number %s already exists";
    public static final String UPDATE_CAR = "Could not update car: car with id %d not found";
    public static final String DELETE_CAR = "Could not delete car: car with id %d not found";
    public static final String ADD_CAR_NUMBER_IS_EMPTY = "Could not add car: car number should not be empty";
    public static final String UPDATE_CAR_NUMBER_IS_NOT_VALID = "Could not update car: car number is not valid";
    public static final String ADD_CAR_NUMBER_IS_NOT_VALID = "Could not add car: car number is not valid";
    public static final String UPDATE_CAR_DRIVER_ID_IS_PRESENT = "Could not update car: you can not add driver here";
    public static final String ADD_CAR_DRIVER_ID_IS_PRESENT = "Could not add new car: you can not add driver here";
    public static final String ADD_CAR_TYPE_IS_EMPTY = "Could not add car: car type should not be empty";

    public static final String GET_DRIVER = "Could not get driver: driver with id %d not found";
    public static final String ADD_DRIVER = "Could not add new driver: driver with license %d already exists";
    public static final String UPDATE_DRIVER = "Could not update driver: driver with id %d not found";
    public static final String DELETE_DRIVER = "Could not delete driver: driver with id %d not found";
    public static final String UPDATE_DRIVER_LICENSE_IS_EMPTY = "Could not update driver: driver's license should not be empty";
    public static final String ADD_DRIVER_LICENSE_IS_EMPTY = "Could not add new driver: driver's license should not be empty";
    public static final String ADD_CAR_TO_DRIVER_THAT_NOT_FOUND = "Could not add new car to driver: driver with id %d not found";
    public static final String ADD_DUPLICATE_CAR_TO_DRIVER = "Could not add new car to driver: this driver already has a car with id %d";
    public static final String ADD_CAR_TO_DRIVER_ABOVE_LIMIT = "Could not add new car to driver: this driver already has 3 cars";
    public static final String REMOVE_CAR_FROM_DRIVER_THAT_NOT_FOUND = "Could not remove car from driver: driver with id %d not found";
    public static final String REMOVE_CAR_THAT_IS_NOT_FOUND_FROM_DRIVER = "Could not remove car from driver: this driver does not have car with id %d";
    public static final String UPDATE_DRIVER_LICENSE_NUMBER_IS_NOT_VALID = "Could not update driver: driver's license number should be 10";
    public static final String ADD_DRIVER_LICENSE_NUMBER_IS_NOT_VALID = "Could not add new driver: driver's license number should be 10";
    public static final String ADD_DRIVER_LICENSE_CATEGORY_IS_EMPTY = "Could not add new driver: driver's license category should not be empty";
    public static final String ADD_DRIVER_LICENSE_EXPIRATION_TIME_IS_EMPTY = "Could not add new driver: driver's license expiration time should not be empty";
    public static final String UPDATE_DRIVER_LICENSE_EXPIRED = "Could not update driver: driver's license expired";
    public static final String ADD_DRIVER_LICENSE_EXPIRED = "Could not add driver: driver's license expired";
    public static final String ADD_DRIVER_LICENSE_EXISTS = "Could not add new driver: driver's license with number %d already exists";
    public static final String ADD_CAR_TO_DRIVER_MISMATCH = "Could not add new driver to car: driver's license does not allow driving this car";
    public static final String ADD_CAR_THAT_HAS_DRIVER_TO_ANOTHER_DRIVER = "Could not add new driver to car: this car already has a driver";

}
