# Arquitetura

A arquitetura do Orion será composta pelos seguintes módulos:

- **Frontend/Backend (App Android)**:  
  Interface em Java(lógica da aplicação) + XML(layout da interface da aplicação) no Android Studio, responsável pela interação com o usuário.

- **Módulo de IA Local**:  
  Contém o modelo LLM embarcado no dispositivo, usando bibliotecas compatíveis com execução offline.

- **Módulo de Cálculos**:  
  Responsável por realizar operações matemáticas e retornar resultados.

- **Persistência de Dados**:  
  Banco SQLite para armazenar histórico local.

- **Diagrama da aplicação**:

<img width="345" height="336" alt="DIAGRAMA" src="https://github.com/user-attachments/assets/712780c6-0c8e-4ca3-adef-4ef7c212f354" />
