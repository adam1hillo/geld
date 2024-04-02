package be.vdab.geld.mensen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

public class MensTest {

    private Mens jan, mie;

    @BeforeEach
    void beforeEach() {
        jan = new Mens(1, "Jan", BigDecimal.TEN);
        mie = new Mens(1, "Mie", BigDecimal.ONE);
    }
    @Test
    void schenkWijzigdHetGeldVanDeBetrokkenMensen() {
        jan.schenk(mie, BigDecimal.ONE);
        assertThat(jan.getGeld()).isEqualTo("9");
        assertThat(mie.getGeld()).isEqualTo("2");
    }
    @Test
    void schenkMisluktBijOnvoldoendeGeld() {
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(() ->
                jan.schenk(mie, BigDecimal.valueOf(11)));
    }
    @Test
    void schenkMisluktAlsAanMensLeegIs() {
        assertThatNullPointerException().isThrownBy(() ->
                jan.schenk(null, BigDecimal.ONE));
    }
    @Test
    void schenkMisluktAlsBedragLeegIs() {
        assertThatNullPointerException().isThrownBy(()->
                jan.schenk(mie, null));
    }
}
