package minipascal.lexico.verificacao;

import minipascal.lexico.core.AnalisadorLexico;
import minipascal.lexico.io.EscritorArquivoSaida;
import minipascal.lexico.io.LeitorArquivoFonte;
import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Testa LeitorArquivoFonte e EscritorArquivoSaida, incluindo o roundtrip
 * completo (ler .txt -> lexer -> gravar .txt) contra o exemplo exato do
 * enunciado.
 */
public class VerificacaoFase6 {

    private static final TabelaPalavrasReservadas TABELA = new TabelaPalavrasReservadas();

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) throws IOException {
        checarLeituraEscritaComAcentuacao();
        checarFormatoDoArquivoDeSaida();
        checarRoundtripIgualAoExemploDoProfessor();
        checarLeituraDeArquivoInexistente();

        System.out.println();
        System.out.println(total + " verificações, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void checarLeituraEscritaComAcentuacao() throws IOException {
        File temp = File.createTempFile("leitor", ".txt");
        temp.deleteOnExit();
        Files.write(temp.toPath(), "programação; ção 'á'".getBytes(StandardCharsets.UTF_8));

        String lido = LeitorArquivoFonte.ler(temp.getAbsolutePath());
        checar("leitura preserva acentuação UTF-8",
                lido.equals("programação; ção 'á'"));
    }

    private static void checarFormatoDoArquivoDeSaida() throws IOException {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("program", TipoToken.PALAVRA_RESERVADA, 1));
        tokens.add(new Token("x", TipoToken.IDENTIFICADOR, 1));

        File temp = File.createTempFile("saida", ".txt");
        temp.deleteOnExit();
        EscritorArquivoSaida.escrever(temp.getAbsolutePath(), tokens);

        String conteudo = new String(Files.readAllBytes(temp.toPath()), StandardCharsets.UTF_8);
        checar("arquivo de saída tem uma linha <lexema, token> por token",
                conteudo.equals("program\tPalavra reservada\nx\tIdentificador\n"));
    }

    private static void checarRoundtripIgualAoExemploDoProfessor() throws IOException {
        String programa =
                "program teste;\n" +
                "var x,y: integer;\n" +
                "const pi := 3.1416;\n" +
                "/* inicio do programa */\n" +
                "begin\n" +
                "read(x);\n" +
                "if (x > y) then\n" +
                "y := x ;\n" +
                "else\n" +
                "y := -x;\n" +
                "writeln(x);\n" +
                "write(y);\n" +
                "end.\n";

        String esperado =
                "program\tPalavra reservada\n" +
                "teste\tIdentificador\n" +
                ";\tSímbolo especial\n" +
                "var\tPalavra reservada\n" +
                "x\tIdentificador\n" +
                ",\tSímbolo especial\n" +
                "y\tIdentificador\n" +
                ":\tSímbolo especial\n" +
                "integer\tPalavra reservada\n" +
                ";\tSímbolo especial\n" +
                "const\tPalavra reservada\n" +
                "pi\tIdentificador\n" +
                ":=\tAtribuição\n" +
                "3.1416\tNúmero real\n" +
                ";\tSímbolo especial\n" +
                "begin\tPalavra reservada\n" +
                "read\tIdentificador\n" +
                "(\tSímbolo especial\n" +
                "x\tIdentificador\n" +
                ")\tSímbolo especial\n" +
                ";\tSímbolo especial\n" +
                "if\tPalavra reservada\n" +
                "(\tSímbolo especial\n" +
                "x\tIdentificador\n" +
                ">\tOperador relacional\n" +
                "y\tIdentificador\n" +
                ")\tSímbolo especial\n" +
                "then\tPalavra reservada\n" +
                "y\tIdentificador\n" +
                ":=\tAtribuição\n" +
                "x\tIdentificador\n" +
                ";\tSímbolo especial\n" +
                "else\tPalavra reservada\n" +
                "y\tIdentificador\n" +
                ":=\tAtribuição\n" +
                "-\tOperador aritmético\n" +
                "x\tIdentificador\n" +
                ";\tSímbolo especial\n" +
                "writeln\tIdentificador\n" +
                "(\tSímbolo especial\n" +
                "x\tIdentificador\n" +
                ")\tSímbolo especial\n" +
                ";\tSímbolo especial\n" +
                "write\tIdentificador\n" +
                "(\tSímbolo especial\n" +
                "y\tIdentificador\n" +
                ")\tSímbolo especial\n" +
                ";\tSímbolo especial\n" +
                "end\tPalavra reservada\n" +
                ".\tFim\n";

        File entrada = File.createTempFile("entrada", ".txt");
        entrada.deleteOnExit();
        Files.write(entrada.toPath(), programa.getBytes(StandardCharsets.UTF_8));

        AnalisadorLexico lexer = new AnalisadorLexico(LeitorArquivoFonte.ler(entrada.getAbsolutePath()), TABELA);
        List<Token> tokens = new ArrayList<>();
        Token t;
        while ((t = lexer.proximoToken()) != null) {
            tokens.add(t);
        }

        File saida = File.createTempFile("saida", ".txt");
        saida.deleteOnExit();
        EscritorArquivoSaida.escrever(saida.getAbsolutePath(), tokens);

        String gerado = new String(Files.readAllBytes(saida.toPath()), StandardCharsets.UTF_8);
        checar("saída do roundtrip completo é idêntica ao exemplo do enunciado",
                gerado.equals(esperado));
    }

    private static void checarLeituraDeArquivoInexistente() {
        boolean lancouExcecao;
        try {
            LeitorArquivoFonte.ler("caminho/que/nao/existe.txt");
            lancouExcecao = false;
        } catch (IOException e) {
            lancouExcecao = true;
        }
        checar("ler arquivo inexistente lança IOException (não trava com NPE ou afins)",
                lancouExcecao);
    }

    private static void checar(String descricao, boolean condicao) {
        total++;
        if (condicao) {
            System.out.println("[OK]    " + descricao);
        } else {
            falhas++;
            System.out.println("[FALHA] " + descricao);
        }
    }
}