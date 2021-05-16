package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.BadRequestException;
import com.game.NotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class PlayerService{

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public List<Player> findAllByAttributes(String name, String title,
                                            Date after, Date before,
                                            Integer minExperience, Integer maxExperience,
                                            Integer minLevel, Integer maxLevel,
                                            Race race, Profession profession,
                                            Boolean banned,
                                            Integer pageNumber, Integer pageSize, PlayerOrder order) {
        return playerRepository.findAllByAttributes(
                name,
                title,
                after, before,
                minExperience,maxExperience,
                minLevel,maxLevel,
                race,
                profession,
                banned,
                PageRequest.of(pageNumber,
                        pageSize,
                        Sort.by(order.getFieldName())));
    }

    public Long getCount(
            String name, String title,
            Date after, Date before,
            Integer minExperience, Integer maxExperience,
            Integer minLevel, Integer maxLevel,
            Race race, Profession profession,
            Boolean banned
    ) {
        return (long) playerRepository.findAllByAttributes(name, title,after, before, minExperience, maxExperience, minLevel, maxLevel, race, profession, banned,
                PageRequest.of(0, Integer.MAX_VALUE)).size();
    }

    public Player get(Long id) {
        return playerRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    public boolean exists(Long id) {
        return playerRepository.existsById(id);
    }

    public Optional<Player> update(
            long id,
            Player player) {

        if (player == null) return Optional.empty();

        if (player.getExperience() != null && player.getExperience() != 0) {
            int level = Math.toIntExact((long) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100.0));
            player.setLevel(level);
            player.setUntilNextLevel(50 * (level + 1) * (level + 2) - player.getExperience());
        }

        Optional<Player> exists = playerRepository.findById(id);
        if (exists.isPresent()) {
            Player updatedPlayer = exists.get();
            updatedPlayer.updateNonNullFields(player);
            playerRepository.save(updatedPlayer);
        }
        return exists;
    }
}
