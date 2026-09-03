package minipascal.lexico.gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import minipascal.lexico.core.AnalisadorLexico;
import minipascal.lexico.io.EscritorArquivoSaida;
import minipascal.lexico.io.LeitorArquivoFonte;
import minipascal.lexico.model.Token;
import minipascal.lexico.model.TipoToken;
import minipascal.lexico.tabela.TabelaPalavrasReservadas;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AnalisadorController implements Initializable {

    @FXML private Button btnAbrirArquivo;
    @FXML private Label labelArquivoAtual;
    @FXML private Button btnExecutar;

    @FXML private TextArea areaCodigoFonte;

    @FXML private TableView<Token> tabelaTokens;
    @FXML private TableColumn<Token, Number> colNumero;
    @FXML private TableColumn<Token, String> colLexema;
    @FXML private TableColumn<Token, String> colTipo;
    @FXML private TableColumn<Token, Number> colLinha;

    @FXML private TableView<Token> tabelaErros;
    @FXML private TableColumn<Token, Number> colErroLinha;
    @FXML private TableColumn<Token, String> colErroLexema;
    @FXML private TableColumn<Token, String> colErroDescricao;

    @FXML private Label labelContagemTokens;
    @FXML private Label labelContagemErros;
    @FXML private Label labelStatus;
    @FXML private Button btnSalvarSaida;

    private String fonteAtual;
    private ObservableList<Token> ultimosTokens;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColunas(colNumero, colLexema, colTipo, colLinha);
        configurarColunas(colErroLinha, colErroLexema, colErroDescricao);
    }

    // Sobrecarga para a tabela principal (4 colunas: #, Lexema, Tipo, Linha).
    private void configurarColunas(TableColumn<Token, Number> numero,
                                    TableColumn<Token, String> lexema,
                                    TableColumn<Token, String> tipo,
                                    TableColumn<Token, Number> linha) {
        numero.setCellValueFactory(dados ->
                new SimpleIntegerProperty(tabelaTokens.getItems().indexOf(dados.getValue()) + 1));
        lexema.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getLexema()));
        tipo.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getTipo().getDescricao()));
        linha.setCellValueFactory(dados ->
                new SimpleIntegerProperty(dados.getValue().getLinha()));
    }

    // Sobrecarga para a tabela de erros (3 colunas: Linha, Lexema, Descrição).
    private void configurarColunas(TableColumn<Token, Number> linha,
                                    TableColumn<Token, String> lexema,
                                    TableColumn<Token, String> descricao) {
        linha.setCellValueFactory(dados ->
                new SimpleIntegerProperty(dados.getValue().getLinha()));
        lexema.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getLexema()));
        descricao.setCellValueFactory(dados ->
                new SimpleStringProperty(dados.getValue().getTipo().getDescricao()));
    }

    @FXML
    private void abrirArquivo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Selecionar arquivo-fonte MiniPascal");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Arquivos de texto (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("Todos os arquivos", "*.*")
        );

        File arquivo = chooser.showOpenDialog(btnAbrirArquivo.getScene().getWindow());
        if (arquivo == null) {
            // Usuário cancelou o FileChooser — não é erro, apenas não faz nada.
            return;
        }

        try {
            fonteAtual = LeitorArquivoFonte.ler(arquivo.getAbsolutePath());
            areaCodigoFonte.setText(fonteAtual);
            labelArquivoAtual.setText(arquivo.getName());
            btnExecutar.setDisable(false);
            labelStatus.setText("STATUS: ARQUIVO CARREGADO");
        } catch (IOException e) {
            labelStatus.setText("STATUS: ERRO AO LER ARQUIVO — " + e.getMessage());
            fonteAtual = null;
            btnExecutar.setDisable(true);
        }
    }

    @FXML
    private void executarAnalise() {
        if (fonteAtual == null) {
            labelStatus.setText("STATUS: NENHUM ARQUIVO CARREGADO");
            return;
        }

        TabelaPalavrasReservadas tabela = new TabelaPalavrasReservadas();
        AnalisadorLexico lexer = new AnalisadorLexico(fonteAtual, tabela);

        ObservableList<Token> tokens = FXCollections.observableArrayList();
        Token token;
        while ((token = lexer.proximoToken()) != null) {
            tokens.add(token);
        }

        ultimosTokens = tokens;
        tabelaTokens.setItems(ultimosTokens);

        ObservableList<Token> erros = FXCollections.observableArrayList();
        for (Token t : ultimosTokens) {
            if (t.getTipo() == TipoToken.ERRO_LEXICO) {
                erros.add(t);
            }
        }
        tabelaErros.setItems(erros);

        labelContagemTokens.setText("TOKENS: " + ultimosTokens.size());
        labelContagemErros.setText("ERRORS: " + erros.size());

        if (erros.isEmpty()) {
            labelStatus.setText("STATUS: ANALISE CONCLUIDA — SEM ERROS");
        } else {
            labelStatus.setText("STATUS: ANALISE CONCLUIDA — " + erros.size() + " ERRO(S) LEXICO(S)");
        }
        btnSalvarSaida.setDisable(false);
    }

    @FXML
    private void salvarSaida() {
        if (ultimosTokens == null || ultimosTokens.isEmpty()) {
            labelStatus.setText("STATUS: NENHUM RESULTADO PARA SALVAR");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvar saída da análise léxica");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos de texto (*.txt)", "*.txt"));
        chooser.setInitialFileName(sugerirNomeSaida());

        File destino = chooser.showSaveDialog(btnSalvarSaida.getScene().getWindow());
        if (destino == null) {
            // Usuário cancelou o FileChooser — não é erro, mantém o estado atual.
            return;
        }

        try {
            EscritorArquivoSaida.escrever(destino.getAbsolutePath(), ultimosTokens);
            labelStatus.setText("STATUS: SAIDA SALVA EM " + destino.getName());
        } catch (IOException e) {
            labelStatus.setText("STATUS: ERRO AO SALVAR — " + e.getMessage());
        }
    }

    // Sugere "<nomeDoArquivoAberto>_saida.txt", mesma convenção usada por
    // Main.gerarNomeSaida() — mantém consistência com o CLI já existente.
    private String sugerirNomeSaida() {
        String nomeAtual = labelArquivoAtual.getText();
        int ponto = nomeAtual.lastIndexOf('.');
        String base = ponto >= 0 ? nomeAtual.substring(0, ponto) : nomeAtual;
        return base + "_saida.txt";
    }
}