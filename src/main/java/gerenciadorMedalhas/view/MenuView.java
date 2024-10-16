package gerenciadorMedalhas.view;

import gerenciadorMedalhas.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuView extends JFrame {

    private JButton btnEscolherParticipantes;
    private JButton btnEscolherModalidades;
    private JButton btnInformarResultados;
    private JButton btnRankingModalidades;
    private JButton btnRankingGeral;

    public MenuView() {
        setTitle("Menu Principal");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null); // Centraliza a janela

        // Cria botões
        criarBotoes();
    }

    private void criarBotoes() {
        btnEscolherParticipantes = criarBotao("Escolher Participantes", 30);
        btnEscolherModalidades = criarBotao("Escolher Modalidades", 70);
        btnInformarResultados = criarBotao("Informar Resultados", 110);
        btnRankingModalidades = criarBotao("Ranking Modalidades", 150);
        btnRankingGeral = criarBotao("Ranking Geral", 190);
    }

    private JButton criarBotao(String texto, int y) {
        JButton botao = new JButton(texto);
        botao.setBounds(100, y, 200, 30);
        add(botao);
        return botao;
    }

    // Métodos para adicionar listeners nos botões
    public void addEscolherParticipantesListener(ActionListener listener) {
        btnEscolherParticipantes.addActionListener(listener);
    }

    public void addEscolherModalidadesListener(ActionListener listener) {
        btnEscolherModalidades.addActionListener(listener);
    }

    // Metodo para exibir um novo frame com checkboxes
    public void showCheckboxFrame(String titulo, List<String> opcoes, List<Integer> ids) {
        JFrame newFrame = new JFrame(titulo);
        newFrame.setSize(400, 300);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.setLayout(new BorderLayout());
        newFrame.setLocationRelativeTo(null); // Centraliza a nova janela

        JLabel label = new JLabel("Escolha as opções:", SwingConstants.CENTER);
        newFrame.add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        List<JCheckBox> checkboxes = new ArrayList<>();

        // Cria os checkboxes com ID associado
        for (int i = 0; i < opcoes.size(); i++) {
            String opcao = opcoes.get(i);
            Integer id = ids.get(i);
            JCheckBox checkbox = new JCheckBox(opcao);
            checkbox.putClientProperty("id", id); // Armazenando o ID no checkbox
            checkboxes.add(checkbox);
            panel.add(checkbox);
        }

        JScrollPane scrollPane = new JScrollPane(panel);
        newFrame.add(scrollPane, BorderLayout.CENTER);

        // Painel para os botões
        JPanel buttonPanel = new JPanel();
        JButton btnVoltar = new JButton("Voltar");
        btnVoltar.addActionListener(e -> voltar(newFrame, checkboxes));
        buttonPanel.add(btnVoltar);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvar(newFrame, checkboxes));
        buttonPanel.add(btnSalvar);

        newFrame.add(buttonPanel, BorderLayout.SOUTH);
        newFrame.setVisible(true);
    }

    // Metodo para salvar os dados selecionados
    private void salvar(JFrame frame, List<JCheckBox> checkboxes) {
        List<Integer> idsPaises = new ArrayList<>();

        // Coletar os IDs dos países selecionados
        for (JCheckBox checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                Integer id = (Integer) checkbox.getClientProperty("id"); // Obtendo o ID
                idsPaises.add(id);
            }
        }

        // Verifica o número de checkboxes selecionados
        if (idsPaises.size() < 16 || idsPaises.size() > 16) {
            JOptionPane.showMessageDialog(frame, "É necessário selecionar exatamente 16 opções para salvar.");
            return; // Retorna para evitar continuar o processo de salvamento
        }

        // Comando SQL para atualizar a tabela paises
        String sql = "UPDATE paises SET participando = 1 WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Integer idPais : idsPaises) {
                stmt.setInt(1, idPais);
                stmt.addBatch(); // Adiciona ao batch
            }
            int[] resultados = stmt.executeBatch(); // Executa o batch

            // Mostrar resultado da atualização
            JOptionPane.showMessageDialog(frame, "Dados salvos com sucesso!");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Erro ao salvar dados: " + e.getMessage());
        }
    }

    // Metodo para voltar ao menu principal
    private void voltar(JFrame frame, List<JCheckBox> checkboxes) {
        boolean algumSelecionado = checkboxes.stream().anyMatch(JCheckBox::isSelected);

        if (algumSelecionado) {
            int resposta = JOptionPane.showConfirmDialog(frame,
                    "Você tem opções selecionadas que não foram salvas. Deseja sair sem salvar?",
                    "Confirmar saída",
                    JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.NO_OPTION) {
                return; // Não sai, apenas retorna
            }
        }

        frame.dispose();
        this.setVisible(true);
    }
}
