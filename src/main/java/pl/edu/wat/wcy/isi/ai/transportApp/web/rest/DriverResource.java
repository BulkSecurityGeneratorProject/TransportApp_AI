package pl.edu.wat.wcy.isi.ai.transportApp.web.rest;

import com.codahale.metrics.annotation.Timed;
import pl.edu.wat.wcy.isi.ai.transportApp.service.DriverService;
import pl.edu.wat.wcy.isi.ai.transportApp.service.dto.DriverDTO;
import pl.edu.wat.wcy.isi.ai.transportApp.web.rest.util.HeaderUtil;
import pl.edu.wat.wcy.isi.ai.transportApp.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Driver.
 */
@RestController
@RequestMapping("/api")
public class DriverResource {

    private final Logger log = LoggerFactory.getLogger(DriverResource.class);

    @Inject
    private DriverService driverService;

    /**
     * POST  /drivers : Create a new driver.
     *
     * @param driverDTO the driverDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new driverDTO, or with status 400 (Bad Request) if the driver has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/drivers")
    @Timed
    public ResponseEntity<DriverDTO> createDriver(@RequestBody DriverDTO driverDTO) throws URISyntaxException {
        log.debug("REST request to save Driver : {}", driverDTO);
        if (driverDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("driver", "idexists", "A new driver cannot already have an ID")).body(null);
        }
        DriverDTO result = driverService.save(driverDTO);
        return ResponseEntity.created(new URI("/api/drivers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("driver", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /drivers : Updates an existing driver.
     *
     * @param driverDTO the driverDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated driverDTO,
     * or with status 400 (Bad Request) if the driverDTO is not valid,
     * or with status 500 (Internal Server Error) if the driverDTO couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/drivers")
    @Timed
    public ResponseEntity<DriverDTO> updateDriver(@RequestBody DriverDTO driverDTO) throws URISyntaxException {
        log.debug("REST request to update Driver : {}", driverDTO);
        if (driverDTO.getId() == null) {
            return createDriver(driverDTO);
        }
        DriverDTO result = driverService.save(driverDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("driver", driverDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /drivers : get all the drivers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of drivers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/drivers")
    @Timed
    public ResponseEntity<List<DriverDTO>> getAllDrivers(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Drivers");
        Page<DriverDTO> page = driverService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/drivers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /drivers/:id : get the "id" driver.
     *
     * @param id the id of the driverDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the driverDTO, or with status 404 (Not Found)
     */
    @GetMapping("/drivers/{id}")
    @Timed
    public ResponseEntity<DriverDTO> getDriver(@PathVariable String id) {
        log.debug("REST request to get Driver : {}", id);
        DriverDTO driverDTO = driverService.findOne(id);
        return Optional.ofNullable(driverDTO)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /drivers/:id : delete the "id" driver.
     *
     * @param id the id of the driverDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/drivers/{id}")
    @Timed
    public ResponseEntity<Void> deleteDriver(@PathVariable String id) {
        log.debug("REST request to delete Driver : {}", id);
        driverService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("driver", id.toString())).build();
    }

}
