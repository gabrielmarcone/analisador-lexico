package minipascal.lexico.model;

/**
 * Classes léxicas da linguagem Mini_Pascal.
 *
 * A descrição de cada constante é exatamente o texto que deve aparecer na
 * coluna "token" do arquivo de saída, no mesmo formato do exemplo dado pelo
 * professor (ex: "program" -> "Palavra reservada").
 *
 * Não usamos códigos numéricos para os tokens, conforme exigido na decisão
 * de projeto (b) do enunciado.
 *
 * IMPORTANTE: como os textos têm acento, a camada de I/O (Fase 6) precisa
 * ler e escrever os arquivos explicitamente em UTF-8 (nunca o charset
 * default da plataforma), senão os acentos viram caracteres corrompidos no
 * Windows. É exatamente esse tipo de problema que corrompeu os acentos no
 * .doc original do professor que vocês me enviaram.
 */
public enum TipoToken {

    PALAVRA_RESERVADA("Palavra reservada"),
    IDENTIFICADOR("Identificador"),
    NUMERO_INTEIRO("Número inteiro"),
    NUMERO_REAL("Número real"),
    OPERADOR_ARITMETICO("Operador aritmético"),
    OPERADOR_RELACIONAL("Operador relacional"),
    OPERADOR_LOGICO("Operador lógico"),
    SIMBOLO_ESPECIAL("Símbolo especial"),
    ATRIBUICAO("Atribuição"),
    FIM("Fim"),
    CONSTANTE_STRING("Constante string"),
    CONSTANTE_CHAR("Constante char"),
    ERRO_LEXICO("Erro léxico");

    private final String descricao;

    TipoToken(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
