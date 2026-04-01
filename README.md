# Sistema de Apostas — Campeonato de Futebol
**Projeto Prático — LPOO (Parte 1)**

## Como executar

### Pré-requisito
- Java JDK 11 ou superior instalado

### Compilar e executar
```bash
chmod +x executar.sh
./executar.sh
```

Ou manualmente:
```bash
mkdir -p out
javac -d out -sourcepath src src/Main.java src/model/*.java src/controller/*.java src/view/*.java
java -cp out Main
```

---

## Estrutura do Projeto

```
src/
├── Main.java                          # Ponto de entrada
├── model/
│   ├── Cadastravel.java               # Interface
│   ├── Pessoa.java                    # Classe abstrata
│   ├── Participante.java              # extends Pessoa
│   ├── Administrador.java             # extends Pessoa
│   ├── EntidadeEsportiva.java         # Classe abstrata
│   ├── Clube.java                     # extends EntidadeEsportiva
│   ├── Campeonato.java                # extends EntidadeEsportiva
│   ├── Partida.java                   # implements Cadastravel
│   ├── Aposta.java                    # Classe concreta
│   └── Grupo.java                     # implements Cadastravel
├── controller/
│   └── Sistema.java                   # Singleton controlador
└── view/
    └── MainFrame.java                 # Interface Swing (AWT/Swing)
```

---

## Conceitos de OO aplicados

| Conceito          | Onde está aplicado |
|-------------------|--------------------|
| **Interface**     | `Cadastravel` — usada por `Pessoa`, `EntidadeEsportiva`, `Partida`, `Grupo` |
| **Classe abstrata** | `Pessoa` e `EntidadeEsportiva` |
| **Herança**       | `Participante`/`Administrador` → `Pessoa`; `Clube`/`Campeonato` → `EntidadeEsportiva` |
| **Encapsulamento** | Atributos privados com getters/setters em todas as classes |
| **Polimorfismo**  | `exibirInfo()` sobrescrito em `Participante`, `Administrador`, `Clube`, `Campeonato` |
| **Construtores**  | Padrão e sobrecarregado em todas as classes |
| **Interface Swing** | `MainFrame` com `JTabbedPane` — 6 abas funcionais |

---

## Regras de negócio implementadas

- Campeonato: máximo de **8 clubes**
- Sistema: máximo de **5 grupos** e **5 participantes**
- Apostas: apenas até **20 minutos antes** da partida
- Pontuação:
  - Acertar resultado (vencedor/empate): **5 pontos**
  - Acertar resultado + placar exato: **10 pontos**
- Classificação por grupo ordenada por pontuação
