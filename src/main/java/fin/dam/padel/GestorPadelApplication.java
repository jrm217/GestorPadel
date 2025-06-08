package fin.dam.padel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GestorPadelApplication {

    public static void main(String[] args) {
        System.out.println(">>> INICIANDO APLICACIÓN");
        SpringApplication.run(GestorPadelApplication.class, args);
        System.out.println(">>> APLICACIÓN ARRANCADA CORRECTAMENTE");
    }
}