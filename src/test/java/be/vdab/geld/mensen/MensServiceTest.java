package be.vdab.geld.mensen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MensServiceTest {

    private MensService mensService;
    @Mock
    private MensRepository mensRepository;
    @Mock
    private SchenkingRepository schenkingRepository;
    private Mens jan, mie;
    @BeforeEach
    void beforeEach() {
        mensService = new MensService(mensRepository, schenkingRepository);
        jan = new Mens(1, "Jan", BigDecimal.TEN);
        mie = new Mens(2, "Mie", BigDecimal.TEN);
    }
    @Test
    void schenkingMetOnbestaandeVanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                ()-> mensService.schenk(new Schenking(1, 2, BigDecimal.ONE)));
    }
    @Test
    void schenkingMetOnbestaandeAanMensMislukt() {
        when(mensRepository.findAndLockById(1)).thenReturn(Optional.of(jan));
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(1, 2, BigDecimal.ONE)));
    }
    @Test
    void schenkVoegtEenSchenkingToeEnWijzigtHetGeldVanDeBetrokkenMensen() {
        when(mensRepository.findAndLockById(1)).thenReturn(Optional.of(jan));
        when(mensRepository.findAndLockById(2)).thenReturn(Optional.of(mie));
        var schenking = new Schenking(1, 2, BigDecimal.ONE);
        mensService.schenk(schenking);
        assertThat(jan.getGeld()).isEqualByComparingTo("9");
        assertThat(mie.getGeld()).isEqualByComparingTo("11");
        verify(mensRepository).findAndLockById(1);
        verify(mensRepository).findAndLockById(2);
        verify(mensRepository).update(jan);
        verify(mensRepository).update(mie);
        verify(schenkingRepository).create(schenking);
    }
    @Test
    void schenkingMetOnvoldoendeGeldMislukt() {
        when(mensRepository.findAndLockById(1)).thenReturn(Optional.of(jan));
        when(mensRepository.findAndLockById(2)).thenReturn(Optional.of(mie));
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(1, 2, BigDecimal.valueOf(11))));
    }
}
