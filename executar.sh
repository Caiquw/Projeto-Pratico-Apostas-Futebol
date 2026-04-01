#!/bin/bash
# Script para compilar e executar o sistema

echo "=== Compilando ==="
mkdir -p out
javac -d out -sourcepath src src/Main.java src/model/*.java src/controller/*.java src/view/*.java

if [ $? -eq 0 ]; then
    echo "=== Compilado com sucesso! Executando ==="
    java -cp out Main
else
    echo "=== Erro na compilação ==="
fi
