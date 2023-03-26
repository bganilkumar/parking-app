package com.demo.parking.service;

import com.demo.parking.exception.ParkingApplicationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class ParkingSpaceTest {

    @Test
    void spaceAvailableTest() {
        ParkingSpace parkingSpace = new ParkingSpace(10);
        IntStream.range(1, 10).boxed().forEach(range -> parkingSpace.park());
        Assertions.assertThat(parkingSpace.isAvailableToPark()).isTrue();
        parkingSpace.park();
        Assertions.assertThat(parkingSpace.isAvailableToPark()).isFalse();
    }

    @Test
    void unparkTest() {
        ParkingSpace parkingSpace = new ParkingSpace(10);
        IntStream.range(0, 10).boxed().forEach(range -> parkingSpace.park());
        Assertions.assertThat(parkingSpace.isAvailableToPark()).isFalse();
        IntStream.range(0, 10).boxed().forEach(range -> parkingSpace.unpark());
        Assertions.assertThat(parkingSpace.isAvailableToPark()).isTrue();
        Assertions.assertThatExceptionOfType(ParkingApplicationException.class).isThrownBy(parkingSpace::unpark);
    }

}