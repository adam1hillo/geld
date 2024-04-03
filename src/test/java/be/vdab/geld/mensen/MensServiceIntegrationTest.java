package be.vdab.geld.mensen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@JdbcTest
@Import({MensService.class, MensRepository.class, SchenkingRepository.class})
@Sql("/mensen.sql")
public class MensServiceIntegrationTest {
    private static final String MENSEN_TABLE = "mensen";
    private static final String SCHENKINGEN_TABLE = "schenkingen";
    private final JdbcClient jdbcClient;
    private final MensService mensService;

    public MensServiceIntegrationTest(JdbcClient jdbcClient, MensService mensService) {
        this.jdbcClient = jdbcClient;
        this.mensService = mensService;
    }
    private long idVanTestMens1() {
        return jdbcClient.sql("select id from mensen where naam = 'test1'")
                .query(Long.class)
                .single();
    }
    private long idVanTestMens2() {
        return jdbcClient.sql("select id from mensen where naam = 'test2'")
                .query(Long.class)
                .single();
    }
    @Test
    void schenkVoegtEenSchenkingToeEnPastHetGeldVanDeMensenAan() {
        long vanMensId = idVanTestMens1();
        long aanMensId = idVanTestMens2();
        mensService.schenk(new Schenking(vanMensId, aanMensId, BigDecimal.ONE));
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, MENSEN_TABLE,
                "geld = 999 and id = " + vanMensId)).isOne();
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, MENSEN_TABLE,
                "geld = 2001 and id = " + aanMensId)).isOne();
        assertThat(JdbcTestUtils.countRowsInTableWhere(jdbcClient, SCHENKINGEN_TABLE,
                "bedrag = 1 and vanMensId = " +  vanMensId + " and aanMensId = " + aanMensId)).isOne();
    }
    @Test
    void schenkingMetOnbestaandeVanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(Long.MAX_VALUE, idVanTestMens2(), BigDecimal.ONE)));
    }
    @Test
    void schenkingMetOnbestaandeAanMensMislukt() {
        assertThatExceptionOfType(MensNietGevondenException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(idVanTestMens1(), Long.MAX_VALUE, BigDecimal.ONE)));
    }
    @Test
    void schenkingMetOnvoldoendeGeldMislukt() {
        assertThatExceptionOfType(OnvoldoendeGeldException.class).isThrownBy(
                () -> mensService.schenk(new Schenking(idVanTestMens1(), idVanTestMens2(), BigDecimal.valueOf(1001))));
    }
}
