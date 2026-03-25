package com.example.project_orion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.concurrent.Executor;

public class LockActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setupBiometricPrompt();
        promptForAuthentication();
    }

    private void setupBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e("BIOMETRIC", "Erro de Autenticação: " + errString + " (" + errorCode + ")");
                Toast.makeText(getApplicationContext(), "Erro: " + errString, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Acesso Concedido.", Toast.LENGTH_SHORT).show();

                startMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Falha na Autenticação.", Toast.LENGTH_SHORT).show();
                // Permite uma nova tentativa
            }
        });

        // Configuração do Diálogo
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Acesso ao Orion")
                .setSubtitle("Autentique-se para continuar")
                .setAllowedAuthenticators(androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();
    }

    private void promptForAuthentication() {

        try {
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e) {
            Log.e("BIOMETRIC", "Exceção ao tentar autenticar: " + e.getMessage());
            Toast.makeText(this, "Não foi possível iniciar a autenticação. Fechando...", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // Finaliza a LockActivity pro usuário não voltar para ela com o botão "Voltar"
        finish();
    }
}
