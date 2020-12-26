package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import com.space.service.specs.Filter;
import com.space.service.specs.QueryOperator;
import com.space.service.specs.ShipSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;


@Service
public class ShipServiceImpl implements ShipService {
    private ShipRepository shipRepository;

    @Autowired
    public void setShipRepository(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public List<Ship> listOfShips(Map<String, String> allParams) {
        int page = 0;
        if (allParams.get("pageNumber") != null) page = Integer.parseInt(allParams.get("pageNumber"));
        int size = 3;
        if (allParams.get("pageSize") != null) size = Integer.parseInt(allParams.get("pageSize"));
        ShipOrder shipOrder = ShipOrder.ID;
        if (allParams.get("order") != null) shipOrder = ShipOrder.valueOf(allParams.get("order"));

        Pageable pageble = PageRequest.of(page, size, Sort.by(shipOrder.getFieldName()));

        List<Filter> filters = createFilterList(allParams);

        ShipSpecification shipSpecification = createSpecification(filters);

        return shipRepository.findAll(shipSpecification, pageble).getContent();
    }

    private List<Filter> createFilterList(Map<String, String> allParams) {
        List<Filter> filters = new ArrayList<>();

        for (Map.Entry<String, String> pair : allParams.entrySet()) {
            switch (pair.getKey()) {
                case "name":
                    filters.add(new Filter("name", QueryOperator.MATCH, Optional.ofNullable(allParams.get("name")).orElse(null)));
                    break;
                case "planet":
                    filters.add(new Filter("planet", QueryOperator.MATCH, Optional.ofNullable(allParams.get("planet")).orElse(null)));
                    break;
                case "shipType":
                    filters.add(new Filter("shipType", QueryOperator.EQUAL, Optional.of(ShipType.valueOf(allParams.get("shipType"))).orElse(null)));
                    break;
                case "after":
                    Date afterDate = new Date(Long.parseLong(allParams.get("after")));
                    filters.add(new Filter("prodDate", QueryOperator.AFTER, afterDate));
                    break;
                case "before":
                    Date beforeDate = new Date(Long.parseLong(allParams.get("before")));
                    filters.add(new Filter("prodDate", QueryOperator.BEFORE, beforeDate));
                    break;
                case "isUsed":
                    filters.add(new Filter("isUsed", QueryOperator.EQUAL, Optional.of(Boolean.valueOf(allParams.get("isUsed"))).orElse(null)));
                    break;
                case "minSpeed":
                    filters.add(new Filter("speed", QueryOperator.GREATER_THAN_EQUAL, Double.parseDouble(allParams.get("minSpeed"))));
                    break;
                case "maxSpeed":
                    filters.add(new Filter("speed", QueryOperator.LESS_THAN_EQUAL, Double.parseDouble(allParams.get("maxSpeed"))));
                    break;
                case "minCrewSize":
                    filters.add(new Filter("crewSize", QueryOperator.GREATER_THAN_EQUAL, Integer.parseInt(allParams.get("minCrewSize"))));
                    break;
                case "maxCrewSize":
                    filters.add(new Filter("crewSize", QueryOperator.LESS_THAN_EQUAL, Integer.parseInt(allParams.get("maxCrewSize"))));
                    break;
                case "minRating":
                    filters.add(new Filter("rating", QueryOperator.GREATER_THAN_EQUAL, Double.parseDouble(allParams.get("minRating"))));
                    break;
                case "maxRating":
                    filters.add(new Filter("rating", QueryOperator.LESS_THAN_EQUAL, Double.parseDouble(allParams.get("maxRating"))));
                    break;
                default:
                    //   System.out.println("No such filter.");
            }
        }
        return filters;
    }

    private ShipSpecification createSpecification(List<Filter> filters) {
        ShipSpecification shipSpecification = new ShipSpecification();
        for (Filter filter : filters) {
            if (filter.getValue() != null)
                shipSpecification.add(filter);
        }
        return shipSpecification;
    }

    @Override
    public Long shipsCount(Map<String, String> allParams) {
        return shipRepository.count(createSpecification(createFilterList(allParams)));
    }

    @Override
    public Ship createShip(Ship ship) {
        Ship validShip = validateShipParams(ship);

        validShip.setRating(computeRating(ship));
        return shipRepository.save(validShip);
    }

    private Ship validateShipParams(Ship ship) {
        if (ship.getName() == null
                || ship.getName().isEmpty()
                || ship.getName().length() > 50
                || ship.getPlanet() == null
                || ship.getPlanet().length() > 50
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getProdDate().getTime() < 0
                || ship.getProdDate().before(new Date(900, Calendar.JANUARY, 1, 0, 0, 0))
                || ship.getProdDate().after(new Date(1119, Calendar.DECEMBER, 31, 24, 0, 0))
                || ship.getSpeed() == null
                || Math.round(ship.getSpeed() * 100) / 100.0 < 0.01
                || Math.round(ship.getSpeed() * 100) / 100.0 > 0.99
                || ship.getCrewSize() == null
                || ship.getCrewSize() > 9999
                || ship.getCrewSize() < 1
        ) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        if (ship.getUsed() == null) ship.setUsed(false);
        return ship;
    }

    @Override
    public Ship getShip(Long id) {
        if (id == null || id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Optional<Ship> ship = shipRepository.findById(id);
        if (!ship.isPresent()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        return ship.get();
    }


    @Override
    public Ship updateShip(Ship ship, Long id) {
        if (id == null || id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Optional<Ship> s = shipRepository.findById(id);
        if (!s.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Ship sh = s.get();
        if (ship.getName() != null) sh.setName(ship.getName());
        if (ship.getPlanet() != null) sh.setPlanet(ship.getPlanet());
        if (ship.getShipType() != null) sh.setShipType(ship.getShipType());
        if (ship.getProdDate() != null) sh.setProdDate(ship.getProdDate());
        if (ship.getUsed() != null) sh.setUsed(ship.getUsed());
        if (ship.getSpeed() != null) sh.setSpeed(ship.getSpeed());
        if (ship.getCrewSize() != null) sh.setCrewSize(ship.getCrewSize());
        sh.setId(id);
        return createShip(sh);
    }

    @Override
    public Ship deleteShip(Long id) {
        if (id == null || id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Optional<Ship> ship = shipRepository.findById(id);
        if (!ship.isPresent())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        shipRepository.deleteById(id);
        return ship.get();
    }

    public Double computeRating(Ship ship) {
        Double v = ship.getSpeed();
        Double k;
        if (ship.getUsed() != null && ship.getUsed()) {
            k = 0.5;
        } else {
            k = 1.0;
        }
        int y0 = 1119;
        int y = ship.getProdDate().getYear();
        return Math.round(100 * 80 * v * k / (y0 - y + 1)) / 100.0;
    }
}
