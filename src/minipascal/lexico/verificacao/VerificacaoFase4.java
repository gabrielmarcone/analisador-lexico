package minipascal.lexico.verificacao;

import minipascal.lexico.core.AnalisadorLexico;
import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Roda o AnalisadorLexico contra trechos isolados e contra os dois
 * programas de exemplo do enunciado, sem JUnit.
 */
public class VerificacaoFase4 {

    private static int total = 0;
    private static int falhas = 0;

    public static void main(String[] args) {
        checarToken("13", "13", TipoToken.NUMERO_INTEIRO);
        checarToken("1.33", "1.33", TipoToken.NUMERO_REAL);
        checarToken("24.40e-04", "24.40e-04", TipoToken.NUMERO_REAL);
        checarToken("24.40e04", "24.40e04", TipoToken.NUMERO_REAL);
        checarToken("mod", "mod", TipoToken.OPERADOR_ARITMETICO);
        checarToken("div", "div", TipoToken.PALAVRA_RESERVADA);
        checarToken("and", "and", TipoToken.OPERADOR_LOGICO);
        checarToken(">=", ">=", TipoToken.OPERADOR_RELACIONAL);
        checarToken("<>", "<>", TipoToken.OPERADOR_RELACIONAL);
        checarToken(":=", ":=", TipoToken.ATRIBUICAO);
        checarToken(":", ":", TipoToken.SIMBOLO_ESPECIAL);
        checarToken("\"Media = \"", "\"Media = \"", TipoToken.CONSTANTE_STRING);
        checarToken("'a'", "'a'", TipoToken.CONSTANTE_CHAR);
        checarToken("Media_das_medias", "Media_das_medias", TipoToken.IDENTIFICADOR);
        checarToken("@", "@", TipoToken.ERRO_LEXICO);

        checarSemTokenDeComentario();
        checarNaoConfundeDivisaoComComentario();
        checarProgramaExemplo();
        checarProgramaPiloto();

        System.out.println();
        System.out.println(total + " verificações, " + falhas + " falha(s).");
        if (falhas > 0) {
            System.exit(1);
        }
    }

    private static void checarSemTokenDeComentario() {
        AnalisadorLexico lexer = new AnalisadorLexico("/* comentario */ x");
        Token t = lexer.proximoToken();
        checar("comentário não deve gerar token, primeiro token é 'x'",
                t != null && t.getLexema().equals("x"));
    }

    private static void checarNaoConfundeDivisaoComComentario() {
        AnalisadorLexico lexer = new AnalisadorLexico("a/2");
        lexer.proximoToken(); // "a"
        Token divisao = lexer.proximoToken();
        checar("'/' sem '*' na frente deve ser Operador aritmetico",
                divisao != null && divisao.getTipo() == TipoToken.OPERADOR_ARITMETICO);
    }

    private static void checarProgramaExemplo() {
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

        List<Token> tokens = new ArrayList<>();
        AnalisadorLexico lexer = new AnalisadorLexico(programa);
        Token t;
        while ((t = lexer.proximoToken()) != null) {
            tokens.add(t);
        }

        checar("nenhum token deve ser de comentário",
                tokens.stream().noneMatch(tok -> tok.getLexema().contains("/*")));
        checar("último token deve ser o Fim ('.')",
                tokens.get(tokens.size() - 1).getTipo() == TipoToken.FIM);
        checar("'program' deve ser Palavra reservada",
                tokens.get(0).getTipo() == TipoToken.PALAVRA_RESERVADA);
        checar("'teste' deve ser Identificador",
                tokens.get(1).getTipo() == TipoToken.IDENTIFICADOR);
        checar("'3.1416' deve ser Numero real",
                tokens.stream().anyMatch(tok -> tok.getLexema().equals("3.1416") && tok.getTipo() == TipoToken.NUMERO_REAL));
        checar("':=' deve aparecer como Atribuicao",
                tokens.stream().anyMatch(tok -> tok.getLexema().equals(":=") && tok.getTipo() == TipoToken.ATRIBUICAO));
    }

    private static void checarProgramaPiloto() {
        String programa =
                "Program Piloto;\n" +
                "    /* declaracoes de variaveis e constantes globais */\n" +
                "      var cont, total: integer ;\n" +
                "Nota1, Nota2, Media_das_medias, med: real;\n" +
                "/* Inicio do Programa */\n" +
                "begin\n" +
                "media_das_medias := 0;\n" +
                "writeln(\"******** ENTRADA DE DADOS ***************\");\n" +
                "writeln(\"Digite o total de alunos\");\n" +
                "read(total);\n" +
                "for cont=1 to total do\n" +
                "begin\n" +
                "writeln(\"Digite os valores da primeira nota do aluno \", cont);\n" +
                "read(Nota1);\n" +
                "writeln(\"Digite os valores da segunda nota do aluno \", cont);\n" +
                "read(Nota2);\n" +
                "med := (Nota1+Nota2)/2.0;\n" +
                "media_das_medias := media_das_medias + med;\n" +
                "write(\"Media = \",med);\n" +
                "end;\n" +
                "write(\"Media Geral = \", Media_das_medias/total);\n" +
                "end.\n";

        List<Token> tokens = new ArrayList<>();
        AnalisadorLexico lexer = new AnalisadorLexico(programa);
        Token t;
        while ((t = lexer.proximoToken()) != null) {
            tokens.add(t);
        }

        checar("nenhum token deve conter '/*' (comentário vazou)",
                tokens.stream().noneMatch(tok -> tok.getLexema().contains("/*")));
        checar("todas as 6 strings do programa devem ter sido reconhecidas",
                tokens.stream().filter(tok -> tok.getTipo() == TipoToken.CONSTANTE_STRING).count() == 6);
        checar("'(Nota1+Nota2)/2.0' -> divisão logo após ')' não deve virar comentário",
                tokens.stream().anyMatch(tok -> tok.getLexema().equals("/") && tok.getTipo() == TipoToken.OPERADOR_ARITMETICO));
        checar("'2.0' deve ser Numero real",
                tokens.stream().anyMatch(tok -> tok.getLexema().equals("2.0") && tok.getTipo() == TipoToken.NUMERO_REAL));
        checar("último token deve ser Fim",
                tokens.get(tokens.size() - 1).getTipo() == TipoToken.FIM);
    }

    private static void checarToken(String entrada, String lexemaEsperado, TipoToken tipoEsperado) {
        AnalisadorLexico lexer = new AnalisadorLexico(entrada);
        Token t = lexer.proximoToken();
        boolean ok = t != null && t.getLexema().equals(lexemaEsperado) && t.getTipo() == tipoEsperado;
        checar("'" + entrada + "' -> " + lexemaEsperado + " / " + tipoEsperado, ok);
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
