package com.example.project_orion;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.ContextCompat; // Import necessário para ContextCompat
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<Message> messageList = new ArrayList<>();

    // Para definir a lista inicial (usado ao carregar o histórico)
    public void setMessages(List<Message> messages) {
        messageList = messages;
        notifyDataSetChanged();
    }

    // Metodo para adicionar mensagem na lista e avisar a tela
    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Certifica de que item_message reflete o XML modificado
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textBody, textSender;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textBody = itemView.findViewById(R.id.text_message_body);
        }

        void bind(Message message) {
            textBody.setText(message.text);

            // Config do LayoutParams para controle de Gravity
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) textBody.getLayoutParams();

            if (message.isUser) {
                // Se for USUÁRIO: alinha à Direita (END) e usa a bolha AZUL

                // Aplicar Layout Gravity para alinhar a bolha
                params.gravity = Gravity.END;

                // Definir o background arredondado (Azul Pastel)
                // Usa ContextCompat para obter Drawables de forma segura
                textBody.setBackground(
                        ContextCompat.getDrawable(itemView.getContext(), R.drawable.bubble_user)
                );


            } else {
                // Se for IA alinha à Esquerda (START) e usa a bolha VERDE

                // Aplica Layout Gravity para alinhar a bolha
                params.gravity = Gravity.START;

                // Define o background arredondado
                textBody.setBackground(
                        ContextCompat.getDrawable(itemView.getContext(), R.drawable.bubble_ai)
                );
            }

            // Aplica os novos parâmetros
            textBody.setLayoutParams(params);
        }
    }
}