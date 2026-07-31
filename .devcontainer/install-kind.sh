#!/usr/bin/env bash
set -e

if [ "$(uname -m)" = "x86_64" ]; then
    curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.32.0/kind-linux-amd64
elif [ "$(uname -m)" = "aarch64" ]; then
    curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.32.0/kind-linux-arm64
else
    echo "Arquitetura não suportada: $(uname -m)"
    exit 1
fi

chmod +x ./kind
sudo mv ./kind /usr/local/bin/kind

kind version