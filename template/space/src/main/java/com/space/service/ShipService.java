package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;


import java.util.List;
import java.util.Map;

public interface ShipService {

    List<Ship> listOfShips(Map<String, String> params);

    Long shipsCount(Map<String, String> params);

    Ship createShip(Ship ship);

    Ship getShip(Long id);

    Ship updateShip(Ship ship, Long id);

    Ship deleteShip(Long id);
}
