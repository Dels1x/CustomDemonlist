package ua.delsix.util;

import org.springframework.stereotype.Component;
import ua.delsix.exception.DemonlistDoesntExist;
import ua.delsix.jpa.entity.Demon;
import ua.delsix.jpa.entity.Demonlist;
import ua.delsix.service.DemonlistService;

@Component
public class DemonlistUtil {
    private final DemonlistService demonlistService;

    public DemonlistUtil(DemonlistService demonlistService) {
        this.demonlistService = demonlistService;
    }

    public void LinkDemonlistToDemon(Demon demon, long id) throws DemonlistDoesntExist {
        Demonlist demonlist = demonlistService.getDemonlistById(id);
        if (demonlist == null) {
            throw new DemonlistDoesntExist();
        }

        demon.setDemonlist(demonlist);
    }
}
