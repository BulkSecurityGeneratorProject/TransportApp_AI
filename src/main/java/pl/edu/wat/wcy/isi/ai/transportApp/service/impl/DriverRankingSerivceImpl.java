package pl.edu.wat.wcy.isi.ai.transportApp.service.impl;

import org.springframework.stereotype.Service;
import pl.edu.wat.wcy.isi.ai.transportApp.domain.Car;
import pl.edu.wat.wcy.isi.ai.transportApp.domain.DriverRankingRow;
import pl.edu.wat.wcy.isi.ai.transportApp.domain.Journey;
import pl.edu.wat.wcy.isi.ai.transportApp.repository.CarRepository;
import pl.edu.wat.wcy.isi.ai.transportApp.repository.DriverRepository;
import pl.edu.wat.wcy.isi.ai.transportApp.repository.JourneyRepository;
import pl.edu.wat.wcy.isi.ai.transportApp.service.DriverRankingSerivce;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jakub on 02.01.17.
 */
@Service
public class DriverRankingSerivceImpl implements DriverRankingSerivce {

    @Inject
    private JourneyRepository journeyRepository;
    @Inject
    private DriverRepository driverRepository;


    @Override
    public List<DriverRankingRow> getRanking() {
        List<Journey> journeys = journeyRepository.findAll();
        Map<String, Double> collect = journeys.stream().filter(f -> f.getTaxi() != null && f.getTaxi().getDriver()!= null)
            .collect(Collectors.groupingBy(j -> j.getTaxi().getDriver().getId(),Collectors.averagingDouble(Journey::getRating)));
        return collect.keySet().stream()
            .map(m -> new DriverRankingRow(m,driverRepository.findOne(m).getName(),collect.get(m)))
            .sorted((s1,s2) -> -1 * Double.compare(s1.getAvergeRating(),s2.getAvergeRating()))
            .collect(Collectors.toList());
    }
}
