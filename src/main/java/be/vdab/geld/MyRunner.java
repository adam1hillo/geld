package be.vdab.geld;

import be.vdab.geld.mensen.Mens;
import be.vdab.geld.mensen.MensService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Scanner;

@Component
public class MyRunner implements CommandLineRunner {
    private final MensService mensService;

    public MyRunner(MensService mensService) {
        this.mensService = mensService;
    }

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Naam");
        String naam = scanner.nextLine();
        System.out.println("Geld:");
        BigDecimal geld = scanner.nextBigDecimal();
        Mens mens = new Mens(0, naam, geld);
        long nieuweId = mensService.create(mens);
        System.out.println("Id van deze mens: " + nieuweId);
    }
}
