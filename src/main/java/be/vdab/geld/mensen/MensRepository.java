package be.vdab.geld.mensen;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public class MensRepository {
    private final JdbcClient jdbcClient;

    public MensRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }
    public long findAantal() {
        var sql = """
                select count(*) as aantalMensen
                from mensen
                """;
        return jdbcClient.sql(sql)
                .query(Long.class)
                .single();
    }

    public void delete(long id) {
        String sql = """
                delete from mensen
                where id = ?
                """;
        jdbcClient.sql(sql)
                .param(id)
                .update();
    }
    public void update(Mens mens) {
        String sql = """
                update mensen
                set naam = ?, geld = ?
                where id = ?
                """;
        if (jdbcClient.sql(sql)
                .params(mens.getNaam(), mens.getGeld(), mens.getId())
                .update() == 0) {
            throw new MensNietGevondenException(mens.getId());
        }
    }
    public long create(Mens mens) {
        String sql = """
                insert into mensen (naam, geld)
                values (?, ?)
                """;

        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .params(mens.getNaam(), mens.getGeld())
                .update(keyHolder);
        return keyHolder.getKey().longValue();
    }
    public List<Mens> findAll() {
        String sql = """
                select id, naam, geld
                from mensen
                order by id
                """;
        return jdbcClient.sql(sql)
                .query(Mens.class)
                .list();
    }
    public List<Mens> findByGeldBetween (BigDecimal van, BigDecimal tot) {
        String sql = """
                select id, naam, geld
                from mensen
                where geld between ? and ?
                order by geld
                """;
        return jdbcClient.sql(sql)
                .params(van, tot)
                .query(Mens.class)
                .list();
    }
    public Optional<Mens> findById(long id) {
        String sql = """
                select id, naam, geld
                from mensen
                where id = ?
                """;
        return jdbcClient.sql(sql)
                .param(id)
                .query(Mens.class)
                .optional();
    }
    public Optional<Mens> findAndLockById(long id) {
        String sql = """
                select id, naam, geld
                from mensen
                where id = ?
                for update
                """;
        return jdbcClient.sql(sql)
                .param(id)
                .query(Mens.class)
                .optional();
    }
    public List<SchenkStatistiekPerMens> findSchenkStatistiekPerMens() {
        String sql = """
                select mensen.id, naam, count(schenkingen.id) as aantal, sum(bedrag) as totaal
                from mensen inner join schenkingen
                on mensen.id = schenkingen.vanMensId
                group by mensen.id
                order by mensen.id
                """;
        return jdbcClient.sql(sql)
                .query(SchenkStatistiekPerMens.class)
                .list();
    }
}
