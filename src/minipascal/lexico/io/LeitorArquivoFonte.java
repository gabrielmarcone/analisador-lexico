package minipascal.lexico.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Lê o conteúdo de um arquivo-fonte, sempre em UTF-8 explícito.
 */
public class LeitorArquivoFonte {

    public static String ler(String caminho) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(caminho));
        return new String(bytes, StandardCharsets.UTF_8);
    }
}