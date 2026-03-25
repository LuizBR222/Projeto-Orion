package com.example.project_orion;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List; // Para usar List
// Import do MediaPipe
import com.google.mediapipe.tasks.genai.llminference.LlmInference;
// Import do DB Helper
import com.example.project_orion.ChatDbHelper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ChatAdapter chatAdapter;

    // O Cérebro da IA
    private LlmInference llmInference;

    // Gerenciador do Banco de Dados
    private ChatDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_chat);
        inputMessage = findViewById(R.id.input_message);
        btnSend = findViewById(R.id.btn_send);

        chatAdapter = new ChatAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(chatAdapter);

        // Inicializa o DB Helper
        dbHelper = new ChatDbHelper(this);

        // Carrega o histórico ANTES de iniciar a IA
        loadChatHistory();

        // Inicia a IA
        initializeAI();

        // Ação do Botão Enviar
        btnSend.setOnClickListener(v -> {
            String text = inputMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });
    }

    // Metodo para carregar as mensagens do banco de dados.
    private void loadChatHistory() {
        List<Message> history = dbHelper.getAllMessages();
        chatAdapter.setMessages(history);

        // Rola para a última mensagem se houver histórico
        if (!history.isEmpty()) {
            recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void initializeAI() {
        Toast.makeText(this, "Conectando-nos às constelações de Orion...", Toast.LENGTH_LONG).show();

        new Thread(() -> {
            try {
                String modelPath = getAssetFilePath(this, "model.bin");

                // Apenas o essencial que funciona em todas as versões
                LlmInference.LlmInferenceOptions options = LlmInference.LlmInferenceOptions.builder()
                        .setModelPath(modelPath)
                        .setMaxTokens(512) // Define o tamanho da resposta
                        .build();

                llmInference = LlmInference.createFromOptions(this, options);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Orion Pronto!", Toast.LENGTH_SHORT).show();

                    // Se o chat estiver vazio, envia a mensagem de saudação
                    if (chatAdapter.getItemCount() == 0) {
                        Message welcomeMessage = new Message("Pronto para uso. Sou o Orion, sua IA offline.", false);
                        chatAdapter.addMessage(welcomeMessage);
                        dbHelper.addMessage(welcomeMessage); // Salva a mensagem de boas-vindas
                    }
                });

            } catch (Exception e) {
                Log.e("IA_ERROR", "Erro: " + e.getMessage());
                runOnUiThread(() -> {
                    Message errorMessage = new Message("Erro ao iniciar IA. Verifique os logs.", false);
                    chatAdapter.addMessage(errorMessage);
                    dbHelper.addMessage(errorMessage); // Salva a mensagem de erro
                });
            }
        }).start();
    }

    private void sendMessage(String text) {
        // Cria a mensagem do usuário
        Message userMessage = new Message(text, true);

        // Mostra a mensagem do usuário
        chatAdapter.addMessage(userMessage);
        inputMessage.setText("");
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

        // Salva a mensagem do usuário no DB
        dbHelper.addMessage(userMessage);

        if (llmInference == null) {
            Message initMessage = new Message("Ainda estou inicializando...", false);
            chatAdapter.addMessage(initMessage);
            dbHelper.addMessage(initMessage); // Salva no DB
            return;
        }

        // Gera a resposta em uma Thread separada
        new Thread(() -> {
            try {
                // Forçar Português e coerência
                // Usando as tags oficiais do Gemma: <start_of_turn>
                String prompt = "<start_of_turn>user\n" +
                        "Você é o Orion, um assistente útil e inteligente. " +
                        "Responda à pergunta abaixo de forma resumida e sempre em Português do Brasil.\n\n" +
                        "Pergunta: " + text +
                        "<end_of_turn>\n<start_of_turn>model\n";

                String responseText = llmInference.generateResponse(prompt);

                // Cria a mensagem de resposta da IA
                Message aiMessage = new Message(responseText.trim(), false);

                runOnUiThread(() -> {
                    // Mostra a mensagem da IA
                    chatAdapter.addMessage(aiMessage);
                    recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);

                    // Salva a mensagem da IA no DB
                    dbHelper.addMessage(aiMessage);
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Message errorMessage = new Message("Erro na resposta: " + e.getMessage(), false);
                    chatAdapter.addMessage(errorMessage);
                    dbHelper.addMessage(errorMessage); // Salva no DB
                });
            }
        }).start();
    }

    // Metodo Auxiliar para copiar o arquivo
    private String getAssetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        try (InputStream is = context.getAssets().open(assetName);
             OutputStream os = new FileOutputStream(file)) {
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
        return file.getAbsolutePath();
    }
}