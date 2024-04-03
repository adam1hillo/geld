package be.vdab.geld.mensen;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MensService {
    private final MensRepository mensRepository;
    private final SchenkingRepository schenkingRepository;

    public MensService(MensRepository mensRepository, SchenkingRepository schenkingRepository) {
        this.mensRepository = mensRepository;
        this.schenkingRepository = schenkingRepository;
    }
    public List<Mens> findAll() {
        return mensRepository.findAll();
    }
    @Transactional
    public long create(Mens mens) {
        return mensRepository.create(mens);
    }
    @Transactional
    public void schenk(Schenking schenking) {
        long vanMensId = schenking.getVanMensId();
        Mens vanMens = mensRepository.findAndLockById(vanMensId)
                .orElseThrow(() -> new MensNietGevondenException(vanMensId));
        long aanMensId = schenking.getAanMensId();
        Mens aanMens = mensRepository.findAndLockById(aanMensId)
                .orElseThrow(() -> new MensNietGevondenException(aanMensId));
        vanMens.schenk(aanMens, schenking.getBedrag());
        mensRepository.update(vanMens);
        mensRepository.update(aanMens);
        schenkingRepository.create(schenking);
    }
    public List<SchenkStatistiekPerMens> findSchenkStatistiekPerMens() {
        return mensRepository.findSchenkStatistiekPerMens();
    }
}
