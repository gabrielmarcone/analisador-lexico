package minipascal.lexico;

import minipascal.lexico.core.AnalisadorLexico;
import minipascal.lexico.io.EscritorArquivoSaida;
import minipascal.lexico.io.LeitorArquivoFonte;
import minipascal.lexico.model.Token;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Uso: java -cp out minipascal.lexico.Main <entrada.txt> [saida.txt]");
            return;
        }

        String caminhoEntrada = args[0];
        String caminhoSaida = args.length >= 2 ? args[1] : gerarNomeSaida(caminhoEntrada);

        try {
            String fonte = LeitorArquivoFonte.ler(caminhoEntrada);
            TabelaPalavrasReservadas tabela = new TabelaPalavrasReservadas();
            AnalisadorLexico lexer = new AnalisadorLexico(fonte, tabela);

            List<Token> tokens = new ArrayList<>();
            Token token;
            while ((token = lexer.proximoToken()) != null) {
                tokens.add(token);
            }

            EscritorArquivoSaida.escrever(caminhoSaida, tokens);
            System.out.println(tokens.size() + " tokens reconhecidos. Saida gravada em " + caminhoSaida);
        } catch (IOException e) {
            System.out.println("Erro ao processar o arquivo: " + e.getMessage());
        }
    }

    private static String gerarNomeSaida(String caminhoEntrada) {
        int ponto = caminhoEntrada.lastIndexOf('.');
        String base = ponto >= 0 ? caminhoEntrada.substring(0, ponto) : caminhoEntrada;
        return base + "_saida.txt";
    }
}