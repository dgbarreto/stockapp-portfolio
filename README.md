# stockapp-portfolio

Módulo KMP (Kotlin Multiplatform) + Compose Multiplatform do [StockApp](https://github.com/dgbarreto/stockapp-app) — app de acompanhamento de investimentos (projeto de estudo).

Domain + data (posição do usuário — ticker/quantidade/preço médio, cliente do [`stockapp-backend`](https://github.com/dgbarreto/stockapp-backend), endpoints `/positions`) e telas Compose do dashboard e da carteira.

## Estrutura

- `portfolio/` — único módulo do repo, alvo Android (via `com.android.kotlin.multiplatform.library`) + iOS (framework estático `Portfolio`), código comum em `portfolio/src/commonMain`.
- `sample/` + `sample-android/` — apps de exemplo, dev-only, pra validar o módulo isoladamente (login via `stockapp-auth` + tela de placeholder até o dashboard existir).

## Status

**Fase 4 — Carteira** (ver roadmap em `docs/roadmap.md` no repo de planejamento): scaffold criado a partir do template `stockapp-auth`, backend (`/positions`) já implementado e testado. Ainda sem domain/data/presentation de `Position` implementados neste módulo.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1

## Rodando

```
./gradlew :portfolio:build
./gradlew :portfolio:testAndroidHostTest
./gradlew :portfolio:iosSimulatorArm64Test
```

---

_Progresso mantido manualmente conforme o projeto avança._
