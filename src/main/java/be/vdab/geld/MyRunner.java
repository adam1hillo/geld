package be.vdab.geld;

import be.vdab.geld.mensen.MensService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MyRunner implements CommandLineRunner {
    private final MensService mensService;

    public MyRunner(MensService mensService) {
        this.mensService = mensService;
    }

    @Override
    public void run(String... args) throws Exception {
        /*Scanner scanner = new Scanner(System.in);
        System.out.println("Id van mens:");
        int vanMensId = scanner.nextInt();
        System.out.println("Id aan mens:");
        int aanMensId = scanner.nextInt();
        System.out.println("Bedrag");
        BigDecimal bedrag = scanner.nextBigDecimal();
        try {
            Schenking schenking = new Schenking(vanMensId, aanMensId, bedrag);
            mensService.schenk(schenking);
            System.out.println("Schenking gelukt");
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
        } catch (MensNietGevondenException ex) {
            System.err.println("Schenking mislukt. Mens ontbreekt. Id: " + ex.getId());
        } catch (OnvoldoendeGeldException ex) {
            System.err.println("Schenking mislukt. Onvoldoende geld.");
        }*/
        mensService.findSchenkStatistiekPerMens().forEach(System.out::println);
    }
}
