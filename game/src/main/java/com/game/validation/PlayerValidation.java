package com.game.validation;

import com.game.entity.Player;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Calendar;
import java.util.GregorianCalendar;

@Component
public final class PlayerValidation implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Player.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        // validate target class itself
        if (!supports(target.getClass())) {
            errors.reject("Wrong target class");
        }

        Player playerToValidate = (Player) target;

        // check if any necessary field is null
        if (playerToValidate.getRace() == null ||
                playerToValidate.getProfession() == null) {
            errors.reject("Not all parameters are specified");
            return;
        }

//      check specified field constraints
        if (playerToValidate.getName().length() > 12 || playerToValidate.getName().length() == 0) {
            errors.reject("name", "name is too big");
        }

        if (playerToValidate.getTitle().length() > 30) {
            errors.reject("title", "title is too big");
        }

        if (playerToValidate.getExperience() < 0 ||
                playerToValidate.getExperience() > 10_000_000) {
            errors.reject("Experience", "Experience is not valid");
        }

        if (
                playerToValidate.getBirthday().before(new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime()) ||
                        playerToValidate.getBirthday().after(new GregorianCalendar(3000, Calendar.DECEMBER, 31).getTime())
        ) {
            errors.rejectValue("birthday","", "birthday is out of range");
        }
    }
}
