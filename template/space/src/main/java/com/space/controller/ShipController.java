package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private ShipService shipService;

    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }


    @GetMapping("/ships")
    public ResponseEntity<List<Ship>> listOfShips(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok().body(shipService.listOfShips(params));
    }

//    public ResponseEntity<List<Ship>> listAllShips(@RequestParam(name = "name", required = false, defaultValue = "0") int name,
//                                                   @RequestParam(name = "planet",required = false,defaultValue = "0") int planet,
//                                                   @RequestParam(name = "shipType",required = false,defaultValue = "0") ShipType shipType,
//                                                   @RequestParam(name = "pageNumber", required = false, defaultValue = "0") int pageNumber,
//                                                   @RequestParam(name = "pageSize", required = false, defaultValue = "3") int pageSize,
//                                                   @RequestParam(name = "order", required = false, defaultValue = "ID") ShipOrder shipOrder) {
//        List<Ship> ships = shipService.listAllShips(pageNumber, pageSize, shipOrder);
//        return ResponseEntity.ok().body(ships);
//    }

    @GetMapping("/ships/count")
    public ResponseEntity<Long> shipsCount(@RequestParam Map<String, String> params) {
        return ResponseEntity.ok().body(shipService.shipsCount(params));
    }

    @PostMapping("/ships")
    public ResponseEntity<Ship> createShip(@RequestBody @Validated Ship ship) {
        return ResponseEntity.ok().body(shipService.createShip(ship));
    }

    @GetMapping("/ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(shipService.getShip(id));
    }

    @PostMapping("/ships/{id}")
    public ResponseEntity<Ship> updateShip(@RequestBody @Validated Ship ship, @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(shipService.updateShip(ship, id));
    }

    @DeleteMapping("/ships/{id}")
    public ResponseEntity<Ship> deleteShip(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(shipService.deleteShip(id));
    }

}
