package minipascal.lexico.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Lê o conteúdo de um arquivo-fonte, sempre em UTF-8 explícito.
 */
public class LeitorArquivoFonte {

    private static final char BOM = '\uFEFF';

    public static String ler(String caminho) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(caminho));
        String conteudo = new String(bytes, StandardCharsets.UTF_8);
        if (!conteudo.isEmpty() && conteudo.charAt(0) == BOM) {
            conteudo = conteudo.substring(1);
        }
        return conteudo;
    }
}