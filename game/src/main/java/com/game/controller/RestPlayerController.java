package com.game.controller;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerService;
import com.game.validation.PlayerValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@Validated
@RequestMapping("/rest/players")
public class RestPlayerController {
    private final PlayerValidation playerValidation;
    private final PlayerService playerService;

    @Autowired
    public RestPlayerController(PlayerService playerService, PlayerValidation playerValidation) {
        this.playerService = playerService;
        this.playerValidation = playerValidation;
    }

    @GetMapping
    public ResponseEntity<List<Player>> getPlayers(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {
        List<Player> playerList = playerService.findAllByAttributes(
                name,
                title,
                millisToDate(after), millisToDate(before),
                minExperience,maxExperience,
                minLevel,maxLevel,
                race,profession,
                banned,
                pageNumber, pageSize, order);
        return new ResponseEntity<>(playerList, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
            @RequestParam(value = "order", required = false, defaultValue = "ID") PlayerOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize
    ) {
        Long count = playerService.getCount(name, title,
                millisToDate(after), millisToDate(before),
                minExperience, maxExperience,
                minLevel, maxLevel,
                race, profession, banned);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Player> savePlayer(
            @Validated @RequestBody Player player,
            BindingResult bindingResult
    ) {
        playerValidation.validate(player, bindingResult);

        if (bindingResult.hasErrors())
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        playerService.save(player);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayer(
            @PathVariable("id") long id
    ) {
        Player player = playerService.get(id);

        if (id <= 0)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        if (!playerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        if (player == null)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @PostMapping("/{id}") // for Edit button
    public ResponseEntity<Player> update(
            @PathVariable long id,
            @RequestBody @Validated Player player,
            BindingResult bindingResult
    ) {
        if (id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!playerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Optional<Player> updatedPlayer = playerService.update(id, player);
        if (updatedPlayer.isPresent()) {
            playerValidation.validate(updatedPlayer.get(), bindingResult);
            if (bindingResult.hasErrors())
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return updatedPlayer.map(value ->
                new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {
        if (id <= 0)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (!playerService.exists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Player player = playerService.get(id);
        playerService.delete(id);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    private Date millisToDate(Long millis) {
        return millis == null ? null : new Date(millis);
    }
}
